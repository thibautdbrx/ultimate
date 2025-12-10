package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ActionMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idJoueur")
    private Joueur joueur;

    @ManyToOne
    @JoinColumn(name = "idMatch")
    private Match match;

    private long points; // Points marqués par le joueur dans ce match
    private long fautes; // Fautes commises par le joueur dans ce match

}
