package org.ultimateam.apiultimate.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Tournois extends Competition{

    public enum Type {CHAMPIONNAT, TOUNROI}

    private Type type;

    public Tournois(Genre genre, Format format, LocalDateTime date_debut, LocalDateTime date_fin) {
        super(genre, format, date_debut, date_fin);
        type = Type.TOUNROI;
    }
}
