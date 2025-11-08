package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.service.JoueurService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités Joueur.
 * Expose les points de terminaison (endpoints) de l'API pour les opérations CRUD sur les joueurs
 * et la gestion de leur assignation aux équipes.
 */
@RestController
@RequestMapping("/api/joueur")
public class JoueurController {

    private final JoueurService joueurService;

    /**
     * Constructeur pour l'injection de la dépendance JoueurService.
     *
     * @param joueurService Le service chargé de la logique métier pour les joueurs.
     */
    public JoueurController(JoueurService joueurService) {
        this.joueurService = joueurService;
    }

    /**
     * Récupère la liste de tous les joueurs.
     * Mappe les requêtes HTTP GET sur /api/joueur.
     *
     * @return Une liste contenant tous les joueurs.
     */
    @GetMapping
    public List<Joueur> getAllJoueurs() {
        return (List<Joueur>) joueurService.getAll();
    }

    /**
     * Récupère un joueur spécifique par son identifiant.
     * Mappe les requêtes HTTP GET sur /api/joueur/{id}.
     *
     * @param id L'identifiant du joueur à récupérer (provient du chemin de l'URL).
     * @return Le joueur correspondant à l'ID, ou null si non trouvé.
     */
    @GetMapping("/{id}")
    public Joueur getJoueurById(@PathVariable Long id) {
        return joueurService.getById(id);
    }

    /**
     * Crée un nouveau joueur.
     * Mappe les requêtes HTTP POST sur /api/joueur.
     *
     * @param joueur L'objet Joueur à créer (désérialisé depuis le corps de la requête JSON).
     * @return Le joueur nouvellement créé (avec son ID assigné).
     */
    @PostMapping
    public Joueur createJoueur(@RequestBody Joueur joueur) {
        return joueurService.addJoueur(joueur);
    }

    /**
     * Assigne un joueur existant à une équipe existante.
     * Mappe les requêtes HTTP PUT sur /api/joueur/{id_joueur}/equipe/{id_equipe}.
     *
     * @param id_joueur L'identifiant du joueur à assigner.
     * @param id_equipe L'identifiant de l'équipe recevant le joueur.
     * @return L'équipe mise à jour avec le nouveau joueur.
     */
    @PutMapping("/{id_joueur}/equipe/{id_equipe}")
    public Equipe assignerEquipe(@PathVariable Long id_joueur, @PathVariable Long id_equipe) { return joueurService.assignerEquipe(id_joueur, id_equipe); }

    /**
     * Retire un joueur d'une équipe.
     * Mappe les requêtes HTTP DELETE sur /api/joueur/{id_joueur}/equipe/{id_equipe}.
     *
     * @param id_joueur L'identifiant du joueur à retirer.
     * @param id_equipe L'identifiant de l'équipe dont le joueur est retiré.
     * @return L'équipe mise à jour après le retrait du joueur.
     */
    @DeleteMapping("/{id_joueur}/equipe/{id_equipe}")
    public Equipe deleteEquipe(@PathVariable Long id_joueur, @PathVariable Long id_equipe) { return joueurService.deleteEquipe(id_joueur, id_equipe); }

    /**
     * Supprime un joueur par son identifiant.
     * Mappe les requêtes HTTP DELETE sur /api/joueur/{id}.
     *
     * @param id L'identifiant du joueur à supprimer.
     */
    @DeleteMapping("/{id}")
    public void deleteJoueur(@PathVariable Long id) {
        joueurService.deleteJoueur(id);
    }
}