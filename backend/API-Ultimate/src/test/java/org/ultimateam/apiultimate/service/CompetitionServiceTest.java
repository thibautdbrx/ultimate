package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ScheduleResult;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

    @Mock private CompetitionRepository competitionRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private ParticipationRepository participationRepository;
    @Mock private EquipeService equipeService;
    @Mock private RoundRobinSchedulerService scheduler;
    @Mock private IndisponibiliteRepository indisponibiliteRepository;
    @Mock private ClassementRepository classementRepository;
    @Mock private TerrainService terrainService;

    @InjectMocks
    private CompetitionService competitionService;

    private Competition competition;
    private Terrain terrain;
    private Equipe equipe;

    @BeforeEach
    void setUp() {
        competition = new Tournoi();
        competition.setIdCompetition(1L);
        competition.setDateDebut(LocalDate.now());
        competition.setDateFin(LocalDate.now().plusDays(5));

        terrain = new Terrain();
        terrain.setIdTerrain(10L);
        // Important pour éviter une liste vide dans le service
        competition.setTerrains(new ArrayList<>(List.of(terrain)));

        equipe = new Equipe();
        equipe.setIdEquipe(100L);
        equipe.setNomEquipe("Lions");
    }

    // --- TESTS SAUVEGARDE ---

    @Test
    void saveCompetition_ShouldSave_WhenDatesPresent() {
        when(competitionRepository.save(competition)).thenReturn(competition);

        Competition result = competitionService.saveCompetition(competition);

        assertNotNull(result);
        verify(competitionRepository).save(competition);
    }

    @Test
    void saveCompetition_ShouldThrow_WhenDatesMissing() {
        competition.setDateDebut(null); // Dates manquantes

        assertThrows(ResponseStatusException.class, () -> {
            competitionService.saveCompetition(competition);
        });
    }

    // --- TESTS GENERATION COMPETITION ---

    @Test
    void genererCompetition_ShouldThrow_WhenNoTerrains() {
        // Arrange : Compétition sans terrains
        competition.setTerrains(new ArrayList<>());

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            competitionService.genererCompetition(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("aucun terrain trouvé"));
    }


    // --- TESTS GESTION TERRAINS ---

    @Test
    void ajouterTerrainACompetition_ShouldAddTerrain() {
        // Arrange
        competition.setTerrains(new ArrayList<>()); // Liste vide au départ
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(terrainService.getById(10L)).thenReturn(terrain);
        when(competitionRepository.save(competition)).thenReturn(competition);

        // Act
        Competition result = competitionService.ajouterTerrainACompetition(1L, 10L);

        // Assert
        assertTrue(result.getTerrains().contains(terrain));
        verify(competitionRepository).save(competition);
    }

    @Test
    void retirerTerrainDeCompetition_ShouldRemoveTerrain() {
        // Arrange
        competition.setTerrains(new ArrayList<>(List.of(terrain))); // Terrain présent
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(terrainService.getById(10L)).thenReturn(terrain);
        when(competitionRepository.save(competition)).thenReturn(competition);

        // Act
        Competition result = competitionService.retirerTerrainDeCompetition(1L, 10L);

        // Assert
        assertFalse(result.getTerrains().contains(terrain));
        verify(competitionRepository).save(competition);
    }
}

