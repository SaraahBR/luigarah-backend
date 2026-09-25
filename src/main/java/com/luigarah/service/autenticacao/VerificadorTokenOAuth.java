package com.luigarah.service.autenticacao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.exception.CredenciaisInvalidasException;
import com.luigarah.exception.RegraDeNegocioException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Set;

/**
 * Confere com o próprio provedor (Google ou Facebook) o token que o frontend recebeu
 * no login social, e devolve o e-mail que o provedor garante ser do usuário.
 *
 * Antes o /api/auth/oauth/sync confiava no e-mail enviado pelo navegador: qualquer
 * pessoa conseguia um JWT de qualquer conta, inclusive do admin.
 *
 * - Google: valida o id_token em oauth2.googleapis.com/tokeninfo (assinatura, validade,
 *   emissor, e-mail verificado) e confere se foi emitido para o nosso GOOGLE_CLIENT_ID.
 * - Facebook: valida o access_token em graph.facebook.com/debug_token com o app secret,
 *   confere se é do nosso app e busca o e-mail na Graph API.
 */
@Component
@Slf4j
public class VerificadorTokenOAuth {

    /** Dados confirmados pelo provedor. */
    public record UsuarioOAuth(String email, String providerId) {}

    private static final Set<String> EMISSORES_GOOGLE = Set.of("accounts.google.com", "https://accounts.google.com");

    private final String googleClientId;
    private final String facebookAppId;
    private final String facebookAppSecret;
    private final String googleTokenInfoUrl;
    private final String facebookGraphUrl;

    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient http = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .build();

    public VerificadorTokenOAuth(
            @Value("${app.oauth.google.client-id:}") String googleClientId,
            @Value("${app.oauth.facebook.app-id:}") String facebookAppId,
            @Value("${app.oauth.facebook.app-secret:}") String facebookAppSecret,
            @Value("${app.oauth.google.tokeninfo-url:https://oauth2.googleapis.com/tokeninfo}") String googleTokenInfoUrl,
            @Value("${app.oauth.facebook.graph-url:https://graph.facebook.com}") String facebookGraphUrl) {
        this.googleClientId = googleClientId;
        this.facebookAppId = facebookAppId;
        this.facebookAppSecret = facebookAppSecret;
        this.googleTokenInfoUrl = googleTokenInfoUrl;
        this.facebookGraphUrl = facebookGraphUrl;
    }

    public UsuarioOAuth verificar(String provider, String token) {
        if (token == null || token.isBlank()) {
            throw new CredenciaisInvalidasException("Login social sem token do provedor. Entre novamente.");
        }
        String p = provider == null ? "" : provider.trim().toLowerCase();
        return switch (p) {
            case "google" -> verificarGoogle(token);
            case "facebook" -> verificarFacebook(token);
            default -> throw new RegraDeNegocioException("Provedor de login não suportado: " + provider);
        };
    }

    // ------------------------------------------------------------------
    // Google
    // ------------------------------------------------------------------

    private UsuarioOAuth verificarGoogle(String idToken) {
        if (googleClientId.isBlank()) {
            log.error("🔐 GOOGLE_CLIENT_ID não configurado: login com Google recusado");
            throw new RegraDeNegocioException("Login com Google indisponível no momento.");
        }

        HttpUrl url = HttpUrl.get(googleTokenInfoUrl).newBuilder()
                .addQueryParameter("id_token", idToken)
                .build();
        JsonNode dados = buscar(url, "Google");

        String audiencia = dados.path("aud").asText("");
        String emissor = dados.path("iss").asText("");
        String email = dados.path("email").asText("");
        boolean emailVerificado = "true".equalsIgnoreCase(dados.path("email_verified").asText(""));

        if (!googleClientId.equals(audiencia) || !EMISSORES_GOOGLE.contains(emissor)) {
            log.warn("🔐 Token do Google emitido para outro app (aud={}, iss={})", audiencia, emissor);
            throw new CredenciaisInvalidasException("Login com Google inválido. Entre novamente.");
        }
        if (email.isBlank() || !emailVerificado) {
            throw new CredenciaisInvalidasException("O e-mail da conta Google não está verificado.");
        }
        return new UsuarioOAuth(email.toLowerCase(), dados.path("sub").asText(null));
    }

    // ------------------------------------------------------------------
    // Facebook
    // ------------------------------------------------------------------

    private UsuarioOAuth verificarFacebook(String accessToken) {
        if (facebookAppId.isBlank() || facebookAppSecret.isBlank()) {
            log.error("🔐 FACEBOOK_CLIENT_ID/FACEBOOK_CLIENT_SECRET não configurados: login com Facebook recusado");
            throw new RegraDeNegocioException("Login com Facebook indisponível no momento.");
        }

        // 1. O token é válido e foi emitido para o nosso app?
        HttpUrl debug = HttpUrl.get(facebookGraphUrl + "/debug_token").newBuilder()
                .addQueryParameter("input_token", accessToken)
                .addQueryParameter("access_token", facebookAppId + "|" + facebookAppSecret)
                .build();
        JsonNode info = buscar(debug, "Facebook").path("data");
        if (!info.path("is_valid").asBoolean(false) || !facebookAppId.equals(info.path("app_id").asText(""))) {
            log.warn("🔐 Token do Facebook inválido ou de outro app (app_id={})", info.path("app_id").asText(""));
            throw new CredenciaisInvalidasException("Login com Facebook inválido. Entre novamente.");
        }

        // 2. E-mail da conta (appsecret_proof prova que a chamada vem do nosso servidor)
        HttpUrl me = HttpUrl.get(facebookGraphUrl + "/me").newBuilder()
                .addQueryParameter("fields", "id,email")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("appsecret_proof", hmacSha256(facebookAppSecret, accessToken))
                .build();
        JsonNode perfil = buscar(me, "Facebook");
        String email = perfil.path("email").asText("");
        if (email.isBlank()) {
            throw new CredenciaisInvalidasException(
                    "Sua conta do Facebook não compartilha um e-mail. Use outro método de login.");
        }
        return new UsuarioOAuth(email.toLowerCase(), perfil.path("id").asText(null));
    }

    // ------------------------------------------------------------------

    /** Consulta o provedor (substituída nos testes). */
    JsonNode buscar(HttpUrl url, String provedor) {
        Request req = new Request.Builder().url(url).get().build();
        try (Response resp = http.newCall(req).execute()) {
            String corpo = resp.body() != null ? resp.body().string() : "";
            if (resp.code() >= 400 && resp.code() < 500) {
                // token expirado, falsificado ou revogado
                throw new CredenciaisInvalidasException("Login com " + provedor + " expirado ou inválido. Entre novamente.");
            }
            if (!resp.isSuccessful()) {
                throw new RegraDeNegocioException("Não foi possível confirmar o login com " + provedor + ". Tente novamente.");
            }
            return mapper.readTree(corpo);
        } catch (IOException e) {
            log.warn("🔐 Falha ao consultar {}: {}", provedor, e.getMessage());
            throw new RegraDeNegocioException("Não foi possível confirmar o login com " + provedor + ". Tente novamente.");
        }
    }

    static String hmacSha256(String chave, String valor) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(valor.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA256 indisponível", e);
        }
    }
}
