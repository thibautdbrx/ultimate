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
import org.ultimateam.apiultimate.DTO.EditJoueurDTO;
import org.ultimateam.apiultimate.DTO.GenreJoueur;
import org.ultimateam.apiultimate.DTO.ImageDTO;
import org.ultimateam.apiultimate.configuration.JwtUtils;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.service.JoueurService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JoueurControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private JoueurService joueurService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private JoueurController joueurController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(joueurController).build();
    }

    @Test
    void getAllJoueurs_ShouldReturnList() throws Exception {
        when(joueurService.getAll(any())).thenReturn(Arrays.asList(new Joueur()));

        mockMvc.perform(get("/api/joueur")
                        .param("genre", "HOMME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getJoueurById_ShouldReturnJoueur() throws Exception {
        Joueur joueur = new Joueur();
        joueur.setIdJoueur(1L);
        when(joueurService.getById(1L)).thenReturn(joueur);

        mockMvc.perform(get("/api/joueur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idJoueur").value(1));
    }

    @Test
    void createJoueur_ShouldReturnSavedJoueur() throws Exception {
        Joueur joueur = new Joueur();
        when(joueurService.addJoueur(any(Joueur.class))).thenReturn(joueur);

        mockMvc.perform(post("/api/joueur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joueur)))
                .andExpect(status().isOk());
    }

    @Test
    void assignerEquipe_ShouldReturnUpdatedJoueur() throws Exception {
        when(joueurService.assignerEquipe(1L, 2L)).thenReturn(new Joueur());

        mockMvc.perform(patch("/api/joueur/1/equipe/2"))
                .andExpect(status().isOk());
    }

    @Test
    void requestJoueur_ShouldReturnRequest() throws Exception {
        String mockToken = "Bearer mon.super.token";
        when(jwtUtils.extractJoueurId(anyString())).thenReturn(10L);
        when(joueurService.demandeJoueur(eq(1L), eq(2L), eq(10L))).thenReturn(new JoueurRequest());

        mockMvc.perform(post("/api/joueur/request/1/equipe/2")
                        .header("Authorization", mockToken))
                .andExpect(status().isOk());
    }

    @Test
    void editImage_ShouldReturnJoueur() throws Exception {
        ImageDTO dto = new ImageDTO();
        // dto.setImage("path/to/img"); // Décommenter si le setter existe

        when(joueurService.updateJoueur(eq(1L), any(ImageDTO.class))).thenReturn(new Joueur());

        mockMvc.perform(patch("/api/joueur/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void editNameJoueur_ShouldReturnJoueur() throws Exception {
        EditJoueurDTO dto = new EditJoueurDTO();
        // dto.setNom("NouveauNom");

        when(joueurService.editName(any(EditJoueurDTO.class), eq(1L))).thenReturn(new Joueur());

        mockMvc.perform(patch("/api/joueur/1/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteJoueur_ShouldCallService() throws Exception {
        doNothing().when(joueurService).deleteJoueur(1L);

        mockMvc.perform(delete("/api/joueur/1"))
                .andExpect(status().isOk());

        verify(joueurService, times(1)).deleteJoueur(1L);
    }

    @Test
    void deleteEquipe_ShouldReturnEquipe() throws Exception {
        when(joueurService.deleteEquipe(1L, 2L)).thenReturn(new Equipe());

        mockMvc.perform(delete("/api/joueur/1/equipe/2"))
                .andExpect(status().isOk());
    }
}