package com.luigarah.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class ConfiguracaoCors implements WebMvcConfigurer {

    @Value("${app.cors.origens-permitidas}")
    private String origensPermitidas;

    /**
     * ✅ CORREÇÃO CRÍTICA: Configurar PathMatcher para NÃO tratar /produtos/* como recurso estático
     * Isso evita o erro NoResourceFoundException: No static resource produtos/categoria/roupas
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Garante que trailing slashes sejam tratados corretamente
        configurer.setUseTrailingSlashMatch(true);
    }

    /**
     * ✅ CONFIGURAÇÃO CORS ÚNICA - Evita duplicação de headers
     * Esta é a ÚNICA configuração CORS do projeto
     */
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
     * ✅ Usa Set para eliminar duplicatas automaticamente
     */
    private String[] getOrigensComWildcard() {
        // Usar HashSet para eliminar duplicatas automaticamente
        Set<String> origens = new HashSet<>(Arrays.asList(origensPermitidas.split(",")));

        // Adicionar wildcards para Vercel (preview deployments)
        origens.add("https://*.vercel.app");

        // Adicionar localhost alternativo
        origens.add("http://localhost:3001");

        return origens.toArray(new String[0]);
    }
}
