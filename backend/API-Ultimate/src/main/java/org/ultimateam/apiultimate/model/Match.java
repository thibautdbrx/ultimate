package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {
    public enum Status {WAITING, FINISHED, ONGOING, PAUSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idMatch;

    @ManyToOne
    @JoinColumn(name = "id_equipe1")
    private Equipe equipe1;

    @ManyToOne
    @JoinColumn(name = "id_equipe2")
    private Equipe equipe2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_winner")
    private Equipe winner;


    @ManyToOne
    @JoinColumn(name = "idCompetition")
    private Competition idCompetition;

    private long scoreEquipe1;
    private long scoreEquipe2;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private LocalDateTime dateMatch;
    @JsonIgnore
    private LocalDateTime datePause = null;

    @JsonIgnore
    private Duration dureeTotale = Duration.ZERO;
    @JsonIgnore
    private Duration dureePauseTotale = Duration.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;

}
