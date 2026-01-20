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
import org.ultimateam.apiultimate.model.Terrain;
import org.ultimateam.apiultimate.service.TerrainService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TerrainControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TerrainService terrainService;

    @InjectMocks
    private TerrainController terrainController;

    @BeforeEach
    void setUp() {
        // Initialisation du contrôleur en isolation
        mockMvc = MockMvcBuilders.standaloneSetup(terrainController).build();
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        // Given
        Terrain t1 = new Terrain();
        t1.setVille("Paris");
        when(terrainService.getAll()).thenReturn(Arrays.asList(t1));

        // When & Then
        mockMvc.perform(get("/api/terrain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ville").value("Paris"));
    }

    @Test
    void getById_ShouldReturnTerrain() throws Exception {
        // Given
        Terrain terrain = new Terrain();
        terrain.setIdTerrain(1L);
        when(terrainService.getById(1L)).thenReturn(terrain);

        // When & Then
        mockMvc.perform(get("/api/terrain/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTerrain").value(1));
    }

    @Test
    void save_ShouldReturnSavedTerrain() throws Exception {
        // Given
        Terrain terrain = new Terrain();
        terrain.setVille("Lyon");
        // On mock le service pour qu'il retourne l'objet sauvegardé
        when(terrainService.saveTerrain(any(Terrain.class))).thenReturn(terrain);

        // When & Then
        mockMvc.perform(post("/api/terrain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terrain)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ville").value("Lyon"));
    }

    @Test
    void deleteById_ShouldCallService() throws Exception {
        // Given
        long idToDelete = 1L;
        doNothing().when(terrainService).deleteById(idToDelete);

        // When & Then
        mockMvc.perform(delete("/api/terrain/{id}", idToDelete))
                .andExpect(status().isOk());

        // On vérifie que le service a bien été appelé
        verify(terrainService, times(1)).deleteById(idToDelete);
    }
}