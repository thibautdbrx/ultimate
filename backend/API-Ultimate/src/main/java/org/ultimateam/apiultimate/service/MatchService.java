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
import org.ultimateam.apiultimate.repository.TerrainRepository;


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
    private final TerrainRepository terrainRepository;
    private final TerrainService terrainService;


    public MatchService(MatchRepository matchRepository, EquipeService equipeService, TournoisService tournoisService, ClassementService classementService, JoueurRepository joueurRepository, JoueurService joueurservice, JoueurService joueurService, ActionMatchService actionMatchService, TerrainRepository terrainRepository, TerrainService terrainService) {
        this.matchRepository = matchRepository;
        this.equipeService = equipeService;
        this.tournoisService = tournoisService;
        this.classementService = classementService;
        this.joueurRepository = joueurRepository;
        this.joueurService = joueurService;
        this.actionMatchService = actionMatchService;
        this.terrainRepository = terrainRepository;
        this.terrainService = terrainService;
    }

    // --------------------- BASIC CRUD ---------------------
    public Match getById(Long id) { return matchRepository.findById(id).orElse(null); }
    public Match save(Match match) { return matchRepository.save(match); }
    public void deleteById(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        }
        matchRepository.deleteById(id);
    }

    public Iterable<Match> getAll() { return matchRepository.findAll(); }
    public List<Match> getStarted() { return matchRepository.findByDateDebutIsNotNullAndDateFinIsNull(); }
    public List<Match> getNotStarted() {
        LocalDateTime dateBefore = LocalDateTime.now().minusDays(2);
        return matchRepository.findByDateMatchAfterAndDateDebutIsNull(dateBefore);
    }
    //public List<Match> getNotStarted() { return matchRepository.findByDateDebutIsNull(); }
    public List<Match> getFinished() { return matchRepository.findByDateFinIsNotNull(); }

    public List<Match> getMatchesByEquipe(long idJoueur) {
        Joueur joueur = joueurRepository.findById(idJoueur).orElse(null);
        if (joueur == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le joueur n'existe pas");
        if (joueur.getEquipe() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le joueur n'a pas d'équipe");
        return matchRepository.findMatchesByEquipe(joueur.getEquipe().getIdEquipe());
    }

    public List<Match> getMatchesByTerrain(long idTerrain) { return matchRepository.findByTerrain_Id_terrain(idTerrain); }


    // --------------------- MATCH CREATION ---------------------
    public Match creerMatch(MatchDTO matchDTO) {
        Equipe e1 = equipeService.getById(matchDTO.getIdEquipes().get(0));
        Equipe e2 = equipeService.getById(matchDTO.getIdEquipes().get(1));
        if (e1 == null || e2 == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Une des équipes n'existe pas");

        Match match = new Match();
        match.setEquipe1(e1);
        match.setEquipe2(e2);
        match.setTerrain(terrainService.getById(matchDTO.getIdTerrain()));
        return save(match);
    }

    // --------------------- MATCH STATE ---------------------
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

    public Match mettreEnPause(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        if (match.getStatus() != Match.Status.ONGOING) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Match pas en cours");

        match.setDatePause(LocalDateTime.now());
        match.setStatus(Match.Status.PAUSED);
        annulerScheduler(match);
        return save(match);
    }

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

        actionMatchService.addPoint(id_match, id_equipe, dto, match.getDatePause());

        checkVictory(match);
        return save(match);
    }

    public Match ajouterFaute(long idMatch, long idEquipe, MatchFauteDTO fauteDTO) {
        Match match = getById(idMatch);
        Equipe equipe = equipeService.getById(idMatch);
        if (match == null || equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match/équipe n'existe pas");;
        if (match.getStatus() != Match.Status.ONGOING) throw new ResponseStatusException(HttpStatus.CONFLICT, "Match n'est pas en jeu");

        actionMatchService.addFaute(idMatch, idEquipe, fauteDTO, match.getDatePause());
        return getById(idMatch);
    }

    // --------------------- CHECK VICTORY ---------------------
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
    private void finirMatchSafe(Match match) {
        if (match.getStatus() == Match.Status.FINISHED) return;

        match.setStatus(Match.Status.FINISHED);
        match.setDateFin(LocalDateTime.now());
        annulerScheduler(match);
        classementService.mettreAJourClassement(match);
        save(match);
    }

    public Match finirMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas");
        finirMatchSafe(match);

        return match;
    }

    // --------------------- SCHEDULER ---------------------
    private void lancerScheduler(Match match, Duration duree) {
        ScheduledFuture<?> future = scheduler.schedule(() -> checkTime(match.getIdMatch()), duree.toSeconds(), TimeUnit.SECONDS);
        matchSchedulers.put(match.getIdMatch(), future);
    }

    private void annulerScheduler(Match match) {
        ScheduledFuture<?> future = matchSchedulers.remove(match.getIdMatch());
        if (future != null) future.cancel(false);
    }

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