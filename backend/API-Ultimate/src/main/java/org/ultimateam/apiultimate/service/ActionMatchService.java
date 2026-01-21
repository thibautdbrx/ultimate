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

    /**
     * Constructeur pour l'injection des dépendances du service.
     *
     * @param actionMatchRepository repository des actions de match.
     * @param matchRepository       repository des matchs.
     * @param equipeService         service de gestion des équipes.
     * @param joueurService         service de gestion des joueurs.
     */

    public ActionMatchService(ActionMatchRepository actionMatchRepository, MatchRepository matchRepository,
                              EquipeService equipeService, JoueurService joueurService) {
        this.actionMatchRepository = actionMatchRepository;
        this.matchRepository = matchRepository;
        this.equipeService = equipeService;
        this.joueurService = joueurService;
    }

    /**
     * Récupère toutes les actions de tous les matchs.
     *
     * @return une liste de toutes les entités {@link ActionMatch}.
     */
    public List<ActionMatch> findAll(){ return actionMatchRepository.findAll();}

    /**
     * Récupère une action par son identifiant.
     *
     * @param id identifiant de l'action.
     * @return l'action trouvée ou {@code null} si introuvable.
     */
    public ActionMatch findById(Long id){ return actionMatchRepository.findById(id).orElse(null);}

    /**
     * Récupère toutes les actions liées à un match donné.
     *
     * @param matchId identifiant du match.
     * @return liste des {@link ActionMatch} associés au match.
     */
    public List<ActionMatch> findByMatchId(Long matchId){ return actionMatchRepository.findByMatch_IdMatch(matchId);}

    /**
     * Récupère les actions par type d'action.
     *
     * @param actionTypeDTO type d'action (par ex. POINT, FAUTE).
     * @return liste des actions correspondant au type fourni.
     */
    public List<ActionMatch> findByActionType(ActionTypeDTO actionTypeDTO){ return actionMatchRepository.findByType(actionTypeDTO);}

    /**
     * Récupère les actions d'un certain type pour un match donné.
     *
     * @param actionTypeDTO type d'action.
     * @param matchId       identifiant du match.
     * @return liste des actions filtrées par type et match.
     */
    public List<ActionMatch> findByActionTypeAndMatchId(ActionTypeDTO actionTypeDTO, Long matchId){ return actionMatchRepository.findByTypeAndMatch_IdMatch(actionTypeDTO, matchId);}

    /**
     * Récupère les actions effectuées par un joueur dans un match.
     *
     * @param idJoueur identifiant du joueur.
     * @param matchId  identifiant du match.
     * @return liste des actions du joueur pour le match.
     */
    public List<ActionMatch> findByJoueurAndMatchId(Long idJoueur, Long matchId){ return actionMatchRepository.findByJoueur_IdJoueurAndMatch_IdMatch(idJoueur, matchId);}

    /**
     * Récupère les actions d'un type précis effectuées par un joueur dans un match.
     *
     * @param actionTypeDTO type d'action.
     * @param matchId       identifiant du match.
     * @param joueurId      identifiant du joueur.
     * @return liste des actions correspondant aux critères.
     */
    public List<ActionMatch> findByActionAndJoueurAndMatchId(ActionTypeDTO actionTypeDTO, Long matchId, Long joueurId){ return actionMatchRepository.findByTypeAndJoueur_IdJoueurAndMatch_IdMatch(actionTypeDTO, joueurId, matchId);}

    /**
     * Ajoute un point pour un joueur dans un match et une équipe donnés.
     *
     * @param id_match      identifiant du match.
     * @param id_equipe     identifiant de l'équipe.
     * @param matchPointDTO DTO contenant l'identifiant du joueur qui marque le point.
     * @return l'action {@link ActionMatch} créée et sauvegardée.
     * @throws ResponseStatusException si le match, l'équipe ou le joueur n'existe pas, si le match n'est pas en cours, ou si le joueur ne fait pas partie du match.
     */
    public ActionMatch addPoint(long id_match, long id_equipe, MatchPointDTO matchPointDTO) {
        Match match = matchRepository.findById(id_match).orElse(null);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        return addAction(id_match, id_equipe, matchPointDTO.getIdJoueur(), ActionTypeDTO.POINT, match.getDatePause());
    }

    /**
     * Ajoute une faute pour un joueur dans un match et une équipe donnés.
     *
     * @param id_match      identifiant du match.
     * @param id_equipe     identifiant de l'équipe.
     * @param matchFauteDTO DTO contenant l'identifiant du joueur auteur de la faute.
     * @return l'action {@link ActionMatch} créée et sauvegardée.
     * @throws ResponseStatusException si le match, l'équipe ou le joueur n'existe pas, si le match n'est pas en cours, ou si le joueur ne fait pas partie du match.
     */
    public ActionMatch addFaute(long id_match, long id_equipe, MatchFauteDTO matchFauteDTO) {
        Match match = matchRepository.findById(id_match).orElse(null);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        return addAction(id_match, id_equipe, matchFauteDTO.getIdJoueur(), ActionTypeDTO.FAUTE, match.getDatePause());
    }

    /**
     * Méthode générique pour ajouter une action (point, faute, etc.) à un match.
     * Vérifie l'existence du match, de l'équipe et du joueur, que le match est en cours,
     * que l'équipe fait partie du match, et que le joueur appartient à l'une des équipes.
     *
     * @param id_match   identifiant du match.
     * @param id_equipe  identifiant de l'équipe réalisant l'action.
     * @param id_joueur  identifiant du joueur réalisant l'action.
     * @param type       type de l'action ({@link ActionTypeDTO}).
     * @return l'action créée et persistée.
     * @throws ResponseStatusException si le match, l'équipe ou le joueur n'existe pas,
     *                                 si le match n'est pas en cours,
     *                                 ou si l'équipe ne participe pas au match,
     *                                 ou si le joueur n'appartient à aucune des équipes du match.
     */
    public ActionMatch addAction(long id_match, long id_equipe, long id_joueur, ActionTypeDTO type) {
        Match match = matchRepository.findById(id_match).orElse(null);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        return addAction(id_match, id_equipe, matchFauteDTO.getIdJoueur(), ActionTypeDTO.FAUTE, match.getDatePause());
    }

    public ActionMatch addAction(long id_match, long id_equipe, long id_joueur, ActionTypeDTO type, LocalDateTime datePause) {
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
        action.setDatePause(datePause);
        action.setMatch(match);
        action.setJoueur(joueur);
        action.setType(type);
        action.setDateAction(LocalDateTime.now());
        action.setDatePause(match.getDatePause());

        return actionMatchRepository.save(action);
    }

    /**
     * Vérifie que le joueur appartient à l'une des deux équipes du match.
     *
     * @param match     le match à vérifier.
     * @param id_joueur identifiant du joueur à vérifier.
     * @throws ResponseStatusException avec statut FORBIDDEN si le joueur n'appartient à aucune des équipes du match.
     */
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
