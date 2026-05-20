package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.StatusOcorrencia;
import com.EC6.Convive.Repository.MoradorRepository;
import com.EC6.Convive.Repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoradorService {

    private final MoradorRepository moradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public Morador insert(Morador morador) {

        usuarioService.prepareToInsertUser(morador);

        morador.setInadimplente(false);

        return moradorRepository.save(morador);
    }

    public List<Morador> listAll() {
        return moradorRepository.findAll();
    }

    public Morador searchById(UUID id) {
        return moradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        moradorRepository.deleteById(id);
    }

    public Page<Morador> findPaginatedAndFiltered(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return moradorRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        }
        return moradorRepository.findAll(pageable);
    }

    public Morador update(UUID id, Morador moradorAtualizado) {
        Morador moradorExistente = searchById(id);

        moradorExistente.setNome(moradorAtualizado.getNome());
        moradorExistente.setEmail(moradorAtualizado.getEmail());
        moradorExistente.setApartamento(moradorAtualizado.getApartamento());

        return moradorRepository.save(moradorExistente);
    }
}