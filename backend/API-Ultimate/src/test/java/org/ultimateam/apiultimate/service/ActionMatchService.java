package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.repository.ActionMatchRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionMatchServiceTest {

    @Mock private ActionMatchRepository actionMatchRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private EquipeService equipeService;
    @Mock private JoueurService joueurService;

    @InjectMocks
    private ActionMatchService actionMatchService;

    private Match match;
    private Equipe equipe1;
    private Equipe equipe2;
    private Joueur joueurE1;

    @BeforeEach
    void setUp() {
        // Initialisation des objets de base pour les tests
        joueurE1 = new Joueur();
        joueurE1.setIdJoueur(100L);
        joueurE1.setNomJoueur("Zidane");

        equipe1 = new Equipe();
        equipe1.setIdEquipe(10L);
        equipe1.setJoueurs(new ArrayList<>(List.of(joueurE1))); // Le joueur est bien dans l'équipe 1

        equipe2 = new Equipe();
        equipe2.setIdEquipe(20L);
        equipe2.setJoueurs(new ArrayList<>());

        match = new Match();
        match.setIdMatch(1L);
        match.setEquipe1(equipe1);
        match.setEquipe2(equipe2);
        match.setStatus(Match.Status.ONGOING); // Le match doit être en cours
    }

    @Test
    void addPoint_ShouldSaveAction_WhenEverythingIsValid() {
        // Arrange
        MatchPointDTO dto = new MatchPointDTO();
        dto.setIdJoueur(100L);
        dto.setPoint(1);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(equipe1);
        when(joueurService.getById(100L)).thenReturn(joueurE1);
        // On simule la sauvegarde (renvoie l'objet qu'on lui donne)
        when(actionMatchRepository.save(any(ActionMatch.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ActionMatch result = actionMatchService.addPoint(1L, 10L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(ActionTypeDTO.POINT, result.getType());
        assertEquals(joueurE1, result.getJoueur());
        assertEquals(match, result.getMatch());
        verify(actionMatchRepository).save(any(ActionMatch.class));
    }

    @Test
    void addAction_ShouldThrow_WhenMatchNotOngoing() {
        // Arrange
        match.setStatus(Match.Status.WAITING); // Match pas commencé

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(equipe1);
        when(joueurService.getById(100L)).thenReturn(joueurE1);

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actionMatchService.addAction(1L, 10L, 100L, ActionTypeDTO.POINT, LocalDateTime.now());
        });
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void addAction_ShouldThrow_WhenPlayerNotInMatch() {
        // Arrange : On crée un joueur "intrus" qui n'est ni dans E1 ni dans E2
        Joueur intrus = new Joueur();
        intrus.setIdJoueur(999L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(equipe1);
        when(joueurService.getById(999L)).thenReturn(intrus);

        // Act & Assert
        // verifyJoueurInMatch doit lever une exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actionMatchService.addAction(1L, 10L, 999L, ActionTypeDTO.POINT, LocalDateTime.now());
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals("Le joueur ne fait partie d'aucune des deux équipes de ce match", ex.getReason());
    }

    @Test
    void addAction_ShouldThrow_WhenTeamNotInMatch() {
        // Arrange : On essaie d'attribuer le point à une équipe tierce (ID 30)
        Equipe equipeTierce = new Equipe();
        equipeTierce.setIdEquipe(30L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(30L)).thenReturn(equipeTierce);
        when(joueurService.getById(100L)).thenReturn(joueurE1); // Le joueur est valide, mais l'équipe non

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actionMatchService.addAction(1L, 30L, 100L, ActionTypeDTO.POINT, LocalDateTime.now());
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Cette équipe ne fait pas partie du match", ex.getReason());
    }
}