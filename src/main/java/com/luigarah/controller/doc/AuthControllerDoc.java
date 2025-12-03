package com.luigarah.controller.doc;

import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.autenticacao.EnviarCodigoVerificacaoRequest;
import com.luigarah.dto.autenticacao.VerificarCodigoRequest;
import com.luigarah.dto.autenticacao.SolicitarResetSenhaRequest;
import com.luigarah.dto.autenticacao.RedefinirSenhaComCodigoRequest;
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
                    - A nova senha deve ter entre 6 e 40 caracteres
                    - A nova senha deve conter: 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)
                    - A nova senha e a confirmação devem ser idênticas
                    
                    **Exemplos de senha válida:**
                    - `Senha@123`
                    - `Admin$789`
                    - `Teste#456`
                    - `MinhaSenh@SuperSegura2025`
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
                                    @ExampleObject(name = "Senha inválida", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Nova senha deve conter: mínimo 6 e máximo 40 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)"
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

    // ============================================================
    // DOCUMENTAÇÃO DOS ENDPOINTS DE VERIFICAÇÃO E RESET DE SENHA
    // ============================================================

    @Operation(
            summary = "Enviar código de verificação de conta",
            description = """
                    Envia um código de 6 dígitos para o email do usuário para confirmação de conta após cadastro tradicional.
                    
                    **Fluxo:**
                    1. Usuário se cadastra no sistema via email e senha
                    2. Sistema envia automaticamente o código de verificação
                    3. Usuário pode solicitar reenvio do código com este endpoint
                    4. Código é válido por **12 horas**
                    5. Após verificação, usuário recebe email de boas-vindas
                    
                    **Observações:**
                    - Cada nova solicitação invalida códigos anteriores
                    - O email deve estar cadastrado no sistema
                    - A conta não deve estar previamente verificada
                    - Usuários que fizeram cadastro via Google OAuth não precisam verificar email
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "sucesso": true,
                                      "mensagem": "Código de verificação enviado com sucesso! Verifique seu email."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Email não cadastrado ou conta já verificada",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email não cadastrado", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Email não cadastrado"
                                            }
                                            """),
                                    @ExampleObject(name = "Conta já verificada", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Esta conta já está verificada"
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro ao enviar email")
    })
    ResponseEntity<?> enviarCodigoVerificacao(EnviarCodigoVerificacaoRequest request);

    @Operation(
            summary = "Verificar código de confirmação de conta",
            description = """
                    Verifica o código de 6 dígitos enviado por email e confirma a conta do usuário.
                    Retorna token JWT após verificação bem-sucedida.
                    
                    **Fluxo:**
                    1. Usuário recebe código de 6 dígitos por email
                    2. Usuário insere código na plataforma
                    3. Sistema valida o código (validade, expiração, uso anterior)
                    4. Se válido, marca conta como verificada
                    5. Envia email de boas-vindas ao Luigarah
                    6. Retorna token JWT para login automático
                    
                    **Validações:**
                    - Código deve existir no banco de dados
                    - Código não deve estar expirado (12 horas)
                    - Código não deve ter sido usado anteriormente
                    - Código deve corresponder ao email informado
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta verificada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "tipo": "Bearer",
                                      "usuario": {
                                        "id": 1,
                                        "nome": "João",
                                        "email": "joao@example.com",
                                        "emailVerificado": true,
                                        "role": "USER"
                                      }
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou já utilizado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Código inválido", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Código inválido."
                                            }
                                            """),
                                    @ExampleObject(name = "Código expirado", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Código expirado. Solicite um novo código."
                                            }
                                            """),
                                    @ExampleObject(name = "Código já usado", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Código já foi utilizado. Solicite um novo código."
                                            }
                                            """)
                            }))
    })
    ResponseEntity<?> verificarCodigo(VerificarCodigoRequest request);

    @Operation(
            summary = "Solicitar código de redefinição de senha",
            description = """
                    Envia um código de 6 dígitos para o email do usuário para redefinição de senha.
                    
                    **Fluxo:**
                    1. Usuário esquece a senha e solicita reset
                    2. Informa o email cadastrado
                    3. Sistema valida se o email existe e é conta local (não OAuth)
                    4. Envia código de 6 dígitos por email
                    5. Código é válido por **12 horas**
                    6. Usuário usa o código para redefinir a senha
                    
                    **Observações:**
                    - Cada nova solicitação invalida códigos anteriores
                    - Apenas contas criadas com email/senha podem redefinir senha
                    - Contas OAuth (Google, Facebook, GitHub) devem usar o mesmo método de login
                    - Por segurança, sempre retorna sucesso mesmo se email não existir
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "sucesso": true,
                                      "mensagem": "Código de redefinição de senha enviado com sucesso! Verifique seu email."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Email não cadastrado ou conta OAuth",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email não cadastrado", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Email não cadastrado"
                                            }
                                            """),
                                    @ExampleObject(name = "Conta OAuth", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Esta conta foi criada com GOOGLE. Use o mesmo método para fazer login."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro ao enviar email")
    })
    ResponseEntity<?> solicitarResetSenha(SolicitarResetSenhaRequest request);

    @Operation(
            summary = "Redefinir senha com código de verificação",
            description = """
                    Redefine a senha do usuário usando o código de 6 dígitos recebido por email.
                    
                    **Fluxo:**
                    1. Usuário recebe código de redefinição por email
                    2. Usuário insere código, email e nova senha
                    3. Sistema valida o código (validade, expiração, uso anterior)
                    4. Sistema valida que as senhas coincidem
                    5. Se válido, atualiza a senha criptografada
                    6. Marca código como usado
                    7. Usuário pode fazer login com a nova senha
                    
                    **Validações:**
                    - Código deve existir e ser válido
                    - Código não deve estar expirado (12 horas)
                    - Código não deve ter sido usado
                    - Nova senha deve ter entre 6 e 40 caracteres
                    - Nova senha deve conter: 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)
                    - Nova senha e confirmação devem ser idênticas
                    
                    **Requisitos de Senha:**
                    - Mínimo 6 caracteres, Máximo 40 caracteres
                    - 1 letra maiúscula (A-Z)
                    - 1 letra minúscula (a-z)
                    - 1 número (0-9)
                    - 1 caractere especial (@$!%*?&#)
                    
                    **Exemplos válidos:**
                    - `Senha@123`
                    - `Admin$789`
                    - `Teste#456`
                    - `MinhaSenh@SuperSegura2025`
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "sucesso": true,
                                      "mensagem": "Senha redefinida com sucesso! Você já pode fazer login com a nova senha."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Código inválido, expirado, já usado ou senhas não coincidem",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Código inválido", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Código inválido."
                                            }
                                            """),
                                    @ExampleObject(name = "Código expirado", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Código expirado. Solicite um novo código."
                                            }
                                            """),
                                    @ExampleObject(name = "Senhas não coincidem", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "As senhas não coincidem"
                                            }
                                            """),
                                    @ExampleObject(name = "Senha inválida", value = """
                                            {
                                              "sucesso": false,
                                              "mensagem": "Nova senha deve conter: mínimo 6 e máximo 40 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@$!%*?&#)"
                                            }
                                            """)
                            }))
    })
    ResponseEntity<?> redefinirSenha(RedefinirSenhaComCodigoRequest request);
}
