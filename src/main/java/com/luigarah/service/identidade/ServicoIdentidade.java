package com.luigarah.service.identidade;

import com.luigarah.dto.identidade.IdentidadeCreateDTO;
import com.luigarah.dto.identidade.IdentidadeDTO;
import com.luigarah.dto.identidade.IdentidadeUpdateDTO;
import com.luigarah.mapper.identidade.IdentidadeMapper;
import com.luigarah.model.identidade.Identidade;
import com.luigarah.repository.identidade.RepositorioIdentidade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoIdentidade {

    private final RepositorioIdentidade repositorioIdentidade;
    private final IdentidadeMapper identidadeMapper;

    /**
     * Busca todas as identidades ordenadas por ordem
     */
    @Transactional(readOnly = true)
    public List<IdentidadeDTO> buscarTodas() {
        return repositorioIdentidade.findAllByOrderByOrdemAsc()
                .stream()
                .map(identidadeMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca apenas identidades ativas
     */
    @Transactional(readOnly = true)
    public List<IdentidadeDTO> buscarAtivas() {
        return repositorioIdentidade.findByAtivoOrderByOrdemAsc("S")
                .stream()
                .map(identidadeMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca identidade por ID
     */
    @Transactional(readOnly = true)
    public IdentidadeDTO buscarPorId(Long id) {
        Identidade identidade = repositorioIdentidade.findById(id)
                .orElseThrow(() -> new RuntimeException("Identidade não encontrada com ID: " + id));
        return identidadeMapper.toDTO(identidade);
    }

    /**
     * Busca identidade por código
     */
    @Transactional(readOnly = true)
    public IdentidadeDTO buscarPorCodigo(String codigo) {
        Identidade identidade = repositorioIdentidade.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Identidade não encontrada com código: " + codigo));
        return identidadeMapper.toDTO(identidade);
    }

    /**
     * Cria nova identidade
     */
    @Transactional
    public IdentidadeDTO criar(IdentidadeCreateDTO dto) {
        // Verifica se já existe identidade com o mesmo código
        if (repositorioIdentidade.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new RuntimeException("Já existe uma identidade com o código: " + dto.getCodigo());
        }

        Identidade identidade = identidadeMapper.toEntity(dto);
        Identidade identidadeSalva = repositorioIdentidade.save(identidade);
        return identidadeMapper.toDTO(identidadeSalva);
    }

    /**
     * Atualiza identidade existente
     */
    @Transactional
    public IdentidadeDTO atualizar(Long id, IdentidadeUpdateDTO dto) {
        Identidade identidade = repositorioIdentidade.findById(id)
                .orElseThrow(() -> new RuntimeException("Identidade não encontrada com ID: " + id));

        // Se está atualizando o código, verifica se já existe outro registro com esse código
        if (dto.getCodigo() != null && !dto.getCodigo().equals(identidade.getCodigo())) {
            if (repositorioIdentidade.findByCodigo(dto.getCodigo()).isPresent()) {
                throw new RuntimeException("Já existe uma identidade com o código: " + dto.getCodigo());
            }
        }

        identidadeMapper.updateEntityFromDTO(dto, identidade);
        Identidade identidadeAtualizada = repositorioIdentidade.save(identidade);
        return identidadeMapper.toDTO(identidadeAtualizada);
    }

    /**
     * Deleta identidade
     */
    @Transactional
    public void deletar(Long id) {
        if (!repositorioIdentidade.existsById(id)) {
            throw new RuntimeException("Identidade não encontrada com ID: " + id);
        }
        repositorioIdentidade.deleteById(id);
    }
}
