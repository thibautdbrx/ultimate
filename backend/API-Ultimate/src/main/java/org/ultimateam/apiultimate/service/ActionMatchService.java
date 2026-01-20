package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
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

    public List<ActionMatch> findAll(){ return actionMatchRepository.findAll();}
    public ActionMatch findById(Long id){ return actionMatchRepository.findById(id).orElse(null);}
    public List<ActionMatch> findByMatchId(Long matchId){ return actionMatchRepository.findByMatch_IdMatch(matchId);}
    public List<ActionMatch> findByActionType(ActionTypeDTO actionTypeDTO){ return actionMatchRepository.findByType(actionTypeDTO);}
    public List<ActionMatch> findByActionTypeAndMatchId(ActionTypeDTO actionTypeDTO, Long matchId){ return actionMatchRepository.findByTypeAndMatch_IdMatch(actionTypeDTO, matchId);}
    public List<ActionMatch> findByJoueurAndMatchId(Long idJoueur, Long matchId){ return actionMatchRepository.findByJoueur_IdJoueurAndMatch_IdMatch(idJoueur, matchId);}
    public List<ActionMatch> findByActionAndJoueurAndMatchId(ActionTypeDTO actionTypeDTO, Long matchId, Long joueurId){ return actionMatchRepository.findByTypeAndJoueur_IdJoueurAndMatch_IdMatch(actionTypeDTO, joueurId, matchId);}



    public ActionMatch addPoint(long id_match, long id_equipe, MatchPointDTO matchPointDTO) {
        return addAction(id_match, id_equipe, matchPointDTO.getIdJoueur(), ActionTypeDTO.POINT);
    }

    public ActionMatch addFaute(long id_match, long id_equipe, MatchFauteDTO matchFauteDTO) {
        return addAction(id_match, id_equipe, matchFauteDTO.getIdJoueur(), ActionTypeDTO.FAUTE);
    }

    public ActionMatch addAction(long id_match, long id_equipe, long id_joueur, ActionTypeDTO type) {
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

        if (!Objects.equals(equipe.getIdEquipe(), match.getEquipe1().getIdEquipe()) && !Objects.equals(equipe.getIdEquipe(), match.getEquipe2().getIdEquipe())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette équipe ne fait pas partie du match");
        }

        ActionMatch action = new ActionMatch();
        action.setMatch(match);
        action.setJoueur(joueur);
        action.setType(type);
        action.setDateAction(LocalDateTime.now());
        action.setDatePause(match.getDatePause());

        return actionMatchRepository.save(action);
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
