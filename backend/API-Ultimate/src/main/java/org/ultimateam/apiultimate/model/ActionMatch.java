package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ActionMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idMatch")
    @JsonIgnore
    private Match match;

    @ManyToOne
    @JoinColumn(name = "idJoueur")
    private Joueur joueur;

    @Enumerated(EnumType.STRING)
    private ActionTypeDTO type;

    private LocalDateTime dateAction;

}
