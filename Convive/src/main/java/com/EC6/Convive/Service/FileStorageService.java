package com.EC6.Convive.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String salvarImagemOcorrencia(MultipartFile arquivo) throws IOException {
        if (arquivo.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(uploadDir).resolve("ocorrencias");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nomeArquivoOriginal = arquivo.getOriginalFilename();
        String nomeArquivoUnico = UUID.randomUUID().toString() + "_" + nomeArquivoOriginal;

        Path caminhoDestino = uploadPath.resolve(nomeArquivoUnico);
        Files.copy(arquivo.getInputStream(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/ocorrencias/" + nomeArquivoUnico;
    }

    public String salvarImagemComunicado(MultipartFile arquivo) throws IOException {
        if (arquivo.isEmpty()) {
            return null;
        }
        Path uploadPath = Paths.get(uploadDir).resolve("comunicados");
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String nomeArquivoOriginal = arquivo.getOriginalFilename();
        String nomeArquivoUnico = UUID.randomUUID().toString() + "_comunicado_" + nomeArquivoOriginal;
        Path caminhoDestino = uploadPath.resolve(nomeArquivoUnico);
        Files.copy(arquivo.getInputStream(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/comunicados/" + nomeArquivoUnico;
    }
}