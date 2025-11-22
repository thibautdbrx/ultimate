package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.repository.EquipeRepository;

import java.util.Collections;
import java.util.List;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;

    /**
     * Constructeur pour l'injection de la dépendance EquipeRepository.
     *
     * @param equipeRepository Le repository pour l'accès aux données des équipes.
     */
    public EquipeService(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
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
     * Supprime une équipe de la base de données en utilisant son identifiant.
     *
     * @param id L'identifiant de l'équipe à supprimer.
     */
    public void deleteById(Long id) {
        if (!equipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'equipe n'existe pas");
        }
        equipeRepository.deleteById(id);
    }

    public Equipe editName(EquipeNameDTO equipedto, long idEquipe) {
        Equipe equipe = getById(idEquipe);
        if (equipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'équipe n'existe pas");
        }
        equipe.setNomEquipe(equipedto.getNomEquipe());
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
            updateGenre(equipe);
        }
    }

    public Equipe updateGenre(Equipe equipe) {
        List<Joueur> joueurs = equipe.getJoueurs();

        if (joueurs.isEmpty()) {
            equipe.setGenre(null);
            return equipe;
        }

        boolean allMale = true;
        boolean allFemale = true;

        for (Joueur joueur : joueurs) {
            if (joueur.getGenre() == Joueur.Genre.MALE) {
                allFemale = false;
            } else if (joueur.getGenre() == Joueur.Genre.FEMALE) {
                allMale = false;
            }
        }

        if (allMale) {
            equipe.setGenre(Equipe.Genre.MALE);
        } else if (allFemale) {
            equipe.setGenre(Equipe.Genre.FEMALE);
        } else {
            equipe.setGenre(Equipe.Genre.MIXTE);
        }

        return equipeRepository.save(equipe);
    }
}
