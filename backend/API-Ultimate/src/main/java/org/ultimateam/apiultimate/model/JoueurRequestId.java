package org.ultimateam.apiultimate.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class JoueurRequestId implements Serializable {

    private Long joueur;
    private Long equipe;

    public JoueurRequestId() {}

    public JoueurRequestId(Long joueur, Long equipe) {
        this.joueur = joueur;
        this.equipe = equipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoueurRequestId)) return false;
        JoueurRequestId that = (JoueurRequestId) o;
        return Objects.equals(joueur, that.joueur)
                && Objects.equals(equipe, that.equipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joueur, equipe);
    }
}
