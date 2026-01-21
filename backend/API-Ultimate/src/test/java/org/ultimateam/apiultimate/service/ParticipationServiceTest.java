package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.CompetitionRepository;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @Mock private ParticipationRepository participationRepository;
    @Mock private EquipeRepository equipeRepository;
    @Mock private CompetitionRepository competitionRepository;

    @InjectMocks
    private ParticipationService participationService;

    private Equipe equipe;
    private Competition competition;
    private ParticipationId participationId;

    @BeforeEach
    void setUp() {
        equipe = mock(Equipe.class);
        // CORRECTION 1 : lenient() pour éviter UnnecessaryStubbingException
        lenient().when(equipe.getIdEquipe()).thenReturn(10L);
        lenient().when(equipe.getGenre()).thenReturn(Genre.HOMME);

        competition = new Tournoi();
        competition.setIdCompetition(20L);
        competition.setGenre(Genre.HOMME);
        competition.setDateDebut(LocalDate.now().plusDays(10));

        participationId = new ParticipationId(10L, 20L);
    }

    @Test
    void addParticipation_ShouldSave_WhenValid() {
        when(equipe.isFull()).thenReturn(true);
        // CORRECTION : Mockito strict n'aime pas quand on mélange des objets réels et des mocks stricts
        // On s'assure que equipe.getGenre() renvoie bien HOMME pour passer la condition du service
        lenient().when(equipe.getGenre()).thenReturn(Genre.HOMME);

        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));
        when(competitionRepository.findById(20L)).thenReturn(Optional.of(competition));
        when(participationRepository.save(any(Participation.class))).thenAnswer(i -> i.getArguments()[0]);

        Participation result = participationService.addParticipation(participationId);

        assertNotNull(result);
        // CORRECTION 2 : On compare l'ID, pas l'objet complet
        assertEquals(10L, result.getId().getIdEquipe());
        verify(participationRepository).save(any(Participation.class));
    }

    @Test
    void addParticipation_ShouldThrow_WhenCompetitionStarted() {
        competition.setDateDebut(LocalDate.now().minusDays(1));

        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));
        when(competitionRepository.findById(20L)).thenReturn(Optional.of(competition));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            participationService.addParticipation(participationId);
        });
        assertTrue(ex.getMessage().contains("déjà commencée"));
    }

    @Test
    void addParticipation_ShouldThrow_WhenTeamNotFull() {
        when(equipe.isFull()).thenReturn(false);

        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));
        when(competitionRepository.findById(20L)).thenReturn(Optional.of(competition));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            participationService.addParticipation(participationId);
        });
        assertTrue(ex.getMessage().contains("équipe n'est pas pleine"));
    }

    @Test
    void addParticipation_ShouldThrow_WhenGenreMismatch() {
        when(equipe.isFull()).thenReturn(true);
        when(equipe.getGenre()).thenReturn(Genre.FEMME);
        competition.setGenre(Genre.HOMME);

        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));
        when(competitionRepository.findById(20L)).thenReturn(Optional.of(competition));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            participationService.addParticipation(participationId);
        });
        assertTrue(ex.getMessage().contains("Pas le même genre"));
    }

    @Test
    void deleteById_ShouldDelete_WhenCompetitionNotStarted() {
        competition.setDateDebut(LocalDate.now().plusDays(5));

        Participation p = new Participation();
        p.setId(participationId);

        when(competitionRepository.findById(20L)).thenReturn(Optional.of(competition));
        when(participationRepository.findAll()).thenReturn(List.of(p));

        participationService.deleteById(participationId);

        verify(participationRepository).delete(p);
    }

    @Test
    void deleteById_ShouldThrow_WhenCompetitionStarted() {
        competition.setDateDebut(LocalDate.now().minusDays(1));

        when(competitionRepository.findById(20L)).thenReturn(Optional.of(competition));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            participationService.deleteById(participationId);
        });
        assertTrue(ex.getMessage().contains("déjà commencée"));
    }

    @Test
    void getParticipationByCompetitionId_ShouldReturnEquipes() {
        Participation p = new Participation();
        p.setId(participationId);

        when(participationRepository.findById_idCompetition(20L)).thenReturn(List.of(p));
        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));

        List<Equipe> result = participationService.getParticipationByCompetitionId(20L);

        assertEquals(1, result.size());
        // On vérifie que l'équipe retournée est bien celle mockée
        assertEquals(equipe, result.get(0));
    }
}*/