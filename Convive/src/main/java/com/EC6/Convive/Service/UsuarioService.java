package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public void prepareToInsertUser(Usuario usuario) {
        if(usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado no sistema");
        }
        usuario.setStatus("Ativo");

        if(usuario.getSenhaHash() != null && !usuario.getSenhaHash().isEmpty())
            usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
    }

    public Usuario insert(Usuario Usuario) {
        return usuarioRepository.save(Usuario);
    }

    public List<Usuario> listAll() {
        return usuarioRepository.findAll();
    }

    public Usuario searchById(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com o ID: " + id));
    }

    public Usuario getByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com o email: " + email));
    }

    public void delete(UUID id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario update(UUID id, Usuario moradorAtualizado) {
        Usuario usuario = searchById(id);

        usuario.setNome(moradorAtualizado.getNome());
        usuario.setEmail(moradorAtualizado.getEmail());
        usuario.setFotoPerfil(moradorAtualizado.getFotoPerfil());

        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> findPaginatedAndFiltered(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        }
        return usuarioRepository.findAll(pageable);
    }
}