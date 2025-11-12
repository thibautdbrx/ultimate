package org.ultimateam.apiultimate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor


public class Classement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idCompetition;
    private int idEquipe;
    private int score_total;
    private int victoires;
    private int defaites;
    private int egalite;
    private int rang;
    private int difference_points;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;


            public Classement(int idEquipe, Long idCompetition, int score_total, int victoires,
                              int defaites, int egalite, int rang, int difference_points) {
                this.idEquipe = idEquipe;
                this.idCompetition = idCompetition;
                this.score_total = score_total;
                this.victoires = victoires;
                this.defaites = defaites;
                this.egalite = egalite;
                this.rang = rang;
                this.difference_points = difference_points;
            }

}


