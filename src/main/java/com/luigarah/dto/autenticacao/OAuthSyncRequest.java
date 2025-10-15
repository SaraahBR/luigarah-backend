package com.luigarah.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request para sincronizar conta OAuth com o backend")
public class OAuthSyncRequest {

    @NotBlank(message = "Provider é obrigatório")
    @Schema(description = "Provedor OAuth", example = "google", allowableValues = {"google", "facebook", "github"})
    private String provider;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Email do usuário", example = "usuario@gmail.com")
    private String email;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do usuário", example = "João")
    private String nome;

    @Schema(description = "Sobrenome do usuário", example = "Silva")
    private String sobrenome;

    @Schema(description = "URL da foto de perfil", example = "https://lh3.googleusercontent.com/a/...")
    private String fotoPerfil;

    @Schema(description = "ID do usuário no provedor OAuth", example = "109876543210")
    private String oauthId;
}

