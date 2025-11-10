package org.ultimateam.apiultimate.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tournois extends Competition{

    public enum Type {CHAMPIONNAT, TOUNROI}

    private Type type;

    public Tournois() {
        super();
        type = Type.TOUNROI;
    }
}
