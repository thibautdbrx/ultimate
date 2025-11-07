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

    @JsonProperty("dateDebut")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date_debut_indisponibilite;
    @JsonProperty("dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date_fin_indisponibilite;

    public Indisponibilite(LocalDateTime date_debut_indisponibilite, LocalDateTime date_fin_indisponibilite, Equipe equipe){
        this.date_debut_indisponibilite = date_debut_indisponibilite;
        this.date_fin_indisponibilite = date_fin_indisponibilite;
        this.equipe = equipe;
    }
}
