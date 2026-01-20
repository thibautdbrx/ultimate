package org.ultimateam.apiultimate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.JoueurRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock EquipeRepository equipeRepository;
    @Mock JoueurRepository joueurRepository;

    @InjectMocks EquipeService equipeService;

    @Test
    void testGetById() {
        Equipe e = new Equipe();
        e.setIdEquipe(1L);
        e.setNomEquipe("Toulouse Ultimate");

        when(equipeRepository.findById(1L)).thenReturn(Optional.of(e));

        Equipe result = equipeService.getById(1L);
        assertNotNull(result);
        assertEquals("Toulouse Ultimate", result.getNomEquipe());
    }

    @Test
    void testGetNbJoueurs() {
        when(joueurRepository.countByEquipe_IdEquipe(1L)).thenReturn(7);
        int nb = equipeService.getNbJoueurs(1L);
        assertEquals(7, nb);
    }
}