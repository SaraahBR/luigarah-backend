package com.luigarah.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para redefinir senha usando código de verificação")
public class RedefinirSenhaComCodigoRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "usuario@example.com")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Pattern(regexp = "^[0-9]{6}$", message = "Código deve conter exatamente 6 dígitos numéricos")
    @Schema(description = "Código de verificação de 6 dígitos recebido por email", example = "123456")
    private String codigo;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 40, message = "Nova senha deve ter entre 6 e 40 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,40}$",
        message = "Nova senha deve conter: mínimo 6 e máximo 40 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)"
    )
    @Schema(
        description = "Nova senha do usuário. Deve conter: mínimo 6 e máximo 40 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)",
        example = "Senha@123",
        minLength = 6,
        maxLength = 40
    )
    private String novaSenha;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    @Schema(description = "Confirmação da nova senha", example = "NovaSenha@123")
    private String confirmarNovaSenha;
}

