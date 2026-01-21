package org.ultimateam.apiultimate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clé composite pour la table des demandes de joueur ({@code JoueurRequest}).
 *
 * Cette classe est marquée {@link Embeddable} et contient les deux identifiants
 * nécessaires pour identifier de manière unique une demande : {@code idJoueur}
 * et {@code idEquipe}.
 *
 * Utilisation : cette clé est intégrée dans l'entité {@code JoueurRequest} via
 * {@link jakarta.persistence.EmbeddedId}.
 */
@Embeddable
@Getter
@Setter
public class JoueurRequestId implements Serializable {

    /**
     * Identifiant du joueur participant à la demande.
     */
    @Column(name = "joueur")
    private Long idJoueur;

    /**
     * Identifiant de l'équipe cible de la demande.
     */
    @Column(name = "equipe")
    private Long idEquipe;

    /**
     * Constructeur sans-argument requis par JPA.
     */
    public JoueurRequestId() {}

    /**
     * Constructeur complet pour créer une clé composite à partir des identifiants
     * du joueur et de l'équipe.
     *
     * @param idJoueur identifiant du joueur
     * @param idEquipe identifiant de l'équipe
     */
    public JoueurRequestId(Long idJoueur, Long idEquipe) {
        this.idJoueur = idJoueur;
        this.idEquipe = idEquipe;
    }

    /**
     * Comparaison d'égalité basée sur les deux composants de la clé.
     *
     * @param o l'objet à comparer
     * @return {@code true} si les deux clés sont égales (mêmes idJoueur et idEquipe), sinon {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoueurRequestId)) return false;
        JoueurRequestId that = (JoueurRequestId) o;
        return Objects.equals(idJoueur, that.idJoueur)
                && Objects.equals(idEquipe, that.idEquipe);
    }

    /**
     * Calcul du code de hachage cohérent avec {@link #equals(Object)}.
     *
     * @return valeur de hachage basée sur {@code idJoueur} et {@code idEquipe}
     */
    @Override
    public int hashCode() {
        return Objects.hash(idJoueur, idEquipe);
    }
}
