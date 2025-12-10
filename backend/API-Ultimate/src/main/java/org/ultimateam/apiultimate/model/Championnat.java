package org.ultimateam.apiultimate.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("CHAMPIONNAT")
@NoArgsConstructor
@Getter
@Setter
public class Championnat extends Competition{

    public Championnat(Genre genre, Format format, LocalDateTime date_debut, LocalDateTime date_fin) {
        super(genre, format, date_debut, date_fin);
    }
}
