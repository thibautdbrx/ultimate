package org.ultimateam.apiultimate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.service.IndisponibiliteService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IndisponibiliteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IndisponibiliteService indisponibiliteService;

    @InjectMocks
    private IndisponibiliteController indisponibiliteController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(indisponibiliteController).build();
    }

    @Test
    void getAllIndisponibilites_ShouldReturnList() throws Exception {
        IndisponibiliteDTO dto = new IndisponibiliteDTO();
        when(indisponibiliteService.getAllDTO()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/indisponibilite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createIndisponibilite_ShouldReturnSavedDto() throws Exception {
        // CORRECTION : Utilisation de String pour les dates comme défini dans ton DTO
        // On utilise le constructeur AllArgsConstructor généré par Lombok
        IndisponibiliteDTO dto = new IndisponibiliteDTO(
                null,
                1L,
                "2026-01-25 10:00",
                "2026-01-25 12:00"
        );

        when(indisponibiliteService.addIndisponibilite(any(IndisponibiliteDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/indisponibilite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateDebut").value("2026-01-25 10:00"));
    }

    @Test
    void updateIndisponibilite_ShouldReturnUpdatedDto() throws Exception {
        // Utilisation des setters (Lombok @Setter)
        IndisponibiliteDTO dto = new IndisponibiliteDTO();
        dto.setIdEquipe(1L);
        dto.setDateDebut("2026-01-25 14:00");
        dto.setDateFin("2026-01-25 16:00");

        when(indisponibiliteService.updateIndisponibilite(any(IndisponibiliteDTO.class), eq(1L))).thenReturn(dto);

        mockMvc.perform(put("/api/indisponibilite/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateDebut").value("2026-01-25 14:00"));
    }

    @Test
    void deleteIndisponibilite_ShouldCallService() throws Exception {
        doNothing().when(indisponibiliteService).deleteIndisponibilite(1L);

        mockMvc.perform(delete("/api/indisponibilite/1"))
                .andExpect(status().isOk());

        verify(indisponibiliteService, times(1)).deleteIndisponibilite(1L);
    }
}