package org.ultimateam.apiultimate.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("TOURNOI")
@NoArgsConstructor
@Getter
@Setter
public class Tournois extends Competition{

    public Tournois(Genre genre, Format format, LocalDate date_debut, LocalDate date_fin) {
        super(genre, format, date_debut, date_fin);
    }
}
