package com.EC6.Convive.Service;

import com.EC6.Convive.Model.AreaComum;
import com.EC6.Convive.Repository.AreaComumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaComumService {

    private final AreaComumRepository areaComumRepository;

    public AreaComum insert(AreaComum areaComum) {
        return areaComumRepository.save(areaComum);
    }

    public List<AreaComum> listAll() {
        return areaComumRepository.findAll();
    }

    public AreaComum searchById(UUID id) {
        return areaComumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área Comum não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        areaComumRepository.deleteById(id);
    }
}