package com.luigarah.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // << suporte a java.time.*
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.SerializationFeature; // para configurar datas
import com.luigarah.util.JsonStringCleaner;

import java.io.IOException;

/**
 * Registra:
 *  - um desserializador global de String que remove \r, \n e espaços nas pontas
 *  - o JavaTimeModule, para (de)serializar LocalDateTime, LocalDate, etc.
 *  - desabilita timestamps numéricos para datas (usa ISO-8601 por padrão)
 *
 * Observação importante:
 *   Usar builder.modules(...) substitui os módulos padrão.
 *   Por isso adicionamos explicitamente o JavaTimeModule aqui.
 */
@Configuration
public class JacksonStringSanitizerConfig {

    /** Desserializa TODO String já limpando \r, \n e espaços nas pontas. */
    static class SanitizingStringDeserializer extends StdScalarDeserializer<String> {
        public SanitizingStringDeserializer() { super(String.class); }
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            // Usa teu util — remove CR/LF e faz trim
            return JsonStringCleaner.clean(p.getValueAsString());
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer sanitizeAllStringsAndEnableJavaTime() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
                // Módulo que aplica o desserializador global para String
                SimpleModule sanitizeModule = new SimpleModule();
                sanitizeModule.addDeserializer(String.class, new SanitizingStringDeserializer());

                // ⚠️ IMPORTANTE: incluir também o JavaTimeModule para LocalDateTime etc.
                JavaTimeModule javaTimeModule = new JavaTimeModule();

                // Instala ambos os módulos. Ao usar modules(...), garantimos que o JavaTimeModule esteja presente.
                builder.modules(sanitizeModule, javaTimeModule);

                // Emite datas como ISO-8601 (ex.: "2025-10-09T19:31:28.123") em vez de timestamps numéricos
                builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        };
    }
}
