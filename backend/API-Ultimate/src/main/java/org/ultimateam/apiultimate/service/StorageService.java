package org.ultimateam.apiultimate.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Service d'abstraction pour le stockage de fichiers.
 *
 * Cette interface définit les opérations minimales attendues pour stocker et récupérer
 * des fichiers sur le système de fichiers (ou un stockage externe), ainsi que l'initialisation
 * et la suppression de fichiers.
 *
 */
public interface StorageService {

    /**
     * Sauvegarde le fichier fourni et retourne le nom (ou chemin relatif) sous lequel il est stocké.
     *
     * Implémentations possibles :
     * - générer un nom unique pour éviter les collisions,
     * - enregistrer le fichier sur le disque, S3, ou tout autre stockage configuré.
     *
     * @param file le fichier multipart à stocker (reçoit le contenu et les métadonnées)
     * @return nom du fichier stocké (généralement unique) ou chemin relatif utilisable pour le rechargement
     * @throws RuntimeException en cas d'erreur d'écriture (selon l'implémentation)
     */
    String store(MultipartFile file);

    /**
     * Construit le chemin vers le fichier stocké correspondant au nom fourni.
     *
     * Cette méthode ne charge pas le contenu du fichier mais retourne un {@link Path}
     * qui permet d'accéder à l'emplacement du fichier (par exemple pour lecture ou suppression).
     *
     * @param filename nom ou identifiant du fichier tel que retourné par {@link #store(MultipartFile)}
     * @return {@link Path} local pointant vers le fichier sur le stockage
     */
    Path load(String filename);

    /**
     * Charge le fichier demandé sous forme de {@link Resource} lisible.
     *
     * Utilisé par exemple pour exposer le fichier via une API HTTP (téléchargement ou affichage).
     *
     * @param filename nom ou identifiant du fichier
     * @return ressource Spring représentant le contenu du fichier
     */
    Resource loadAsResource(String filename);

    /**
     * Initialise le service de stockage (création des dossiers, vérifications de permissions, etc.).
     *
     * Doit être appelée au démarrage ou avant la première utilisation pour s'assurer que l'espace de stockage est prêt.
     */
    void init();

    /**
     * Supprime le fichier identifié par {@code filename} du stockage.
     *
     * @param filename nom ou identifiant du fichier à supprimer
     */
    void delete(String filename);
}
