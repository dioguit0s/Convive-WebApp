package com.EC6.Convive.Service;

import com.EC6.Convive.Model.AreaComum;
import com.EC6.Convive.Model.StatusArea;
import com.EC6.Convive.Repository.AreaComumRepository;
import com.EC6.Convive.Repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaComumService {

    private final AreaComumRepository areaComumRepository;
    private final ReservaRepository reservaRepository;

    public AreaComum insert(AreaComum areaComum) {
        validarNomeUnico(areaComum.getNome(), null);
        return areaComumRepository.save(areaComum);
    }

    public AreaComum criar(String nome, int capacidade, StatusArea statusArea) {
        AreaComum area = new AreaComum();
        area.setNome(nome.trim());
        area.setCapacidade(capacidade);
        area.setStatusArea(statusArea != null ? statusArea : StatusArea.ATIVA);
        return insert(area);
    }

    public List<AreaComum> listAll() {
        return areaComumRepository.findAll();
    }

    public Page<AreaComum> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
        return areaComumRepository.findAllByOrderByNomeAsc(pageable);
    }

    public AreaComum searchById(UUID id) {
        return areaComumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área Comum não encontrada com o ID: " + id));
    }

    @Transactional
    public void delete(UUID id) {
        reservaRepository.deleteAllReservasWithThisArea(id);
        areaComumRepository.deleteById(id);
    }

    public AreaComum searchByName(String name) {
        return areaComumRepository.findByNome(name)
                .orElseThrow(() -> new RuntimeException("Area nao encontrada com esse nome: " + name));
    }

    public AreaComum update(AreaComum areaComum) {
        return areaComumRepository.save(areaComum);
    }

    public AreaComum atualizar(UUID id, String nome, int capacidade, StatusArea statusArea) {
        AreaComum area = searchById(id);
        validarNomeUnico(nome, id);
        area.setNome(nome.trim());
        area.setCapacidade(capacidade);
        area.setStatusArea(statusArea);
        return update(area);
    }

    private void validarNomeUnico(String nome, UUID idExcluir) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da área é obrigatório.");
        }
        boolean duplicado = idExcluir == null
                ? areaComumRepository.existsByNomeIgnoreCase(nome.trim())
                : areaComumRepository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), idExcluir);
        if (duplicado) {
            throw new IllegalArgumentException("Já existe uma área comum com este nome.");
        }
    }
}