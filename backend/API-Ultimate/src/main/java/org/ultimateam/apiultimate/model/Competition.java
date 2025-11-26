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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Competition {
    public enum Genre {MIXTE, FEMMME, HOMME};
    public enum Format {V5, v7};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCompetition;

    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Enumerated(EnumType.STRING)
    private Format format;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    /**
    @OneToMany(mappedBy = "idCompetition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchs = new ArrayList<>();*/

    public Competition(Genre genre, Format format, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.genre = genre;
        this.format = format;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

}
