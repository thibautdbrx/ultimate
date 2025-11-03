package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.repository.JoueurRepository;

@Service
public class JoueurService {

    public final JoueurRepository joueurRepository;
    public final EquipeService equipeService;

    public JoueurService(JoueurRepository joueurRepository, EquipeService equipeService) {
        this.joueurRepository = joueurRepository;
        this.equipeService = equipeService;
    }

    public Iterable<Joueur> getAll() {
        return joueurRepository.findAll();
    }
    public Joueur getById(Long id) {
        return joueurRepository.findById(id).orElse(null);
    }
    public Joueur addJoueur(Joueur joueur) {
        return joueurRepository.save(joueur);
    }
    public void deleteJoueur(Long id) {
        joueurRepository.deleteById(id);
    }


    public Joueur assignerEquipe(Long id_joueur, Long id_equipe) {
        Joueur joueur = getById(id_joueur);
        Equipe equipe =equipeService.getById(id_equipe);
        equipe.addJoueur(joueur);
        joueur.setEquipe(equipe);
        joueurRepository.save(joueur);
        equipeService.save(equipe);
        return joueur;
    }
}
