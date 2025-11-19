package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private EquipeService equipeService;

    @InjectMocks
    private MatchService matchService;

    private Match match;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        match = new Match();
        match.setIdMatch(1L);
        match.setStatus(Match.Status.WAITING);
        match.setScoreEquipe1(0);
        match.setScoreEquipe2(0);
        match.setDureePauseTotale(Duration.ZERO);
    }

    @Test
    void commencerMatch_OK() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Match result = matchService.commencerMatch(1L);

        assertEquals(Match.Status.ONGOING, result.getStatus());
        assertNotNull(result.getDateDebut());
    }

    @Test
    void mettreEnPause_OK() {
        match.setStatus(Match.Status.ONGOING);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Match result = matchService.mettreEnPause(1L);

        assertEquals(Match.Status.PAUSED, result.getStatus());
        assertNotNull(result.getDatePause());
    }

    @Test
    void reprendreMatch_OK_avecPause() {
        // Simulation du match commencé et en pause
        LocalDateTime dateDebut = LocalDateTime.now().minusMinutes(30); // 30 min de jeu
        LocalDateTime datePause = LocalDateTime.now().minusMinutes(5);  // pause de 5 min

        match.setStatus(Match.Status.PAUSED);
        match.setDateDebut(dateDebut);
        match.setDatePause(datePause);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Reprendre le match
        Match result = matchService.reprendreMatch(1L);

        // Vérifications du statut et de la pause
        assertEquals(Match.Status.ONGOING, result.getStatus());
        assertNull(result.getDatePause());

        // La pause de 5 min doit être correctement comptabilisée
        assertEquals(5, result.getDureePauseTotale().toMinutes());

        // ⚡ Vérification stable du temps total : temps écoulé depuis dateDebut + pause
        Duration tempsTotal = Duration.between(dateDebut, LocalDateTime.now()).plus(result.getDureePauseTotale());
        assertTrue(tempsTotal.toMinutes() >= 35, "Le temps total doit inclure les 30 min de jeu + 5 min de pause");

        // Vérification du temps effectif de jeu (hors pause) ≤ 90 minutes
        Duration tempsEffectif = Duration.between(dateDebut, LocalDateTime.now());
        Duration tempsJoueEffectif = tempsEffectif.minus(result.getDureePauseTotale());
        assertTrue(tempsJoueEffectif.toMinutes() <= 90, "Le temps effectif de jeu ne doit pas dépasser 90 minutes");
    }

    @Test
    @Disabled
    void ajouterPoint_Equipe1_OK() {
        Equipe e1 = new Equipe();
        e1.setIdEquipe(10L);
        Equipe e2 = new Equipe();
        e2.setIdEquipe(20L);

        match.setEquipe1(e1);
        match.setEquipe2(e2);
        match.setStatus(Match.Status.ONGOING);
        match.setDateDebut(LocalDateTime.now());

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(e1);
        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        //MatchDTO dto = new MatchPointDTO();
        //dto.setPoint(1);

        //Match result = matchService.ajouterPoint(1L, 10L, dto);

        //assertEquals(1, result.getScoreEquipe1());
    }

    @Test
    void checkVictory_Equipe1_gagne() {
        Equipe e1 = new Equipe();
        e1.setIdEquipe(10L);
        Equipe e2 = new Equipe();
        e2.setIdEquipe(20L);

        match.setEquipe1(e1);
        match.setEquipe2(e2);
        match.setScoreEquipe1(15);
        match.setScoreEquipe2(7);

        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        matchService.checkVictory(match);

        assertEquals(Match.Status.FINISHED, match.getStatus());
        assertEquals(e1, match.getWinner());
    }

    @Test
    void checkTime_finAutomatique_OK() {
        match.setStatus(Match.Status.ONGOING);
        match.setDureePauseTotale(Duration.ZERO);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        matchService.checkTime(1L);

        assertEquals(Match.Status.FINISHED, match.getStatus());
        assertNotNull(match.getDateFin());
    }

    @Test
    void checkTime_compensation_relaunchScheduler() {
        match.setStatus(Match.Status.ONGOING);
        match.setDureePauseTotale(Duration.ofMinutes(5));

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        matchService.checkTime(1L);

        // Pause remise à zéro après relance du scheduler
        assertEquals(Duration.ZERO, match.getDureePauseTotale());
    }
}