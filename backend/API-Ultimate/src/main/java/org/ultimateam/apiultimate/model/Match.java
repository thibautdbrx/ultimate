package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {
    public enum Status {WAITING, FINISHED, ONGOING}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "id_equipe1")
    private Equipe equipe1;

    @ManyToOne
    @JoinColumn(name = "id_equipe2")
    private Equipe equipe2;

    /**
    @ManyToOne
    @JoinColumn(name = "id_competition")
    private Competition id_competition;
    */


    private long score_equipe1;
    private long score_equipe2;
    private LocalDateTime date_debut;
    private LocalDateTime date_fin;

    @Enumerated(EnumType.STRING)
    private Status status;


    public Match(Equipe equipe1, Equipe equipe2) {
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;

        this.status = Status.WAITING;
        this.score_equipe1 = 0;
        this.score_equipe2 = 0;
    }

    public void ajouterPoint(Equipe equipe) {
        if (this.status == Status.FINISHED) {
            throw new IllegalStateException("Impossible de retirer un point, le match est terminé");
        }
        if (Objects.equals(equipe.getId_equipe(), this.equipe1.getId_equipe())) {
            score_equipe1++;
        } else if (Objects.equals(equipe.getId_equipe(), this.equipe2.getId_equipe())) {
            score_equipe2++;
        } else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }
    }

    public void retirerPoint(Equipe equipe) {
        if (this.status == Status.FINISHED) {
            throw new IllegalStateException("Impossible de retirer un point, le match est terminé");
        }
        if (Objects.equals(equipe.getId_equipe(), this.equipe1.getId_equipe())) {
            score_equipe1--;
        }
        if (Objects.equals(equipe.getId_equipe(), this.equipe2.getId_equipe())) {
            score_equipe2--;
        }
        else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }
    }

    public void finirMatch() {
        this.date_fin = LocalDateTime.now();
        this.status = Status.FINISHED;

    }
    public void commencerMatch() {
        this.date_debut = LocalDateTime.now();
        this.status = Status.ONGOING;
    }
}
