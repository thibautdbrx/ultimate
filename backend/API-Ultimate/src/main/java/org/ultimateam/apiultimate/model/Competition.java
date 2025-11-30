package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long idCompetition;

    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Enumerated(EnumType.STRING)
    private Format format;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    /**
    @OneToMany(mappedBy = "idCompetition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchs = new ArrayList<>();*/

    public Competition(Genre genre, Format format, LocalDate dateDebut, LocalDate dateFin) {
        this.genre = genre;
        this.format = format;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

}
