package org.ultimateam.apiultimate.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.EditJoueurDTO;
import org.ultimateam.apiultimate.DTO.GenreJoueur;
import org.ultimateam.apiultimate.DTO.ImageDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.model.JoueurRequestId;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.JoueurRepository;
import org.ultimateam.apiultimate.repository.JoueurRequestRepository;

import java.util.List;

@Service
public class JoueurService {

    public final JoueurRepository joueurRepository;
    public final EquipeService equipeService;
    public final JoueurRequestRepository joueurRequestRepository;
    private final EquipeRepository equipeRepository;

    /**
     * Constructeur pour l'injection des dépendances JoueurRepository et EquipeService.
     *
     * @param joueurRepository Le repository pour l'accès aux données des joueurs.
     * @param equipeService Le service pour la gestion des équipes.
     */
    public JoueurService(JoueurRepository joueurRepository, EquipeService equipeService, JoueurRequestRepository joueurRequestRepository, EquipeRepository equipeRepository) {
        this.joueurRepository = joueurRepository;
        this.equipeService = equipeService;
        this.joueurRequestRepository = joueurRequestRepository;
        this.equipeRepository = equipeRepository;
    }

    /**
     * Récupère la liste de tous les joueurs.
     *
     * @return Un Itérable contenant tous les joueurs.
     */
    public List<Joueur> getAll(GenreJoueur genre) {
        if(genre==null){
            return joueurRepository.findAll();
        }
        return joueurRepository.findAllByGenre(genre);

    }

    /**
     * Récupère un joueur spécifique par son identifiant (ID).
     *
     * @param id L'identifiant du joueur à rechercher.
     * @return Le joueur trouvé, ou null si aucun joueur ne correspond à l'ID.
     */
    public Joueur getById(Long id) {
        return joueurRepository.findById(id).orElse(null);
    }

    /**
     * Ajoute un nouveau joueur ou met à jour un joueur existant dans la base de données.
     *
     * @param joueur Le joueur à sauvegarder.
     * @return Le joueur sauvegardé (incluant l'ID généré si c'est une création).
     */
    public Joueur addJoueur(Joueur joueur) {
        return joueurRepository.save(joueur);
    }

    /**
     * Supprime un joueur de la base de données en utilisant son identifiant.
     *
     * @param id L'identifiant du joueur à supprimer.
     */
    public void deleteJoueur(Long id) {
        if (!joueurRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le joueur n'existe pas");
        }
        joueurRepository.deleteById(id);
    }

    public List<Joueur> getJoueurByEquipe(Long idEquipe){
        return joueurRepository.findAllByEquipe_IdEquipe(idEquipe);
    }

    /**
     * Assigne un joueur à une équipe spécifique.
     * Récupère le joueur et l'équipe par leurs ID, établit l'association
     * des deux côtés et sauvegarde les modifications.
     *
     * @param id_joueur L'identifiant du joueur à assigner.
     * @param id_equipe L'identifiant de l'équipe qui reçoit le joueur.
     * @return L'équipe mise à jour après l'assignation.
     */
    public Joueur assignerEquipe(Long id_joueur, Long id_equipe) {
        Joueur joueur = getById(id_joueur);
        Equipe equipe =equipeService.getById(id_equipe);
        equipe.addJoueur(joueur);
        joueur.setEquipe(equipe);
        equipeService.updateGenre(equipe);
        joueurRepository.save(joueur);
        equipeService.save(equipe);
        return joueur;
    }


    public JoueurRequest demandeJoueur(long idJoueur, long idEquipe, long idJoueurDuToken) {
        // Vérification que le joueur n'agit que pour lui-même
        if (idJoueur != idJoueurDuToken) {
            throw new AccessDeniedException("Vous ne pouvez créer une demande que pour votre propre joueur");
        }

        // Ensuite exécuter la logique normale
        Joueur joueur = joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé"));

        Equipe equipe = equipeRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée"));

        JoueurRequest request = new JoueurRequest(joueur, equipe);
        request.setId(request.getId());

        return joueurRequestRepository.save(request);
    }

    public Joueur accepterDemande(long idJoueur, long idEquipe) {
        JoueurRequestId id = new JoueurRequestId(idJoueur, idEquipe);
        JoueurRequest joueurRequest = joueurRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Demande inexistante"
                ));
        joueurRequestRepository.deleteById(id);
        return assignerEquipe(joueurRequest.getId().getIdJoueur(), joueurRequest.getId().getIdEquipe());
    }

    public void refuseDemande(long idJoueur, long idEquipe) {
        JoueurRequestId id = new JoueurRequestId(idJoueur, idEquipe);
        JoueurRequest joueurRequest = joueurRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Demande inexistante"
                ));
        joueurRequestRepository.deleteById(id);
    }

    public List<Joueur> getJoueurSolo(GenreJoueur genre) {
        if(genre == null){
            return joueurRepository.findAllByEquipe_IdEquipeIsNull();
        }
        return joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(genre);
    }

    /**
     * Retire un joueur d'une équipe.
     * Récupère le joueur et l'équipe, puis rompt l'association en mettant
     * l'équipe du joueur à null et en sauvegardant les changements.
     *
     * @param id_joueur L'identifiant du joueur à retirer.
     * @param id_equipe L'identifiant de l'équipe dont le joueur est retiré.
     * @return L'équipe mise à jour après le retrait du joueur.
     */
    public Equipe deleteEquipe(Long id_joueur, Long id_equipe) {
        Joueur joueur = getById(id_joueur);
        Equipe equipe = equipeService.getById(id_equipe);
        equipe.removeJoueur(joueur);
        joueur.setEquipe(null);
        equipeService.updateGenre(equipe);
        joueurRepository.save(joueur);
        equipeService.save(equipe);

        return equipe;
    }

    public Joueur updateJoueur(long idJoueur, ImageDTO imageDTO) {
        if (!joueurRepository.existsById(idJoueur)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le joueur n'existe pas");
        }
        Joueur joueur = getById(idJoueur);
        joueur.setPhotoJoueur(imageDTO.getImage());
        return joueurRepository.save(joueur);
    }

    public Joueur editName(EditJoueurDTO nameDTO, long idJoueur) {
        Joueur joueur = joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Joueur non trouvée"));
        if (nameDTO.getNomJoueur() !=null) joueur.setNomJoueur(nameDTO.getNomJoueur());
        if (nameDTO.getPrenomJoueur() !=null) joueur.setPrenomJoueur(nameDTO.getPrenomJoueur());
        if (nameDTO.getGenre() !=null) joueur.setGenre(nameDTO.getGenre());
        return joueurRepository.save(joueur);
    }

    public List<Joueur> getGenre(GenreJoueur genre) { return joueurRepository.findAllByGenre(genre);}

    public List<JoueurRequest> getAllRequests() { return joueurRequestRepository.findAll();}

}