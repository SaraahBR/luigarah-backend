package com.luigarah.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * DTO para atualização de usuário pelo ADMIN
 * Permite apenas alteração de dados não sensíveis
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados permitidos para atualização pelo ADMIN")
public class UsuarioAdminUpdateDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do usuário", example = "João", required = true)
    private String nome;

    @Size(max = 100, message = "Sobrenome deve ter no máximo 100 caracteres")
    @Schema(description = "Sobrenome do usuário", example = "Silva")
    private String sobrenome;

    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Schema(description = "Telefone do usuário", example = "(11) 98765-4321")
    private String telefone;

    @Schema(description = "Papel do usuário (USER/ADMIN)", example = "USER")
    private String role;

    @Valid
    @Schema(description = "Lista de endereços do usuário (opcional)")
    private List<EnderecoDTO> enderecos;

    // ADMIN NÃO PODE ALTERAR (conforme requisitos de segurança):
    // - senha (usuário altera a própria senha)
    // - documentos
    // - dados bancários
    // - provider (LOCAL/GOOGLE/FACEBOOK)
    // - emailVerificado (processo automático)
}
