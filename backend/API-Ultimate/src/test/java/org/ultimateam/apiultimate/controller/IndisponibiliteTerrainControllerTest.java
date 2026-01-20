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
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.service.IndisponibiliteTerrainService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IndisponibiliteTerrainControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IndisponibiliteTerrainService service;

    @InjectMocks
    private IndisponibiliteTerrainController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        // Préparation de l'objectMapper pour transformer les objets en JSON
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        IndisponibiliteTerrain indispo = new IndisponibiliteTerrain();
        when(service.findAll()).thenReturn(Arrays.asList(indispo));

        mockMvc.perform(get("/api/indisponibilites-terrain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByTerrainId_ShouldReturnFilteredList() throws Exception {
        Long idTerrain = 1L;
        when(service.findByTerrainId(idTerrain)).thenReturn(Arrays.asList(new IndisponibiliteTerrain()));

        mockMvc.perform(get("/api/indisponibilites-terrain/terrain/{idTerrain}", idTerrain))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_ShouldReturnSavedIndisponibilite() throws Exception {
        Long idTerrain = 1L;
        IndisponibiliteTerrain indispo = new IndisponibiliteTerrain();
        // Si ton entité a un ID généré après sauvegarde, tu peux le setter ici pour le test

        when(service.save(eq(idTerrain), any(IndisponibiliteTerrain.class))).thenReturn(indispo);

        mockMvc.perform(post("/api/indisponibilites-terrain")
                        .param("idTerrain", idTerrain.toString()) // Utilisation de @RequestParam
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(indispo)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ShouldCallService() throws Exception {
        Long idToDelete = 1L;
        doNothing().when(service).deleteById(idToDelete);

        mockMvc.perform(delete("/api/indisponibilites-terrain/{id}", idToDelete))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteById(idToDelete);
    }
}