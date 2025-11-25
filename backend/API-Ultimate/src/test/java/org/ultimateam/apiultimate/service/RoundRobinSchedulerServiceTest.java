package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinSchedulerServiceTest {

    private RoundRobinSchedulerService scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new RoundRobinSchedulerService();
    }

    @Test
    void testRoundRobinSimple() {
        // 4 équipes => 6 matchs aller
        List<Equipe> equipes = List.of(
                new Equipe("A"),
                new Equipe("B"),
                new Equipe("C"),
                new Equipe("D")
        );

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 10);

        List<Match> matchs = scheduler.generateSchedule(equipes, start, end, false);

        // --- Vérif 1 : bon nombre de matchs ---
        assertEquals(6, matchs.size(), "Round Robin aller simple pour 4 équipes = 6 matchs");

        // --- Vérif 2 : personne ne joue deux fois le même jour ---
        Map<LocalDate, Set<String>> teamsByDay = new HashMap<>();

        for (Match m : matchs) {
            LocalDate d = m.getDateMatch().toLocalDate();

            teamsByDay.putIfAbsent(d, new HashSet<>());
            Set<String> teamsToday = teamsByDay.get(d);

            assertFalse(teamsToday.contains(m.getEquipe1().getNomEquipe()),
                    "Une équipe joue deux fois le même jour");
            assertFalse(teamsToday.contains(m.getEquipe2().getNomEquipe()),
                    "Une équipe joue deux fois le même jour");

            teamsToday.add(m.getEquipe1().getNomEquipe());
            teamsToday.add(m.getEquipe2().getNomEquipe());
        }

        // --- Vérif 3 : tous les matchs dans la bonne journée & horaires ---
        for (Match m : matchs) {
            LocalDateTime dt = m.getDateMatch();

            assertFalse(dt.toLocalDate().isBefore(start));
            assertFalse(dt.toLocalDate().isAfter(end));

            LocalTime time = dt.toLocalTime();
            assertTrue(time.isAfter(LocalTime.of(8, 59)), "Match avant 9h");
            assertTrue(time.isBefore(LocalTime.of(18, 1)), "Match après 18h");

            assertTrue(m.getTerrain() >= 1 && m.getTerrain() <= 5,
                    "Terrain invalide (doit être entre 1 et 5)");
        }
    }

    @Test
    void testRoundRobinHomeAway() {
        // 3 équipes -> 3 matchs aller, 3 matchs retour
        List<Equipe> equipes = List.of(
                new Equipe("A"),
                new Equipe("B"),
                new Equipe("C")
        );

        LocalDate start = LocalDate.of(2025, 2, 1);
        LocalDate end = LocalDate.of(2025, 2, 15);

        List<Match> matchs = scheduler.generateSchedule(equipes, start, end, true);

        // 3 équipes -> 3 matchs aller + 3 retours = 6 matchs
        assertEquals(6, matchs.size());
    }
}