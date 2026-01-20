package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Indisponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIndisponibilite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipe")
    @JsonManagedReference
    private Equipe equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_match", nullable = true)
    private Match match;

    @JsonProperty("dateDebut")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateDebutIndisponibilite;

    @JsonProperty("dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateFinIndisponibilite;

    public Indisponibilite(LocalDateTime dateDebutIndisponibilite, LocalDateTime dateFinIndisponibilite, Equipe equipe, Match match) {
        this.dateDebutIndisponibilite = dateDebutIndisponibilite;
        this.dateFinIndisponibilite = dateFinIndisponibilite;
        this.equipe = equipe;
        this.match = match;
    }

    public Indisponibilite(LocalDateTime dateDebut, LocalDateTime dateFin, Equipe equipe){
        this(dateDebut, dateFin, equipe, null);
    }
}
