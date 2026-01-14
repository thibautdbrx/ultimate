package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.DTO.GenreJoueur;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.JoueurRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final JoueurRepository joueurRepository;
    private final ClassementService classementService;

    /**
     * Constructeur pour l'injection de la dépendance EquipeRepository.
     *
     * @param equipeRepository Le repository pour l'accès aux données des équipes.
     */
    public EquipeService(EquipeRepository equipeRepository, JoueurRepository joueurRepository, ClassementService classementService) {
        this.equipeRepository = equipeRepository;
        this.joueurRepository = joueurRepository;
        this.classementService = classementService;
    }

    /**
     * Récupère la liste de toutes les équipes enregistrées.
     *
     * @return Un Itérable contenant toutes les entités Equipe.
     */
    public List<Equipe> findAll() { return equipeRepository.findAll(); }

    /**
     * Récupère une équipe spécifique en utilisant son identifiant (ID).
     *
     * @param id L'identifiant de l'équipe à rechercher.
     * @return L'équipe trouvée, ou null si aucune équipe ne correspond à l'ID.
     */
    public Equipe getById(Long id) { return equipeRepository.findById(id).orElse(null); }

    /**
     * Sauvegarde une équipe dans la base de données.
     * Si l'équipe a déjà un ID, elle est mise à jour. Sinon, elle est créée.
     *
     * @param equipe L'entité Equipe à sauvegarder.
     * @return L'équipe sauvegardée (potentiellement mise à jour avec un ID généré).
     */
    public Equipe save(Equipe equipe) { return equipeRepository.save(equipe);}

    /**
     * Supprime une équipe de la base de données en utilisant son identifiant et les classements associé.
     *
     * @param id L'identifiant de l'équipe à supprimer.
     */
    public void deleteById(Long id) {
        if (!equipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'equipe n'existe pas");
        }
        equipeRepository.deleteById(id);
        classementService.deleteByIdEquipe(id);

    }

    public Equipe editName(EquipeNameDTO equipedto, long idEquipe) {
        Equipe equipe = getById(idEquipe);
        if (equipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'équipe n'existe pas");
        }
        if (equipedto.getNom() != null) {
            equipe.setNomEquipe(equipedto.getNom());
        }
        if (equipedto.getDescription() != null) {
            equipe.setDescription(equipedto.getDescription());
        }

        return equipeRepository.save(equipe);
    }

    public List<Indisponibilite> getIndisponibilites(Long equipeId) {
        Equipe equipe = getById(equipeId);
        if (equipe != null) {
            return equipe.getIndisponibilites();
        } else {
            return Collections.emptyList();
        }
    }

    public void updateAllGenre(List<Equipe> equipes) {
        for (Equipe equipe : equipes) {
            //updateGenre(equipe);
        }
    }
/**
    public Equipe updateGenre(Equipe equipe) {
        List<Joueur> joueurs = equipe.getJoueurs();

        if (joueurs.isEmpty()) {
            equipe.setGenre(null);
            return equipe;
        }

        boolean allMale = true;
        boolean allFemale = true;

        for (Joueur joueur : joueurs) {
            if (joueur.getGenre() == GenreJoueur.HOMME) {
                allFemale = false;
            } else if (joueur.getGenre() == GenreJoueur.FEMME) {
                allMale = false;
            }
        }

        if (allMale) {
            equipe.setGenre(Genre.HOMME);
        } else if (allFemale) {
            equipe.setGenre(Genre.FEMME);
        } else {
            equipe.setGenre(Genre.MIXTE);
        }

        return equipeRepository.save(equipe);
    }
*/
    public int getNbJoueurs(Long equipeId) {
        return joueurRepository.countByEquipe_IdEquipe(equipeId);
    }

    public List<Equipe> getEquipeGenre(Genre genre) {
        updateAllGenre(findAll());
        return equipeRepository.findAllByGenre(genre);
    }

    public List<Equipe> getNotFull(){
        List<Equipe> all = equipeRepository.findAll();
        return all.stream()
                .filter(e -> !e.isFull())
                .collect(Collectors.toList());
    }
}
