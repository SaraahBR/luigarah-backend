package com.luigarah.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para registro de novo usuário")
public class RegistroRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do usuário", example = "João", required = true, maxLength = 100)
    private String nome;

    @Size(max = 100, message = "Sobrenome deve ter no máximo 100 caracteres")
    @Schema(description = "Sobrenome do usuário", example = "Silva", maxLength = 100)
    private String sobrenome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    @Schema(description = "Email do usuário", example = "joao@example.com", required = true, maxLength = 255)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Schema(description = "Senha do usuário", example = "senha123", required = true, minLength = 6, maxLength = 100)
    private String senha;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Schema(description = "Telefone do usuário", example = "(11) 98765-4321", maxLength = 20)
    private String telefone;

    @Schema(description = "Data de nascimento do usuário", example = "2002-04-13")
    private LocalDate dataNascimento;

    @Size(max = 20, message = "Gênero deve ter no máximo 20 caracteres")
    @Schema(description = "Gênero do usuário", example = "Feminino", maxLength = 20)
    private String genero;

    @Size(max = 500, message = "URL da foto deve ter no máximo 500 caracteres")
    @Schema(description = "URL da foto de perfil", example = "https://lh3.googleusercontent.com/a/...", maxLength = 500)
    private String fotoUrl;
}
