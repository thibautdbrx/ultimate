package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Représente une période d'indisponibilité pour une {@link Equipe}.
 *
 * Cette entité contient les dates de début et de fin d'une indisponibilité
 * et la référence vers l'équipe concernée. Elle est utilisée pour exclure
 * des créneaux lors de la génération des plannings ou pour afficher les
 * disponibilités côté client.
 *
 * Rappel concernant le calcul des points dans les classements (règle projet) :
 * - victoire = +3 points
 * - égalité  = +1 point
 * - défaite  = +0 point
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Indisponibilite {

    /**
     * Identifiant de l'indisponibilité (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIndisponibilite;

    /**
     * Équipe concernée par cette indisponibilité.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipe")
    @JsonManagedReference
    private Equipe equipe;

    /**
     * Date et heure de début de l'indisponibilité.
     */
    @JsonProperty("dateDebut")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateDebutIndisponibilite;

    /**
     * Date et heure de fin de l'indisponibilité.
     */
    @JsonProperty("dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateFinIndisponibilite;

    /**
     * Constructeur pratique pour créer une indisponibilité.
     *
     * @param dateDebutIndisponibilite début de la période d'indisponibilité
     * @param dateFinIndisponibilite fin de la période d'indisponibilité
     * @param equipe équipe concernée
     */
    public Indisponibilite(LocalDateTime dateDebutIndisponibilite, LocalDateTime dateFinIndisponibilite, Equipe equipe){
        this.dateDebutIndisponibilite = dateDebutIndisponibilite;
        this.dateFinIndisponibilite = dateFinIndisponibilite;
        this.equipe = equipe;
    }
}
