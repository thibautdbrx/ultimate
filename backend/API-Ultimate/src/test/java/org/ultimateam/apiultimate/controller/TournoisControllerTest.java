package org.ultimateam.apiultimate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournoi;
import org.ultimateam.apiultimate.service.CompetitionService;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TournoisControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TournoisService tournoisService;

    @Mock
    private CompetitionService competitionService;

    @InjectMocks
    private TournoisController tournoisController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tournoisController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Pour supporter les LocalDate/LocalDateTime
    }

    @Test
    void findAll_ShouldReturnTournoisList() throws Exception {
        when(tournoisService.getAllTournois()).thenReturn(Arrays.asList(new Tournoi()));

        mockMvc.perform(get("/api/tournois"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findById_ShouldReturnTournoi() throws Exception {
        Tournoi tournoi = new Tournoi();
        tournoi.setIdCompetition(1L);
        when(tournoisService.getTournoisById(1L)).thenReturn(tournoi);

        mockMvc.perform(get("/api/tournois/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompetition").value(1));
    }

    @Test
    void findMatches_ShouldReturnMatchList() throws Exception {
        when(tournoisService.getMatchesByTournois(1L)).thenReturn(Arrays.asList(new Match()));

        mockMvc.perform(get("/api/tournois/1/matchs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void creerTournois_ShouldReturnSavedTournoi() throws Exception {
        Tournoi tournoi = new Tournoi();
        // Configure tournoi properties here if needed
        when(tournoisService.saveTournois(any(Tournoi.class))).thenReturn(tournoi);

        mockMvc.perform(post("/api/tournois")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournoi)))
                .andExpect(status().isOk());
    }

    @Test
    void editTournoi_ShouldReturnUpdatedTournoi() throws Exception {
        EquipeNameDTO dto = new EquipeNameDTO();
        dto.setNom("Nouveau nom de tournoi");

        when(tournoisService.editTournois(any(EquipeNameDTO.class), eq(1L))).thenReturn(new Tournoi());

        mockMvc.perform(patch("/api/tournois/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void genererMatchs_ShouldReturnMatches() throws Exception {
        when(competitionService.genererCompetition(1L)).thenReturn(Arrays.asList(new Match()));

        mockMvc.perform(put("/api/tournois/1/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteById_ShouldCallService() throws Exception {
        doNothing().when(tournoisService).deleteTournoisById(1L);

        mockMvc.perform(delete("/api/tournois/1"))
                .andExpect(status().isOk());

        verify(tournoisService, times(1)).deleteTournoisById(1L);
    }
}