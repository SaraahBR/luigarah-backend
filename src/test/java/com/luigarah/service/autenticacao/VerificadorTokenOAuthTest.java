package com.luigarah.service.autenticacao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.exception.CredenciaisInvalidasException;
import com.luigarah.exception.RegraDeNegocioException;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regras de aceitação do token do login social, com as respostas do Google/Facebook
 * simuladas (nenhuma chamada real à internet).
 */
class VerificadorTokenOAuthTest {

    private static final String CLIENT_ID = "meu-app.apps.googleusercontent.com";

    /** Verificador com respostas fixas por caminho da URL consultada. */
    static class VerificadorFalso extends VerificadorTokenOAuth {
        final Map<String, String> respostas = new LinkedHashMap<>();
        final List<HttpUrl> consultas = new ArrayList<>();

        VerificadorFalso(String googleClientId, String fbId, String fbSecret) {
            super(googleClientId, fbId, fbSecret, "https://google.test/tokeninfo", "https://facebook.test");
        }

        @Override
        JsonNode buscar(HttpUrl url, String provedor) {
            consultas.add(url);
            try {
                return new ObjectMapper().readTree(respostas.get(url.encodedPath()));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private VerificadorFalso google(String resposta) {
        VerificadorFalso v = new VerificadorFalso(CLIENT_ID, "", "");
        v.respostas.put("/tokeninfo", resposta);
        return v;
    }

    @Test
    @DisplayName("Google: token do nosso app com e-mail verificado é aceito")
    void googleValido() {
        VerificadorFalso v = google("""
                {"aud":"%s","iss":"https://accounts.google.com","email":"Maria@Gmail.com",
                 "email_verified":"true","sub":"123"}""".formatted(CLIENT_ID));

        VerificadorTokenOAuth.UsuarioOAuth u = v.verificar("google", "id-token");

        assertEquals("maria@gmail.com", u.email());
        assertEquals("123", u.providerId());
        assertEquals("id-token", v.consultas.get(0).queryParameter("id_token"));
    }

    @Test
    @DisplayName("Google: token emitido para outro app é recusado")
    void googleOutroApp() {
        VerificadorFalso v = google("""
                {"aud":"app-de-outra-pessoa","iss":"accounts.google.com","email":"admin@luigarah.com",
                 "email_verified":"true","sub":"1"}""");
        assertThrows(CredenciaisInvalidasException.class, () -> v.verificar("google", "id-token"));
    }

    @Test
    @DisplayName("Google: e-mail não verificado ou emissor estranho é recusado")
    void googleEmailNaoVerificado() {
        VerificadorFalso naoVerificado = google("""
                {"aud":"%s","iss":"accounts.google.com","email":"a@b.com","email_verified":"false"}""".formatted(CLIENT_ID));
        assertThrows(CredenciaisInvalidasException.class, () -> naoVerificado.verificar("google", "t"));

        VerificadorFalso emissor = google("""
                {"aud":"%s","iss":"evil.com","email":"a@b.com","email_verified":"true"}""".formatted(CLIENT_ID));
        assertThrows(CredenciaisInvalidasException.class, () -> emissor.verificar("google", "t"));
    }

    @Test
    @DisplayName("Facebook: confere o app no debug_token e pega o e-mail com appsecret_proof")
    void facebookValido() {
        VerificadorFalso v = new VerificadorFalso("", "fb-app", "segredo");
        v.respostas.put("/debug_token", """
                {"data":{"is_valid":true,"app_id":"fb-app","user_id":"999"}}""");
        v.respostas.put("/me", """
                {"id":"999","email":"joao@hotmail.com"}""");

        VerificadorTokenOAuth.UsuarioOAuth u = v.verificar("facebook", "access-token");

        assertEquals("joao@hotmail.com", u.email());
        assertEquals("fb-app|segredo", v.consultas.get(0).queryParameter("access_token"));
        assertEquals(VerificadorTokenOAuth.hmacSha256("segredo", "access-token"),
                v.consultas.get(1).queryParameter("appsecret_proof"));
    }

    @Test
    @DisplayName("Facebook: token de outro app ou sem e-mail é recusado")
    void facebookInvalido() {
        VerificadorFalso outroApp = new VerificadorFalso("", "fb-app", "segredo");
        outroApp.respostas.put("/debug_token", """
                {"data":{"is_valid":true,"app_id":"outro-app"}}""");
        assertThrows(CredenciaisInvalidasException.class, () -> outroApp.verificar("facebook", "t"));

        VerificadorFalso semEmail = new VerificadorFalso("", "fb-app", "segredo");
        semEmail.respostas.put("/debug_token", """
                {"data":{"is_valid":true,"app_id":"fb-app"}}""");
        semEmail.respostas.put("/me", """
                {"id":"999"}""");
        assertThrows(CredenciaisInvalidasException.class, () -> semEmail.verificar("facebook", "t"));
    }

    @Test
    @DisplayName("Sem configuração, sem token ou provedor desconhecido: recusa sem consultar ninguém")
    void recusasSemConsulta() {
        VerificadorFalso semConfig = new VerificadorFalso("", "", "");
        assertThrows(RegraDeNegocioException.class, () -> semConfig.verificar("google", "t"));
        assertThrows(RegraDeNegocioException.class, () -> semConfig.verificar("facebook", "t"));
        assertThrows(CredenciaisInvalidasException.class, () -> semConfig.verificar("google", " "));
        assertThrows(RegraDeNegocioException.class, () -> semConfig.verificar("github", "t"));
        assertTrue(semConfig.consultas.isEmpty());
    }
}
