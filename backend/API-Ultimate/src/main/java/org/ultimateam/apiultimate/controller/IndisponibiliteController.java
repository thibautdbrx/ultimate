package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.service.IndisponibiliteService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des indisponibilités des équipes.
 */
@RestController
@RequestMapping("/api/indisponibilite")
@RequiredArgsConstructor
@Tag(name = "Indisponibilite", description = "Endpoints pour gérer les indisponibilités des équipes")
public class IndisponibiliteController {

    private final IndisponibiliteService indisponibiliteService;

    /**
     * Récupère la liste complète de toutes les indisponibilités.
     *
     * @return Une liste de {@link IndisponibiliteDTO}.
     */
    @Operation(summary = "Lister toutes les indisponibilités", description = "Retourne la liste complète de toutes les indisponibilités enregistrées.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<IndisponibiliteDTO> getAllIndisponibilites() {
        return indisponibiliteService.getAllDTO();
    }

    /**
     * Récupère une indisponibilité par son identifiant.
     *
     * @param id Identifiant unique de l'indisponibilité.
     * @return Le {@link IndisponibiliteDTO} correspondant.
     */
    @Operation(summary = "Récupérer une indisponibilité", description = "Retourne l'indisponibilité correspondant à l'identifiant fourni.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public IndisponibiliteDTO getIndisponibiliteById(@PathVariable Long id) {
        return indisponibiliteService.getByIdDTO(id);
    }

    /**
     * Récupère la liste des indisponibilités pour une équipe spécifique.
     *
     * @param id Identifiant unique de l'équipe.
     * @return Une liste de {@link IndisponibiliteDTO}.
     */
    @Operation(summary = "Lister les indisponibilités d'une équipe", description = "Retourne la liste des indisponibilités pour l'équipe donnée.")
    @GetMapping("/equipe/{id}")
    @PreAuthorize("permitAll()")
    public List<IndisponibiliteDTO> getEquipeIndisponibilite(@PathVariable Long id) {
        return indisponibiliteService.getEquipeIndisponibilite(id);
    }

    /**
     * Crée une nouvelle indisponibilité pour une équipe.
     * Réservé aux administrateurs.
     *
     * @param dto Objet {@link IndisponibiliteDTO} à créer.
     * @return Le {@link IndisponibiliteDTO} créé.
     */
    @Operation(summary = "Créer une indisponibilité", description = "Crée une nouvelle indisponibilité. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IndisponibiliteDTO createIndisponibilite(@RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.addIndisponibilite(dto);
    }

    /**
     * Met à jour une indisponibilité existante.
     * Réservé aux administrateurs.
     *
     * @param idIndisponibilite Identifiant de l'indisponibilité à mettre à jour.
     * @param dto Objet contenant les nouvelles informations.
     * @return Le {@link IndisponibiliteDTO} mis à jour.
     */
    @Operation(summary = "Mettre à jour une indisponibilité", description = "Met à jour une indisponibilité existante. Réservé aux administrateurs.")
    @PutMapping("/{idIndisponibilite}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IndisponibiliteDTO updateIndisponibilite(@PathVariable Long idIndisponibilite, @RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.updateIndisponibilite(dto, idIndisponibilite);
    }

    /**
     * Supprime une indisponibilité.
     * Réservé aux administrateurs.
     *
     * @param idIndisponibilite Identifiant de l'indisponibilité à supprimer.
     */
    @Operation(summary = "Supprimer une indisponibilité", description = "Supprime une indisponibilité. Réservé aux administrateurs.")
    @DeleteMapping("/{idIndisponibilite}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteIndisponibilite(@PathVariable Long idIndisponibilite) {
        indisponibiliteService.deleteIndisponibilite(idIndisponibilite);
    }
}