package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.repository.JoueurRespository;

@Service
public class JoueurService {

    public final JoueurRespository joueurRespository;

    public JoueurService(JoueurRespository joueurRespository) {
        this.joueurRespository = joueurRespository;
    }

    public Iterable<Joueur> getAll() {
        return joueurRespository.findAll();
    }
    public Joueur getById(Long id) {
        return joueurRespository.findById(id).orElse(null);
    }
    public Joueur addJoueur(Joueur joueur) {
        return joueurRespository.save(joueur);
    }
    public void deleteJoueur(Long id) {
        joueurRespository.deleteById(id);
    }
}
