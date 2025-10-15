package com.luigarah.controller.doc;

import com.luigarah.dto.identidade.IdentidadeCreateDTO;
import com.luigarah.dto.identidade.IdentidadeDTO;
import com.luigarah.dto.identidade.IdentidadeUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Documentação central dos endpoints de IDENTIDADE (Swagger/OpenAPI).
 *
 * Identidades representam a classificação dos produtos por público-alvo:
 *  • homem (Masculino)
 *  • mulher (Feminino)
 *  • unissex (Unissex)
 *  • infantil (Infantil)
 *
 * Convenções de resposta:
 *  • Sucesso: HTTP 200/201 com IdentidadeDTO ou List<IdentidadeDTO>
 *  • Erro:    HTTP 400/404/409/500 com mensagem de erro
 *
 * Tratamento de erros:
 *  • 400 – parâmetros inválidos / validação (Bean Validation)
 *  • 404 – recurso não encontrado
 *  • 409 – conflito de regra de negócio (código duplicado)
 *  • 500 – erro interno inesperado
 */
@Tag(
        name = "Identidades",
        description = "Endpoints para gerenciamento de identidades de produtos (Masculino, Feminino, Unissex, Infantil)."
)
public interface IdentidadeControllerDoc {

    // ============================================================
    // LISTAGEM
    // ============================================================

    @Operation(
            summary = "Listar todas as identidades",
            description = """
                Retorna todas as identidades cadastradas no sistema, ordenadas por <code>ordem ASC</code>.
                Inclui identidades ativas e inativas.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    [
                                      {
                                        "id": 1,
                                        "codigo": "homem",
                                        "nome": "Masculino",
                                        "ordem": 1,
                                        "ativo": "S",
                                        "dataCriacao": "2025-10-11T17:13:18.433",
                                        "dataAtualizacao": null
                                      },
                                      {
                                        "id": 2,
                                        "codigo": "mulher",
                                        "nome": "Feminino",
                                        "ordem": 2,
                                        "ativo": "S",
                                        "dataCriacao": "2025-10-11T17:13:18.433",
                                        "dataAtualizacao": null
                                      },
                                      {
                                        "id": 3,
                                        "codigo": "unissex",
                                        "nome": "Unissex",
                                        "ordem": 3,
                                        "ativo": "S",
                                        "dataCriacao": "2025-10-11T17:13:18.433",
                                        "dataAtualizacao": null
                                      },
                                      {
                                        "id": 4,
                                        "codigo": "infantil",
                                        "nome": "Infantil",
                                        "ordem": 4,
                                        "ativo": "S",
                                        "dataCriacao": "2025-10-11T17:13:18.433",
                                        "dataAtualizacao": null
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao buscar identidades.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 500",
                                    value = """
                                    {
                                      "mensagem": "Erro ao buscar identidades: <detalhe>"
                                    }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<List<IdentidadeDTO>> listarTodas();

    @Operation(
            summary = "Listar identidades ativas",
            description = """
                Retorna apenas as identidades com status <code>ativo = 'S'</code>, ordenadas por <code>ordem ASC</code>.
                Útil para filtros de produto no frontend.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de identidades ativas retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    [
                                      {
                                        "id": 1,
                                        "codigo": "homem",
                                        "nome": "Masculino",
                                        "ordem": 1,
                                        "ativo": "S",
                                        "dataCriacao": "2025-10-11T17:13:18.433",
                                        "dataAtualizacao": null
                                      },
                                      {
                                        "id": 2,
                                        "codigo": "mulher",
                                        "nome": "Feminino",
                                        "ordem": 2,
                                        "ativo": "S",
                                        "dataCriacao": "2025-10-11T17:13:18.433",
                                        "dataAtualizacao": null
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<IdentidadeDTO>> listarAtivas();

    // ============================================================
    // BUSCA POR ID
    // ============================================================

    @Operation(
            summary = "Buscar identidade por ID",
            description = """
                Retorna uma identidade específica pelo seu ID.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Identidade encontrada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    {
                                      "id": 2,
                                      "codigo": "mulher",
                                      "nome": "Feminino",
                                      "ordem": 2,
                                      "ativo": "S",
                                      "dataCriacao": "2025-10-11T17:13:18.433",
                                      "dataAtualizacao": null
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Identidade não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Identidade não encontrada com ID: 999"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<IdentidadeDTO> buscarPorId(
            @Parameter(description = "ID da identidade", example = "2", required = true)
            Long id
    );

    // ============================================================
    // BUSCA POR CÓDIGO
    // ============================================================

    @Operation(
            summary = "Buscar identidade por código",
            description = """
                Retorna uma identidade específica pelo seu código único.
                Códigos válidos: <code>homem</code>, <code>mulher</code>, <code>unissex</code>, <code>infantil</code>.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Identidade encontrada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    {
                                      "id": 2,
                                      "codigo": "mulher",
                                      "nome": "Feminino",
                                      "ordem": 2,
                                      "ativo": "S",
                                      "dataCriacao": "2025-10-11T17:13:18.433",
                                      "dataAtualizacao": null
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Identidade não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Identidade não encontrada com código: xyz"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<IdentidadeDTO> buscarPorCodigo(
            @Parameter(description = "Código da identidade", example = "mulher", required = true)
            String codigo
    );

    // ============================================================
    // CRIAÇÃO
    // ============================================================

    @Operation(
            summary = "Criar nova identidade",
            description = """
                Cria uma nova identidade no sistema.
                O código deve ser único.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Identidade criada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 201",
                                    value = """
                                    {
                                      "id": 5,
                                      "codigo": "plus-size",
                                      "nome": "Plus Size",
                                      "ordem": 5,
                                      "ativo": "S",
                                      "dataCriacao": "2025-10-11T18:30:00.000",
                                      "dataAtualizacao": null
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 400",
                                    value = """
                                    {
                                      "mensagem": "Código é obrigatório"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - código já existe.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 409",
                                    value = """
                                    {
                                      "mensagem": "Já existe uma identidade com o código: mulher"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<IdentidadeDTO> criar(
            @RequestBody(
                    description = "Dados da nova identidade",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeCreateDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de criação",
                                    value = """
                                    {
                                      "codigo": "plus-size",
                                      "nome": "Plus Size",
                                      "ordem": 5,
                                      "ativo": "S"
                                    }
                                    """
                            )
                    )
            )
            IdentidadeCreateDTO dto
    );

    // ============================================================
    // ATUALIZAÇÃO
    // ============================================================

    @Operation(
            summary = "Atualizar identidade",
            description = """
                Atualiza uma identidade existente.
                Apenas os campos fornecidos serão atualizados (patch parcial).
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Identidade atualizada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    {
                                      "id": 2,
                                      "codigo": "mulher",
                                      "nome": "Feminino",
                                      "ordem": 2,
                                      "ativo": "N",
                                      "dataCriacao": "2025-10-11T17:13:18.433",
                                      "dataAtualizacao": "2025-10-11T19:00:00.000"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 400",
                                    value = """
                                    {
                                      "mensagem": "Ativo deve ser 'S' ou 'N'"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Identidade não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Identidade não encontrada com ID: 999"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - código já existe.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 409",
                                    value = """
                                    {
                                      "mensagem": "Já existe uma identidade com o código: homem"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<IdentidadeDTO> atualizar(
            @Parameter(description = "ID da identidade a atualizar", example = "2", required = true)
            Long id,
            @RequestBody(
                    description = "Dados a atualizar (apenas campos fornecidos serão alterados)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IdentidadeUpdateDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de atualização",
                                    value = """
                                    {
                                      "ativo": "N"
                                    }
                                    """
                            )
                    )
            )
            IdentidadeUpdateDTO dto
    );

    // ============================================================
    // DELEÇÃO
    // ============================================================

    @Operation(
            summary = "Deletar identidade",
            description = """
                Remove uma identidade do sistema.
                ⚠️ Atenção: Se houver produtos vinculados a esta identidade, a operação pode falhar.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Identidade deletada com sucesso.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Identidade não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Identidade não encontrada com ID: 999"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ou violação de integridade referencial.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 500",
                                    value = """
                                    {
                                      "mensagem": "Não é possível deletar a identidade pois existem produtos vinculados"
                                    }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID da identidade a deletar", example = "2", required = true)
            Long id
    );
}
