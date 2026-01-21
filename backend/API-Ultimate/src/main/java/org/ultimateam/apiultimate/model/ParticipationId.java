package org.ultimateam.apiultimate.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clé composite pour identifier une participation d'une équipe à une compétition.
 *
 * Cette classe est utilisée comme clé embarquée ({@code @Embeddable}) dans l'entité
 * {@link Participation} afin d'identifier de façon unique une participation via
 * la paire (idEquipe, idCompetition).
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ParticipationId implements Serializable {

    /**
     * Identifiant de l'équipe participant à la compétition.
     */
    private Long idEquipe;

    /**
     * Identifiant de la compétition.
     */
    private Long idCompetition;

    /**
     * Constructeur principal de la clé composite.
     *
     * @param idEquipe identifiant de l'équipe
     * @param idCompetition identifiant de la compétition
     */
    public ParticipationId(Long idEquipe, long idCompetition) {
        this.idEquipe = idEquipe;
        this.idCompetition = idCompetition;
    }

    /**
     * Vérifie l'égalité entre deux clés de participation.
     *
     * Deux instances sont considérées égales si leurs champs {@code idEquipe}
     * et {@code idCompetition} sont égaux (gestion correcte des valeurs nulles).
     *
     * @param o objet à comparer
     * @return {@code true} si les deux clés représentent la même participation, sinon {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationId that = (ParticipationId) o;
        return Objects.equals(idEquipe, that.idEquipe) &&
                Objects.equals(idCompetition, that.idCompetition);
    }

    /**
     * Calcule le code de hachage basé sur {@code idCompetition} et {@code idEquipe}.
     *
     * Doit être cohérent avec {@link #equals(Object)}.
     *
     * @return valeur de hachage de la clé composite
     */
    @Override
    public int hashCode() {
        return Objects.hash(idCompetition, idEquipe);
    }
}
