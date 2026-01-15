package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.DTO.NombreJoueurs;

import java.util.ArrayList;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    private NombreJoueurs nbJoueurs;

    @Transient
    @JsonIgnore
    public boolean isFull() {
        if (nbJoueurs == null) return false;
        return joueurs.size() >= nbJoueurs.getValue();
    }

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
        if (this.isFull())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"L'équipe est pleine");
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
