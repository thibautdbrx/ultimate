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

}