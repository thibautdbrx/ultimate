package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {
    public enum Status {WAITING, FINISHED, ONGOING, PAUSED}

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

    @Transient
    @JsonIgnore
    private Duration dureeTotale = Duration.ZERO;

    @Transient
    @JsonIgnore
    private Duration dureePauseTotale = Duration.ZERO;

    @Transient
    @JsonIgnore
    private LocalDateTime date_pause;


    @Transient
    @JsonIgnore
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;

    public void ajouterPoint(Equipe equipe) {
        checkVictory();
        if (this.status == Status.FINISHED) {
            throw new IllegalStateException("Impossible d'ajouter un point, le match est terminé");
        }
        if (Objects.equals(equipe.getId_equipe(), this.equipe1.getId_equipe())) {
            score_equipe1++;
            checkVictory();
        } else if (Objects.equals(equipe.getId_equipe(), this.equipe2.getId_equipe())) {
            score_equipe2++;
            checkVictory();
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
            checkVictory();
        }
        if (Objects.equals(equipe.getId_equipe(), this.equipe2.getId_equipe())) {
            score_equipe2--;
            checkVictory();
        }
        else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }
    }

    public void commencerMatch() {
        if (status == Status.ONGOING || status == Status.PAUSED)
            throw new IllegalStateException("Le match est déjà commencé");
        if (status == Status.FINISHED)
            throw new IllegalStateException("Impossible de commencer un match terminé");
        this.date_debut = LocalDateTime.now();
        this.status = Status.ONGOING;
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkVictory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void finirMatch() {
        if (status == Status.FINISHED)
            return;

        this.date_fin = LocalDateTime.now();
        this.status = Status.FINISHED;
        scheduler.shutdownNow();

    }

    public void mettreEnPause() {
        if (status != Status.ONGOING)
            throw new IllegalStateException("Impossible de mettre en pause un match non démarré ou déjà en pause");

        this.date_pause = LocalDateTime.now();
        status = Status.PAUSED;
    }

    public void reprendreMatch() {
        if (status != Status.PAUSED)
            throw new IllegalStateException("Impossible de reprendre un match qui n'est pas en pause");

        dureePauseTotale = dureePauseTotale.plus(Duration.between(date_pause, LocalDateTime.now()));
        this.date_pause = null;
        status = Status.ONGOING;
    }

    public void checkVictory() {
        if (status != Status.ONGOING)
            return;

        if (score_equipe1 >= 15 || score_equipe2 >= 15) {
            finirMatch();
            return;
        }

        Duration dureeTotale = Duration.between(date_debut, LocalDateTime.now()).minus(dureePauseTotale);
        if (dureeTotale.compareTo(Duration.ofMinutes(90)) >= 0) {
            finirMatch();
        }
    }

}
