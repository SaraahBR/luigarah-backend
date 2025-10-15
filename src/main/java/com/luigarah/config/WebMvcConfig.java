package com.luigarah.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * ✅ CORREÇÃO CRÍTICA: Desabilita o ResourceHttpRequestHandler para endpoints de API
 * Isso evita: NoResourceFoundException: No static resource produtos/categoria/roupas
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * ✅ Define prioridade MÁXIMA para os controllers terem precedência sobre recursos estáticos
     */
    @Bean
    public WebMvcRegistrations webMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
                // Prioridade MÁXIMA - controllers são checados ANTES de recursos estáticos
                mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
                return mapping;
            }
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ IMPORTANTE: Não chamar super.addResourceHandlers()
        // Isso previne a configuração padrão do Spring Boot de adicionar handlers para /static/**, /public/**, etc.

        // Configura APENAS Swagger UI como recurso estático
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .resourceChain(false);

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
    }
}
