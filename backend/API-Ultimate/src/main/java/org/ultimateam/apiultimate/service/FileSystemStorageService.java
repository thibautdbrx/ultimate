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

/**
 * Service d'implémentation du stockage des fichiers sur le système de fichiers local.
 *
 * <p>Cette implémentation sauvegarde les fichiers dans le dossier configuré via
 * {@link StorageProperties#getUploadDir()} et préfixe chaque nom de fichier par
 * un UUID pour éviter les collisions. Elle fournit des méthodes pour initialiser
 * le dossier, stocker un fichier, récupérer un {@link Path} ou un {@link Resource}
 * et supprimer un fichier.</p>
 */
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    /**
     * Constructeur qui initialise le dossier racine de stockage à partir des
     * propriétés de configuration et crée le dossier si nécessaire.
     *
     * @param properties configuration contenant le chemin du dossier d'upload
     */
    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        init();
    }

    /**
     * Prépare le dossier de stockage en créant les répertoires manquants.
     *
     * <p>Si la création du dossier échoue, une {@link RuntimeException} est lancée.</p>
     */
    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier d'upload", e);
        }
    }

    /**
     * Stocke un fichier multipart sur le disque.
     *
     * <p>La méthode :
     * <ul>
     *   <li>vérifie que le fichier n'est pas vide,</li>
     *   <li>nettoie le nom original et vérifie qu'il ne contient pas de parcours de chemin (".."),</li>
     *   <li>préfixe le nom par un UUID pour éviter les collisions,</li>
     *   <li>copie le flux d'entrée vers le fichier destination en remplaçant si nécessaire,</li>
     *   <li>retourne le nom de fichier stocké.</li>
     * </ul>
     * </p>
     *
     * @param file le fichier reçu via multipart
     * @return le nom de fichier (avec UUID) tel qu'il est enregistré sur le disque
     * @throws RuntimeException en cas de fichier vide, nom dangereux ou erreur d'E/S
     */
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

    /**
     * Construit un {@link Path} absolu vers le fichier stocké à partir de son nom.
     *
     * @param filename nom du fichier tel qu'enregistré (UUID + nom original)
     * @return le {@link Path} résolu pointant vers le fichier sur le disque
     */
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * Charge un fichier en tant que {@link Resource} lisible pour le renvoyer au client.
     *
     * <p>La méthode transforme le {@link Path} en {@link UrlResource} et vérifie
     * que la ressource existe et est lisible avant de la retourner. En cas d'erreur
     * (fichier introuvable, URL malformée), une {@link RuntimeException} est levée.</p>
     *
     * @param filename nom du fichier tel qu'enregistré
     * @return la {@link Resource} représentant le fichier lisible
     * @throws RuntimeException si le fichier n'existe pas, n'est pas lisible ou si l'URL est mal formée
     */
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

    /**
     * Supprime le fichier identifié par son nom du stockage.
     *
     * <p>La suppression est silencieuse si le fichier n'existe pas. En cas d'erreur
     * d'E/S, une {@link RuntimeException} est levée.</p>
     *
     * @param filename nom du fichier à supprimer
     */
    @Override
    public void delete(String filename) {
        try {
            Files.deleteIfExists(load(filename));
        } catch (IOException e) {
            throw new RuntimeException("Erreur suppression");
        }
    }
}