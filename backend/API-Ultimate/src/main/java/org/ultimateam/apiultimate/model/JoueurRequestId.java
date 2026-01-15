package org.ultimateam.apiultimate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class JoueurRequestId implements Serializable {

    @Column(name = "joueur")
    private Long idJoueur;

    @Column(name = "equipe")
    private Long idEquipe;

    public JoueurRequestId() {}

    public JoueurRequestId(Long idJoueur, Long idEquipe) {
        this.idJoueur = idJoueur;
        this.idEquipe = idEquipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoueurRequestId)) return false;
        JoueurRequestId that = (JoueurRequestId) o;
        return Objects.equals(idJoueur, that.idJoueur)
                && Objects.equals(idEquipe, that.idEquipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idJoueur, idEquipe);
    }
}
