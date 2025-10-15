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
import java.util.List;

@Configuration
public class ConfiguracaoCors implements WebMvcConfigurer {

    @Value("${app.cors.origens-permitidas}")
    private String origensPermitidas;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(getOrigensComWildcard())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource fonteCorsConfiguration() {
        CorsConfiguration configuracao = new CorsConfiguration();

        // ✅ Origens permitidas com wildcards para Vercel
        configuracao.setAllowedOriginPatterns(Arrays.asList(getOrigensComWildcard()));

        // ✅ Métodos HTTP permitidos
        configuracao.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // ✅ Headers permitidos
        configuracao.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Permitir credenciais (cookies, Authorization header)
        configuracao.setAllowCredentials(true);

        // ✅ Expor headers importantes na resposta
        configuracao.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));

        // ✅ Cache do preflight (1 hora)
        configuracao.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
    }

    /**
     * ✅ Adiciona wildcards para Vercel além das origens configuradas
     */
    private String[] getOrigensComWildcard() {
        List<String> origens = new java.util.ArrayList<>(Arrays.asList(origensPermitidas.split(",")));

        // Adicionar wildcards para Vercel (preview deployments)
        if (!origens.contains("https://*.vercel.app")) {
            origens.add("https://*.vercel.app");
        }

        // Adicionar localhost alternativo se não existir
        if (!origens.contains("http://localhost:3001")) {
            origens.add("http://localhost:3001");
        }

        return origens.toArray(new String[0]);
    }
}
