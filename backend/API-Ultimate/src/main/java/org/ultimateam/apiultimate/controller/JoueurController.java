package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.service.JoueurService;

import java.util.List;

@RestController
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

    @PostMapping
    public Joueur createJoueur(@RequestBody Joueur joueur) {
        return joueurService.addJoueur(joueur);
    }

    @PostMapping("/{id_joueur}/equipe/{id_equipe}")
    public Equipe assignerEquipe(@PathVariable Long id_joueur, @PathVariable Long id_equipe) { return joueurService.assignerEquipe(id_joueur, id_equipe); }

    @DeleteMapping("/{id_joueur}/equipe/{id_equipe}")
    public Equipe deleteEquipe(@PathVariable Long id_joueur, @PathVariable Long id_equipe) { return joueurService.deleteEquipe(id_joueur, id_equipe); }

    @DeleteMapping("/{id}")
    public void deleteJoueur(@PathVariable Long id) {
        joueurService.deleteJoueur(id);
    }
}
