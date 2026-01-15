package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ultimateam.apiultimate.DTO.ScheduleResult;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Terrain; // AJOUTÉ

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinSchedulerServiceTest {

    private static final int MATCH_DURATION_MIN = 90;
    private static final int BREAK_DURATION_MIN = 10;
    private static final int SLOT_DURATION_MIN = MATCH_DURATION_MIN + BREAK_DURATION_MIN;
    private RoundRobinSchedulerService scheduler;
    private List<Terrain> terrainsDeTest; // AJOUTÉ

    @BeforeEach
    void setUp() {
        scheduler = new RoundRobinSchedulerService();

        // Initialisation d'une liste de terrains pour les tests
        terrainsDeTest = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            Terrain t = new Terrain();
            t.setId_terrain(i);
            t.setNom("Terrain " + i);
            terrainsDeTest.add(t);
        }
    }

    @Test
    void testRoundRobinSimple() {
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

        // APPEL CORRIGÉ (Ajout de terrainsDeTest en 2ème argument)
        ScheduleResult result = scheduler.generateSchedule(equipes, terrainsDeTest, start, end, false, indispo);

        List<Match> matchs = result.getMatchs();
        List<Indisponibilite> indisponibilites = result.getIndisponibilites();

        assertEquals(6, matchs.size(), "Round Robin aller simple pour 4 équipes = 6 matchs");

        for (Match m : matchs) {
            LocalDateTime dt = m.getDateMatch();

            assertFalse(dt.toLocalDate().isBefore(start));
            assertFalse(dt.toLocalDate().isAfter(end));

            LocalTime time = dt.toLocalTime();
            assertTrue(time.isAfter(LocalTime.of(8, 59)), "Match avant 9h");
            assertTrue(time.isBefore(LocalTime.of(18, 1)), "Match après 18h");

            // VÉRIFICATION CORRIGÉE (On vérifie l'ID de l'objet Terrain)
            assertNotNull(m.getTerrain(), "Le terrain ne doit pas être nul");
            long terrainId = m.getTerrain().getId_terrain();
            assertTrue(terrainId >= 1 && terrainId <= 5,
                    "ID du terrain invalide (doit être entre 1 et 5)");
        }

        for (Match m : matchs) {
            LocalDateTime dt_debut = m.getDateMatch();
            LocalDateTime dt_fin = m.getDateMatch().plusMinutes(SLOT_DURATION_MIN);

            for (Indisponibilite indisponibilite : indisponibilites) {
                LocalDateTime dt_d = indisponibilite.getDateDebutIndisponibilite();
                LocalDateTime dt_f = indisponibilite.getDateFinIndisponibilite();

                boolean test_fin_indispo = dt_f.isAfter(dt_debut) && dt_f.isBefore(dt_fin);
                boolean test_debut_indispo = dt_d.isAfter(dt_debut) && dt_d.isBefore(dt_fin);

                assertFalse(test_debut_indispo, "Match prévu pendant le début d'une indisponibilité");
                assertFalse(test_fin_indispo, "Match prévu pendant la fin d'une indisponibilité");
            }
        }
    }

    @Test
    void testRoundRobinHomeAway() {
        List<Equipe> equipes = List.of(
                new Equipe("A"),
                new Equipe("B"),
                new Equipe("C")
        );

        LocalDate start = LocalDate.of(2025, 2, 1);
        LocalDate end = LocalDate.of(2025, 2, 15);

        List<Indisponibilite> indispo = new ArrayList<>();

        // APPEL CORRIGÉ
        ScheduleResult result = scheduler.generateSchedule(equipes, terrainsDeTest, start, end, true, indispo);
        List<Match> matchs = result.getMatchs();

        assertEquals(6, matchs.size(), "3 équipes AR = 6 matchs");
    }
}