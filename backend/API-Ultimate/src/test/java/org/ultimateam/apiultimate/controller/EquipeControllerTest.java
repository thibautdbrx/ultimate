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
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.EquipeService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EquipeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EquipeService equipeService;

    @InjectMocks
    private EquipeController equipeController;

    @BeforeEach
    void setUp() {
        // Initialisation manuelle pour le mode Unit Test (Mockito pur)
        mockMvc = MockMvcBuilders.standaloneSetup(equipeController).build();
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        when(equipeService.findAll()).thenReturn(Arrays.asList(new Equipe()));

        mockMvc.perform(get("/api/equipe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_ShouldReturnEquipe() throws Exception {
        Equipe equipe = new Equipe();
        equipe.setNomEquipe("Les Titans");
        when(equipeService.getById(1L)).thenReturn(equipe);

        mockMvc.perform(get("/api/equipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEquipe").value("Les Titans"));
    }

    @Test
    void getIndisponibilites_ShouldReturnList() throws Exception {
        when(equipeService.getIndisponibilites(1L)).thenReturn(Arrays.asList(new Indisponibilite()));

        mockMvc.perform(get("/api/equipe/1/indisponibilite"))
                .andExpect(status().isOk());
    }

    @Test
    void getEquipeGenre_ShouldReturnFilteredList() throws Exception {
        when(equipeService.getEquipeGenre(Genre.HOMME)).thenReturn(Arrays.asList(new Equipe()));

        mockMvc.perform(get("/api/equipe/genre")
                        .param("genre", "M"))
                .andExpect(status().isOk());
    }

    @Test
    void createEquipe_ShouldReturnSavedEquipe() throws Exception {
        Equipe equipe = new Equipe();
        equipe.setNomEquipe("Nouvelle Equipe");
        when(equipeService.save(any(Equipe.class))).thenReturn(equipe);

        mockMvc.perform(post("/api/equipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEquipe").value("Nouvelle Equipe"));
    }

    @Test
    void editNomEquipe_ShouldReturnUpdatedEquipe() throws Exception {
        // CORRECTION : Instanciation via constructeur vide + setters (Lombok @Data)
        EquipeNameDTO dto = new EquipeNameDTO();
        dto.setNom("Nouveau Nom");
        dto.setDescription("Nouvelle Description");

        Equipe updatedEquipe = new Equipe();
        updatedEquipe.setNomEquipe("Nouveau Nom");

        when(equipeService.editName(any(EquipeNameDTO.class), eq(1L))).thenReturn(updatedEquipe);

        mockMvc.perform(patch("/api/equipe/1/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEquipe").value("Nouveau Nom"));
    }

    @Test
    void deleteById_ShouldCallService() throws Exception {
        doNothing().when(equipeService).deleteById(1L);

        mockMvc.perform(delete("/api/equipe/1"))
                .andExpect(status().isOk());

        verify(equipeService, times(1)).deleteById(1L);
    }

    @Test
    void openEquipe_ShouldReturnAvailableEquipes() throws Exception {
        when(equipeService.getNotFull(10L)).thenReturn(Arrays.asList(new Equipe()));

        mockMvc.perform(get("/api/equipe/open")
                        .param("idJoueur", "10"))
                .andExpect(status().isOk());
    }
}