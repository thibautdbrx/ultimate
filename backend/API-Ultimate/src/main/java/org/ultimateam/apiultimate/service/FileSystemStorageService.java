package org.ultimateam.apiultimate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ultimateam.apiultimate.configuration.StorageProperties;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        init();
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier d'upload", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) throw new RuntimeException("Fichier vide.");

            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (original.contains("..")) throw new RuntimeException("Nom dangereux");

            String filename = UUID.randomUUID() + "_" + original;

            Path destination = rootLocation.resolve(filename);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return filename;

        } catch (IOException e) {
            throw new RuntimeException("Erreur d'upload", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier introuvable");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur URL", e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Files.deleteIfExists(load(filename));
        } catch (IOException e) {
            throw new RuntimeException("Erreur suppression");
        }
    }
}