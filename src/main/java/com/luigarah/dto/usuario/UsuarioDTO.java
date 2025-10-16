package com.luigarah.dto.usuario;

import com.luigarah.model.autenticacao.AuthProvider;
import com.luigarah.model.autenticacao.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados do usuário")
public class UsuarioDTO {

    @Schema(description = "ID do usuário", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nome do usuário", example = "João", maxLength = 100)
    private String nome;

    @Schema(description = "Sobrenome do usuário", example = "Silva", maxLength = 100)
    private String sobrenome;

    @Schema(description = "Email do usuário", example = "joao@example.com", maxLength = 255)
    private String email;

    @Schema(description = "Telefone do usuário", example = "(11) 98765-4321", maxLength = 20)
    private String telefone;

    @Schema(description = "Data de nascimento", example = "1990-05-20")
    private LocalDate dataNascimento;

    @Schema(description = "Gênero do usuário", example = "Masculino", maxLength = 20)
    private String genero;

    @Schema(description = "URL da foto de perfil", example = "https://lh3.googleusercontent.com/a/...", maxLength = 500)
    private String fotoPerfil;

    @Schema(description = "Papel do usuário no sistema", example = "USER")
    private Role role;

    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Indica se o email foi verificado", example = "true")
    private Boolean emailVerificado;

    @Schema(description = "Provedor de autenticação", example = "LOCAL")
    private AuthProvider provider;

    @Schema(description = "Data de criação da conta", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAtualizacao;

    @Schema(description = "Data do último acesso", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime ultimoAcesso;

    @Schema(description = "Lista de endereços do usuário")
    private java.util.List<EnderecoDTO> enderecos;
}
