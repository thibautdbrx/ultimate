package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EditJoueurDTO;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.DTO.GenreJoueur;
import org.ultimateam.apiultimate.DTO.ImageDTO;
import org.ultimateam.apiultimate.configuration.JwtUtils;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.service.JoueurService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités Joueur.
 * Expose les points de terminaison (endpoints) de l'API pour les opérations CRUD sur les joueurs
 * et la gestion de leur assignation aux équipes.
 */
@RestController
@Tag(name = "Joueur", description = "Endpoints pour gérer les joueurs")
@RequestMapping("/api/joueur")
public class JoueurController {

    private final JoueurService joueurService;
    private final JwtUtils jwtUtils;

    public JoueurController(JoueurService joueurService, JwtUtils jwtUtils) {
        this.joueurService = joueurService;
        this.jwtUtils = jwtUtils;
    }

    @Operation(
            summary = "Lister tous les joueurs",
            description = "Retourne la liste de tous les joueurs. Il est possible de filtrer par genre via un paramètre optionnel."
    )
    @GetMapping
    public List<Joueur> getAllJoueurs(@RequestParam(required = false) GenreJoueur genre ) {
        return joueurService.getAll(genre);
    }

    @Operation(
            summary = "Récupérer un joueur par son identifiant",
            description = "Retourne le joueur correspondant à l'identifiant fourni. Une erreur est renvoyée si le joueur n'existe pas."
    )
    @GetMapping("/{id}")
    public Joueur getJoueurById(@PathVariable Long id) {
        return joueurService.getById(id);
    }

    @Operation(
            summary = "Lister les joueurs d'une équipe",
            description = "Retourne la liste des joueurs appartenant à l'équipe identifiée par son id."
    )
    @GetMapping("/equipe/{idEquipe}")
    public List<Joueur> getJoueurByEquipe(@PathVariable Long idEquipe) {
        return joueurService.getJoueurByEquipe(idEquipe);
    }

    @Operation(
            summary = "Lister les joueurs sans équipe",
            description = "Retourne la liste des joueurs non assignés à une équipe. Un filtre par genre peut être appliqué."
    )
    @GetMapping("/solo")
    public List<Joueur> getJoueurSolo(@RequestParam(required = false) GenreJoueur genre) {
        return joueurService.getJoueurSolo(genre);
    }

    @Operation(
            summary = "Créer un nouveau joueur",
            description = "Crée un nouveau joueur à partir des informations fournies dans le corps de la requête."
    )
    @PostMapping
    public Joueur createJoueur(@RequestBody Joueur joueur) {
        return joueurService.addJoueur(joueur);
    }

    @Operation(
            summary = "Assigner un joueur à une équipe",
            description = "Assigne un joueur existant à une équipe existante. Une erreur est renvoyée si le joueur ou l'équipe n'existe pas."
    )
    @PatchMapping("/{idJoueur}/equipe/{idEquipe}")
    public Joueur assignerEquipe(@PathVariable long idJoueur, @PathVariable long idEquipe) { return joueurService.assignerEquipe(idJoueur, idEquipe); }

    @PostMapping("/request/{idJoueur}/equipe/{idEquipe}")
    public ResponseEntity<JoueurRequest> requestJoueur(
            @PathVariable long idJoueur,
            @PathVariable long idEquipe,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long joueurIdToken = jwtUtils.extractJoueurId(token);

        JoueurRequest request = joueurService.demandeJoueur(idJoueur, idEquipe, joueurIdToken);
        return ResponseEntity.ok(request);
    }

    @PatchMapping("/request/{idJoueur}/equipe/{idEquipe}/accept")
    public Joueur accepterDemande(@PathVariable long idJoueur, @PathVariable long idEquipe) {return joueurService.accepterDemande(idJoueur, idEquipe); }

    @PatchMapping("/request/{idJoueur}/equipe/{idEquipe}/refuse")
    public void refuseDemande(@PathVariable long idJoueur, @PathVariable long idEquipe) {joueurService.refuseDemande(idJoueur, idEquipe);}

    @Operation(
            summary = "Mettre à jour l'image d'un joueur",
            description = "Met à jour l'image ou l'avatar associé à un joueur."
    )
    @PatchMapping("/{idJoueur}")
    public Joueur editImage(@RequestBody ImageDTO imageDTO, @PathVariable long idJoueur) { return joueurService.updateJoueur(idJoueur, imageDTO);}

    @Operation(
            summary = "Modifier les informations d'un joueur",
            description = "Permet de modifier le nom et/ou les informations principales d'un joueur."
    )
    @PatchMapping("/{idJoueur}/name")
    public Joueur editNameJoueur(@RequestBody EditJoueurDTO nameDTO, @PathVariable long idJoueur) { return joueurService.editName(nameDTO, idJoueur);}

    @Operation(
            summary = "Retirer un joueur de son équipe",
            description = "Supprime l'association entre un joueur et une équipe."
    )
    @DeleteMapping("/{idJoueur}/equipe/{idEquipe}")
    public Equipe deleteEquipe(@PathVariable Long idJoueur, @PathVariable Long idEquipe) { return joueurService.deleteEquipe(idJoueur, idEquipe); }

    @Operation(
            summary = "Supprimer un joueur",
            description = "Supprime définitivement le joueur correspondant à l'identifiant fourni."
    )
    @DeleteMapping("/{id}")
    public void deleteJoueur(@PathVariable Long id) {
        joueurService.deleteJoueur(id);
    }
}
