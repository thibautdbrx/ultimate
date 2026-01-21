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
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.service.ChampionnatService;
import org.ultimateam.apiultimate.service.CompetitionService;
import org.ultimateam.apiultimate.service.TournoisService;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/*
@ExtendWith(MockitoExtension.class)
class CompetitionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private TournoisService tournoisService;

    @Mock
    private ChampionnatService championnatService;

    @InjectMocks
    private CompetitionController competitionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(competitionController).build();
        // Initialisation de l'objectMapper avec le module JavaTime pour gérer LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void findAll_ShouldReturnCompetitions() throws Exception {
        // On utilise Championnat car Competition est abstraite
        Championnat comp = new Championnat();
        comp.setNomCompetition("Ligue 1");

        when(competitionService.getAllCompetition()).thenReturn(Arrays.asList(comp));

        mockMvc.perform(get("/api/competition"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomCompetition").value("Ligue 1"));
    }

    @Test
    void creerCompetition_ShouldReturnSavedCompetition() throws Exception {
        // Utilisation du constructeur de la classe concrète Championnat
        // On utilise l'Enum Genre de ton DTO et Format de ta classe Competition
        Championnat championnat = new Championnat();
        championnat.setNomCompetition("Tournoi d'été");
        championnat.setGenre(Genre.HOMME);
        championnat.setFormat(Competition.Format.V5);
        championnat.setDateDebut(LocalDate.now());
        championnat.setDateFin(LocalDate.now().plusDays(2));

        when(competitionService.saveCompetition(any(Competition.class))).thenReturn(championnat);

        mockMvc.perform(post("/api/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(championnat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomCompetition").value("Tournoi d'été"));
    }

    @Test
    void findById_ShouldReturnCompetition() throws Exception {
        Championnat comp = new Championnat();
        comp.setIdCompetition(1L);
        when(competitionService.getCompetitionById(1L)).thenReturn(comp);

        mockMvc.perform(get("/api/competition/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompetition").value(1));
    }

    @Test
    void deleteById_ShouldCallService() throws Exception {
        doNothing().when(competitionService).deleteCompetitionById(1L);

        mockMvc.perform(delete("/api/competition/1"))
                .andExpect(status().isOk());

        verify(competitionService, times(1)).deleteCompetitionById(1L);
    }

    @Test
    void ajouterTerrain_ShouldReturnUpdatedCompetition() throws Exception {
        Championnat comp = new Championnat();
        when(competitionService.ajouterTerrainACompetition(1L, 5L)).thenReturn(comp);

        mockMvc.perform(post("/api/competition/1/terrain/5"))
                .andExpect(status().isOk());
    }
}
*/