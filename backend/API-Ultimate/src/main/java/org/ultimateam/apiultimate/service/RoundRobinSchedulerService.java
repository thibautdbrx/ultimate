package org.ultimateam.apiultimate.service;

import org.antlr.v4.runtime.misc.Interval;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RoundRobinSchedulerService {

    public record Interval(LocalDateTime start, LocalDateTime end) {}

    private static final int START_HOUR = 9;
    private static final int END_HOUR = 18;

    private static final int MATCH_DURATION_MIN = 90;
    private static final int BREAK_DURATION_MIN = 10;
    private static final int SLOT_DURATION_MIN = MATCH_DURATION_MIN + BREAK_DURATION_MIN;

    private static final int MAX_FIELDS = 5;

    Map<Equipe, List<Interval>> autoBlocks = new HashMap<>();


    public List<Match> generateSchedule(
            List<Equipe> equipes,
            LocalDate startDate,
            LocalDate endDate,
            boolean homeAndAway,
            List<Indisponibilite> indisponibilites
    ) {
        if (equipes.size() < 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Il faut au moins 2 équipes.");
        }

        List<Pair<Equipe, Equipe>> pairs = generateRoundRobinPairs(equipes, homeAndAway);

        List<LocalTime> timeSlots = generateTimeSlots();

        long totalMatches = pairs.size();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        long slotsPerDay = (long) timeSlots.size() * MAX_FIELDS;
        long totalPossibleSlots = slotsPerDay * totalDays;

        if (totalMatches > totalPossibleSlots) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Période insuffisante : " + totalMatches + " matchs à jouer mais seulement "
                            + totalPossibleSlots + " créneaux disponibles."
            );
        }

        List<Match> result = new ArrayList<>();
        int matchIndex = 0;

        LocalDate currentDay = startDate;

        while (matchIndex < totalMatches) {

            for (LocalTime time : timeSlots) {
                for (int field = 1; field <= MAX_FIELDS; field++) {

                    if (matchIndex >= totalMatches) break;

                    Pair<Equipe, Equipe> pair = pairs.get(matchIndex);
                    Equipe A = pair.getLeft();
                    Equipe B = pair.getRight();

                    LocalDateTime dateMatch = LocalDateTime.of(currentDay, time);

                    // Vérifier indisponibilité déclarée + bloquée
                    boolean Aok = isAvailable(A, dateMatch, autoBlocks, indisponibilites);
                    boolean Bok = isAvailable(B, dateMatch, autoBlocks, indisponibilites);

                    if (!Aok || !Bok) {
                        continue; // on essaie le créneau suivant
                    }

                    // Création du match
                    Match match = new Match();
                    match.setEquipe1(A);
                    match.setEquipe2(B);
                    match.setDateMatch(dateMatch);
                    match.setTerrain(field);

                    result.add(match);

                    // On bloque les équipes pour ce créneau
                    blockEquipe(A, dateMatch, autoBlocks);
                    blockEquipe(B, dateMatch, autoBlocks);

                    matchIndex++;
                }
            }

            currentDay = currentDay.plusDays(1);

            if (currentDay.isAfter(endDate)) {
                break; // fin du calendrier
            }
        }

        return result;
    }

    private List<Pair<Equipe, Equipe>> generateRoundRobinPairs(List<Equipe> equipes, boolean homeAndAway) {
        List<Pair<Equipe, Equipe>> matches = new ArrayList<>();
        int n = equipes.size();

        List<Equipe> rotated = new ArrayList<>(equipes);

        if (n % 2 == 1) {
            rotated.add(null); // équipe fantôme
        }

        int rounds = rotated.size() - 1;

        for (int round = 0; round < rounds; round++) {
            for (int i = 0; i < rotated.size() / 2; i++) {
                Equipe a = rotated.get(i);
                Equipe b = rotated.get(rotated.size() - 1 - i);

                if (a != null && b != null) {
                    matches.add(Pair.of(a, b));
                    if (homeAndAway) {
                        matches.add(Pair.of(b, a));
                    }
                }
            }

            Equipe last = rotated.remove(rotated.size() - 1);
            rotated.add(1, last);
        }

        return matches;
    }

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

    private boolean isAvailable(
            Equipe equipe,
            LocalDateTime dateMatch,
            Map<Equipe, List<Interval>> autoBlocks,
            List<Indisponibilite> declaredBlocks
    ) {
        LocalDateTime start = dateMatch;
        LocalDateTime end = dateMatch.plusMinutes(SLOT_DURATION_MIN);

        // indispo déclarées
        for (Indisponibilite ind : declaredBlocks) {
            if (ind.getEquipe().equals(equipe)) {
                if (!(end.isBefore(ind.getDateDebutIndisponibilite()) || start.isAfter(ind.getDateFinIndisponibilite()))) {
                    return false;
                }
            }
        }

        // indispo générées par les autres matchs déjà planifiés
        List<Interval> blocks = autoBlocks.getOrDefault(equipe, List.of());
        for (Interval b : blocks) {
            if (!(end.isBefore(b.start()) || start.isAfter(b.end()))) {
                return false;
            }
        }

        return true;
    }

    private void blockEquipe(
            Equipe equipe,
            LocalDateTime dateMatch,
            Map<Equipe, List<Interval>> autoBlocks
    ) {
        autoBlocks.putIfAbsent(equipe, new ArrayList<>());
        autoBlocks.get(equipe).add(
                new Interval(dateMatch, dateMatch.plusMinutes(SLOT_DURATION_MIN))
        );
    }
}