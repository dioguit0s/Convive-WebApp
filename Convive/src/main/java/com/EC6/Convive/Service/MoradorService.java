package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.StatusOcorrencia;
import com.EC6.Convive.Repository.MoradorRepository;
import com.EC6.Convive.Repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoradorService {

    private final MoradorRepository moradorRepository;
    private final UsuarioRepository usuarioRepository;

    public Morador insert(Morador morador) {

        if (usuarioRepository.existsByEmail(morador.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado no sistema.");
        }

        morador.setStatus("Ativo");
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
}