package com.luigarah.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

        // Configuração de segurança JWT
        String securitySchemeName = "bearerAuth";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Insira o token JWT obtido no login. Formato: Bearer {token}");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(securitySchemeName);

        return new OpenAPI()
                .servers(List.of(sameOrigin))
                .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("API Luigarah")
                        .version("1.0.0")
                        .description("""
                                API REST para gerenciamento de produtos de luxo da Luigarah.
                                
                                ## Autenticação
                                Esta API utiliza **JWT (JSON Web Token)** para autenticação.
                                
                                ### Como usar:
                                1. Faça login em `/api/auth/login` ou registre-se em `/api/auth/registrar`
                                2. Copie o token JWT retornado
                                3. Clique no botão **Authorize** (🔒) no topo desta página
                                4. Cole o token no campo (sem o prefixo "Bearer")
                                5. Clique em **Authorize** e depois **Close**
                                
                                ### Roles (Papéis):
                                - **USER**: Usuário comum - pode visualizar produtos, gerenciar carrinho e lista de desejos
                                - **ADMIN**: Administrador - acesso total ao sistema, incluindo criar/editar/deletar produtos
                                
                                ### Login Social:
                                Suporte para autenticação via Google e Facebook (OAuth2)
                                """)
                        .contact(new Contact()
                                .name("Equipe Luigarah")
                                .email("dev@luigarah.com")
                                .url("https://luigarah.vercel.app"))
                        .license(new License()
                                .name("Licença MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
