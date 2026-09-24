package com.luigarah.service.traducao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cliente do Google Cloud Translation (API v2 / Basic) com chave de API.
 * A chave vem de GOOGLE_TRANSLATE_API_KEY; sem ela a tradução fica desligada
 * e o site mostra os produtos em português.
 */
@Component
@Slf4j
public class GoogleTranslateClient {

    private static final String URL = "https://translation.googleapis.com/language/translate/v2";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    /** Limite da API: até 128 textos por requisição. */
    private static final int MAX_TEXTOS = 128;

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public GoogleTranslateClient(@Value("${app.traducao.google-api-key:}") String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    public boolean habilitado() {
        return !apiKey.isEmpty();
    }

    /**
     * Traduz os textos do português para o idioma de destino, mantendo a ordem.
     * Textos vazios voltam vazios sem consumir cota.
     */
    public List<String> traduzir(List<String> textos, String idiomaDestino) throws IOException {
        List<String> resultado = new ArrayList<>(textos);
        List<Integer> posicoes = new ArrayList<>();
        List<String> pendentes = new ArrayList<>();
        for (int i = 0; i < textos.size(); i++) {
            String t = textos.get(i);
            if (t != null && !t.isBlank()) {
                posicoes.add(i);
                pendentes.add(t);
            }
        }

        for (int inicio = 0; inicio < pendentes.size(); inicio += MAX_TEXTOS) {
            List<String> lote = pendentes.subList(inicio, Math.min(inicio + MAX_TEXTOS, pendentes.size()));
            List<String> traduzidos = chamarApi(lote, idiomaDestino);
            for (int j = 0; j < traduzidos.size(); j++) {
                resultado.set(posicoes.get(inicio + j), traduzidos.get(j));
            }
        }
        return resultado;
    }

    private List<String> chamarApi(List<String> textos, String idiomaDestino) throws IOException {
        String corpo = mapper.writeValueAsString(Map.of(
                "q", textos,
                "source", "pt",
                "target", idiomaDestino,
                "format", "text"
        ));
        Request req = new Request.Builder()
                .url(URL)
                // chave no cabeçalho para não aparecer em URLs/logs
                .header("X-goog-api-key", apiKey)
                .post(RequestBody.create(corpo, JSON))
                .build();

        try (Response resp = http.newCall(req).execute()) {
            String body = resp.body() != null ? resp.body().string() : "";
            if (!resp.isSuccessful()) {
                throw new IOException("Google Translation respondeu " + resp.code() + ": " + body);
            }
            JsonNode traducoes = mapper.readTree(body).path("data").path("translations");
            List<String> saida = new ArrayList<>();
            traducoes.forEach(n -> saida.add(n.path("translatedText").asText("")));
            if (saida.size() != textos.size()) {
                throw new IOException("Google Translation devolveu " + saida.size() + " de " + textos.size() + " textos");
            }
            return saida;
        }
    }
}
