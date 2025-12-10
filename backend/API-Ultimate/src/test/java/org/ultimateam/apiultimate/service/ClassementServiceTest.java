package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.ClassementRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassementServiceTest {

    @Mock
    private ClassementRepository classementRepository;

    @InjectMocks
    private ClassementService classementService;

    private Competition competition;
    private Equipe equipe1;
    private Equipe equipe2;
    private Classement classement1;
    private Classement classement2;

    @BeforeEach
    void setUp() {
        // Initialisation des objets de base pour les tests
        competition = new Tournois();
        competition.setIdCompetition(1L);

        equipe1 = new Equipe();
        equipe1.setIdEquipe(10L);
        equipe1.setNomEquipe("Les Dragons");

        equipe2 = new Equipe();
        equipe2.setIdEquipe(20L);
        equipe2.setNomEquipe("Les Requins");

        // On simule des classements vierges existants
        classement1 = new Classement(new ParticipationId(1L, 10L));
        classement1.setEquipe(equipe1);
        classement1.setCompetition(competition);

        classement2 = new Classement(new ParticipationId(1L, 20L));
        classement2.setEquipe(equipe2);
        classement2.setCompetition(competition);
    }

    // --- TEST DELETE ---

    @Test
    void deleteById_ShouldDelete_WhenExists() {
        ParticipationId id = new ParticipationId(1L, 10L);
        when(classementRepository.existsById(id)).thenReturn(true);

        classementService.deleteById(id);

        verify(classementRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowException_WhenNotFound() {
        ParticipationId id = new ParticipationId(1L, 10L);
        when(classementRepository.existsById(id)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            classementService.deleteById(id);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // --- TEST MISE A JOUR (MATCH) ---

    @Test
    void mettreAJourClassement_ShouldDoNothing_WhenMatchNotFinished() {
        Match match = new Match();
        match.setStatus(Match.Status.WAITING);

        classementService.mettreAJourClassement(match);

        // Vérifie qu'aucune interaction n'a eu lieu avec le repo
        verify(classementRepository, never()).findByCompetitionAndEquipe(any(), any());
        verify(classementRepository, never()).save(any());
    }

    @Test
    void mettreAJourClassement_ShouldUpdateStats_WhenEquipe1Wins() {
        // Simulation d'un match fini : Eq1 gagne 3-1
        Match match = new Match();
        match.setStatus(Match.Status.FINISHED);
        match.setIdCompetition(competition);
        match.setEquipe1(equipe1);
        match.setEquipe2(equipe2);
        match.setScoreEquipe1(3);
        match.setScoreEquipe2(1);

        // Mock du repository
        when(classementRepository.findByCompetitionAndEquipe(competition, equipe1)).thenReturn(classement1);
        when(classementRepository.findByCompetitionAndEquipe(competition, equipe2)).thenReturn(classement2);

        // Exécution
        classementService.mettreAJourClassement(match);

        // Vérifications pour l'Equipe 1 (Gagnant)
        assertEquals(3, classement1.getScore());         // +3 points victoire
        assertEquals(1, classement1.getVictoires());
        assertEquals(3, classement1.getPoint_marque());
        assertEquals(1, classement1.getPoint_encaisse());
        assertEquals(2, classement1.getDifference_points()); // 3 - 1 = 2

        // Vérifications pour l'Equipe 2 (Perdant)
        assertEquals(0, classement2.getScore());         // +0 points défaite
        assertEquals(1, classement2.getDefaites());
        assertEquals(1, classement2.getPoint_marque());
        assertEquals(3, classement2.getPoint_encaisse());
        assertEquals(-2, classement2.getDifference_points()); // 1 - 3 = -2

        // Vérifie que la sauvegarde a été appelée 2 fois (une par équipe)
        verify(classementRepository, times(2)).save(any(Classement.class));
    }

    @Test
    void mettreAJourClassement_ShouldUpdateStats_WhenDraw() {
        // Simulation d'un match nul : 2-2
        Match match = new Match();
        match.setStatus(Match.Status.FINISHED);
        match.setIdCompetition(competition);
        match.setEquipe1(equipe1);
        match.setEquipe2(equipe2);
        match.setScoreEquipe1(2);
        match.setScoreEquipe2(2);

        when(classementRepository.findByCompetitionAndEquipe(competition, equipe1)).thenReturn(classement1);
        when(classementRepository.findByCompetitionAndEquipe(competition, equipe2)).thenReturn(classement2);

        classementService.mettreAJourClassement(match);

        // Vérifications Equipe 1
        assertEquals(1, classement1.getScore()); // +1 point nul
        assertEquals(1, classement1.getEgalites());
        assertEquals(0, classement1.getDifference_points());

        // Vérifications Equipe 2
        assertEquals(1, classement2.getScore()); // +1 point nul
        assertEquals(1, classement2.getEgalites());
        assertEquals(0, classement2.getDifference_points());
    }

    // --- TEST TRI CLASSEMENT ---

    @Test
    void triClassement_ShouldAssignRanks() {
        // On simule une liste renvoyée par le repo (déjà triée par la query SQL)
        // Disons que Eq1 a plus de points que Eq2
        List<Classement> mockList = Arrays.asList(classement1, classement2);

        when(classementRepository.findAllByCompetitionIdOrderByRank(1L)).thenReturn(mockList);

        List<Classement> result = classementService.triClassement(1L);

        assertEquals(2, result.size());
        // Vérifie que les rangs sont bien assignés dans l'ordre de la liste
        assertEquals(1, result.get(0).getRang()); // Le premier élément reçoit rang 1
        assertEquals(2, result.get(1).getRang()); // Le deuxième reçoit rang 2
    }
}