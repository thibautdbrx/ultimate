package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.model.Match;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final EquipeService equipeService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public MatchService(MatchRepository matchRepository, EquipeService equipeService) {
        this.matchRepository = matchRepository;
        this.equipeService = equipeService;
    }
    public Iterable<Match> getAll() { return matchRepository.findAll();}
    public Match getById(Long id) { return matchRepository.findById(id).orElse(null);}
    public Match save(Match match) { return matchRepository.save(match);}
    public void deleteById(Long id) { matchRepository.deleteById(id);}

    public Match creerMatch(long id_equipe1, long id_equipe2) {
        Equipe equipe1 = equipeService.getById(id_equipe1);
        Equipe equipe2 = equipeService.getById(id_equipe2);

        if (equipe1 == null || equipe2 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Une des équipes n'existe pas");
        }

        Match match = new Match();
        match.setEquipe1(equipe1);
        match.setEquipe2(equipe2);

        // Sauvegarde le match
        return save(match);
    }


    public Match commencerMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.ONGOING || match.getStatus() == Match.Status.PAUSED)
            throw new IllegalStateException("Le match est déjà commencé");
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Impossible de commencer un match terminé");

        match.setDate_debut(LocalDateTime.now());
        match.setStatus(Match.Status.ONGOING);
        save(match);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkVictory(match);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
        return save(match);
    }

    public Match finirMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Le match est déjà terminé");

        match.setDate_fin(LocalDateTime.now());
        match.setStatus(Match.Status.FINISHED);
        scheduler.shutdownNow();
        return save(match);
    }

    public Match mettreEnPause(long id) {
        Match match = getById(id);
        if (match.getStatus() != Match.Status.ONGOING)
            throw new IllegalStateException("Impossible de mettre en pause un match non démarré ou déjà en pause");

        match.setDate_pause(LocalDateTime.now());
        match.setStatus(Match.Status.PAUSED);
        return save(match);
    }

    public Match reprendreMatch(long id) {
        Match match = getById(id);
        if (match.getStatus() != Match.Status.PAUSED)
            throw new IllegalStateException("Impossible de reprendre un match qui n'est pas en pause");

        Duration nouvellePause = Duration.between(match.getDate_pause(), LocalDateTime.now());
        match.setDureePauseTotale(match.getDureePauseTotale().plus(nouvellePause));
        match.setDate_pause(null);
        match.setStatus(Match.Status.ONGOING);
        return save(match);
    }

    public Match ajouterPoint(long id_match, long id_equipe) {
        Match match = getById(id_match);
        Equipe equipe = equipeService.getById(id_equipe);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Impossible d'ajouter un point, le match est terminé");

        if (Objects.equals(equipe.getId_equipe(), match.getEquipe1().getId_equipe())) {
            match.setScore_equipe1(match.getScore_equipe1() + 1);
        } else if (Objects.equals(equipe.getId_equipe(), match.getEquipe2().getId_equipe())) {
            match.setScore_equipe2(match.getScore_equipe2() + 1);
        } else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }

        checkVictory(match);
        return save(match);
    }

    public Match retirerPoint(long id_match, long id_equipe) {
        Match match = getById(id_match);
        Equipe equipe = equipeService.getById(id_equipe);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Impossible de retirer un point, le match est terminé");

        if (Objects.equals(equipe.getId_equipe(), match.getEquipe1().getId_equipe())) {
            match.setScore_equipe1(Math.max(0, match.getScore_equipe1() - 1));
        } else if (Objects.equals(equipe.getId_equipe(), match.getEquipe2().getId_equipe())) {
            match.setScore_equipe2(Math.max(0, match.getScore_equipe2() - 1));
        } else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }

        checkVictory(match);
        return save(match);
    }

    public void checkVictory(Match match) {
        if (match.getStatus() != Match.Status.ONGOING)
            return;

        // Victoire par score
        if (match.getScore_equipe1() >= 15 || match.getScore_equipe2() >= 15) {
            finirMatch(match.getMatchId());
            return;
        }

        // Victoire par durée (90 min sans les pauses)
        Duration dureeTotale = Duration.between(match.getDate_debut(), LocalDateTime.now()).minus(match.getDureePauseTotale());

        if (dureeTotale.compareTo(Duration.ofMinutes(90)) >= 0) {
            finirMatch(match.getMatchId());
        }

    }

}



