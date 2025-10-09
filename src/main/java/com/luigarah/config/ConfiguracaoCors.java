package com.luigarah.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class ConfiguracaoCors implements WebMvcConfigurer {

    @Value("${app.cors.origens-permitidas}")
    private String origensPermitidas;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(origensPermitidas.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public CorsConfigurationSource fonteCorsConfiguration() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOriginPatterns(Arrays.asList(origensPermitidas.split(",")));
        configuracao.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(Arrays.asList("*"));
        configuracao.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
    }
}
