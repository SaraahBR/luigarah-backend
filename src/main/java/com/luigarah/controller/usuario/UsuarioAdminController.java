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
}
