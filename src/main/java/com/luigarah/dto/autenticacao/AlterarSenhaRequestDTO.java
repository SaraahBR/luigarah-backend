package com.luigarah.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de alteração de senha.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para alteração de senha do usuário autenticado")
public class AlterarSenhaRequestDTO {

    @Schema(
            description = "Senha atual do usuário (para validação de segurança)",
            example = "Admin@123",
            required = true
    )
    @NotBlank(message = "Senha atual é obrigatória")
    private String senhaAtual;

    @Schema(
            description = "Nova senha desejada. Recomenda-se usar letras maiúsculas, minúsculas, números e caracteres especiais.",
            example = "MinhaNovaSenh@Segura2025!",
            required = true,
            minLength = 6,
            maxLength = 100
    )
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 100, message = "Nova senha deve ter entre 6 e 100 caracteres")
    private String novaSenha;

    @Schema(
            description = "Confirmação da nova senha (deve ser idêntica ao campo novaSenha)",
            example = "MinhaNovaSenh@Segura2025!",
            required = true
    )
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarNovaSenha;
}
