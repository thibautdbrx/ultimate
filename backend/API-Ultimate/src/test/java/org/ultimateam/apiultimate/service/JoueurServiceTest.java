package org.ultimateam.apiultimate.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.*;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.JoueurRepository;
import org.ultimateam.apiultimate.repository.JoueurRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoueurServiceTest {

    @Mock private JoueurRepository joueurRepository;
    @Mock private EquipeService equipeService;
    @Mock private JoueurRequestRepository joueurRequestRepository;
    @Mock private EquipeRepository equipeRepository;

    @InjectMocks private JoueurService joueurService;

    private Joueur joueur;
    private Equipe equipe;

    @BeforeEach
    void setUp() {
        joueur = new Joueur();
        joueur.setIdJoueur(1L);
        joueur.setNomJoueur("Doe");
        joueur.setPrenomJoueur("John");
        joueur.setGenre(GenreJoueur.HOMME);

        equipe = new Equipe();
        equipe.setIdEquipe(10L);
        equipe.setNomEquipe("Team A");
        // On initialise avec une ArrayList modifiable pour que les addJoueur fonctionnent
        equipe.setJoueurs(new ArrayList<>());
        equipe.setGenre(Genre.OPEN);
    }

    // --- BASIC CRUD TESTS ---

    @Test
    void getAll_ShouldReturnAllPlayers_WhenGenreIsNull() {
        when(joueurRepository.findAll()).thenReturn(List.of(joueur));
        List<Joueur> result = joueurService.getAll(null);
        assertEquals(1, result.size());
    }

    @Test
    void getById_ShouldReturnJoueur_WhenExists() {
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        Joueur result = joueurService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void addJoueur_ShouldSaveJoueur() {
        when(joueurRepository.save(joueur)).thenReturn(joueur);
        Joueur result = joueurService.addJoueur(joueur);
        assertEquals(joueur, result);
    }

    @Test
    void deleteJoueur_ShouldThrowException_WhenNotExists() {
        when(joueurRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> joueurService.deleteJoueur(1L));
    }

    @Test
    void editName_ShouldUpdateFields() {
        EditJoueurDTO dto = new EditJoueurDTO();
        dto.setNomJoueur("Smith");
        dto.setGenre(GenreJoueur.FEMME);

        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(joueurRepository.save(any(Joueur.class))).thenAnswer(i -> i.getArguments()[0]);

        Joueur result = joueurService.editName(dto, 1L);

        assertEquals("Smith", result.getNomJoueur());
        assertEquals(GenreJoueur.FEMME, result.getGenre());
    }

    @Test
    void updateJoueur_ShouldUpdatePhoto() {
        ImageDTO img = new ImageDTO();
        img.setImage("http://new-image.com");

        when(joueurRepository.existsById(1L)).thenReturn(true);
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(joueurRepository.save(any(Joueur.class))).thenAnswer(i -> i.getArguments()[0]);

        Joueur result = joueurService.updateJoueur(1L, img);
        assertEquals("http://new-image.com", result.getPhotoJoueur());
    }

    // --- ASSIGNER EQUIPE TESTS (LOGIQUE COMPLEXE) ---

    @Test
    void assignerEquipe_ShouldAssign_WhenRulesMet() {
        // Arrange
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeService.getById(10L)).thenReturn(equipe);

        // Act
        Joueur result = joueurService.assignerEquipe(1L, 10L);

        // Assert
        assertNotNull(result.getEquipe());
        assertEquals(10L, result.getEquipe().getIdEquipe());

        // On vérifie surtout que les sauvegardes ont bien été appelées
        verify(joueurRepository).save(joueur);
        verify(equipeService).save(equipe);
    }

    @Test
    void assignerEquipe_ShouldThrow_WhenJoueurAlreadyInTeam() {
        // On force la présence du joueur dans l'équipe
        equipe.setJoueurs(new ArrayList<>(List.of(joueur)));

        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeService.getById(10L)).thenReturn(equipe);

        assertThrows(ResponseStatusException.class, () -> joueurService.assignerEquipe(1L, 10L));
    }

    @Test
    void assignerEquipe_ShouldThrow_WhenTeamFull() {
        // On mock l'équipe pour forcer le isFull() à true sans remplir la liste
        Equipe mockEquipe = mock(Equipe.class);
        when(mockEquipe.getJoueurs()).thenReturn(new ArrayList<>());
        when(mockEquipe.isFull()).thenReturn(true);

        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeService.getById(10L)).thenReturn(mockEquipe);

        assertThrows(ResponseStatusException.class, () -> joueurService.assignerEquipe(1L, 10L));
    }

    @Test
    void assignerEquipe_ShouldThrow_WhenGenderQuotaExceeded_H2F3() {
        // Cas : Equipe H2F3 avec déjà 2 hommes -> doit refuser un 3ème homme
        equipe.setGenre(Genre.H2F3);

        Joueur h1 = new Joueur(); h1.setGenre(GenreJoueur.HOMME);
        Joueur h2 = new Joueur(); h2.setGenre(GenreJoueur.HOMME);
        // Important : on set directement la liste pour être sûr que getNbHommes itère dessus
        equipe.setJoueurs(new ArrayList<>(List.of(h1, h2)));

        // Le joueur qu'on essaie d'ajouter (this.joueur) est un HOMME (défini dans setUp)

        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeService.getById(10L)).thenReturn(equipe);

        assertThrows(ResponseStatusException.class, () -> joueurService.assignerEquipe(1L, 10L));
    }

    @Test
    void deleteEquipe_ShouldRemovePlayerFromTeam() {
        // Arrange : Le joueur est dans l'équipe
        equipe.addJoueur(joueur);
        joueur.setEquipe(equipe);

        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeService.getById(10L)).thenReturn(equipe);

        // Act
        Equipe result = joueurService.deleteEquipe(1L, 10L);

        // Assert
        assertNull(joueur.getEquipe());
        verify(equipeService).save(equipe);
        verify(joueurRepository).save(joueur);
    }

    // --- JOUEUR REQUEST TESTS ---

    @Test
    void demandeJoueur_ShouldCreateRequest_WhenValid() {
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));
        when(joueurRequestRepository.save(any(JoueurRequest.class))).thenAnswer(i -> i.getArguments()[0]);

        JoueurRequest result = joueurService.demandeJoueur(1L, 10L, 1L);

        assertNotNull(result);
        assertEquals(joueur, result.getJoueur());
        assertEquals(equipe, result.getEquipe());
    }

    @Test
    void demandeJoueur_ShouldThrow_WhenTokenMismatch() {
        // ID joueur (1L) != ID token (999L)
        assertThrows(AccessDeniedException.class, () -> joueurService.demandeJoueur(1L, 10L, 999L));
    }

    @Test
    void accepterDemande_ShouldAssignAndCleanupRequests() {
        JoueurRequestId reqId = new JoueurRequestId(1L, 10L);
        JoueurRequest req = new JoueurRequest(joueur, equipe);
        req.setId(reqId);

        when(joueurRequestRepository.findById(reqId)).thenReturn(Optional.of(req));

        // Mock des autres requêtes à supprimer
        when(joueurRequestRepository.findAll()).thenReturn(List.of(req));

        // Mock de l'assignation
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueur));
        when(equipeService.getById(10L)).thenReturn(equipe);

        joueurService.accepterDemande(1L, 10L);

        verify(joueurRequestRepository).deleteById(reqId);
        verify(joueurRequestRepository).deleteAll(anyList());
        // Vérifie que l'assignation a eu lieu
        verify(joueurRepository).save(joueur);
    }

    // --- GET JOUEUR SOLO (FILTRAGE) TESTS ---

    @Test
    void getJoueurSolo_ShouldFilterByTeamConstraints() {
        // Cas : Equipe H2F3 avec déjà 2 hommes -> on ne veut plus que des femmes
        equipe.setGenre(Genre.H2F3);
        Joueur h1 = new Joueur(); h1.setGenre(GenreJoueur.HOMME);
        Joueur h2 = new Joueur(); h2.setGenre(GenreJoueur.HOMME);
        // Important : on initialise bien la liste
        equipe.setJoueurs(new ArrayList<>(List.of(h1, h2))); // getNbHommes() renverra 2

        when(equipeRepository.findById(10L)).thenReturn(Optional.of(equipe));

        // Mock lenient pour éviter "PotentialStubbingProblem" si l'ordre des appels change
        // On dit à Mockito : "Si on demande des HOMMES, renvoie vide. Si on demande des FEMMES, renvoie une liste."
        lenient().when(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.HOMME))
                .thenReturn(new ArrayList<>());
        lenient().when(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.FEMME))
                .thenReturn(List.of(new Joueur()));

        // On simule que l'équipe contient déjà h1 et h2 en base
        when(joueurRepository.findAllByEquipe_IdEquipe(10L)).thenReturn(new ArrayList<>(List.of(h1, h2)));

        List<Joueur> result = joueurService.getJoueurSolo(10L);

        // Résultat attendu : 2 joueurs déjà dans l'équipe + 1 femme libre trouvée = 3
        assertEquals(3, result.size());
    }
}