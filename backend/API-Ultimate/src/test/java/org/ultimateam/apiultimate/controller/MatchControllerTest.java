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
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).build();
    }

    @Test
    void getAllMatch_ShouldReturnList() throws Exception {
        when(matchService.getAll()).thenReturn(Arrays.asList(new Match()));

        mockMvc.perform(get("/api/match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_ShouldReturnMatch() throws Exception {
        Match match = new Match();
        when(matchService.getById(1L)).thenReturn(match);

        mockMvc.perform(get("/api/match/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getMatchStarted_ShouldReturnList() throws Exception {
        when(matchService.getStarted()).thenReturn(Arrays.asList(new Match()));

        mockMvc.perform(get("/api/match/started"))
                .andExpect(status().isOk());
    }

    @Test
    void createMatch_ShouldReturnSavedMatch() throws Exception {
        MatchDTO dto = new MatchDTO(); // Remplir si nécessaire selon tes champs
        Match match = new Match();

        when(matchService.creerMatch(any(MatchDTO.class))).thenReturn(match);

        mockMvc.perform(post("/api/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void startMatch_ShouldReturnUpdatedMatch() throws Exception {
        when(matchService.commencerMatch(1L)).thenReturn(new Match());

        mockMvc.perform(put("/api/match/1/start"))
                .andExpect(status().isOk());
    }

    @Test
    void pauseMatch_ShouldReturnUpdatedMatch() throws Exception {
        when(matchService.mettreEnPause(1L)).thenReturn(new Match());

        mockMvc.perform(put("/api/match/1/pause"))
                .andExpect(status().isOk());
    }

    @Test
    void addPoint_ShouldReturnMatch() throws Exception {
        MatchPointDTO dto = new MatchPointDTO();
        when(matchService.ajouterPoint(eq(1L), eq(2L), any(MatchPointDTO.class))).thenReturn(new Match());

        mockMvc.perform(patch("/api/match/1/equipe/2/point")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addFaute_ShouldReturnMatch() throws Exception {
        MatchFauteDTO dto = new MatchFauteDTO();
        when(matchService.ajouterFaute(eq(1L), eq(2L), any(MatchFauteDTO.class))).thenReturn(new Match());

        mockMvc.perform(patch("/api/match/1/equipe/2/faute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMatch_ShouldCallService() throws Exception {
        doNothing().when(matchService).deleteById(1L);

        mockMvc.perform(delete("/api/match/1"))
                .andExpect(status().isOk());

        verify(matchService, times(1)).deleteById(1L);
    }
}