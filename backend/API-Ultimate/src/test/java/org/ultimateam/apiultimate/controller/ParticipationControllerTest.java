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
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.service.ParticipationService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParticipationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ParticipationService participationService;

    @InjectMocks
    private ParticipationController participationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(participationController).build();
    }

    @Test
    void getAllParticipation_ShouldReturnList() throws Exception {
        when(participationService.getAll()).thenReturn(Arrays.asList(new Participation()));

        mockMvc.perform(get("/api/participation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getParticipationByCompetitionId_ShouldReturnEquipes() throws Exception {
        Long idComp = 1L;
        when(participationService.getParticipationByCompetitionId(idComp))
                .thenReturn(Arrays.asList(new Equipe()));

        mockMvc.perform(get("/api/participation/competition/{idCompetition}", idComp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getParticipationByEquipeId_ShouldReturnParticipations() throws Exception {
        Long idEquipe = 10L;
        when(participationService.getParticipationByEquipeId(idEquipe))
                .thenReturn(Arrays.asList(new Participation()));

        mockMvc.perform(get("/api/participation/equipe/{idEquipe}", idEquipe))
                .andExpect(status().isOk());
    }

    @Test
    void createParticipation_Single_ShouldReturnSavedParticipation() throws Exception {
        // Supposons que ParticipationId a un constructeur vide ou des setters
        ParticipationId id = new ParticipationId();
        Participation participation = new Participation();

        when(participationService.addParticipation(any(ParticipationId.class))).thenReturn(participation);

        mockMvc.perform(post("/api/participation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isOk());
    }

    @Test
    void createParticipation_List_ShouldReturnList() throws Exception {
        // Simulation du DTO avec une liste d'IDs
        ListEquipeDTO listDto = new ListEquipeDTO();
        // listDto.setIdCompetition(1L);
        // listDto.setIdsEquipes(Arrays.asList(1L, 2L));

        when(participationService.addListParticipation(any(ListEquipeDTO.class)))
                .thenReturn(Arrays.asList(new Participation(), new Participation()));

        mockMvc.perform(post("/api/participation/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteParticipation_ShouldReturnRemainingParticipations() throws Exception {
        ParticipationId id = new ParticipationId();
        // Ton controller renvoie List<Participation> après suppression
        when(participationService.deleteById(any(ParticipationId.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/api/participation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}