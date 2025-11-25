package org.ultimateam.apiultimate.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoundRobinSchedulerService {

    private static final int START_HOUR = 9;
    private static final int END_HOUR = 18;

    private static final int MATCH_DURATION_MIN = 90;
    private static final int BREAK_DURATION_MIN = 10;
    private static final int SLOT_DURATION_MIN = MATCH_DURATION_MIN + BREAK_DURATION_MIN;

    private static final int MAX_FIELDS = 5;

    // ================================
    // PUBLIC METHOD – MAIN ENTRY POINT
    // ================================
    public List<Match> generateSchedule(
            List<Equipe> equipes,
            LocalDate startDate,
            LocalDate endDate,
            boolean homeAndAway
    ) {
        if (equipes.size() < 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Il faut au moins 2 équipes.");
        }

        // 1) Générer les paires Round Robin
        List<Pair<Equipe, Equipe>> pairs = generateRoundRobinPairs(equipes, homeAndAway);

        // 2) Générer les créneaux horaires
        List<LocalTime> timeSlots = generateTimeSlots();

        // 3) Vérifier la période disponible
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

        // 4) Répartir les matchs
        List<Match> result = new ArrayList<>();
        int matchIndex = 0;

        LocalDate currentDay = startDate;

        while (matchIndex < totalMatches) {

            Set<Equipe> teamsUsedToday = new HashSet<>();

            for (LocalTime time : timeSlots) {
                for (int field = 1; field <= MAX_FIELDS; field++) {

                    if (matchIndex >= totalMatches) break;

                    Pair<Equipe, Equipe> pair = pairs.get(matchIndex);
                    Equipe A = pair.getLeft();
                    Equipe B = pair.getRight();

                    // Une équipe ne joue pas deux fois dans la journée
                    if (teamsUsedToday.contains(A) || teamsUsedToday.contains(B)) {
                        continue;
                    }

                    Match match = new Match();
                    match.setEquipe1(A);
                    match.setEquipe2(B);
                    match.setDateMatch(LocalDateTime.of(currentDay, time));
                    match.setTerrain(field);

                    result.add(match);

                    teamsUsedToday.add(A);
                    teamsUsedToday.add(B);

                    matchIndex++;
                }
            }

            currentDay = currentDay.plusDays(1);

            if (currentDay.isAfter(endDate))
                break;
        }

        return result;
    }

    // ================================
    //  ROUND ROBIN PAIR GENERATION
    // ================================
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

            // rotation du tableau sauf premier élément
            Equipe last = rotated.remove(rotated.size() - 1);
            rotated.add(1, last);
        }

        return matches;
    }

    // ================================
    //  TIME SLOT GENERATOR
    // ================================
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
}