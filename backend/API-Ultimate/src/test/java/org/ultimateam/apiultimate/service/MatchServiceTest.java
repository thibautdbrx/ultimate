package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Terrain;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.repository.JoueurRepository;
import org.ultimateam.apiultimate.repository.TerrainRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock private MatchRepository matchRepository;
    @Mock private EquipeService equipeService;
    @Mock private ActionMatchService actionMatchService;
    @Mock private ClassementService classementService;
    // On mock les autres dépendances pour éviter les NullPointerException dans le constructeur
    @Mock private TournoisService tournoisService;
    @Mock private JoueurRepository joueurRepository;
    @Mock private JoueurService joueurService;
    @Mock private TerrainRepository terrainRepository;
    @Mock private TerrainService terrainService;

    @InjectMocks
    private MatchService matchService;

    // --- TESTS DU CYCLE DE VIE (COMMENCER / PAUSE / FINIR) ---

    @Test
    void commencerMatch_ShouldChangeStatusToOngoing() {
        // Arrange
        Match match = new Match();
        match.setIdMatch(1L);
        match.setStatus(Match.Status.WAITING); //

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Match result = matchService.commencerMatch(1L);

        // Assert
        assertEquals(Match.Status.ONGOING, result.getStatus()); //
        assertNotNull(result.getDateDebut()); //
        verify(matchRepository).save(match);
    }

    @Test
    void commencerMatch_ShouldThrow_WhenAlreadyStarted() {
        Match match = new Match();
        match.setStatus(Match.Status.ONGOING); // Déjà en cours

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        assertThrows(ResponseStatusException.class, () -> matchService.commencerMatch(1L)); //
    }

    @Test
    void mettreEnPause_ShouldChangeStatusToPaused() {
        Match match = new Match();
        match.setIdMatch(1L);
        match.setStatus(Match.Status.ONGOING);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);

        Match result = matchService.mettreEnPause(1L);

        assertEquals(Match.Status.PAUSED, result.getStatus()); //
        assertNotNull(result.getDatePause());
    }

    @Test
    void finirMatch_ShouldCloseMatchAndUpdateClassement() {
        Match match = new Match();
        match.setIdMatch(1L);
        match.setStatus(Match.Status.ONGOING);
        match.setDureePauseTotale(Duration.ZERO);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);

        matchService.finirMatch(1L);

        assertEquals(Match.Status.FINISHED, match.getStatus()); //
        assertNotNull(match.getDateFin()); //
        // Vérifie qu'on appelle bien le service de classement pour mettre à jour les points
        verify(classementService).mettreAJourClassement(match); //
    }

    // --- TESTS DES POINTS ET VICTOIRE ---

    @Test
    void ajouterPoint_ShouldIncreaseScore() {
        // Arrange
        Match match = new Match();
        match.setIdMatch(1L);
        match.setStatus(Match.Status.ONGOING); // Le match doit être en cours
        match.setScoreEquipe1(0);

        Equipe e1 = new Equipe(); e1.setIdEquipe(10L);
        Equipe e2 = new Equipe(); e2.setIdEquipe(20L);
        match.setEquipe1(e1);
        match.setEquipe2(e2);

        MatchPointDTO dto = new MatchPointDTO();
        dto.setPoint(1);
        dto.setIdJoueur(100L); // ID fictif du joueur

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(e1); // On récupère l'équipe 1
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Match result = matchService.ajouterPoint(1L, 10L, dto);

        // Assert
        assertEquals(1, result.getScoreEquipe1()); // Score incrémenté
        // Vérifie qu'on enregistre l'action
        verify(actionMatchService).addPoint(1L, 10L, dto); //
    }

    @Test
    void ajouterPoint_ShouldEndMatch_WhenScoreReaches15() {
        // Arrange : Le score est à 14-0
        Match match = new Match();
        match.setIdMatch(1L);
        match.setStatus(Match.Status.ONGOING);
        match.setScoreEquipe1(14); //
        match.setScoreEquipe2(0);
        match.setDureePauseTotale(Duration.ZERO);

        Equipe e1 = new Equipe(); e1.setIdEquipe(10L);
        Equipe e2 = new Equipe(); e2.setIdEquipe(20L);
        match.setEquipe1(e1);
        match.setEquipe2(e2);

        MatchPointDTO dto = new MatchPointDTO();
        dto.setPoint(1); // On ajoute le 15ème point

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(e1);
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Match result = matchService.ajouterPoint(1L, 10L, dto);

        // Assert
        assertEquals(15, result.getScoreEquipe1());
        assertEquals(Match.Status.FINISHED, result.getStatus(), "Le match doit finir à 15 points"); //
        assertEquals(e1, result.getWinner()); // L'équipe 1 doit être déclarée vainqueur
        verify(classementService).mettreAJourClassement(match); // Le classement doit être mis à jour
    }

    @Test
    void ajouterPoint_ShouldThrow_WhenMatchNotOngoing() {
        Match match = new Match();
        match.setStatus(Match.Status.PAUSED); // Match en pause

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(10L)).thenReturn(new Equipe());

        // Impossible de marquer si le match n'est pas ONGOING
        assertThrows(ResponseStatusException.class, () -> {
            matchService.ajouterPoint(1L, 10L, new MatchPointDTO()); //
        });
    }

    @Test
    void ajouterPoint_ShouldThrow_WhenTeamNotInMatch() {
        Match match = new Match();
        match.setStatus(Match.Status.ONGOING);

        Equipe e1 = new Equipe(); e1.setIdEquipe(10L);
        Equipe e2 = new Equipe(); e2.setIdEquipe(20L);
        match.setEquipe1(e1);
        match.setEquipe2(e2);

        Equipe e3 = new Equipe(); e3.setIdEquipe(99L); // Équipe externe

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(equipeService.getById(99L)).thenReturn(e3);

        // L'équipe 99 ne joue pas ce match
        assertThrows(ResponseStatusException.class, () -> {
            matchService.ajouterPoint(1L, 99L, new MatchPointDTO()); //
        });
    }
}