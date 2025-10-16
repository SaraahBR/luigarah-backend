package com.luigarah.controller.usuario;

import com.luigarah.dto.usuario.UsuarioAdminDTO;
import com.luigarah.dto.usuario.UsuarioAdminUpdateDTO;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.service.usuario.UsuarioAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Controller para administração de usuários
 * Acesso restrito a ADMIN
 * Conforme LGPD: não expõe dados sensíveis
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@Tag(name = "Administração de Usuários", description = "Endpoints para gerenciamento de usuários (ADMIN apenas)")
@SecurityRequirement(name = "bearer-key")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Lista todos os usuários do sistema (sem dados sensíveis - LGPD)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<List<UsuarioAdminDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioAdminService.listarTodos());
    }

    @GetMapping("/paginado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Listar usuários com paginação",
            description = "Lista usuários com paginação e ordenação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Página de usuários retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Page<UsuarioAdminDTO>> listarComPaginacao(
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC/DESC)") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(usuarioAdminService.listarComPaginacao(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna detalhes de um usuário específico (sem dados sensíveis - LGPD)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<UsuarioAdminDTO> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id
    ) {
        return ResponseEntity.ok(usuarioAdminService.buscarPorId(id));
    }

    @GetMapping("/buscar/nome")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Buscar usuários por nome",
            description = "Busca usuários cujo nome contenha o texto fornecido (case-insensitive)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<List<UsuarioAdminDTO>> buscarPorNome(
            @Parameter(description = "Nome a buscar") @RequestParam String nome
    ) {
        return ResponseEntity.ok(usuarioAdminService.buscarPorNome(nome));
    }

    @GetMapping("/buscar/email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Buscar usuários por email",
            description = "Busca usuários cujo email contenha o texto fornecido (case-insensitive)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<List<UsuarioAdminDTO>> buscarPorEmail(
            @Parameter(description = "Email a buscar") @RequestParam String email
    ) {
        return ResponseEntity.ok(usuarioAdminService.buscarPorEmail(email));
    }

    @GetMapping("/buscar/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Buscar usuários por role",
            description = "Retorna todos os usuários com a role especificada (USER ou ADMIN)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<List<UsuarioAdminDTO>> buscarPorRole(
            @Parameter(description = "Role (USER ou ADMIN)") @PathVariable Role role
    ) {
        return ResponseEntity.ok(usuarioAdminService.buscarPorRole(role));
    }

    @GetMapping("/buscar/status/{ativo}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Buscar usuários por status",
            description = "Retorna usuários ativos ou inativos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<List<UsuarioAdminDTO>> buscarPorStatus(
            @Parameter(description = "Status ativo (true ou false)") @PathVariable Boolean ativo
    ) {
        return ResponseEntity.ok(usuarioAdminService.buscarPorStatus(ativo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza dados permitidos do usuário (nome, sobrenome, email, telefone, role). " +
                    "ADMIN NÃO pode alterar senha ou dados sensíveis (LGPD).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<UsuarioAdminDTO> atualizar(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UsuarioAdminUpdateDTO updateDTO
    ) {
        return ResponseEntity.ok(usuarioAdminService.atualizar(id, updateDTO));
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Desativar usuário",
            description = "Desativa um usuário (soft delete). O usuário não pode mais fazer login, mas seus dados são mantidos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Usuário já está desativado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Map<String, String>> desativar(
            @Parameter(description = "ID do usuário") @PathVariable Long id
    ) {
        usuarioAdminService.desativar(id);
        return ResponseEntity.ok(Map.of("message", "Usuário desativado com sucesso"));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Ativar usuário",
            description = "Reativa um usuário previamente desativado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Usuário já está ativo"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Map<String, String>> ativar(
            @Parameter(description = "ID do usuário") @PathVariable Long id
    ) {
        usuarioAdminService.ativar(id);
        return ResponseEntity.ok(Map.of("message", "Usuário ativado com sucesso"));
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Estatísticas de usuários",
            description = "Retorna estatísticas gerais sobre usuários do sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Map<String, Object>> estatisticas() {
        return ResponseEntity.ok(Map.of(
                "total", usuarioAdminService.contarTodos(),
                "ativos", usuarioAdminService.contarAtivos(),
                "inativos", usuarioAdminService.contarTodos() - usuarioAdminService.contarAtivos(),
                "admins", usuarioAdminService.contarPorRole(Role.ADMIN),
                "users", usuarioAdminService.contarPorRole(Role.USER)
        ));
    }

    /**
     * Atualizar foto de perfil de um usuário por URL (ADMIN)
     * PUT /api/admin/usuarios/{id}/foto
     */
    @PutMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar foto de perfil de usuário por URL (ADMIN)",
            description = "Permite que um admin atualize a URL da foto de perfil de qualquer usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "URL da foto de perfil",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                    {
                                      "fotoUrl": "https://exemplo.com/foto.jpg"
                                    }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Foto atualizada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                                            {
                                              "sucesso": true,
                                              "mensagem": "Foto de perfil atualizada com sucesso",
                                              "usuario": {
                                                "id": 1,
                                                "nome": "João",
                                                "email": "joao@example.com",
                                                "fotoPerfil": "https://exemplo.com/foto.jpg"
                                              }
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "URL da foto é obrigatória"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Map<String, Object>> atualizarFotoPerfilPorUrl(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String fotoUrl = request.get("fotoUrl");

        if (fotoUrl == null || fotoUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("sucesso", false, "mensagem", "URL da foto é obrigatória")
            );
        }

        UsuarioAdminDTO usuario = usuarioAdminService.atualizarFotoPerfil(id, fotoUrl);

        return ResponseEntity.ok(Map.of(
            "sucesso", true,
            "mensagem", "Foto de perfil atualizada com sucesso",
            "usuario", usuario
        ));
    }

    /**
     * Upload de foto de perfil para um usuário (ADMIN)
     * POST /api/admin/usuarios/{id}/foto/upload
     */
    @PostMapping(value = "/{id}/foto/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Upload de foto de perfil de usuário (ADMIN)",
            description = """
                    Permite que um admin faça upload de uma foto de perfil para qualquer usuário.
                    
                    **Formatos aceitos:** JPG, JPEG, PNG, WEBP, GIF
                    **Tamanho máximo:** 5MB
                    
                    **Exemplo de uso:**
                    ```javascript
                    const formData = new FormData();
                    formData.append('file', fotoFile);
                    
                    const response = await fetch('/api/admin/usuarios/1/foto/upload', {
                      method: 'POST',
                      headers: { 'Authorization': `Bearer ${adminToken}` },
                      body: formData
                    });
                    ```
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Foto enviada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                                            {
                                              "sucesso": true,
                                              "mensagem": "Foto de perfil enviada com sucesso",
                                              "fotoUrl": "http://localhost:8080/uploads/usuarios/user_1.jpg",
                                              "usuario": {
                                                "id": 1,
                                                "nome": "João",
                                                "email": "joao@example.com",
                                                "fotoPerfil": "http://localhost:8080/uploads/usuarios/user_1.jpg"
                                              }
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Arquivo inválido ou muito grande"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Map<String, Object>> uploadFotoPerfilUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Parameter(description = "Arquivo de imagem da foto de perfil", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        // Validações
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("sucesso", false, "mensagem", "Arquivo vazio")
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(
                Map.of("sucesso", false, "mensagem", "Arquivo deve ser uma imagem")
            );
        }

        // Validar tamanho (máximo 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(
                Map.of("sucesso", false, "mensagem", "Arquivo muito grande (máx 5MB)")
            );
        }

        // TODO: Implementar integração com ImageStorageService
        // String fotoUrl = imageStorageService.save("usuarios/user_" + id, contentType, file.getSize(), file.getInputStream());

        // Por enquanto, URL mockada
        String fotoUrl = "http://localhost:8080/uploads/usuarios/user_" + id + ".jpg";

        UsuarioAdminDTO usuario = usuarioAdminService.atualizarFotoPerfil(id, fotoUrl);

        return ResponseEntity.ok(Map.of(
            "sucesso", true,
            "mensagem", "Foto de perfil enviada com sucesso",
            "fotoUrl", fotoUrl,
            "usuario", usuario
        ));
    }

    /**
     * Remover foto de perfil de um usuário (ADMIN)
     * DELETE /api/admin/usuarios/{id}/foto
     */
    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Remover foto de perfil de usuário (ADMIN)",
            description = "Permite que um admin remova a foto de perfil de qualquer usuário",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Foto removida com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                                            {
                                              "sucesso": true,
                                              "mensagem": "Foto de perfil removida com sucesso"
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
            }
    )
    public ResponseEntity<Map<String, Object>> removerFotoPerfilUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id
    ) {
        usuarioAdminService.removerFotoPerfil(id);

        return ResponseEntity.ok(Map.of(
            "sucesso", true,
            "mensagem", "Foto de perfil removida com sucesso"
        ));
    }
}
