package org.ultimateam.apiultimate.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ParticipationId implements Serializable {

    private Long idEquipe;
    private Long idCompetition;

    public ParticipationId(Long idEquipe, long idCompetition) {
        this.idEquipe = idEquipe;
        this.idCompetition = idCompetition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationId that = (ParticipationId) o;
        return Objects.equals(idEquipe, that.idEquipe) &&
                Objects.equals(idCompetition, that.idCompetition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCompetition, idEquipe);
    }
}
