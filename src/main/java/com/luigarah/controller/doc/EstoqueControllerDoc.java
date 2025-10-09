package com.luigarah.controller.doc;

import com.luigarah.dto.ProdutoTamanhoDTO;
import com.luigarah.dto.RespostaProdutoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Estoque",
        description = "Gerenciamento de estoque por produto. " +
                "• Roupas/Sapatos: estoque por tamanho (etiqueta). " +
                "• Bolsas: estoque único por produto (sem etiqueta).")
public interface EstoqueControllerDoc {

    @Operation(
            summary = "Listar estoque do produto",
            description = """
            • **Roupas/Sapatos** → retorna uma linha por etiqueta com `qtdEstoque`.
            • **Bolsas** → retorna lista com um único item, `etiqueta = null` e `qtdEstoque` do produto.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque listado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RespostaProdutoDTO.class),
                            examples = {
                                    @ExampleObject(name = "Roupas/Sapatos",
                                            value = """
                        {
                          "dados": [
                            {"etiqueta":"S","qtdEstoque":8},
                            {"etiqueta":"M","qtdEstoque":12}
                          ],
                          "sucesso": true,
                          "mensagem": "Estoque listado com sucesso"
                        }
                        """),
                                    @ExampleObject(name = "Bolsas",
                                            value = """
                        {
                          "dados": [
                            {"etiqueta": null, "qtdEstoque": 5}
                          ],
                          "sucesso": true,
                          "mensagem": "Estoque listado com sucesso"
                        }
                        """)
                            }
                    )
            )
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> listarEstoque(Long id);

    @Operation(
            summary = "Atualizar estoque em massa",
            description = """
            • **Roupas/Sapatos** → envie um array de {etiqueta, qtdEstoque} para **definir** a quantidade de cada etiqueta informada (upsert por etiqueta).  
            • **Bolsas** → envie um array com **um item** {qtdEstoque} (etiqueta ignorada) para **definir** o estoque do produto.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque atualizado.")
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoqueEmMassa(
            Long id,
            @RequestBody(
                    required = true,
                    description = "Itens de estoque",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas/Sapatos",
                                            value = """
                        [
                          {"etiqueta":"S","qtdEstoque":10},
                          {"etiqueta":"M","qtdEstoque":7}
                        ]
                        """),
                                    @ExampleObject(name = "Bolsas",
                                            value = """
                        [
                          {"qtdEstoque": 5}
                        ]
                        """)
                            }
                    )
            ) List<ProdutoTamanhoDTO> itens);

    @Operation(
            summary = "Atualizar estoque de uma etiqueta (roupas/sapatos)",
            description = """
            Atualiza o estoque **de uma única etiqueta** usando:
            - `modo=set` → define exatamente o valor  
            - `modo=inc` → incrementa  
            - `modo=dec` → decrementa (nunca fica negativo)
            """
    )
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoquePorEtiqueta(
            Long id,
            String etiqueta,
            String modo,
            int valor
    );

    @Operation(
            summary = "Atualizar estoque (bolsas, sem etiqueta)",
            description = """
            Atualiza o estoque do produto **sem tamanho**:
            - `modo=set` → define exatamente o valor  
            - `modo=inc` → incrementa  
            - `modo=dec` → decrementa (nunca fica negativo)
            """
    )
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoqueSemTamanho(
            Long id,
            String modo,
            int valor
    );
}
