package org.ultimateam.apiultimate.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListEquipeDTO {
    private long idCompetition;
    private List<Long> idEquipes;
}
