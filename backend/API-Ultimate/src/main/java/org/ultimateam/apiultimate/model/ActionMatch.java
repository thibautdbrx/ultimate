package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;

import java.time.LocalDateTime;

/**
 * Représente une action réalisée pendant un match (par exemple but, carton, remplacement, etc.).
 *
 * Cette entité lie une action au {@link Match} concerné et au {@link Joueur} qui a réalisé
 * l'action. Le type de l'action est stocké dans {@link ActionTypeDTO} (enum ou DTO selon
 * l'implémentation) et la date/heure de l'action est conservée dans {@link #dateAction}.
 *
 * Les accesseurs (getters/setters) sont fournis automatiquement par Lombok (@Getter/@Setter).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ActionMatch {

    /**
     * Identifiant unique de l'action (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Match auquel est associée l'action.
     *
     * Cette relation est une association ManyToOne vers l'entité {@link Match}.
     * Le champ est annoté avec {@link JsonIgnore} pour éviter les problèmes de sérialisation
     * circulaire lors de la conversion en JSON.
     */
    @ManyToOne
    @JoinColumn(name = "idMatch")
    @JsonIgnore
    private Match match;

    /**
     * Joueur ayant réalisé l'action.
     *
     * Association ManyToOne vers l'entité {@link Joueur}.
     */
    @ManyToOne
    @JoinColumn(name = "idJoueur")
    private Joueur joueur;

    /**
     * Type de l'action (par exemple BUT, CARTON_ROUGE, REMPLACEMENT, ...).
     *
     * Stocké sous forme d'énumération (EnumType.STRING) représentant les différents
     * types d'actions définis dans {@link ActionTypeDTO}.
     */
    @Enumerated(EnumType.STRING)
    private ActionTypeDTO type;

    /**
     * Date et heure auxquelles l'action a été effectuée.
     *
     * Utile pour ordonner les actions ou afficher le temps de l'action dans l'interface.
     */
    private LocalDateTime dateAction;

}
