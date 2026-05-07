package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Administrador;
import com.EC6.Convive.Repository.AdminstradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdminstradorRepository adminRepository;

    public Administrador insert(Administrador admin) {
        return adminRepository.save(admin);
    }

    public Administrador getById(UUID id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador com esse ID não encontrado"));
    }

    public List<Administrador> listAll() {
        return adminRepository.findAll();
    }

    public void delete(UUID id) {
        adminRepository.deleteById(id);
    }
}
