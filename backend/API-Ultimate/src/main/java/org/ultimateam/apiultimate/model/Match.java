package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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
    @JsonBackReference("equipe1-matches")
    private Equipe equipe1;

    @ManyToOne
    @JoinColumn(name = "id_equipe2")
    @JsonBackReference("equipe2-matches")
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
}
