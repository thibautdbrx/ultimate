package org.ultimateam.apiultimate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO pour la gestion des périodes d'indisponibilité des équipes.
 *
 * Cette classe est utilisée pour transmettre les informations sur les périodes
 * pendant lesquelles une équipe n'est pas disponible pour participer à des matchs.
 * Les dates sont au format {@code yyyy-MM-dd HH:mm}.
 *
 * @see org.ultimateam.apiultimate.controller.IndisponibiliteController
 * @see org.ultimateam.apiultimate.model.Indisponibilite
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndisponibiliteDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idIndisponibilite;
    private Long idEquipe;
    private String dateDebut;
    private String dateFin;
}
