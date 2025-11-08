package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Equipe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_equipe;

    private String nom_equipe;

    //A compléter pour afficher la liste des joueurs
    @OneToMany(mappedBy = "equipe", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Joueur> joueurs = new ArrayList<>();

    /**
     * Constructeur pour créer une nouvelle instance d'Equipe avec un nom spécifié.
     *
     * @param nom_equipe Le nom de l'équipe.
     */
    public Equipe(String nom_equipe) {
        this.nom_equipe = nom_equipe;
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