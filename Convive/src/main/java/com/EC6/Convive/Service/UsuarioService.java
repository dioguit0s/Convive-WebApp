package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository UsuarioRepository;

    public Usuario insert(Usuario Usuario) {
        return UsuarioRepository.save(Usuario);
    }

    public List<Usuario> listAll() {
        return UsuarioRepository.findAll();
    }

    public Usuario searchById(UUID id) {
        return UsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        UsuarioRepository.deleteById(id);
    }
}