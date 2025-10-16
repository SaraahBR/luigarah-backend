package com.luigarah.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para atualização de perfil do usuário autenticado.
 * ✅ Email NÃO está aqui - vem do JWT token
 * ✅ Senha NÃO está aqui - tem endpoint próprio (/alterar-senha)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para atualização de perfil do usuário autenticado")
public class AtualizarPerfilRequest {

    @Schema(description = "Nome do usuário", example = "Maria", maxLength = 100)
    private String nome;

    @Schema(description = "Sobrenome do usuário", example = "Silva", maxLength = 100)
    private String sobrenome;

    @Schema(
        description = "Telefone do usuário (aceita com ou sem formatação)",
        example = "(11) 98765-4321"
    )
    @Pattern(
        regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$|^\\d{10,11}$",
        message = "Telefone inválido. Use formato: (XX) XXXXX-XXXX ou XXXXXXXXXXX"
    )
    private String telefone;

    @Schema(description = "Data de nascimento no formato ISO 8601", example = "1990-05-20")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @Schema(
        description = "Gênero do usuário",
        example = "Feminino",
        allowableValues = {"Masculino", "Feminino", "Não Especificado"}
    )
    @Pattern(
        regexp = "^(Masculino|Feminino|Não Especificado)$",
        message = "Gênero inválido. Use: Masculino, Feminino ou Não Especificado"
    )
    private String genero;

    @Schema(description = "URL da foto de perfil", example = "https://lh3.googleusercontent.com/a/...", maxLength = 500)
    @Pattern(
        regexp = "^https?://.*\\.(jpg|jpeg|png|gif|webp|JPG|JPEG|PNG|GIF|WEBP)$|^https?://.*googleusercontent\\.com/.*$|^https?://.*fbcdn\\.net/.*$",
        message = "URL de foto inválida"
    )
    private String fotoPerfil;

    @Valid
    @Schema(description = "Lista de endereços do usuário")
    private List<EnderecoDTO> enderecos;

    /**
     * Remove formatação do telefone antes de salvar
     */
    public void setTelefone(String telefone) {
        if (telefone != null && !telefone.isEmpty()) {
            this.telefone = telefone.replaceAll("[^0-9]", "");
        } else {
            this.telefone = null;
        }
    }
}
