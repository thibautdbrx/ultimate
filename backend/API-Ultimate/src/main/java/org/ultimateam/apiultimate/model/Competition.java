package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ultimateam.apiultimate.DTO.Genre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de base représentant une compétition (tournoi, championnat, ...).
 *
 * Contient les informations générales partagées par tous les types de
 * compétitions : genre, format, dates, nom et description.
 *
 * Règles de scoring conventionnelles (utilisées pour calculer le score dans
 * les classements) :
 *
 *   - victoire = +3 points</li>
 *   - égalité = +1 point</li>
 *   - défaite = +0 point</li>
 *
 * Ces règles sont appliquées au niveau des services/entités de classement
 * (par exemple dans {@code Classement}) pour calculer le score d'une équipe.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Competition {
    public enum Format {V5, v7};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long idCompetition;

    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Enumerated(EnumType.STRING)
    private Format format;

    /**
     * Retourne le type concret de la compétition (le nom de la classe fille).
     *
     * Utile pour exposer un champ lisible côté client indiquant le type
     * (par exemple "Championnat" ou "Tournoi").
     *
     * @return nom simple de la classe concrète implémentant {@code Competition}
     */
    @JsonProperty("typeCompetition")
    public String getTypeCompetition() {
        return this.getClass().getSimpleName();
    }

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String nomCompetition;
    private String descriptionCompetition;

    @ManyToMany
    @JoinTable(
            name = "competition_terrains",
            joinColumns = @JoinColumn(name = "id_competition"),
            inverseJoinColumns = @JoinColumn(name = "id_terrain")
    )

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Terrain> terrains = new ArrayList<>();

    @Column(nullable = true)
    private Boolean commencer = false; // true : competition non modifiable

    public Competition(Genre genre, Format format, LocalDate dateDebut, LocalDate dateFin,String nomCompetition, String descriptionCompetition) {
        this.genre = genre;
        this.format = format;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nomCompetition = nomCompetition;
        this.descriptionCompetition = descriptionCompetition;
    }

}
