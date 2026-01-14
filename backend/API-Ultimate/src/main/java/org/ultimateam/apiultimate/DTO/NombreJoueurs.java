package org.ultimateam.apiultimate.DTO;

import lombok.Getter;

@Getter
public enum NombreJoueurs {
    CINQ(5),
    SEPT(7);

    private final int value;

    NombreJoueurs(int value) {
        this.value = value;
    }

    public static NombreJoueurs fromValue(int value) {
        for (NombreJoueurs n : values()) {
            if (n.value == value) {
                return n;
            }
        }
        throw new IllegalArgumentException("nbJoueurs doit être 5 ou 7");
    }
}