package org.ultimateam.apiultimate.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("CHAMPIONNAT")
@NoArgsConstructor
@Getter
@Setter
public class Championnat extends Competition{
    /**
    public Championnat(Genre genre, Format format, LocalDate date_debut, LocalDate date_fin, String nomCompetition, String descriptionCompetition) {
        super(genre, format, date_debut, date_fin,nomCompetition, descriptionCompetition);
    }**/
}
