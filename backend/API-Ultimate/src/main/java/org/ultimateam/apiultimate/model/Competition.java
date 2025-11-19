package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private long id_competition;

    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Enumerated(EnumType.STRING)
    private Format format;

    private LocalDateTime date_debut;
    private LocalDateTime date_fin;

    /**
    @OneToMany(mappedBy = "idCompetition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchs = new ArrayList<>();*/

    public Competition(Genre genre, Format format, LocalDateTime date_debut, LocalDateTime date_fin) {
        this.genre = genre;
        this.format = format;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }

}
