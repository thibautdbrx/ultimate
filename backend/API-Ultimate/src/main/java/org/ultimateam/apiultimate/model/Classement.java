
package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Classement {

    @EmbeddedId
    private ParticipationId idClassement;

    @ManyToOne
    @MapsId("idEquipe")
    @JoinColumn(name = "idEquipe")
    private Equipe equipe;

    @ManyToOne
    @MapsId("idCompetition")
    @JoinColumn(name = "idCompetition")
    private Competition competition;


    private long point_marque =0;
    private long point_encaisse = 0;
    private long difference_points =0; // point_marqué - point_encaissé

    private long victoires = 0;
    private long defaites = 0;
    private long egalites = 0;

    private long rang = 0;
    private long score = 0 ; // comment c'est calculer : +3 victoire, +1 égalité, +0 défaite

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    public Classement(ParticipationId idClassement) {
        this.idClassement = idClassement;
    }


}





