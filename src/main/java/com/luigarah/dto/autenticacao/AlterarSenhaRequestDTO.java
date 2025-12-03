package com.luigarah.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
            description = "Nova senha desejada. Deve conter: mínimo 6 e máximo 40 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)",
            example = "Senha@123",
            required = true,
            minLength = 6,
            maxLength = 40
    )
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 40, message = "Nova senha deve ter entre 6 e 40 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,40}$",
        message = "Nova senha deve conter: mínimo 6 e máximo 40 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)"
    )
    private String novaSenha;

    @Schema(
            description = "Confirmação da nova senha (deve ser idêntica ao campo novaSenha)",
            example = "MinhaNovaSenh@Segura2025!",
            required = true
    )
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarNovaSenha;
}
