package org.ultimateam.apiultimate.model;

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
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    @Enumerated(EnumType.STRING)
    private ActionTypeDTO type;

    private LocalDateTime dateAction;}
