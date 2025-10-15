package com.luigarah.dto.autenticacao;

import com.luigarah.dto.usuario.UsuarioDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta de autenticação contendo o token JWT e dados do usuário")
public class AuthResponseDTO {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    @Builder.Default
    private String tipo = "Bearer";

    @Schema(description = "Dados do usuário autenticado")
    private UsuarioDTO usuario;
}
