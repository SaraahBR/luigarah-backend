package com.luigarah.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI luigaraOpenAPI() {

        // URL relativa: usa o mesmo domínio/porta (Render ou localhost)
        Server sameOrigin = new Server()
                .url("/")
                .description("Servidor Atual (Render ou Localhost)");

        return new OpenAPI()
                .servers(List.of(sameOrigin))
                .info(new Info()
                        .title("API Luigarah")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de produtos de luxo da Luigarah")
                        .contact(new Contact()
                                .name("Equipe Luigarah")
                                .email("dev@luigarah.com")
                                .url("https://luigarah.vercel.app"))
                        .license(new License()
                                .name("Licença MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
