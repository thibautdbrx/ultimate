package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité représentant un terrain de sport au sein du système.
 * * <p>Un terrain est caractérisé par son nom, sa localisation géographique (ville)
 * et ses coordonnées GPS (latitude et longitude) afin de permettre la planification
 * des matchs et leur affichage sur une carte.</p>
 */
@Entity
@Data
@Getter
@Setter
public class Terrain {

    /**
     * Identifiant unique du terrain.
     * Accès en lecture seule via l'API (géré automatiquement par la base de données).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idTerrain;

    private String nom;
    private String ville;
    private Double latitude;
    private Double longitude;
}