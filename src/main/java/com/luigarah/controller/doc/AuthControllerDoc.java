package com.luigarah.controller.doc;

import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * Documentação dos endpoints de autenticação (Swagger/OpenAPI).
 */
@Tag(
        name = "Autenticação",
        description = "Endpoints para autenticação JWT, registro de usuários e login social (Google/Facebook)"
)
public interface AuthControllerDoc {

    @Operation(
            summary = "Login com email e senha",
            description = """
                    Autentica um usuário com email e senha, retornando um token JWT.
                    O token deve ser incluído no header Authorization como "Bearer {token}" nas requisições autenticadas.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "tipo": "Bearer",
                                      "usuario": {
                                        "id": 1,
                                        "nome": "João",
                                        "sobrenome": "Silva",
                                        "email": "joao@example.com",
                                        "role": "USER",
                                        "ativo": true
                                      }
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "sucesso": false, "mensagem": "Email ou senha incorretos" }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "sucesso": false, "mensagem": "Email é obrigatório" }
                                    """)))
    })
    ResponseEntity<AuthResponseDTO> login(LoginRequestDTO loginRequest);

    @Operation(
            summary = "Registrar novo usuário",
            description = """
                    Cria uma nova conta de usuário com role USER.
                    Após o registro bem-sucedido, retorna um token JWT para autenticação automática.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Email já está em uso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "sucesso": false, "mensagem": "Email já está em uso" }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<AuthResponseDTO> registrar(RegistroRequestDTO registroRequest);

    @Operation(
            summary = "Obter perfil do usuário autenticado",
            description = "Retorna os dados do perfil do usuário atualmente autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioDTO> getPerfil();

    @Operation(
            summary = "Atualizar perfil do usuário autenticado",
            description = "Atualiza os dados do perfil do usuário atualmente autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "409", description = "Email já está em uso")
    })
    ResponseEntity<UsuarioDTO> atualizarPerfil(RegistroRequestDTO updateRequest);

    @Operation(
            summary = "Alterar senha do usuário autenticado",
            description = """
                    Permite que o usuário autenticado altere sua senha de forma segura.
                    
                    **Processo:**
                    1. O usuário deve estar autenticado (enviar token JWT no header Authorization)
                    2. Deve informar a senha atual para validação de segurança
                    3. Deve informar a nova senha e confirmá-la
                    4. A nova senha será criptografada com BCrypt antes de ser salva
                    
                    **Requisitos de Segurança:**
                    - A senha atual deve ser válida
                    - A nova senha deve ter entre 6 e 100 caracteres
                    - A nova senha e a confirmação devem ser idênticas
                    - Recomenda-se usar senhas fortes com letras maiúsculas, minúsculas, números e caracteres especiais
                    
                    **Exemplo de senha forte:** `MinhaSenh@Segura123!`
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "Senha alterada com sucesso"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senhas não coincidem",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Senhas não coincidem", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Nova senha e confirmação não coincidem"
                                            }
                                            """),
                                    @ExampleObject(name = "Senha muito curta", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Nova senha deve ter entre 6 e 100 caracteres"
                                            }
                                            """),
                                    @ExampleObject(name = "Campo obrigatório vazio", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Senha atual é obrigatória"
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou senha atual incorreta",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Token inválido", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Token JWT inválido ou expirado"
                                            }
                                            """),
                                    @ExampleObject(name = "Senha atual incorreta", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Senha atual incorreta"
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<String> alterarSenha(AlterarSenhaRequestDTO request);
}
