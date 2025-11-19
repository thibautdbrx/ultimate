package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Joueur", description = "Endpoints pour gérer les joueurs")
@RequestMapping("/api/joueur")
public class JoueurController {

    private final JoueurService joueurService;

    public JoueurController(JoueurService joueurService) {
        this.joueurService = joueurService;
    }

    @GetMapping
    public List<Joueur> getAllJoueurs() {
        return (List<Joueur>) joueurService.getAll();
    }

    @GetMapping("/{id}")
    public Joueur getJoueurById(@PathVariable Long id) {
        return joueurService.getById(id);
    }

    @GetMapping("/equipe/{idEquipe}")
    public List<Joueur> getJoueurByEquipe(@PathVariable Long idEquipe) {
        return joueurService.getJoueurByEquipe(idEquipe);
    }

    @PostMapping
    public Joueur createJoueur(@RequestBody Joueur joueur) {
        return joueurService.addJoueur(joueur);
    }

    @PatchMapping("/{idJoueur}/equipe/{idEquipe}")
    public Equipe assignerEquipe(@PathVariable Long idJoueur, @PathVariable Long idEquipe) { return joueurService.assignerEquipe(idJoueur, idEquipe); }

    @DeleteMapping("/{idJoueur}/equipe/{idEquipe}")
    public Equipe deleteEquipe(@PathVariable Long idJoueur, @PathVariable Long idEquipe) { return joueurService.deleteEquipe(idJoueur, idEquipe); }

    @DeleteMapping("/{id}")
    public void deleteJoueur(@PathVariable Long id) {
        joueurService.deleteJoueur(id);
    }
}
