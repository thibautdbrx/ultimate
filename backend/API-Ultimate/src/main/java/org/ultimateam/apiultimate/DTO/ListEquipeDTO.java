package org.ultimateam.apiultimate.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO pour l'ajout de plusieurs équipes à une compétition.
 *
 * Cette classe est utilisée pour transmettre une liste d'identifiants d'équipes
 * à ajouter à une compétition spécifique. Elle permet d'inscrire plusieurs équipes
 * en une seule requête.
 *
 * @see org.ultimateam.apiultimate.controller.ParticipationController#createParticipation(ListEquipeDTO)
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListEquipeDTO {
    private long idCompetition;
    private List<Long> idEquipes;
}
