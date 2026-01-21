package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.JoueurRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service gérant la logique métier des matchs.
 *
 * Ce service fournit des opérations CRUD sur {@link Match} ainsi que les
 * transitions d'état (démarrer, mettre en pause, reprendre, terminer), la gestion
 * des points/faute, la vérification de la victoire, et la planification de la
 * fin de match via un scheduler.
 */
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final EquipeService equipeService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<Long, ScheduledFuture<?>> matchSchedulers = new HashMap<>();
    private final TournoisService tournoisService;
    private final ClassementService classementService;
    private final JoueurRepository joueurRepository;
    private final JoueurService joueurService;
    private final ActionMatchService actionMatchService;

    /**
     * Constructeur avec injection des dépendances du service.
     *
     * @param matchRepository repository pour accéder aux matchs
     * @param equipeService service pour accéder aux équipes
     * @param tournoisService service pour accéder aux tournois
     * @param classementService service de gestion des classements
     * @param joueurRepository repository des joueurs
     * @param joueurService service de gestion des joueurs
     * @param actionMatchService service gérant les actions de match (points/faute)
     */
    public MatchService(MatchRepository matchRepository, EquipeService equipeService, TournoisService tournoisService, ClassementService classementService, JoueurRepository joueurRepository, JoueurService joueurService, ActionMatchService actionMatchService) {
        this.matchRepository = matchRepository;
        this.equipeService = equipeService;
        this.tournoisService = tournoisService;
        this.classementService = classementService;
        this.joueurRepository = joueurRepository;
        this.joueurService = joueurService;
        this.actionMatchService = actionMatchService;
    }

    // --------------------- BASIC CRUD ---------------------

    /**
     * Récupère un match par son identifiant.
     *
     * @param id identifiant du match
     * @return l'entité {@link Match} trouvée ou {@code null} si inexistante
     */
    public Match getById(Long id) { return matchRepository.findById(id).orElse(null); }

    /**
     * Persiste un {@link Match} en base (création ou mise à jour).
     *
     * @param match l'entité match à sauvegarder
     * @return le match persisté
     */
    public Match save(Match match) { return matchRepository.save(match); }

    /**
     * Supprime un match par son identifiant.
     *
     * @param id identifiant du match à supprimer
     * @throws ResponseStatusException si le match n'existe pas
     */
    public void deleteById(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        }
        matchRepository.deleteById(id);
    }

    /**
     * Récupère tous les matchs.
     *
     * @return un Iterable contenant tous les {@link Match}
     */
    public Iterable<Match> getAll() { return matchRepository.findAll(); }

    /**
     * Récupère la liste des matchs commencés mais non terminés.
     *
     * @return liste des matchs "en cours"
     */
    public List<Match> getStarted() { return matchRepository.findByDateDebutIsNotNullAndDateFinIsNull(); }

    /**
     * Récupère la liste des matchs qui n'ont pas encore commencé et dont la date de match est récente.
     *
     * @return liste des matchs planifiés mais non commencés
     */
    public List<Match> getNotStarted() {
        LocalDateTime dateBefore = LocalDateTime.now().minusDays(2);
        return matchRepository.findByDateMatchAfterAndDateDebutIsNull(dateBefore);
    }

    /**
     * Récupère la liste des matchs terminés.
     *
     * @return liste des matchs dont la date de fin est renseignée
     */
    public List<Match> getFinished() { return matchRepository.findByDateFinIsNotNull(); }

    // --------------------- MATCH CREATION ---------------------

    /**
     * Crée un nouveau match à partir d'un {@link MatchDTO} contenant au moins deux identifiants d'équipes.
     *
     * @param matchDTO DTO contenant les identifiants des équipes participantes
     * @return le {@link Match} créé et persisté
     * @throws ResponseStatusException si une des équipes n'existe pas
     */
    public Match creerMatch(MatchDTO matchDTO) {
        Equipe e1 = equipeService.getById(matchDTO.getIdEquipes().get(0));
        Equipe e2 = equipeService.getById(matchDTO.getIdEquipes().get(1));
        if (e1 == null || e2 == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Une des équipes n'existe pas");

        Match match = new Match();
        match.setEquipe1(e1);
        match.setEquipe2(e2);
        return save(match);
    }

    // --------------------- MATCH STATE ---------------------

    /**
     * Démarre un match : positionne la date de début, le statut ONGOING, initialise la durée de pause
     * et lance le scheduler qui termine le match après la durée prévue.
     *
     * @param id identifiant du match à démarrer
     * @return le {@link Match} mis à jour
     * @throws ResponseStatusException si le match n'existe pas ou n'est pas en état WAITING
     */
    public Match commencerMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        if (match.getStatus() != Match.Status.WAITING) throw new ResponseStatusException(HttpStatus.CONFLICT, "Match déjà commencé ou terminé");

        match.setDateDebut(LocalDateTime.now());
        match.setStatus(Match.Status.ONGOING);
        match.setDureePauseTotale(Duration.ZERO);
        lancerScheduler(match, Duration.ofMinutes(100));
        return save(match);
    }

    /**
     * Met un match en pause : positionne la date de pause, change le statut et annule le scheduler en cours.
     *
     * @param id identifiant du match à mettre en pause
     * @return le {@link Match} mis à jour
     * @throws ResponseStatusException si le match n'existe pas ou n'est pas en cours
     */
    public Match mettreEnPause(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        if (match.getStatus() != Match.Status.ONGOING) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Match pas en cours");

        match.setDatePause(LocalDateTime.now());
        match.setStatus(Match.Status.PAUSED);
        annulerScheduler(match);
        return save(match);
    }

    /**
     * Reprend un match précédemment mis en pause : calcule la durée de pause, met à jour la durée totale
     * et relance (ou termine) le scheduler selon le temps restant.
     *
     * @param id identifiant du match à reprendre
     * @return le {@link Match} mis à jour
     * @throws ResponseStatusException si le match n'existe pas ou n'est pas en pause
     */
    public Match reprendreMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        if (match.getStatus() != Match.Status.PAUSED) throw new ResponseStatusException(HttpStatus.CONFLICT, "Match pas en pause");

        Duration pause = Duration.between(match.getDatePause(), LocalDateTime.now());
        match.setDureePauseTotale(match.getDureePauseTotale().plus(pause));
        match.setDatePause(null);
        match.setStatus(Match.Status.ONGOING);

        Duration dureeJouee = Duration.between(match.getDateDebut(), LocalDateTime.now()).minus(match.getDureePauseTotale());
        Duration remaining = Duration.ofMinutes(100).minus(dureeJouee);

        if (remaining.isNegative() || remaining.isZero()) {
            finirMatchSafe(match);
        } else {
            lancerScheduler(match, remaining);
        }
        return save(match);
    }

    /**
     * Ajoute des points à l'équipe concernée pour un match en cours et enregistre l'action.
     *
     * La méthode vérifie l'existence du match et de l'équipe, que le match est en cours,
     * que le nombre de points ajouté n'est pas nul, et empêche que le score devienne négatif.
     *
     * @param id_match identifiant du match
     * @param id_equipe identifiant de l'équipe qui reçoit les points
     * @param dto DTO contenant le nombre de points à ajouter
     * @return le {@link Match} mis à jour après ajout des points
     * @throws ResponseStatusException en cas d'incohérences (match/équipe inexistants, match non en cours, points invalides)
     */
    public Match ajouterPoint(long id_match, long id_equipe, MatchPointDTO dto) {
        Match match = getById(id_match);
        Equipe equipe = equipeService.getById(id_equipe);

        if (match == null || equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match/équipe n'existe pas");;
        if (match.getStatus() != Match.Status.ONGOING) throw new ResponseStatusException(HttpStatus.CONFLICT, "Match n'est pas en jeu");
        if (dto.getPoint() == 0) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible d'ajouter 0 point");


        if (Objects.equals(equipe.getIdEquipe(), match.getEquipe1().getIdEquipe())) {
            if (match.getScoreEquipe1() + dto.getPoint() <=0)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible de mettre des points négatifs");
            match.setScoreEquipe1(match.getScoreEquipe1() + dto.getPoint());
        } else if (Objects.equals(equipe.getIdEquipe(), match.getEquipe2().getIdEquipe())) {
            if (match.getScoreEquipe2() + dto.getPoint() <=0)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible de mettre des points négatifs");
            match.setScoreEquipe2(match.getScoreEquipe2() + dto.getPoint());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette équipe ne fait pas partie du match");

        actionMatchService.addPoint(id_match, id_equipe, dto);

        checkVictory(match);
        return save(match);
    }

    /**
     * Enregistre une faute pour un joueur d'une équipe dans un match en cours.
     *
     * @param idMatch identifiant du match
     * @param idEquipe identifiant de l'équipe (note : la méthode originale récupérait l'équipe via idMatch, attention aux erreurs potentielles)
     * @param fauteDTO DTO contenant l'information sur la faute
     * @return le {@link Match} mis à jour (rechargé depuis la base)
     * @throws ResponseStatusException si le match/équipe n'existe pas ou si le match n'est pas en cours
     */
    public Match ajouterFaute(long idMatch, long idEquipe, MatchFauteDTO fauteDTO) {
        Match match = getById(idMatch);
        Equipe equipe = equipeService.getById(idMatch);
        if (match == null || equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match/équipe n'existe pas");;
        if (match.getStatus() != Match.Status.ONGOING) throw new ResponseStatusException(HttpStatus.CONFLICT, "Match n'est pas en jeu");

        actionMatchService.addFaute(idMatch, idEquipe, fauteDTO);
        return getById(idMatch);
    }

    // --------------------- CHECK VICTORY ---------------------

    /**
     * Vérifie si un des deux scores atteint la condition de victoire et termine le match en conséquence.
     *
     * @param match le {@link Match} à vérifier
     */
    public void checkVictory(Match match) {
        if (match.getStatus() == Match.Status.FINISHED) return;

        long score1 = match.getScoreEquipe1();
        long score2 = match.getScoreEquipe2();
        long SCORE_MAX = 15;

        if (score1 >= SCORE_MAX && score1 > score2) {
            match.setWinner(match.getEquipe1());
            finirMatchSafe(match);
        }
        else if (score2 >= SCORE_MAX && score2 > score1) {
            match.setWinner(match.getEquipe2());
            finirMatchSafe(match);
        }
    }

    // --------------------- FINIR MATCH ---------------------

    /**
     * Termine un match en toute sécurité : met le statut FINISHED, enregistre la date de fin,
     * annule le scheduler, met à jour le classement et sauvegarde le match.
     *
     * @param match le {@link Match} à terminer
     */
    private void finirMatchSafe(Match match) {
        if (match.getStatus() == Match.Status.FINISHED) return;

        match.setStatus(Match.Status.FINISHED);
        match.setDateFin(LocalDateTime.now());
        annulerScheduler(match);
        classementService.mettreAJourClassement(match);
        save(match);
    }

    /**
     * Termine un match identifié par son id via la logique de terminaison sûre.
     *
     * @param id identifiant du match à terminer
     * @return le {@link Match} terminé
     * @throws ResponseStatusException si le match n'existe pas
     */
    public Match finirMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        finirMatchSafe(match);

        return match;
    }

    // --------------------- SCHEDULER ---------------------

    /**
     * Lance un scheduler qui exécutera la vérification/fin du match après une durée donnée.
     *
     * @param match le {@link Match} pour lequel planifier la tâche
     * @param duree durée restante avant exécution
     */
    private void lancerScheduler(Match match, Duration duree) {
        ScheduledFuture<?> future = scheduler.schedule(() -> checkTime(match.getIdMatch()), duree.toSeconds(), TimeUnit.SECONDS);
        matchSchedulers.put(match.getIdMatch(), future);
    }

    /**
     * Annule le scheduler associé à un match si présent.
     *
     * @param match le {@link Match} dont on souhaite annuler la tâche planifiée
     */
    private void annulerScheduler(Match match) {
        ScheduledFuture<?> future = matchSchedulers.remove(match.getIdMatch());
        if (future != null) future.cancel(false);
    }

    /**
     * Vérifie le temps restant pour un match : si une compensation de pause est présente,
     * elle est replanifiée, sinon le match est terminé.
     *
     * @param idMatch identifiant du match à vérifier
     */
    public void checkTime(long idMatch) {
        Match match = getById(idMatch);
        if (!match.getDureePauseTotale().isZero()) {
            Duration compens = match.getDureePauseTotale();
            match.setDureePauseTotale(Duration.ZERO);
            save(match);
            lancerScheduler(match, compens);
            return;
        }
        finirMatchSafe(match);
    }

}