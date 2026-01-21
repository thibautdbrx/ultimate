package org.ultimateam.apiultimate.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration pour le stockage des fichiers.
 *
 * <p>Cette classe permet de mapper les propriétés définies dans le fichier
 * {@code application.properties} (avec le préfixe {@code file}) vers des objets Java.
 * Elle est principalement utilisée pour définir le répertoire racine des téléchargements (uploads).</p>
 */
@ConfigurationProperties(prefix = "file")
/**
 * Chemin du répertoire où les fichiers téléchargés seront stockés.
 * Valeur par défaut : "uploads".
 */
public class StorageProperties {
    private String uploadDir = "uploads";
    /**
     * Récupère le chemin du répertoire de stockage.
     *
     * @return le chemin du répertoire sous forme de {@link String}
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * Définit le chemin du répertoire de stockage.
     *
     * @param uploadDir le nouveau chemin du répertoire à utiliser
     */
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
