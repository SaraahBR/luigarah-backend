package com.luigarah.controller.usuario;

import com.luigarah.dto.usuario.EnderecoDTO;
import com.luigarah.service.usuario.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario/enderecos")
@RequiredArgsConstructor
@Tag(name = "Endereços", description = "Gerenciamento de endereços do usuário")
@SecurityRequirement(name = "bearer-key")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @GetMapping
    @Operation(
            summary = "Listar endereços",
            description = "Retorna todos os endereços do usuário autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de endereços retornada"),
                    @ApiResponse(responseCode = "401", description = "Não autenticado")
            }
    )
    public ResponseEntity<List<EnderecoDTO>> listar() {
        return ResponseEntity.ok(enderecoService.listarEnderecos());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar endereço por ID",
            description = "Retorna um endereço específico do usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este endereço")
            }
    )
    public ResponseEntity<EnderecoDTO> buscarPorId(
            @Parameter(description = "ID do endereço") @PathVariable Long id
    ) {
        return ResponseEntity.ok(enderecoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Adicionar endereço",
            description = "Adiciona um novo endereço para o usuário autenticado",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            }
    )
    public ResponseEntity<EnderecoDTO> adicionar(@Valid @RequestBody EnderecoDTO enderecoDTO) {
        EnderecoDTO novoEndereco = enderecoService.adicionar(enderecoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEndereco);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar endereço",
            description = "Atualiza um endereço existente do usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Endereço atualizado"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este endereço")
            }
    )
    public ResponseEntity<EnderecoDTO> atualizar(
            @Parameter(description = "ID do endereço") @PathVariable Long id,
            @Valid @RequestBody EnderecoDTO enderecoDTO
    ) {
        return ResponseEntity.ok(enderecoService.atualizar(id, enderecoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar endereço",
            description = "Remove um endereço do usuário",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Endereço deletado"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Sem permissão para deletar este endereço")
            }
    )
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do endereço") @PathVariable Long id
    ) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/marcar-principal")
    @Operation(
            summary = "Marcar como endereço principal",
            description = "Define este endereço como principal e desmarca os outros",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Endereço marcado como principal"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Sem permissão")
            }
    )
    public ResponseEntity<EnderecoDTO> marcarComoPrincipal(
            @Parameter(description = "ID do endereço") @PathVariable Long id
    ) {
        return ResponseEntity.ok(enderecoService.marcarComoPrincipal(id));
    }
}

