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
    @JsonIgnore
    private LocalDateTime date_pause = null;

    @JsonIgnore
    private Duration dureeTotale = Duration.ZERO;
    @JsonIgnore
    private Duration dureePauseTotale = Duration.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;

}
