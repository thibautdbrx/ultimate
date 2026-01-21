package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EditJoueurDTO;
import org.ultimateam.apiultimate.DTO.GenreJoueur;
import org.ultimateam.apiultimate.DTO.ImageDTO;
import org.ultimateam.apiultimate.configuration.JwtUtils;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.service.JoueurService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités {@link Joueur}.
 */
@RestController
@RequestMapping("/api/joueur")
@RequiredArgsConstructor
@Tag(name = "Joueur", description = "Endpoints pour gérer les joueurs")
public class JoueurController {

    private final JoueurService joueurService;
    private final JwtUtils jwtUtils;

    /**
     * Récupère la liste de tous les joueurs, avec un filtre optionnel par genre.
     *
     * @param genre Genre optionnel pour filtrer les joueurs.
     * @return Une liste de tous les {@link Joueur}.
     */
    @Operation(summary = "Lister tous les joueurs", description = "Retourne la liste de tous les joueurs. Filtre par genre optionnel.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Joueur> getAllJoueurs(@RequestParam(required = false) GenreJoueur genre) {
        return joueurService.getAll(genre);
    }

    /**
     * Récupère un joueur par son identifiant.
     *
     * @param id Identifiant unique du joueur.
     * @return Le {@link Joueur} correspondant.
     */
    @Operation(summary = "Récupérer un joueur", description = "Retourne le joueur correspondant à l'identifiant fourni.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Joueur getJoueurById(@PathVariable Long id) {
        return joueurService.getById(id);
    }

    /**
     * Récupère la liste des joueurs appartenant à une équipe spécifique.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Une liste de {@link Joueur}.
     */
    @Operation(summary = "Lister les joueurs d'une équipe", description = "Retourne la liste des joueurs appartenant à l'équipe donnée.")
    @GetMapping("/equipe/{idEquipe}")
    @PreAuthorize("permitAll()")
    public List<Joueur> getJoueurByEquipe(@PathVariable Long idEquipe) {
        return joueurService.getJoueurByEquipe(idEquipe);
    }

    /**
     * Récupère la liste des joueurs non assignés à une équipe.
     *
     * @param idEquipe Filtre optionnel par ID d'équipe.
     * @return Une liste de {@link Joueur} sans équipe.
     */
    @Operation(summary = "Lister les joueurs sans équipe", description = "Retourne la liste des joueurs disponibles (solo).")
    @GetMapping("/solo/")
    @PreAuthorize("permitAll()")
    public List<Joueur> getJoueurSolo(@RequestParam(required = false) Long idEquipe) {
        return joueurService.getJoueurSolo(idEquipe);
    }

    /**
     * Crée un nouveau joueur (Profil).
     * Réservé aux administrateurs.
     *
     * @param joueur Objet {@link Joueur} à créer.
     * @return Le {@link Joueur} créé.
     */
    @Operation(summary = "Créer un joueur", description = "Crée un nouveau profil joueur. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Joueur createJoueur(@RequestBody Joueur joueur) {
        return joueurService.addJoueur(joueur);
    }

    /**
     * Assigne un joueur à une équipe (Force administrative).
     * Réservé aux administrateurs.
     *
     * @param idJoueur Identifiant du joueur.
     * @param idEquipe Identifiant de l'équipe.
     * @return Le {@link Joueur} mis à jour.
     */
    @Operation(summary = "Assigner un joueur (Admin)", description = "Assigne administrativement un joueur à une équipe. Réservé aux administrateurs.")
    @PatchMapping("/{idJoueur}/equipe/{idEquipe}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Joueur assignerEquipe(@PathVariable long idJoueur, @PathVariable long idEquipe) {
        return joueurService.assignerEquipe(idJoueur, idEquipe);
    }

    /**
     * Permet à un joueur connecté de demander à rejoindre une équipe.
     *
     * @param idJoueur Identifiant du joueur.
     * @param idEquipe Identifiant de l'équipe cible.
     * @param authHeader Token d'authentification.
     * @return La demande créée.
     */
    @Operation(summary = "Demander à rejoindre une équipe", description = "Le joueur connecté demande à rejoindre une équipe.")
    @PostMapping("/request/{idJoueur}/equipe/{idEquipe}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JoueurRequest> requestJoueur(
            @PathVariable long idJoueur,
            @PathVariable long idEquipe,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long joueurIdToken = jwtUtils.extractJoueurId(token);
        JoueurRequest request = joueurService.demandeJoueur(idJoueur, idEquipe, joueurIdToken);
        return ResponseEntity.ok(request);
    }

    /**
     * Accepte une demande d'adhésion.
     * Accessible aux utilisateurs authentifiés (ex: capitaine/gestionnaire) ou Admin.
     *
     * @param idJoueur Identifiant du joueur.
     * @param idEquipe Identifiant de l'équipe.
     * @return Le joueur intégré à l'équipe.
     */
    @Operation(summary = "Accepter une demande", description = "Accepte la demande d'un joueur pour rejoindre l'équipe.")
    @PatchMapping("/request/{idJoueur}/equipe/{idEquipe}/accept")
    @PreAuthorize("isAuthenticated()")
    public Joueur accepterDemande(@PathVariable long idJoueur, @PathVariable long idEquipe) {
        return joueurService.accepterDemande(idJoueur, idEquipe);
    }

    /**
     * Refuse une demande d'adhésion.
     *
     * @param idJoueur Identifiant du joueur.
     * @param idEquipe Identifiant de l'équipe.
     */
    @Operation(summary = "Refuser une demande", description = "Refuse la demande d'un joueur.")
    @PatchMapping("/request/{idJoueur}/equipe/{idEquipe}/refuse")
    @PreAuthorize("isAuthenticated()")
    public void refuseDemande(@PathVariable long idJoueur, @PathVariable long idEquipe) {
        joueurService.refuseDemande(idJoueur, idEquipe);
    }

    /**
     * Récupère toutes les demandes en attente.
     *
     * @return Liste des demandes.
     */
    @Operation(summary = "Lister les demandes", description = "Affiche toutes les demandes d'adhésion en cours.")
    @GetMapping("/requests")
    @PreAuthorize("isAuthenticated()")
    public List<JoueurRequest> getRequests() {
        return joueurService.getAllRequests();
    }

    /**
     * Met à jour l'image d'un joueur.
     * Accessible au joueur lui-même (authentifié).
     *
     * @param imageDTO DTO contenant l'image.
     * @param idJoueur Identifiant du joueur.
     * @return Le joueur mis à jour.
     */
    @Operation(summary = "Mettre à jour l'image", description = "Modifie l'avatar du joueur connecté.")
    @PatchMapping("/{idJoueur}")
    @PreAuthorize("isAuthenticated()")
    public Joueur editImage(@RequestBody ImageDTO imageDTO, @PathVariable long idJoueur) {
        return joueurService.updateJoueur(idJoueur, imageDTO);
    }

    /**
     * Met à jour les informations textuelles d'un joueur.
     * Accessible au joueur lui-même (authentifié).
     *
     * @param nameDTO DTO contenant les infos.
     * @param idJoueur Identifiant du joueur.
     * @return Le joueur mis à jour.
     */
    @Operation(summary = "Modifier les infos", description = "Modifie le nom/prénom du joueur connecté.")
    @PatchMapping("/{idJoueur}/name")
    @PreAuthorize("isAuthenticated()")
    public Joueur editNameJoueur(@RequestBody EditJoueurDTO nameDTO, @PathVariable long idJoueur) {
        return joueurService.editName(nameDTO, idJoueur);
    }

    /**
     * Retire un joueur d'une équipe.
     * Réservé aux administrateurs.
     *
     * @param idJoueur Identifiant du joueur.
     * @param idEquipe Identifiant de l'équipe.
     * @return L'équipe mise à jour.
     */
    @Operation(summary = "Retirer un joueur d'une équipe", description = "Supprime le lien entre un joueur et une équipe. Réservé aux administrateurs.")
    @DeleteMapping("/{idJoueur}/equipe/{idEquipe}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Equipe deleteEquipe(@PathVariable Long idJoueur, @PathVariable Long idEquipe) {
        return joueurService.deleteEquipe(idJoueur, idEquipe);
    }

    /**
     * Supprime définitivement un joueur.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du joueur à supprimer.
     */
    @Operation(summary = "Supprimer un joueur", description = "Supprime définitivement un profil joueur. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteJoueur(@PathVariable Long id) {
        joueurService.deleteJoueur(id);
    }
}