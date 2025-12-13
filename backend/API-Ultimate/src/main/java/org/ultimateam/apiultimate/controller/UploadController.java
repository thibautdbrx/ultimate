package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ultimateam.apiultimate.service.StorageService;

import java.io.IOException;
import java.util.Map;

@RestController
@Tag(name = "Upload Controller", description = "Gestion des fichiers")
@RequestMapping("/api/files")
public class UploadController {

    private final StorageService storageService;

    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(
            summary = "Uploader un fichier",
            description = "Permet d'uploader un fichier et retourne le nom du fichier et son URL."
    )
    @Parameter(
            name = "file",
            description = "Fichier à uploader",
            required = true
    )
    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> upload(@RequestParam("file") MultipartFile file) {
        String filename = storageService.store(file);
        return ResponseEntity.status(201).body(
                Map.of("filename", filename, "url", "/api/files/" + filename)
        );
    }

    @Operation(
            summary = "Télécharger un fichier",
            description = "Retourne le fichier demandé à partir de son nom."
    )
    @Parameter(
            name = "filename",
            description = "Nom du fichier à récupérer",
            required = true
    )
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serve(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        String contentType = "application/octet-stream";
        try {
            contentType = file.getURL().openConnection().getContentType();
        } catch (IOException e) {
            // fallback
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }
}