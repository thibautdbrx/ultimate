package org.ultimateam.apiultimate.DTO;

import lombok.Getter;

/**
 * Énumération représentant le nombre de joueurs autorisés par équipe pour une compétition.
 * * <p>Cette énumération restreint les formats de jeu aux standards acceptés (5 ou 7 joueurs)
 * et permet de valider la configuration des compétitions ou des équipes.</p>
 */
@Getter
public enum NombreJoueurs {
    CINQ(5),
    SEPT(7);

    private final int value;

    /**
     * Constructeur de l'énumération.
     *
     * @param value la valeur numérique associée au format
     */
    NombreJoueurs(int value) {
        this.value = value;
    }

    /**
     * Convertit une valeur entière en instance de l'énumération NombreJoueurs.
     *
     * @param value l'entier à convertir (doit être 5 ou 7)
     * @return l'instance de {@link NombreJoueurs} correspondante
     * @throws IllegalArgumentException si la valeur fournie n'est ni 5 ni 7
     */
    public static NombreJoueurs fromValue(int value) {
        for (NombreJoueurs n : values()) {
            if (n.value == value) {
                return n;
            }
        }
        throw new IllegalArgumentException("nbJoueurs doit être 5 ou 7");
    }
}