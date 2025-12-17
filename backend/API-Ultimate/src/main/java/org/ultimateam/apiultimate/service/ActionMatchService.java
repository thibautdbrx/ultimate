package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.ActionMatchRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ActionMatchService {

    private final ActionMatchRepository actionMatchRepository;
    private final MatchRepository matchRepository;
    private final EquipeService equipeService;
    private final JoueurService joueurService;

    public ActionMatchService(ActionMatchRepository actionMatchRepository, MatchRepository matchRepository,
                              EquipeService equipeService, JoueurService joueurService) {
        this.actionMatchRepository = actionMatchRepository;
        this.matchRepository = matchRepository;
        this.equipeService = equipeService;
        this.joueurService = joueurService;
    }

    public ActionMatch addPoint(long id_match, long id_equipe, long id_joueur) {
        return addAction(id_match, id_equipe, id_joueur, ActionType.POINT);
    }

    public ActionMatch addFaute(long id_match, long id_equipe, long id_joueur) {
        return addAction(id_match, id_equipe, id_joueur, ActionType.FAUTE);
    }

    private ActionMatch addAction(long id_match, long id_equipe, long id_joueur, ActionType type) {
        Match match = matchRepository.findById(id_match).orElse(null);
        Equipe equipe = equipeService.getById(id_equipe);
        Joueur joueur = joueurService.getById(id_joueur);

        if (match == null || equipe == null || joueur == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match, l'équipe ou le joueur n'existe pas");
        }

        if (match.getStatus() != Match.Status.ONGOING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Le match n'est pas en cours");
        }

        verifyJoueurInMatch(match, id_joueur);

        if (!Objects.equals(equipe.getIdEquipe(), match.getEquipe1().getIdEquipe()) &&
                !Objects.equals(equipe.getIdEquipe(), match.getEquipe2().getIdEquipe())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette équipe ne fait pas partie du match");
        }
        ActionMatch action = new ActionMatch();
        action.setMatch(match);
        action.setJoueur(joueur);
        action.setType(type);
        action.setDateAction(LocalDateTime.now());

        actionMatchRepository.save(action);
        if (type == ActionType.POINT) {
            if (Objects.equals(equipe.getIdEquipe(), match.getEquipe1().getIdEquipe())) {
                match.setScoreEquipe1(match.getScoreEquipe1() + 1);
            } else {
                match.setScoreEquipe2(match.getScoreEquipe2() + 1);
            }
            matchRepository.save(match);
        }

        return action;
    }

    private void verifyJoueurInMatch(Match match, long id_joueur) {
        boolean joueurEstDansEquipe1 = match.getEquipe1().getJoueurs().stream()
                .anyMatch(j -> Objects.equals(j.getIdJoueur(), id_joueur));

        boolean joueurEstDansEquipe2 = match.getEquipe2().getJoueurs().stream()
                .anyMatch(j -> Objects.equals(j.getIdJoueur(), id_joueur));

        if (!joueurEstDansEquipe1 && !joueurEstDansEquipe2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le joueur ne fait partie d'aucune des deux équipes de ce match");
        }
    }

}
