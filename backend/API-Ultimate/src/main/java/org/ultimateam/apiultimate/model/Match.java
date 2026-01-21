package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Représente un match entre deux équipes dans une compétition.
 *
 * Cette entité contient les références aux deux équipes participantes, le gagnant
 * éventuel, les scores, les dates (programmation, début et fin), la durée effective
 * et l'état du match. Les actions réalisées lors du match sont stockées dans
 * {@link ActionMatch} et liées via {@link #actions}.
 *
 * Remarque : les getters/setters sont fournis par Lombok (@Getter/@Setter) ;
 * la documentation des champs ci-dessous permettra à la Javadoc générée
 * d'exposer l'information utile même si les getters/setters ne sont pas présents
 * explicitement dans le code source.
 *
 * Règle de scoring projet (convention utilisée pour les classements) : victoire = +3, égalité = +1, défaite = +0.
 *
 * @see ActionMatch
 * @see Equipe
 * @see Competition
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {
    /**
     * État possible d'un match.
     *
     * WAITING : match programmé mais non démarré.
     * ONGOING : match en cours.
     * PAUSED  : match momentanément en pause.
     * FINISHED: match terminé.
     */
    public enum Status {WAITING, FINISHED, ONGOING, PAUSED}

    /**
     * Identifiant unique du match (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMatch;

    /**
     * Liste des actions enregistrées pour ce match (buts, cartons, remplacements, ...).
     *
     * Relation OneToMany vers {@link ActionMatch}.
     */
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionMatch> actions = new ArrayList<>();

    /**
     * Équipe à domicile (première équipe) participant au match.
     */
    @ManyToOne
    @JoinColumn(name = "id_equipe1")
    private Equipe equipe1;

    /**
     * Équipe visiteuse (seconde équipe) participant au match.
     */
    @ManyToOne
    @JoinColumn(name = "id_equipe2")
    private Equipe equipe2;

    /**
     * Équipe gagnante du match (null si pas encore déterminée ou en cas d'égalité sans vainqueur).
     */
    @ManyToOne()//fetch = FetchType.LAZY
    @JoinColumn(name = "id_winner")
    private Equipe winner = null;

    /**
     * Compétition à laquelle ce match appartient.
     *
     * Association ManyToOne vers {@link Competition}.
     */
    @ManyToOne
    @JoinColumn(name = "idCompetition")
    private Competition idCompetition;

    /**
     * Score de l'équipe 1.
     */
    private long scoreEquipe1;

    /**
     * Score de l'équipe 2.
     */
    private long scoreEquipe2;

    /**
     * Date et heure de début effective du match (null si non démarré).
     */
    private LocalDateTime dateDebut;

    /**
     * Date et heure de fin effective du match (null si pas terminé).
     */
    private LocalDateTime dateFin;

    /**
     * Date et heure programmée du match (planning).
     */
    private LocalDateTime dateMatch;

    /**
     * Identifiant/numéro du terrain ou de la salle où se déroule le match.
     */
    private long terrain = 0;

    /**
     * Date/heure de la dernière pause (null si pas en pause).
     */
    private LocalDateTime datePause = null;

    /**
     * Durée totale jouée (excluant les pauses) — suivie en mémoire; initialisée à zéro.
     */
    @JsonIgnore
    private Duration dureeTotale = Duration.ZERO;

    /**
     * Durée cumulée des pauses pour ce match.
     */
    private Duration dureePauseTotale = Duration.ZERO;

    /**
     * Statut courant du match (WAITING, ONGOING, PAUSED, FINISHED).
     */
    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;

}
