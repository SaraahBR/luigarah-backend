package com.luigarah.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar código de confirmação de conta")
public class VerificarCodigoRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "usuario@example.com")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Pattern(regexp = "^[0-9]{6}$", message = "Código deve conter exatamente 6 dígitos numéricos")
    @Schema(description = "Código de verificação de 6 dígitos", example = "123456")
    private String codigo;
}

