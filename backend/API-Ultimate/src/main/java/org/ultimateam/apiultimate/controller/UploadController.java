package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ultimateam.apiultimate.service.StorageService;

import java.io.IOException;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des fichiers (Upload et Download).
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Upload Controller", description = "Gestion des fichiers (Images, Documents)")
public class UploadController {

    private final StorageService storageService;

    /**
     * Upload un fichier sur le serveur.
     * Accessible à tout utilisateur authentifié (Joueur, Admin).
     *
     * @param file Le fichier à uploader.
     * @return Une réponse HTTP contenant le nom du fichier et son URL d'accès.
     */
    @Operation(summary = "Uploader un fichier", description = "Permet d'uploader un fichier. Nécessite d'être authentifié.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> upload(
            @Parameter(description = "Fichier à uploader", required = true)
            @RequestParam("file") MultipartFile file) {

        String filename = storageService.store(file);
        return ResponseEntity.status(201).body(
                Map.of("filename", filename, "url", "/api/files/" + filename)
        );
    }

    /**
     * Télécharge ou affiche un fichier depuis le serveur.
     * Accessible publiquement.
     *
     * @param filename Le nom du fichier à récupérer.
     * @return Le fichier demandé.
     */
    @Operation(summary = "Télécharger un fichier", description = "Récupère un fichier par son nom. Accès public.")
    @GetMapping("/{filename:.+}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> serve(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        String contentType = "application/octet-stream";
        try {
            contentType = file.getURL().openConnection().getContentType();
        } catch (IOException e) {
            // fallback si le type n'est pas détecté
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }
}