package org.ultimateam.apiultimate.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("TOURNOI")
@NoArgsConstructor
@Getter
@Setter
public class Tournoi extends Competition{
    /**
    public Tournois(Genre genre, Format format, LocalDate date_debut, LocalDate date_fin, String nomCompetition, String descriptionCompetition) {
        super(genre, format, date_debut, date_fin, nomCompetition, descriptionCompetition);
    }*/
}
