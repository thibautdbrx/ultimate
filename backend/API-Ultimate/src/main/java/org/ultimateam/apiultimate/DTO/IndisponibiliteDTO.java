package org.ultimateam.apiultimate.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndisponibiliteDTO {
    private Long idIndisponibilite;
    private Long idEquipe;
    private String dateDebut;
    private String dateFin;
}
