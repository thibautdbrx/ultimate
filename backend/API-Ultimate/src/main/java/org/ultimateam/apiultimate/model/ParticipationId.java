package org.ultimateam.apiultimate.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class ParticipationId implements Serializable {

    private Long idEquipe;
    private Long idCompetition;
}
