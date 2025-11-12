package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.model.Match;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final EquipeService equipeService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    /**
     * Constructeur pour l'injection des dépendances MatchRepository et EquipeService.
     *
     * @param matchRepository Le repository pour l'accès aux données des matchs.
     * @param equipeService Le service pour la gestion des équipes.
     */
    public MatchService(MatchRepository matchRepository, EquipeService equipeService) {
        this.matchRepository = matchRepository;
        this.equipeService = equipeService;
    }

    /**
     * Récupère la liste de tous les matchs.
     *
     * @return Un Itérable contenant tous les matchs.
     */
    public Iterable<Match> getAll() { return matchRepository.findAll();}

    /**
     * Récupère un match spécifique par son identifiant (ID).
     *
     * @param id L'identifiant du match à rechercher.
     * @return Le match trouvé, ou null si aucun match ne correspond à l'ID.
     */
    public Match getById(Long id) { return matchRepository.findById(id).orElse(null);}
    public List<Match> getStarted(){
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findByDateDebutIsNotNullAndDateFinIsNull();
    }
    public List<Match> getNotStarted(){
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findByDateDebutIsNull();
    }
    public Match save(Match match) { return matchRepository.save(match);}

    /**
     * Supprime un match de la base de données en utilisant son identifiant.
     *
     * @param id L'identifiant du match à supprimer.
     */
    public void deleteById(Long id) { matchRepository.deleteById(id);}

    /**
     * Crée un nouveau match en associant deux équipes via leurs identifiants.
     * Vérifie si les équipes existent avant de créer le match.
     *
     * @param id_equipe1 L'identifiant de la première équipe.
     * @param id_equipe2 L'identifiant de la seconde équipe.
     * @return Le nouveau match créé et sauvegardé, avec le statut WAITING.
     */
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


    /**
     * Démarre un match existant.
     * Met le statut du match à ONGOING, enregistre la date de début
     * et lance une tâche planifiée pour vérifier les conditions de victoire.
     *
     * @param id L'identifiant du match à démarrer.
     * @return Le match mis à jour (statut ONGOING).
     */
    public Match commencerMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.ONGOING || match.getStatus() == Match.Status.PAUSED)
            throw new IllegalStateException("Le match est déjà commencé");
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Impossible de commencer un match terminé");

        match.setDateDebut(LocalDateTime.now());
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

    /**
     * Termine un match.
     * Met le statut du match à FINISHED, enregistre la date de fin
     * et arrête la tâche planifiée (scheduler) associée à ce match.
     *
     * @param id L'identifiant du match à terminer.
     * @return Le match mis à jour (statut FINISHED).
     */
    public Match finirMatch(long id) {
        Match match = getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Le match est déjà terminé");

        match.setDateFin(LocalDateTime.now());
        match.setStatus(Match.Status.FINISHED);
        scheduler.shutdownNow();
        return save(match);
    }


    /**
     * Met en pause un match qui est actuellement en cours (ONGOING).
     * Enregistre l'heure de début de la pause et change le statut à PAUSED.
     *
     * @param id L'identifiant du match à mettre en pause.
     * @return Le match mis à jour (statut PAUSED).
     */
    public Match mettreEnPause(long id) {
        Match match = getById(id);
        if (match.getStatus() != Match.Status.ONGOING)
            throw new IllegalStateException("Impossible de mettre en pause un match non démarré ou déjà en pause");

        match.setDate_pause(LocalDateTime.now());
        match.setStatus(Match.Status.PAUSED);
        return save(match);
    }

    /**
     * Reprend un match qui était en pause (PAUSED).
     * Calcule la durée de la pause, l'ajoute à la durée de pause totale,
     * réinitialise l'heure de pause et repasse le statut à ONGOING.
     *
     * @param id L'identifiant du match à reprendre.
     * @return Le match mis à jour (statut ONGOING).
     */
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

    /**
     * Ajoute un point à une équipe spécifique participant à un match.
     * Vérifie ensuite si cette action déclenche une condition de victoire.
     *
     * @param id_match L'identifiant du match concerné.
     * @param id_equipe L'identifiant de l'équipe qui marque le point.
     * @return Le match mis à jour avec le nouveau score.
     */
    public Match ajouterPoint(long id_match, long id_equipe) {
        Match match = getById(id_match);
        Equipe equipe = equipeService.getById(id_equipe);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Impossible d'ajouter un point, le match est terminé");

        if (Objects.equals(equipe.getIdEquipe(), match.getEquipe1().getIdEquipe())) {
            match.setScore_equipe1(match.getScore_equipe1() + 1);
        } else if (Objects.equals(equipe.getIdEquipe(), match.getEquipe2().getIdEquipe())) {
            match.setScore_equipe2(match.getScore_equipe2() + 1);
        } else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }

        checkVictory(match);
        return save(match);
    }

    /**
     * Retire un point à une équipe spécifique participant à un match.
     * Le score de l'équipe ne peut pas descendre en dessous de 0.
     *
     * @param id_match L'identifiant du match concerné.
     * @param id_equipe L'identifiant de l'équipe à qui retirer le point.
     * @return Le match mis à jour avec le nouveau score.
     */
    public Match retirerPoint(long id_match, long id_equipe) {
        Match match = getById(id_match);
        Equipe equipe = equipeService.getById(id_equipe);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (match.getStatus() == Match.Status.FINISHED)
            throw new IllegalStateException("Impossible de retirer un point, le match est terminé");

        if (Objects.equals(equipe.getIdEquipe(), match.getEquipe1().getIdEquipe())) {
            match.setScore_equipe1(Math.max(0, match.getScore_equipe1() - 1));
        } else if (Objects.equals(equipe.getIdEquipe(), match.getEquipe2().getIdEquipe())) {
            match.setScore_equipe2(Math.max(0, match.getScore_equipe2() - 1));
        } else {
            throw new IllegalArgumentException("Cette équipe ne fait pas partie du match");
        }

        checkVictory(match);
        return save(match);
    }

    /**
     * Vérifie les conditions de victoire pour un match en cours.
     * Le match est terminé si une équipe atteint 15 points ou si la durée
     * de jeu effective (hors pauses) atteint 90 minutes.
     *
     * @param match Le match à vérifier.
     */
    public void checkVictory(Match match) {
        if (match.getStatus() != Match.Status.ONGOING)
            return;

        // Victoire par score
        if (match.getScore_equipe1() >= 15 || match.getScore_equipe2() >= 15) {
            finirMatch(match.getIdMatch());
            return;
        }

        // Victoire par durée (90 min sans les pauses)
        Duration dureeTotale = Duration.between(match.getDateDebut(), LocalDateTime.now()).minus(match.getDureePauseTotale());

        if (dureeTotale.compareTo(Duration.ofMinutes(90)) >= 0) {
            finirMatch(match.getIdMatch());
        }

    }

}
