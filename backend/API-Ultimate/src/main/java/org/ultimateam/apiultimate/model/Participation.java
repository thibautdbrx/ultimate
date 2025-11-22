package org.ultimateam.apiultimate.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Participation {

    @EmbeddedId
    private ParticipationId id;


    public Participation(Equipe equipe, Competition competition) {
        this.id = new ParticipationId(equipe.getIdEquipe(), competition.getIdCompetition());
    }

}