package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ultimateam.apiultimate.DTO.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une équipe participant à des compétitions.
 *
 * Contient les informations basiques (nom, description), la liste des joueurs
 * et les indisponibilités. Les getters/setters sont fournis par Lombok.
 *
 * Rappel important sur le calcul des points dans les classements :
 * - victoire = +3 points
 * - égalité  = +1 point
 * - défaite  = +0 point
 *
 * Ces règles sont appliquées au niveau des entités/services de classement
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Equipe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idEquipe;

    private String nomEquipe;
    private String description;

    @OneToMany(mappedBy = "equipe", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Joueur> joueurs = new ArrayList<>();

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JsonBackReference
    private List<Indisponibilite> indisponibilites = new ArrayList<>();

    private Genre genre = null;

    public Equipe(String nomEquipe) {
        this.nomEquipe = nomEquipe;
    }

    /**
     * Ajoute un joueur à l'équipe.
     * Cette méthode établit la relation en définissant l'équipe pour le joueur spécifié.
     *
     * @param joueur Le joueur à ajouter à cette équipe.
     */
    public void addJoueur(Joueur joueur) {
        joueur.setEquipe(this);
    }

    /**
     * Retire un joueur de l'équipe.
     * Cette méthode rompt la relation en mettant l'équipe du joueur à null.
     *
     * @param joueur Le joueur à retirer de cette équipe.
     */
    public void removeJoueur(Joueur joueur) {
        joueur.setEquipe(null);
    }
}
