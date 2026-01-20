package org.ultimateam.apiultimate.DTO;

import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.model.Match;

import java.util.List;

public record ScheduleResult(
        List<Match> matchs,
        List<Indisponibilite> indisponibilites,
        List<IndisponibiliteTerrain> indisponibiliteTerrains
) {
    public List<Match> getMatchs() {
        return matchs;
    }

    public List<Indisponibilite> getIndisponibilites() {
        return indisponibilites;
    }

    public List<IndisponibiliteTerrain> getIndisponibiliteTerrains() { return indisponibiliteTerrains; }

    public void addMatch(Match match) {
        matchs.add(match);
    }

    public void addIndisponibilite(Indisponibilite indisponibilite) {
        indisponibilites.add(indisponibilite);
    }

    public void addIndisponibiliteTerrain(IndisponibiliteTerrain indisponibiliteTerrain) {
        indisponibiliteTerrains.add(indisponibiliteTerrain);
    }
}