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
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.service.ActionMatchService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ActionMatchControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ActionMatchService actionMatchService;

    @InjectMocks
    private ActionMatchController actionMatchController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(actionMatchController).build();
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        when(actionMatchService.findAll()).thenReturn(Arrays.asList(new ActionMatch()));

        mockMvc.perform(get("/api/action-match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByMatchId_ShouldReturnList() throws Exception {
        when(actionMatchService.findByMatchId(1L)).thenReturn(Arrays.asList(new ActionMatch()));

        mockMvc.perform(get("/api/action-match/match/1"))
                .andExpect(status().isOk());
    }

    @Test
    void findByActionType_ShouldReturnList() throws Exception {
        // Test avec l'enum ActionTypeDTO (ex: POINT ou FAUTE)
        when(actionMatchService.findByActionType(ActionTypeDTO.POINT)).thenReturn(Arrays.asList(new ActionMatch()));

        mockMvc.perform(get("/api/action-match/type/POINT"))
                .andExpect(status().isOk());
    }

    @Test
    void findByActionAndJoueurAndMatchId_ShouldReturnList() throws Exception {
        when(actionMatchService.findByActionAndJoueurAndMatchId(ActionTypeDTO.FAUTE, 1L, 2L))
                .thenReturn(Arrays.asList(new ActionMatch()));

        mockMvc.perform(get("/api/action-match/match/1/joueur/2/type/FAUTE"))
                .andExpect(status().isOk());
    }

    @Test
    void addPoint_ShouldUseRequestParams() throws Exception {
        // CORRECTION : Ajout de any(java.time.LocalDateTime.class) car le controller appelle LocalDateTime.now()
        when(actionMatchService.addPoint(eq(1L), eq(2L), any(MatchPointDTO.class), any(java.time.LocalDateTime.class)))
                .thenReturn(new ActionMatch());

        mockMvc.perform(post("/api/action-match/1/equipe/2/point")
                        .param("idJoueurPoint", "10")
                        .param("idJoueurSert", "11"))
                .andExpect(status().isOk());
    }

    @Test
    void addFaute_ShouldUseRequestBody() throws Exception {
        // CORRECTION : Ajout de any(java.time.LocalDateTime.class) pour correspondre au 4ème paramètre
        MatchFauteDTO dto = new MatchFauteDTO();
        when(actionMatchService.addFaute(eq(1L), eq(2L), any(MatchFauteDTO.class), any(java.time.LocalDateTime.class)))
                .thenReturn(new ActionMatch());

        mockMvc.perform(post("/api/action-match/1/equipe/2/faute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}