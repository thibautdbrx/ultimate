package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Match;
import org.apache.commons.lang3.tuple.Pair;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class RoundRobinSchedulerServiceTest {


    private static final int MATCH_DURATION_MIN = 90;  // Durée d'un match
    private static final int BREAK_DURATION_MIN = 10;  // Pause entre deux matchs
    private static final int SLOT_DURATION_MIN = MATCH_DURATION_MIN + BREAK_DURATION_MIN;
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


        List<Indisponibilite> indispo = new ArrayList<>();
        indispo.add(new Indisponibilite(LocalDateTime.of(2025, 1, 1, 14, 30),
                LocalDateTime.of(2025, 1, 1, 15, 30),
                equipes.get(1)));

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 2);

        TournoisService.ScheduleResult result = scheduler.generateSchedule(equipes, start, end, false, indispo);
        //System.out.println(result.getIndisponibilites().get(0).getDateDebutIndisponibilite());
        //System.out.println(result.getMatchs().get(0).getDateMatch());

        List<Match> matchs = result.getMatchs();
        //System.out.println(matchs);
        List<Indisponibilite> indisponibilites = result.getIndisponibilites();
        //System.out.println(indisponibilites.get(0).getDateDebutIndisponibilite());

        //System.out.println(indisponibilites);

        // --- Vérif 1 : bon nombre de matchs ---
        assertEquals(6, matchs.size(), "Round Robin aller simple pour 4 équipes = 6 matchs");


        /*
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

         */


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

        // --- Vérif 4 : tous les matchs ne sont pas pendant des indisponnibiltiés ---
        for (Match m : matchs) {
            LocalDateTime dt_debut = m.getDateMatch();
            LocalDateTime dt_fin = m.getDateMatch().plusMinutes(SLOT_DURATION_MIN);

            for (Indisponibilite indisponibilite : indisponibilites) {
                //System.out.println(indisponibilite);
                LocalDateTime dt_d = indisponibilite.getDateDebutIndisponibilite();
                LocalDateTime dt_f = indisponibilite.getDateFinIndisponibilite();

                System.out.println(dt_debut);
                System.out.println(dt_fin);


                boolean test_fin_indispo = dt_f.isAfter(dt_debut) && dt_f.isBefore(dt_fin);
                boolean test_debut_indispo = dt_d.isAfter(dt_debut) && dt_d.isBefore(dt_fin);

                assertFalse(test_debut_indispo, "test_debut_indispo");
                assertFalse(test_fin_indispo, "test_fin_indispo");

            }



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

        List<Indisponibilite> indispo = new ArrayList<>();

        TournoisService.ScheduleResult result = scheduler.generateSchedule(equipes, start, end, true, indispo);
        List<Match> matchs = result.getMatchs();
        List<Indisponibilite> indisponibilites = result.getIndisponibilites();


        // 3 équipes -> 3 matchs aller + 3 retours = 6 matchs
        assertEquals(6, matchs.size());
    }
}