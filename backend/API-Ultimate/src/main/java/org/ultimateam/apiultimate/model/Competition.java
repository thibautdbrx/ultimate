package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ultimateam.apiultimate.DTO.Genre;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Competition {
    public enum Format {V5, v7};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long idCompetition;

    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Enumerated(EnumType.STRING)
    private Format format;

    @JsonProperty("typeCompetition")
    public String getTypeCompetition() {
        return this.getClass().getSimpleName();
    }

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String nomCompetition;
    private String descriptionCompetition;

    /**
    @OneToMany(mappedBy = "idCompetition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchs = new ArrayList<>();*/

    public Competition(Genre genre, Format format, LocalDate dateDebut, LocalDate dateFin,String nomCompetition, String descriptionCompetition) {
        this.genre = genre;
        this.format = format;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nomCompetition = nomCompetition;
        this.descriptionCompetition = descriptionCompetition;
    }

}
