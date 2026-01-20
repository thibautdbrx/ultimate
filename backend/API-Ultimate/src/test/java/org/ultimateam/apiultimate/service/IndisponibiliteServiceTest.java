package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.repository.IndisponibiliteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndisponibiliteServiceTest {

    @Mock private IndisponibiliteRepository indisponibiliteRepository;
    @Mock private EquipeService equipeService;

    @InjectMocks
    private IndisponibiliteService indisponibiliteService;

    private Equipe equipe;

    @BeforeEach
    void setUp() {
        equipe = new Equipe();
        equipe.setIdEquipe(1L);
    }

    @Test
    void addIndisponibilite_ShouldSave_WhenValid() {
        // Arrange
        String start = "2025-01-01 10:00";
        String end = "2025-01-01 12:00";

        IndisponibiliteDTO dto = new IndisponibiliteDTO(null, 1L, start, end);

        when(equipeService.getById(1L)).thenReturn(equipe);
        // On retourne l'objet sauvegardé (qui aura été modifié par le service avant l'appel)
        when(indisponibiliteRepository.save(any(Indisponibilite.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        IndisponibiliteDTO result = indisponibiliteService.addIndisponibilite(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdEquipe());
        assertEquals(start, result.getDateDebut());
        assertEquals(end, result.getDateFin());
        verify(indisponibiliteRepository).save(any(Indisponibilite.class));
    }

    @Test
    void addIndisponibilite_ShouldThrow_WhenDatesInvalid() {
        // Arrange : Date de fin AVANT date de début
        String start = "2025-01-01 12:00";
        String end = "2025-01-01 10:00";
        IndisponibiliteDTO dto = new IndisponibiliteDTO(null, 1L, start, end);

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            indisponibiliteService.addIndisponibilite(dto);
        });
        assertTrue(ex.getMessage().contains("date de début doit être avant"));
    }

    @Test
    void addIndisponibilite_ShouldThrow_WhenEquipeMissing() {
        IndisponibiliteDTO dto = new IndisponibiliteDTO(null, null, "2025-01-01 10:00", "2025-01-01 12:00");

        assertThrows(ResponseStatusException.class, () -> {
            indisponibiliteService.addIndisponibilite(dto);
        });
    }

    @Test
    void updateIndisponibilite_ShouldUpdateDates() {
        // Arrange
        Long id = 5L;
        Indisponibilite existing = new Indisponibilite();
        existing.setIdIndisponibilite(id);
        existing.setEquipe(equipe);
        existing.setDateDebutIndisponibilite(LocalDateTime.of(2025, 1, 1, 8, 0));
        existing.setDateFinIndisponibilite(LocalDateTime.of(2025, 1, 1, 9, 0));

        // Nouvelles dates
        String newStart = "2025-01-01 14:00";
        String newEnd = "2025-01-01 16:00";
        IndisponibiliteDTO dto = new IndisponibiliteDTO(id, 1L, newStart, newEnd);

        when(indisponibiliteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(indisponibiliteRepository.save(any(Indisponibilite.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        IndisponibiliteDTO result = indisponibiliteService.updateIndisponibilite(dto, id);

        // Assert
        assertEquals(newStart, result.getDateDebut());
        assertEquals(newEnd, result.getDateFin());
    }

    @Test
    void deleteIndisponibilite_ShouldDelete_WhenExists() {
        when(indisponibiliteRepository.existsById(1L)).thenReturn(true);

        indisponibiliteService.deleteIndisponibilite(1L);

        verify(indisponibiliteRepository).deleteById(1L);
    }

    @Test
    void deleteIndisponibilite_ShouldThrow_WhenNotFound() {
        when(indisponibiliteRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            indisponibiliteService.deleteIndisponibilite(1L);
        });
    }

    @Test
    void getAllDTO_ShouldMapCorrectly() {
        Indisponibilite i1 = new Indisponibilite();
        i1.setEquipe(equipe);
        i1.setDateDebutIndisponibilite(LocalDateTime.of(2025, 1, 1, 10, 0));
        i1.setDateFinIndisponibilite(LocalDateTime.of(2025, 1, 1, 12, 0));

        when(indisponibiliteRepository.findAll()).thenReturn(List.of(i1));

        List<IndisponibiliteDTO> result = indisponibiliteService.getAllDTO();

        assertEquals(1, result.size());
        assertEquals("2025-01-01 10:00", result.get(0).getDateDebut());
    }
}