package com.luigarah.controller;

import com.luigarah.controller.doc.IdentidadeControllerDoc;
import com.luigarah.dto.IdentidadeCreateDTO;
import com.luigarah.dto.IdentidadeDTO;
import com.luigarah.dto.IdentidadeUpdateDTO;
import com.luigarah.service.ServicoIdentidade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/identidades")
@RequiredArgsConstructor
@Tag(name = "Identidades", description = "Gerenciamento de identidades de produtos (Masculino, Feminino, Unissex, Infantil)")
public class ControladorIdentidade implements IdentidadeControllerDoc {

    private final ServicoIdentidade servicoIdentidade;

    @Override
    @GetMapping
    @Operation(summary = "Listar todas as identidades", description = "Retorna todas as identidades ordenadas por ordem")
    public ResponseEntity<List<IdentidadeDTO>> listarTodas() {
        List<IdentidadeDTO> identidades = servicoIdentidade.buscarTodas();
        return ResponseEntity.ok(identidades);
    }

    @Override
    @GetMapping("/ativas")
    @Operation(summary = "Listar identidades ativas", description = "Retorna apenas as identidades ativas ordenadas por ordem")
    public ResponseEntity<List<IdentidadeDTO>> listarAtivas() {
        List<IdentidadeDTO> identidades = servicoIdentidade.buscarAtivas();
        return ResponseEntity.ok(identidades);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Buscar identidade por ID", description = "Retorna uma identidade específica pelo ID")
    public ResponseEntity<IdentidadeDTO> buscarPorId(@PathVariable Long id) {
        IdentidadeDTO identidade = servicoIdentidade.buscarPorId(id);
        return ResponseEntity.ok(identidade);
    }

    @Override
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar identidade por código", description = "Retorna uma identidade específica pelo código (homem, mulher, unissex, infantil)")
    public ResponseEntity<IdentidadeDTO> buscarPorCodigo(@PathVariable String codigo) {
        IdentidadeDTO identidade = servicoIdentidade.buscarPorCodigo(codigo);
        return ResponseEntity.ok(identidade);
    }

    @Override
    @PostMapping
    @Operation(summary = "Criar nova identidade", description = "Cria uma nova identidade")
    public ResponseEntity<IdentidadeDTO> criar(@Valid @RequestBody IdentidadeCreateDTO dto) {
        IdentidadeDTO identidadeCriada = servicoIdentidade.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(identidadeCriada);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar identidade", description = "Atualiza uma identidade existente")
    public ResponseEntity<IdentidadeDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody IdentidadeUpdateDTO dto) {
        IdentidadeDTO identidadeAtualizada = servicoIdentidade.atualizar(id, dto);
        return ResponseEntity.ok(identidadeAtualizada);
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar identidade", description = "Deleta uma identidade")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoIdentidade.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
