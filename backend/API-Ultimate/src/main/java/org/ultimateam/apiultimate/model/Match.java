package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {
    public enum Status {WAITING, FINISHED, ONGOING, PAUSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMatch;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionMatch> actions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_equipe1")
    private Equipe equipe1;

    @ManyToOne
    @JoinColumn(name = "id_equipe2")
    private Equipe equipe2;

    @ManyToOne()//fetch = FetchType.LAZY
    @JoinColumn(name = "id_winner")
    private Equipe winner = null;

    @ManyToOne
    @JoinColumn(name = "idCompetition")
    private Competition idCompetition;

    private long scoreEquipe1;
    private long scoreEquipe2;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private LocalDateTime dateMatch;
    private long terrain = 0;

    private LocalDateTime datePause = null;

    @JsonIgnore
    private Duration dureeTotale = Duration.ZERO;

    private Duration dureePauseTotale = Duration.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;

}
