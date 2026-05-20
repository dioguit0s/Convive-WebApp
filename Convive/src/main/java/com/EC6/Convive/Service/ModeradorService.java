package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Repository.ModeradorRepository;
import lombok.RequiredArgsConstructor;
import org.h2.engine.Mode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeradorService {

    private final ModeradorRepository moderadorRepository;
    private final UsuarioService usuarioService;

    public Moderador insert(Moderador moderador) {

        usuarioService.prepareToInsertUser(moderador);

        return moderadorRepository.save(moderador);
    }

    public List<Moderador> listAll() {
        return moderadorRepository.findAll();
    }

    public Moderador searchById(UUID id) {
        return moderadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moderador não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        moderadorRepository.deleteById(id);
    }

    public List<Moderador> getAllActiveMods() {
        return moderadorRepository.findAllByStatus("Ativo");
    }

    public Moderador update(UUID id, Moderador moderadorAtualizado) {
        Moderador moderadorExistente = searchById(id);
        moderadorExistente.setNome(moderadorAtualizado.getNome());
        moderadorExistente.setEmail(moderadorAtualizado.getEmail());
        moderadorExistente.setApartamento(moderadorAtualizado.getApartamento());

        return moderadorRepository.save(moderadorExistente);
    }
}