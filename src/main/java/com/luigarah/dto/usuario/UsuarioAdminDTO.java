package com.luigarah.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para visualização de usuário pelo ADMIN
 * Conforme LGPD: NÃO expõe dados sensíveis (senha, documentos)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados do usuário para visualização administrativa (sem dados sensíveis - LGPD)")
public class UsuarioAdminDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "João")
    private String nome;

    @Schema(description = "Sobrenome do usuário", example = "Silva")
    private String sobrenome;

    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @Schema(description = "Telefone do usuário", example = "(11) 98765-4321")
    private String telefone;

    @Schema(description = "Data de nascimento", example = "1990-01-15")
    private LocalDate dataNascimento;

    @Schema(description = "Gênero do usuário", example = "Masculino")
    private String genero;

    @Schema(description = "URL da foto de perfil", example = "https://lh3.googleusercontent.com/a/...")
    private String fotoPerfil;

    @Schema(description = "Papel do usuário no sistema", example = "USER")
    private String role;

    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Indica se o email foi verificado", example = "true")
    private Boolean emailVerificado;

    @Schema(description = "Provedor de autenticação", example = "LOCAL")
    private String provider;

    @Schema(description = "Lista de endereços do usuário")
    private List<EnderecoDTO> enderecos;

    // DADOS SENSÍVEIS NÃO INCLUÍDOS (LGPD):
    // - senha (nunca exposta)
    // - documentos (CPF, RG, etc)
    // - dados bancários
    // - informações médicas
}
