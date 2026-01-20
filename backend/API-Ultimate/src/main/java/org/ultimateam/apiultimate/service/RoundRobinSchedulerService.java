package org.ultimateam.apiultimate.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ScheduleResult;
import org.ultimateam.apiultimate.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RoundRobinSchedulerService {

    /**
     * Petite record interne représentant un bloc de temps (début et fin)
     */
    public record Interval(LocalDateTime start, LocalDateTime end) {}

    // Constantes de configuration pour la génération du planning
    private static final int START_HOUR = 9;   // Début des matchs
    private static final int END_HOUR = 18;    // Fin des matchs

    private static final int MATCH_DURATION_MIN = 100;  // Durée d'un match
    private static final int BREAK_DURATION_BETWEEN_MATCHES_MIN = 10;  // Pause entre deux matchs
    private static final int SLOT_DURATION_MIN = MATCH_DURATION_MIN + BREAK_DURATION_BETWEEN_MATCHES_MIN;

    /**
     * autoBlocks garde la liste des créneaux où chaque équipe est automatiquement bloquée
     * (parce qu'un match lui a déjà été attribué).
     */
    Map<Equipe, List<Interval>> autoBlocks = new HashMap<>();
    Map<Terrain, List<Interval>> terrainAutoBlocks = new HashMap<>();


    /**
     * Méthode principale qui génère un calendrier Round Robin pour un tournoi.
     */
    public ScheduleResult generateSchedule(
            List<Equipe> equipes,
            List<Terrain> terrainsDisponibles,
            LocalDate startDate,
            LocalDate endDate,
            boolean homeAndAway, // aller-retour ou non
            List<Indisponibilite> indisponibilites,
            List<IndisponibiliteTerrain> indisponibilitesTerrains
    ) {
        terrainAutoBlocks.clear();
        //autoBlocks.clear();
        // Objet qui contiendra les matchs + indisponibilités renvoyés
        ScheduleResult result =
                new ScheduleResult(new ArrayList<Match>(), indisponibilites, indisponibilitesTerrains);

        if (equipes.size() < 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Il faut au moins 2 équipes.");
        }

        // Génère toutes les paires de matchs selon le round robin
        List<Pair<Equipe, Equipe>> pairs = generateRoundRobinPairs(equipes, homeAndAway);

        // Créneaux horaires dans la journée
        List<LocalTime> timeSlots = generateTimeSlots();

        // Calcul du nombre total de matchs et de créneaux disponibles
        int nbTerrains = terrainsDisponibles.size();
        long totalMatches = pairs.size();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        long slotsPerDay = (long) timeSlots.size() * nbTerrains;
        long totalPossibleSlots = slotsPerDay * totalDays;

        // Vérification que la période est suffisante
        if (totalMatches > totalPossibleSlots) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Période insuffisante : " + totalMatches +
                            " matchs à jouer mais seulement " +
                            totalPossibleSlots + " créneaux disponibles."
            );
        }

        int matchIndex = 0;
        LocalDate currentDay = startDate;

        // Boucle principale qui tente de placer tous les matchs
        while (matchIndex < totalMatches) {

            for (LocalTime time : timeSlots) {
                for (Terrain terrain : terrainsDisponibles) {

                    if (matchIndex >= totalMatches) break;

                    LocalDateTime dateMatch = LocalDateTime.of(currentDay, time);

                    if (!isTerrainAvailable(terrain, dateMatch, indisponibilitesTerrains)) {
                        continue; // Le terrain est occupé ou indisponible, on passe au suivant
                    }

                    // On récupère la paire A vs B
                    Pair<Equipe, Equipe> pair = pairs.get(matchIndex);
                    Equipe A = pair.getLeft();
                    Equipe B = pair.getRight();
                    if (A == null || B == null) break;


                    //System.out.println(dateMatch);

                    // Vérifie si A et B sont disponibles (indispos déclarées + autoBlocks)
                    boolean Aok = isAvailable(A, dateMatch, autoBlocks, indisponibilites);
                    boolean Bok = isAvailable(B, dateMatch, autoBlocks, indisponibilites);

                    if (!Aok || !Bok) {
                        continue; // on prend le prochain créneau
                    }

                    // Création du match
                    Match match = new Match();
                    match.setEquipe1(A);
                    match.setEquipe2(B);
                    match.setDateMatch(dateMatch);
                    match.setTerrain(terrain);

                    result.addMatch(match);

                    // On bloque les équipes pendant ce créneau
                    blockEquipe(A, dateMatch, autoBlocks, result);
                    blockEquipe(B, dateMatch, autoBlocks, result);

                    blockTerrain(terrain, dateMatch, result);

                    matchIndex++;
                }
            }

            // Passage au jour suivant
            currentDay = currentDay.plusDays(1);

            if (currentDay.isAfter(endDate)) {
                break;
            }
        }

        return result;
    }

    private boolean isTerrainAvailable(Terrain terrain, LocalDateTime dateMatch, List<IndisponibiliteTerrain> declaredBlocks) {
        LocalDateTime start = dateMatch;
        LocalDateTime end = dateMatch.plusMinutes(SLOT_DURATION_MIN);

        if (declaredBlocks != null) {
            for (IndisponibiliteTerrain ind : declaredBlocks) {
                if (ind.getTerrain() != null &&
                        java.util.Objects.equals(ind.getTerrain().getIdTerrain(), terrain.getIdTerrain())) {

                    boolean isOverlapping = end.isBefore(ind.getDateDebutIndisponibilite());
                    isOverlapping = isOverlapping || start.isAfter(ind.getDateFinIndisponibilite());
                    System.out.println("Chevauchement :" + !isOverlapping + " \n date fin indispo : " + ind.getDateFinIndisponibilite() + "date debut match : " + start
                    + "\n date debut indispo : " + ind.getDateDebutIndisponibilite() + "date fin match : " + end + "\n \n ");


                    if (!isOverlapping) {
                        System.out.println("Terrain " + terrain.getIdTerrain() + " bloqué par indispo le " + dateMatch);
                        return false;
                    }
                }
            }
        }

        // 2. Vérification des indisponibilités automatiques (Matchs qu'on vient de placer)
        List<Interval> blocks = terrainAutoBlocks.getOrDefault(terrain, new ArrayList<>());
        for (Interval b : blocks) {
            // Même logique de chevauchement
            boolean isOverlapping = start.isBefore(b.end()) && end.isAfter(b.start());
            if (isOverlapping) {
                return false;
            }
        }

        return true;
    }

    private void blockTerrain(Terrain terrain, LocalDateTime dateMatch, ScheduleResult result) {
        terrainAutoBlocks.putIfAbsent(terrain, new ArrayList<>());
        terrainAutoBlocks.get(terrain).add(new Interval(dateMatch, dateMatch.plusMinutes(SLOT_DURATION_MIN)));
        result.addIndisponibiliteTerrain(new IndisponibiliteTerrain(dateMatch,dateMatch.plusMinutes(SLOT_DURATION_MIN),terrain));
    }


    /**
     * Génère les paires Round Robin :
     * - Round robin algorithm classique avec rotation des équipes
     * - Gère optionnellement les matchs aller-retour
     */
    private List<Pair<Equipe, Equipe>> generateRoundRobinPairs(List<Equipe> equipes, boolean homeAndAway) {
        List<Pair<Equipe, Equipe>> matches = new ArrayList<>();
        List<Equipe> rot = new ArrayList<>(equipes);

        // Si nombre impair d'équipes, ajoute une "fantôme" (null)
        if (rot.size() % 2 == 1) rot.add(null);

        int n = rot.size();

        for (int round = 0; round < n - 1; round++) {
            for (int i = 0; i < n / 2; i++) {
                Equipe a = rot.get(i);
                Equipe b = rot.get(n - 1 - i);

                if (a != null && b != null) {
                    matches.add(Pair.of(a, b));
                    if (homeAndAway) matches.add(Pair.of(b, a));
                }
            }
            // Rotation : fixe la première équipe et tourne les autres
            Equipe last = rot.remove(rot.size() - 1);
            rot.add(1, last);
        }

        return matches;
    }






    /**
     * Génère tous les créneaux horaires possibles entre 9h et 18h,
     * espacés de la durée d'un match + pause.
     */
    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();

        LocalTime current = LocalTime.of(START_HOUR, 0);
        LocalTime end = LocalTime.of(END_HOUR, 0);

        while (current.plusMinutes(SLOT_DURATION_MIN).isBefore(end.plusMinutes(1))) {
            slots.add(current);
            current = current.plusMinutes(SLOT_DURATION_MIN);
        }

        return slots;
    }

    /**
     * Vérifie si une équipe est disponible pour un créneau donné.
     * - Vérifie les indisponibilités déclarées
     * - Vérifie les créneaux déjà bloqués automatiquement
     */
    private boolean isAvailable(
            Equipe equipe,
            LocalDateTime dateMatch,
            Map<Equipe, List<Interval>> autoBlocks,
            List<Indisponibilite> declaredBlocks
    ) {
        LocalDateTime start = dateMatch;
        LocalDateTime end = dateMatch.plusMinutes(SLOT_DURATION_MIN);

        // Vérification indisponibilités déclarées
        for (Indisponibilite ind : declaredBlocks) {
            if (ind.getEquipe() != null) {
                if (ind.getEquipe().equals(equipe)) {
                    boolean noOverlap =
                            end.isBefore(ind.getDateDebutIndisponibilite()) ||
                                    start.isAfter(ind.getDateFinIndisponibilite());

                    if (!noOverlap) return false;
                }
            }
        }

        // Vérification indispos générées par les matchs déjà placés
        List<Interval> blocks = autoBlocks.getOrDefault(equipe, new ArrayList<>());
        for (Interval b : blocks) {
            boolean noOverlap =
                    end.isBefore(b.start()) ||
                            start.isAfter(b.end());

            if (!noOverlap) return false;
        }

        return true;
    }

    /**
     * Bloque automatiquement une équipe pour un créneau (lorsqu'un match lui est attribué)
     */
    private void blockEquipe(Equipe equipe, LocalDateTime dateMatch, Map<Equipe, List<Interval>> autoBlocks, ScheduleResult result) {
        autoBlocks.putIfAbsent(equipe, new ArrayList<>());
        autoBlocks.get(equipe).add(new Interval(dateMatch, dateMatch.plusMinutes(SLOT_DURATION_MIN)));

        // Ajoute directement dans le résultat
        //System.out.println(dateMatch);
        result.addIndisponibilite(new Indisponibilite(dateMatch, dateMatch.plusMinutes(SLOT_DURATION_MIN), equipe));
        //System.out.println(result.getIndisponibilites().get(0).getDateDebutIndisponibilite());

    }

}
