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
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.JoueurRepository;
import org.ultimateam.apiultimate.repository.JoueurRequestRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final JoueurRepository joueurRepository;
    private final ClassementService classementService;
    private final JoueurRequestRepository joueurRequestRepository;

    /**
     * Constructeur pour l'injection de la dépendance EquipeRepository, JoueurRepository et ClassementService.
     *
     * @param equipeRepository Le repository pour l'accès aux données des équipes.
     * @param joueurRepository Le repository pour l'accès aux données des joueurs.
     * @param classementService Le service manipulant les classements liés aux équipes.
     */
    public EquipeService(EquipeRepository equipeRepository, JoueurRepository joueurRepository, ClassementService classementService, JoueurRequestRepository joueurRequestRepository) {
        this.equipeRepository = equipeRepository;
        this.joueurRepository = joueurRepository;
        this.classementService = classementService;
        this.joueurRequestRepository = joueurRequestRepository;
    }

    /**
     * Récupère la liste de toutes les équipes enregistrées en base de données.
     *
     * @return Une liste contenant toutes les entités {@link Equipe}.
     */
    public List<Equipe> findAll() { return equipeRepository.findAll(); }

    /**
     * Récupère une équipe spécifique en utilisant son identifiant (ID).
     *
     * @param id L'identifiant de l'équipe à rechercher.
     * @return L'équipe trouvée, ou {@code null} si aucune équipe ne correspond à l'ID.
     */
    public Equipe getById(Long id) { return equipeRepository.findById(id).orElse(null); }

    /**
     * Sauvegarde une équipe dans la base de données.
     * Si l'équipe a déjà un ID, elle est mise à jour ; sinon elle est créée.
     *
     * @param equipe L'entité {@link Equipe} à sauvegarder.
     * @return L'équipe sauvegardée (potentiellement mise à jour avec un ID généré).
     */
    public Equipe save(Equipe equipe) { return equipeRepository.save(equipe);}

    /**
     * Supprime une équipe de la base de données en utilisant son identifiant et supprime aussi les classements associés.
     *
     * @param id L'identifiant de l'équipe à supprimer.
     * @throws ResponseStatusException Si l'équipe n'existe pas (404).
     */
    public void deleteById(Long id) {
        if (!equipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'equipe n'existe pas");
        }
        equipeRepository.deleteById(id);
        classementService.deleteByIdEquipe(id);

    }

    /**
     * Modifie le nom et/ou la description d'une équipe identifiée par {@code idEquipe}.
     * Seuls les champs fournis dans {@code equipedto} sont mis à jour.
     *
     * @param equipedto DTO contenant les nouvelles valeurs pour le nom et la description.
     * @param idEquipe L'identifiant de l'équipe à modifier.
     * @return L'équipe mise à jour.
     * @throws ResponseStatusException Si l'équipe n'existe pas (404).
     */

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

    /**
     * Récupère la liste des indisponibilités pour une équipe donnée.
     *
     * @param equipeId L'identifiant de l'équipe.
     * @return La liste des {@link Indisponibilite} associées à l'équipe, ou une liste vide si l'équipe n'existe pas.
     */

    public List<Indisponibilite> getIndisponibilites(Long equipeId) {
        Equipe equipe = getById(equipeId);
        if (equipe != null) {
            return equipe.getIndisponibilites();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Met à jour le genre pour chaque équipe de la liste fournie en appelant {@link #updateGenre(Equipe)}.
     *
     * @param equipes Liste d'équipes dont il faut recalculer le genre.
     */

    public void updateAllGenre(List<Equipe> equipes) {
        for (Equipe equipe : equipes) {
            //updateGenre(equipe);
        }
    }


    /**
     * Calcule et met à jour le genre d'une équipe en fonction des genres de ses joueurs :
     * - HOMME si tous les joueurs sont de genre HOMME,
     * - FEMME si tous les joueurs sont de genre FEMME,
     * - MIXTE sinon.
     * Si l'équipe n'a pas de joueurs, le genre est mis à {@code null}.
     *
     * @param equipe L'équipe dont le genre doit être recalculé.
     * @return L'équipe sauvegardée avec le genre mis à jour.
     */
    /*
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

    /**
     * Compte le nombre de joueurs liés à une équipe donnée.
     *
     * @param equipeId L'identifiant de l'équipe.
     * @return Le nombre de joueurs associés à l'équipe.
     */

    public int getNbJoueurs(Long equipeId) {
        return joueurRepository.countByEquipe_IdEquipe(equipeId);
    }

    /**
     * Met à jour les genres de toutes les équipes en base, puis renvoie la liste des équipes dont le genre
     * correspond au genre demandé.
     *
     * @param genre Le genre filtré ({@link Genre}).
     * @return Liste des équipes correspondant au genre fourni.
     */

    public List<Equipe> getEquipeGenre(Genre genre) {
        updateAllGenre(findAll());
        List<Equipe> equipes = equipeRepository.findAll();
        equipes.removeIf(equipe -> !equipe.isFull());
        return equipeRepository.findAllByGenre(genre);
    }

    public List<Equipe> getNotFull(Long idJoueur){
        List<Equipe> equipes;
        Joueur joueur = joueurRepository.findById(idJoueur).orElse(null);
        if (joueur == null) {
            equipes = Collections.emptyList();
        }
        else {
            List<JoueurRequest> joueursRequests = joueurRequestRepository.getByJoueur(joueur);
            equipes = equipeRepository.findAllById(joueursRequests.stream().map(JoueurRequest::getEquipe).map(Equipe::getIdEquipe).collect(Collectors.toList()));
        }
        List<Equipe> all = equipeRepository.findAll();
        return all.stream()
                .filter(e -> !e.isFull())
                .filter(e -> !equipes.contains(e))
                .collect(Collectors.toList());

    }
}
