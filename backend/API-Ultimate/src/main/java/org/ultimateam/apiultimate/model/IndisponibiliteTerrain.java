package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité représentant une plage d'indisponibilité pour un terrain spécifique.
 * * <p>Une indisponibilité peut être créée manuellement ou générée automatiquement
 * lorsqu'un {@link Match} est planifié sur le terrain. Elle permet d'éviter
 * les chevauchements lors de la génération des calendriers.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class IndisponibiliteTerrain {

    /** Identifiant unique de l'indisponibilité. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le terrain concerné par cette indisponibilité. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terrain")
    private Terrain terrain;

    /** * Le match associé à cette indisponibilité, le cas échéant.
     * Ce champ peut être nul si l'indisponibilité est due à une raison externe (maintenance, etc.).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_match", nullable = true)
    private Match match;

    /** Date et heure de début de l'indisponibilité. Format : yyyy-MM-dd HH:mm */
    @JsonProperty("dateDebut")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateDebutIndisponibilite;

    /** Date et heure de fin de l'indisponibilité. Format : yyyy-MM-dd HH:mm */
    @JsonProperty("dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateFinIndisponibilite;

    /**
     * Constructeur complet pour initialiser une nouvelle indisponibilité de terrain.
     *
     * @param dateDebut La date de début.
     * @param dateFin   La date de fin.
     * @param terrain   Le terrain concerné.
     * @param match     Le match lié (peut être {@code null}).
     */
    public IndisponibiliteTerrain(LocalDateTime dateDebut, LocalDateTime dateFin, Terrain terrain, Match match) {
        this.dateDebutIndisponibilite = dateDebut;
        this.dateFinIndisponibilite = dateFin;
        this.terrain = terrain;
        this.match = match;
    }

}