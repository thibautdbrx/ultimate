package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.IndisponibiliteService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des indisponibilités des équipes.
 *
 * Ce contrôleur expose des endpoints pour lister, créer, mettre à jour et supprimer
 * les périodes d'indisponibilité des équipes. Une indisponibilité est définie par une période
 * (date de début et date de fin) pendant laquelle une équipe ne peut pas participer à des matchs.
 */
@RestController
@Tag(name = "Indisponibilite", description = "Endpoints pour gérer les indisponibilités des équipes")
@RequestMapping("/api/indisponibilite")
public class IndisponibiliteController {

    /** Service utilisé pour gérer les opérations liées aux indisponibilités. */
    private final IndisponibiliteService indisponibiliteService;

    /**
     * Constructeur du contrôleur.
     *
     * @param indisponibiliteService Service injecté pour gérer les indisponibilités.
     */
    public IndisponibiliteController(IndisponibiliteService indisponibiliteService) {
        this.indisponibiliteService = indisponibiliteService;
    }

    /**
     * Récupère la liste complète de toutes les indisponibilités des équipes.
     *
     * @return Une liste de {@link IndisponibiliteDTO} représentant toutes les indisponibilités enregistrées.
     */
    @Operation(
            summary = "Lister toutes les indisponibilités",
            description = "Retourne la liste complète de toutes les indisponibilités des équipes."
    )
    @GetMapping
    public List<IndisponibiliteDTO> getAllIndisponibilites() {
        return indisponibiliteService.getAllDTO();
    }


    /**
     * Récupère une indisponibilité par son identifiant.
     *
     * @param id Identifiant unique de l'indisponibilité.
     * @return Le {@link IndisponibiliteDTO} correspondant à l'identifiant fourni.
     */
    @Operation(
            summary = "Récupérer une indisponibilité par son identifiant",
            description = "Retourne l'indisponibilité correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique de l'indisponibilité.",
            required = true
    )
    @GetMapping("/{id}")
    public IndisponibiliteDTO getIndisponibiliteById(@PathVariable Long id) {
        return indisponibiliteService.getByIdDTO(id);
    }

    /**
     * Récupère la liste des indisponibilités pour une équipe spécifique.
     *
     * @param id Identifiant unique de l'équipe.
     * @return Une liste de {@link IndisponibiliteDTO} pour l'équipe identifiée.
     */
    @Operation(
            summary = "Lister les indisponibilités d'une équipe",
            description = "Retourne la liste des indisponibilités pour l'équipe identifiée par son id."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique de l'équipe.",
            required = true
    )
    @GetMapping("/equipe/{id}")
    public List<IndisponibiliteDTO> getEquipeIndisponibilite(@PathVariable Long id){
        return indisponibiliteService.getEquipeIndisponibilite(id);
    }

    /**
     * Crée une nouvelle indisponibilité pour une équipe.
     *
     * @param dto Objet {@link IndisponibiliteDTO} contenant les informations de l'indisponibilité à créer.
     *            Les dates doivent être au format "yyyy-MM-dd HH:mm".
     * @return Le {@link IndisponibiliteDTO} nouvellement créé.
     */
    @Operation(
            summary = "Créer une indisponibilité",
            description = "Crée une nouvelle indisponibilité pour une équipe à partir des informations fournies."
    )
    @PostMapping
    public IndisponibiliteDTO createIndisponibilite(@RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.addIndisponibilite(dto);
    }
    /**
     * Met à jour une indisponibilité existante.
     *
     * @param idIndisponibilite Identifiant unique de l'indisponibilité à mettre à jour.
     * @param dto Objet {@link IndisponibiliteDTO} contenant les nouvelles informations de l'indisponibilité.
     *            Les dates doivent être au format "yyyy-MM-dd HH:mm".
     * @return Le {@link IndisponibiliteDTO} mis à jour.
     */
    //Json = "dateDebut" : "yyyy-MM-dd HH:mm", "dateFin" : "yyyy-MM-dd HH:mm"

    @Operation(
            summary = "Mettre à jour une indisponibilité",
            description = "Met à jour une indisponibilité existante identifiée par son id."
    )
    @Parameter(
            name = "idIndisponibilite",
            description = "Identifiant unique de l'indisponibilité à mettre à jour.",
            required = true
    )
    @PutMapping("/{idIndisponibilite}")
    public IndisponibiliteDTO updateIndisponibilite(@PathVariable Long idIndisponibilite, @RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.updateIndisponibilite(dto, idIndisponibilite);
    }

    /**
     * Supprime une indisponibilité par son identifiant.
     *
     * @param idIndisponibilite Identifiant unique de l'indisponibilité à supprimer.
     */
    @Operation(
            summary = "Supprimer une indisponibilité",
            description = "Supprime l'indisponibilité correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "idIndisponibilite",
            description = "Identifiant unique de l'indisponibilité à supprimer.",
            required = true
    )
    @DeleteMapping("/{idIndisponibilite}")
    public void deleteIndisponibilite(@PathVariable Long idIndisponibilite) {
        indisponibiliteService.deleteIndisponibilite(idIndisponibilite);
    }


}
