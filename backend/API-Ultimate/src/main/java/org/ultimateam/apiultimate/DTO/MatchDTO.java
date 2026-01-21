package org.ultimateam.apiultimate.DTO;

import lombok.Data;

import java.util.List;

/**
 * DTO pour la création d'un nouveau match.
 *
 * Ce DTO est utilisé pour transmettre les informations nécessaires à la création d'un match,
 * notamment les identifiants des équipes participantes.
 *
 * @see org.ultimateam.apiultimate.controller.MatchController#createMatch(MatchDTO)
 * @see org.ultimateam.apiultimate.model.Match
 */

@Data
public class MatchDTO {
    private List<Long> idEquipes;
}
