package org.ultimateam.apiultimate.DTO;

import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Match;

import java.util.List;

/**
 * Record représentant le résultat d'un processus de planification.
 *
 * Cette classe encapsule les matchs planifiés ainsi que les indisponibilités
 * associées à une équipe ou une compétition. Elle est utilisée pour retourner
 * les résultats d'un algorithme de planification ou de génération de calendrier.
 *
 * @param matchs           Liste des matchs planifiés.
 *                         Peut être vide si aucun match n'a pu être planifié.
 *
 * @param indisponibilites Liste des indisponibilités prises en compte
 *                         ou générées pendant le processus de planification.
 *
 * @see Match
 * @see Indisponibilite
 */

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