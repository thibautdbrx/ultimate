package org.ultimateam.apiultimate.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Classement {

    private Long id_competition;
    private int id_equipe;
    private int score_total;
    private int victoires;
    private int defaites;
    private int egalite;
    private int rang;
    private int difference_points;
    private LocaldateTime dateDebut;
    private LocaldateTime dateFin;

            public Classement(int id_equipe, Long id_competition, int score_total, int victoires,
                              int defaites, int egalite, int rang, int difference_points) {
                this.id_equipe = id_equipe;
                this.id_competition = id_competition;
                this.score_total = score_total;
                this.victoires = victoires;
                this.defaites = defaites;
                this.egalite = egalite;
                this.rang = rang;
                this.difference_points = difference_points;
            }

    }


}


