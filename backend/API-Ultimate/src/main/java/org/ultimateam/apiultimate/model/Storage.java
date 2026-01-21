package org.ultimateam.apiultimate.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Représente la configuration de stockage des fichiers.
 *
 * Cette classe est mappée aux propriétés de configuration préfixées par "file" grâce
 * à l'annotation @ConfigurationProperties. Elle expose le répertoire d'upload configuré
 * et fournit des accesseurs pour le récupérer ou le modifier.
 */
@Component
@ConfigurationProperties(prefix = "file")
public class Storage {
    private String uploadDir;

    /**
     * Retourne le chemin du répertoire d'upload configuré.
     *
     * La valeur est chargée depuis les propriétés de l'application (préfixe "file").
     * Utilisez ce chemin pour lire/écrire les fichiers uploadés par l'application.
     *
     * @return le chemin du répertoire de stockage des fichiers uploadés
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * Définit le chemin du répertoire d'upload.
     *
     * Exemple d'utilisation : si vous souhaitez changer dynamiquement l'emplacement des
     * fichiers uploadés en tests ou via une configuration externe, appelez ce setter.
     *
     * @param uploadDir le chemin absolu ou relatif du répertoire d'uploads
     */
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
