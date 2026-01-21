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
 * Contrôleur REST pour la gestion des entités {@link Joueur}.
 *
 * Ce contrôleur expose les endpoints de l'API pour les opérations CRUD sur les joueurs,
 * ainsi que pour la gestion de leur assignation aux équipes, des demandes d'adhésion,
 * et la mise à jour de leurs informations personnelles.
 */
@RestController
@Tag(name = "Joueur", description = "Endpoints pour gérer les joueurs")
@RequestMapping("/api/joueur")
public class JoueurController {

    /** Service utilisé pour gérer les opérations liées aux joueurs. */
    private final JoueurService joueurService;
    /** Utilitaire pour manipuler les tokens JWT et extraire les informations des joueurs authentifiés. */
    private final JwtUtils jwtUtils;

    /**
     * Constructeur du contrôleur.
     *
     * @param joueurService Service injecté pour gérer les joueurs.
     * @param jwtUtils Utilitaire pour manipuler les tokens JWT.
     */
    public JoueurController(JoueurService joueurService, JwtUtils jwtUtils) {
        this.joueurService = joueurService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Récupère la liste de tous les joueurs, avec un filtre optionnel par genre.
     *
     * @param genre Genre optionnel pour filtrer les joueurs (Homme, Femme, Mixte, etc.).
     * @return Une liste de tous les {@link Joueur} correspondant au filtre.
     */
    @Operation(
            summary = "Lister tous les joueurs",
            description = "Retourne la liste de tous les joueurs. Il est possible de filtrer par genre via un paramètre optionnel."
    )
    @GetMapping
    public List<Joueur> getAllJoueurs(@RequestParam(required = false) GenreJoueur genre ) {
        return joueurService.getAll(genre);
    }

    /**
     * Récupère un joueur par son identifiant.
     *
     * @param id Identifiant unique du joueur.
     * @return Le {@link Joueur} correspondant à l'identifiant fourni.
     * @throws RuntimeException Si le joueur n'existe pas.
     */
    @Operation(
            summary = "Récupérer un joueur par son identifiant",
            description = "Retourne le joueur correspondant à l'identifiant fourni. Une erreur est renvoyée si le joueur n'existe pas."
    )
    @GetMapping("/{id}")
    public Joueur getJoueurById(@PathVariable Long id) {
        return joueurService.getById(id);
    }

    /**
     * Récupère la liste des joueurs appartenant à une équipe spécifique.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Une liste de {@link Joueur} appartenant à l'équipe.
     */
    @Operation(
            summary = "Lister les joueurs d'une équipe",
            description = "Retourne la liste des joueurs appartenant à l'équipe identifiée par son id."
    )
    @GetMapping("/equipe/{idEquipe}")
    public List<Joueur> getJoueurByEquipe(@PathVariable Long idEquipe) {
        return joueurService.getJoueurByEquipe(idEquipe);
    }

    /**
     * Récupère la liste des joueurs non assignés à une équipe, avec un filtre optionnel par genre.
     *
     * @param genre Genre optionnel pour filtrer les joueurs sans équipe.
     * @return Une liste de {@link Joueur} non assignés à une équipe.
     */
    @Operation(
            summary = "Lister les joueurs sans équipe",
            description = "Retourne la liste des joueurs non assignés à une équipe. Un filtre par genre peut être appliqué."
    )
    @GetMapping("/solo")
    public List<Joueur> getJoueurSolo(@RequestParam(required = false) GenreJoueur genre) {
        return joueurService.getJoueurSolo(genre);
    }

    /**
     * Crée un nouveau joueur à partir des informations fournies.
     *
     * @param joueur Objet {@link Joueur} contenant les informations du joueur à créer.
     * @return Le {@link Joueur} nouvellement créé.
     */
    @Operation(
            summary = "Créer un nouveau joueur",
            description = "Crée un nouveau joueur à partir des informations fournies dans le corps de la requête."
    )
    @PostMapping
    public Joueur createJoueur(@RequestBody Joueur joueur) {
        return joueurService.addJoueur(joueur);
    }

    /**
     * Assigne un joueur existant à une équipe existante.
     *
     * @param idJoueur Identifiant unique du joueur à assigner.
     * @param idEquipe Identifiant unique de l'équipe cible.
     * @return Le {@link Joueur} mis à jour avec son équipe assignée.
     * @throws RuntimeException Si le joueur ou l'équipe n'existe pas.
     */
    @Operation(
            summary = "Assigner un joueur à une équipe",
            description = "Assigne un joueur existant à une équipe existante. Une erreur est renvoyée si le joueur ou l'équipe n'existe pas."
    )
    @PatchMapping("/{idJoueur}/equipe/{idEquipe}")
    public Joueur assignerEquipe(@PathVariable long idJoueur, @PathVariable long idEquipe) { return joueurService.assignerEquipe(idJoueur, idEquipe); }

    /**
     * Permet à un joueur de faire une demande pour rejoindre une équipe.
     *
     * @param idJoueur Identifiant unique du joueur qui fait la demande.
     * @param idEquipe Identifiant unique de l'équipe cible.
     * @param authHeader En-tête HTTP contenant le token JWT du joueur authentifié.
     * @return Une {@link ResponseEntity} contenant la {@link JoueurRequest} créée.
     */
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

    /**
     * Accepte une demande d'adhésion d'un joueur à une équipe.
     *
     * @param idJoueur Identifiant unique du joueur dont la demande est acceptée.
     * @param idEquipe Identifiant unique de l'équipe qui accepte la demande.
     * @return Le {@link Joueur} mis à jour avec son équipe assignée.
     */
    @PatchMapping("/request/{idJoueur}/equipe/{idEquipe}/accept")
    public Joueur accepterDemande(@PathVariable long idJoueur, @PathVariable long idEquipe) {return joueurService.accepterDemande(idJoueur, idEquipe); }

    /**
     * Refuse une demande d'adhésion d'un joueur à une équipe.
     *
     * @param idJoueur Identifiant unique du joueur dont la demande est refusée.
     * @param idEquipe Identifiant unique de l'équipe qui refuse la demande.
     */
    @PatchMapping("/request/{idJoueur}/equipe/{idEquipe}/refuse")
    public void refuseDemande(@PathVariable long idJoueur, @PathVariable long idEquipe) {joueurService.refuseDemande(idJoueur, idEquipe);}

    /**
     * Récupère la liste de toutes les demandes d'adhésion en attente.
     *
     * @return Une liste de {@link JoueurRequest} représentant les demandes en attente.
     */
    @GetMapping("/requests")
    public List<JoueurRequest> getRequests() {return joueurService.getAllRequests();}

    /**
     * Met à jour l'image ou l'avatar d'un joueur.
     *
     * @param imageDTO Objet contenant les données de l'image à mettre à jour.
     * @param idJoueur Identifiant unique du joueur à mettre à jour.
     * @return Le {@link Joueur} mis à jour avec sa nouvelle image.
     */
    @Operation(
            summary = "Mettre à jour l'image d'un joueur",
            description = "Met à jour l'image ou l'avatar associé à un joueur."
    )
    @PatchMapping("/{idJoueur}")
    public Joueur editImage(@RequestBody ImageDTO imageDTO, @PathVariable long idJoueur) { return joueurService.updateJoueur(idJoueur, imageDTO);}

    /**
     * Met à jour le nom ou les informations principales d'un joueur.
     *
     * @param nameDTO Objet contenant les nouvelles informations du joueur.
     * @param idJoueur Identifiant unique du joueur à mettre à jour.
     * @return Le {@link Joueur} mis à jour.
     */
    @Operation(
            summary = "Modifier les informations d'un joueur",
            description = "Permet de modifier le nom et/ou les informations principales d'un joueur."
    )
    @PatchMapping("/{idJoueur}/name")
    public Joueur editNameJoueur(@RequestBody EditJoueurDTO nameDTO, @PathVariable long idJoueur) { return joueurService.editName(nameDTO, idJoueur);}

    /**
     * Retire un joueur de son équipe actuelle.
     *
     * @param idJoueur Identifiant unique du joueur à retirer.
     * @param idEquipe Identifiant unique de l'équipe dont le joueur est retiré.
     * @return L'{@link Equipe} mise à jour après le retrait du joueur.
     */
    @Operation(
            summary = "Retirer un joueur de son équipe",
            description = "Supprime l'association entre un joueur et une équipe."
    )
    @DeleteMapping("/{idJoueur}/equipe/{idEquipe}")
    public Equipe deleteEquipe(@PathVariable Long idJoueur, @PathVariable Long idEquipe) { return joueurService.deleteEquipe(idJoueur, idEquipe); }

    /**
     * Supprime définitivement un joueur.
     *
     * @param id Identifiant unique du joueur à supprimer.
     */
    @Operation(
            summary = "Supprimer un joueur",
            description = "Supprime définitivement le joueur correspondant à l'identifiant fourni."
    )
    @DeleteMapping("/{id}")
    public void deleteJoueur(@PathVariable Long id) {
        joueurService.deleteJoueur(id);
    }
}
