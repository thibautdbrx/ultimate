package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.ClassementRepository;

import java.util.ArrayList;
import java.util.Collections;
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

    // --- TESTS MISE A JOUR CLASSEMENT (LOGIQUE PRINCIPALE) ---

    @Test
    void mettreAJourClassement_ShouldUpdateStats_WhenWin() {
        // Arrange : Match fini, 15-5 pour Equipe 1
        Match match = createMatch(15, 5);

        // Classements existants (vierges)
        Classement c1 = new Classement(); c1.setEquipe(match.getEquipe1());
        Classement c2 = new Classement(); c2.setEquipe(match.getEquipe2());

        when(classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(1L, 10L))
                .thenReturn(c1);
        when(classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(1L, 20L))
                .thenReturn(c2);

        // Act
        classementService.mettreAJourClassement(match);

        // Assert : Equipe 1 (Vainqueur)
        assertEquals(3, c1.getScore(), "3 points pour la victoire");
        assertEquals(1, c1.getVictoires());
        assertEquals(15, c1.getPoint_marque());
        assertEquals(5, c1.getPoint_encaisse());
        assertEquals(10, c1.getDifference_points()); // 15 - 5

        // Assert : Equipe 2 (Perdant)
        assertEquals(0, c2.getScore(), "0 point pour la défaite");
        assertEquals(1, c2.getDefaites());
        assertEquals(5, c2.getPoint_marque());
        assertEquals(15, c2.getPoint_encaisse());
        assertEquals(-10, c2.getDifference_points()); // 5 - 15

        // On vérifie que save a été appelé 2 fois (une par équipe)
        verify(classementRepository, times(2)).save(any(Classement.class));
    }

    @Test
    void mettreAJourClassement_ShouldUpdateStats_WhenDraw() {
        // Arrange : Match nul 10-10
        Match match = createMatch(10, 10);

        Classement c1 = new Classement();
        Classement c2 = new Classement();

        when(classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(1L, 10L)).thenReturn(c1);
        when(classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(1L, 20L)).thenReturn(c2);

        // Act
        classementService.mettreAJourClassement(match);

        // Assert
        assertEquals(1, c1.getScore(), "1 point pour le nul");
        assertEquals(1, c1.getEgalites());
        assertEquals(0, c1.getDifference_points());

        assertEquals(1, c2.getScore());
        assertEquals(1, c2.getEgalites());
    }

    @Test
    void mettreAJourClassement_ShouldDoNothing_WhenMatchNotFinished() {
        Match match = createMatch(0, 0);
        match.setStatus(Match.Status.ONGOING); // Pas fini

        classementService.mettreAJourClassement(match);

        // Rien ne doit se passer, aucun appel au repo
        verify(classementRepository, never()).findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(anyLong(), anyLong());
    }

    // --- TESTS TRI ET RANG ---

    @Test
    void triClassement_ShouldAssignRanks() {
        // Arrange : Une liste simulée qui revient déjà triée par la requête SQL (OrderByRank)
        Classement premier = new Classement(); premier.setScore(9);
        Classement deuxieme = new Classement(); deuxieme.setScore(6);
        Classement troisieme = new Classement(); troisieme.setScore(3);

        List<Classement> listeTriee = new ArrayList<>(List.of(premier, deuxieme, troisieme));

        when(classementRepository.findAllByCompetitionIdOrderByRank(1L)).thenReturn(listeTriee);

        // Act
        List<Classement> result = classementService.triClassement(1L);

        // Assert
        assertEquals(1, result.get(0).getRang());
        assertEquals(2, result.get(1).getRang());
        assertEquals(3, result.get(2).getRang());

        verify(classementRepository).saveAll(listeTriee);
    }

    // --- TESTS SUPPRESSION ---

    @Test
    void deleteByIdCompetition_ShouldDelete_WhenFound() {
        when(classementRepository.findAllByCompetition_IdCompetition(1L))
                .thenReturn(List.of(new Classement(), new Classement()));

        classementService.deleteByIdCompetition(1L);

        verify(classementRepository).deleteAll(anyList());
    }

    @Test
    void deleteByIdCompetition_ShouldThrow_WhenNotFound() {
        when(classementRepository.findAllByCompetition_IdCompetition(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> {
            classementService.deleteByIdCompetition(1L);
        });
    }

    @Test
    void deleteByIdEquipe_ShouldThrow_WhenNotFound() {
        when(classementRepository.findAllByEquipe_IdEquipe(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> {
            classementService.deleteByIdEquipe(1L);
        });
    }

    // --- Helpers ---

    private Match createMatch(int s1, int s2) {
        Match m = new Match();
        m.setStatus(Match.Status.FINISHED);

        Competition c = new Tournoi();
        c.setIdCompetition(1L);
        m.setIdCompetition(c);

        Equipe e1 = new Equipe(); e1.setIdEquipe(10L);
        Equipe e2 = new Equipe(); e2.setIdEquipe(20L);
        m.setEquipe1(e1);
        m.setEquipe2(e2);

        m.setScoreEquipe1(s1);
        m.setScoreEquipe2(s2);
        return m;
    }
}