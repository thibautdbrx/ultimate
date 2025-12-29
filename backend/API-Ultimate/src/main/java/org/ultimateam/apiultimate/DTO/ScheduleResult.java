package org.ultimateam.apiultimate.DTO;

import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Match;

import java.util.List;

public record ScheduleResult(
        List<Match> matchs,
        List<Indisponibilite> indisponibilites
) {
    public List<Match> getMatchs() {
        return matchs;
    }

    public List<Indisponibilite> getIndisponibilites() {
        return indisponibilites;
    }

    public void addMatch(Match match) {
        matchs.add(match);
    }

    public void addIndisponibilite(Indisponibilite indisponibilite) {
        indisponibilites.add(indisponibilite);
    }
}