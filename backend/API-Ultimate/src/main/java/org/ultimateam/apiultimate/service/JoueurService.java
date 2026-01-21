package org.ultimateam.apiultimate.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.EditJoueurDTO;
import org.ultimateam.apiultimate.DTO.Genre;
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
import java.util.Objects;

/**
 * Service responsable des opérations liées aux joueurs.
 *
 * <p>Ce service fournit des méthodes pour récupérer, créer, modifier et supprimer
 * des {@link Joueur}, gérer leur appartenance à une {@link Equipe} ainsi que
 * traiter les demandes d'affectation (JoueurRequest).</p>
 */
@Service
public class JoueurService {

    public final JoueurRepository joueurRepository;
    public final EquipeService equipeService;
    public final JoueurRequestRepository joueurRequestRepository;
    private final EquipeRepository equipeRepository;

    /**
     * Constructeur pour l'injection des dépendances JoueurRepository, EquipeService,
     * JoueurRequestRepository et EquipeRepository.
     *
     * @param joueurRepository repository pour l'accès aux joueurs
     * @param equipeService service pour la gestion des équipes
     * @param joueurRequestRepository repository pour les demandes de joueur
     * @param equipeRepository repository pour l'accès aux équipes
     */
    public JoueurService(JoueurRepository joueurRepository, EquipeService equipeService, JoueurRequestRepository joueurRequestRepository, EquipeRepository equipeRepository) {
        this.joueurRepository = joueurRepository;
        this.equipeService = equipeService;
        this.joueurRequestRepository = joueurRequestRepository;
        this.equipeRepository = equipeRepository;
    }

    /**
     * Récupère la liste de tous les joueurs ou filtre par genre si fourni.
     *
     * @param genre le genre à filtrer (peut être {@code null} pour ne pas filtrer)
     * @return la liste des {@link Joueur} correspondant au filtre
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
     * @return Le joueur trouvé, ou {@code null} si aucun joueur ne correspond à l'ID.
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
     * @throws ResponseStatusException si le joueur n'existe pas
     */
    public void deleteJoueur(Long id) {
        if (!joueurRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le joueur n'existe pas");
        }
        joueurRepository.deleteById(id);
    }

    /**
     * Récupère la liste des joueurs appartenant à une équipe donnée.
     *
     * @param idEquipe L'identifiant de l'équipe
     * @return la liste des {@link Joueur} associés à l'équipe
     */
    public List<Joueur> getJoueurByEquipe(Long idEquipe){
        return joueurRepository.findAllByEquipe_IdEquipe(idEquipe);
    }

    /**
     * Assigne un joueur à une équipe spécifique.
     *
     * <p>La méthode récupère le joueur et l'équipe par leurs ID, met à jour
     * les relations bidirectionnelles (ajout/suppression), met à jour le genre
     * de l'équipe et persiste les entités modifiées.</p>
     *
     * @param id_joueur L'identifiant du joueur à assigner.
     * @param id_equipe L'identifiant de l'équipe qui reçoit le joueur.
     * @return Le joueur mis à jour après l'assignation.
     */
    public Joueur assignerEquipe(Long id_joueur, Long id_equipe) {
        Joueur joueur = getById(id_joueur);
        Equipe equipe =equipeService.getById(id_equipe);
        if (equipe.getJoueurs().contains(joueur)) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,"Le joueur fait déjà parti de l'équipe");
        }
        else if (equipe.isFull())
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,"Equipe complete");
        else if (equipe.getGenre() == Genre.H2F3){
            if (joueur.getGenre() == GenreJoueur.HOMME && getNbHommes(equipe) >=2 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre d'homme maximum atteint");
            }
            else if (joueur.getGenre() == GenreJoueur.FEMME && getNbFemmes(equipe) >=3 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre de femme maximum atteint");
            }
        }
        else if (equipe.getGenre() == Genre.H3F2){
            if (joueur.getGenre() == GenreJoueur.HOMME && getNbHommes(equipe) >=3 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre d'homme maximum atteint");
            }
            else if (joueur.getGenre() == GenreJoueur.FEMME && getNbFemmes(equipe) >=2 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre de femme maximum atteint");
            }
        }
        else if (equipe.getGenre() == Genre.H4F3){
            if (getNbHommes(equipe) >=4 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre d'homme maximum atteint");
            }
            else if (joueur.getGenre() == GenreJoueur.FEMME && getNbFemmes(equipe) >=3 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre de femme maximum atteint");
            }
        }
        else if (equipe.getGenre() == Genre.H3F4){
            if (joueur.getGenre() == GenreJoueur.HOMME && getNbHommes(equipe) >=3 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre d'homme maximum atteint");
            }
            else if (joueur.getGenre() == GenreJoueur.FEMME && getNbFemmes(equipe) >=4 ){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre de femme maximum atteint");
            }
        }
        else if (equipe.getGenre() == Genre.FEMME){
            if (joueur.getGenre() == GenreJoueur.HOMME){
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Equipe ne peut pas contenir d'hommes");
            }
        }
        equipe.addJoueur(joueur);
        joueur.setEquipe(equipe);
        //equipeService.updateGenre(equipe);
        joueurRepository.save(joueur);
        equipeService.save(equipe);
        return joueur;
    }

    /**
     * Crée une demande d'affectation d'un joueur à une équipe (JoueurRequest).
     *
     * <p>La méthode vérifie que l'utilisateur représenté par le token agit pour
     * son propre joueur, puis crée et persiste la demande.</p>
     *
     * @param idJoueur identifiant du joueur concerné
     * @param idEquipe identifiant de l'équipe demandée
     * @param idJoueurDuToken identifiant du joueur provenant du token (sécurité)
     * @return la {@link JoueurRequest} créée et persistée
     * @throws AccessDeniedException si l'utilisateur n'agit pas pour son propre joueur
     * @throws EntityNotFoundException si le joueur ou l'équipe n'existe pas
     */
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

        if (joueur.getEquipe() != null) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Impossible de rejoindre plusieurs équipes");
        }
        JoueurRequest request = new JoueurRequest(joueur, equipe);
        request.setId(request.getId());

        return joueurRequestRepository.save(request);
    }

    /**
     * Accepte une demande d'affectation joueur->équipe et effectue l'assignation.
     *
     * <p>La méthode recherche la demande par son identifiant composite, la supprime
     * puis appelle {@link #assignerEquipe} pour rattacher le joueur à l'équipe.</p>
     *
     * @param idJoueur identifiant du joueur
     * @param idEquipe identifiant de l'équipe
     * @return le {@link Joueur} assigné à l'équipe
     * @throws ResponseStatusException si la demande n'existe pas
     */
    public Joueur accepterDemande(long idJoueur, long idEquipe) {
        JoueurRequestId id = new JoueurRequestId(idJoueur, idEquipe);
        JoueurRequest joueurRequest = joueurRequestRepository.findById(id).orElse(null);
        if (joueurRequest == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande inexistante");

        }
        Joueur joueur = assignerEquipe(joueurRequest.getId().getIdJoueur(), joueurRequest.getId().getIdEquipe());
        joueurRequestRepository.deleteById(id);
        List<JoueurRequest> toDelet = joueurRequestRepository.findAll().stream().filter(req -> Objects.equals(req.getId().getIdJoueur(), joueurRequest.getId().getIdJoueur())).toList();
        joueurRequestRepository.deleteAll(toDelet);
        return joueur;
    }

    /**
     * Refuse et supprime une demande d'affectation existante.
     *
     * @param idJoueur identifiant du joueur
     * @param idEquipe identifiant de l'équipe
     * @throws ResponseStatusException si la demande n'existe pas
     */
    public void refuseDemande(long idJoueur, long idEquipe) {
        JoueurRequestId id = new JoueurRequestId(idJoueur, idEquipe);
        if (!joueurRequestRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande inexistante");
        }
        joueurRequestRepository.deleteById(id);
    }

    /**
     * Récupère les joueurs n'appartenant à aucune équipe (joueurs "solo").
     *
     * @return la liste des {@link Joueur} solo correspondant au filtre
     */
    public List<Joueur> getJoueurSolo(Long idEquipe) {
        if (idEquipe == null) {
            return joueurRepository.findAllByEquipe_IdEquipeIsNull();
        }
        List<Joueur> joueurs = joueurRepository.findAllByEquipe_IdEquipe(idEquipe);
        Equipe equipe = equipeRepository.findById(idEquipe).orElse(null);
        if (equipe == null)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Equipe introuvable");

        if (equipe.isFull())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Equipe complete");
        else if (equipe.getGenre() == Genre.OPEN)
            return joueurRepository.findAllByEquipe_IdEquipeIsNull();
        else if (equipe.getGenre() == Genre.FEMME)
            return joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.FEMME);

        else if (equipe.getGenre() == Genre.H2F3){
            if (getNbHommes(equipe) <2 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.HOMME));
            }
            else if (getNbFemmes(equipe) <3 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.FEMME));
            }
        }
        else if (equipe.getGenre() == Genre.H3F2){
            if (getNbHommes(equipe) <3 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.HOMME));
            }
            else if (getNbFemmes(equipe) <2 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.FEMME));
            }
        }
        else if (equipe.getGenre() == Genre.H4F3){
            if (getNbHommes(equipe) <4 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.HOMME));
            }
            else if (getNbFemmes(equipe) <3 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.FEMME));
            }
        }
        else if (equipe.getGenre() == Genre.H3F4){
            if (getNbHommes(equipe) <3 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.HOMME));
            }
            else if (getNbFemmes(equipe) <4 ){
                joueurs.addAll(joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur.FEMME));
            }
        }
        /**
        if(genre == null){
            return joueurRepository.findAllByEquipe_IdEquipeIsNull();
        }
        return joueurRepository.findAllByEquipe_IdEquipeIsNullAndGenre(genre);
         */
        return joueurs;
    }

    /**
     * Calcule le nombre de joueurs de genre masculin inscrits dans une équipe.
     *
     * @param equipe l'équipe à analyser
     * @return le nombre total d'hommes dans l'équipe
     */
    private long getNbHommes(Equipe equipe){
        long nb = 0;
        for (Joueur joueur : equipe.getJoueurs()) {
            if (joueur.getGenre() == GenreJoueur.HOMME) {
                nb++;
            }
        }
        return nb;
    }

    /**
     * Calcule le nombre de joueuses de genre féminin inscrites dans une équipe.
     *
     * @param equipe l'équipe à analyser
     * @return le nombre total de femmes dans l'équipe
     */
    private long getNbFemmes(Equipe equipe){
        long nb = 0;
        for (Joueur joueur : equipe.getJoueurs()) {
            if (joueur.getGenre() == GenreJoueur.FEMME) {
                nb++;
            }
        }
        return nb;
    }

    /**
     * Retire un joueur d'une équipe et met à jour les entités concernées.
     *
     * <p>La méthode rompt l'association entre le joueur et l'équipe, met à jour le genre
     * de l'équipe et persiste les changements.</p>
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
        //equipeService.updateGenre(equipe);
        joueurRepository.save(joueur);
        equipeService.save(equipe);

        return equipe;
    }

    /**
     * Met à jour la photo d'un joueur et persiste la modification.
     *
     * @param idJoueur identifiant du joueur à mettre à jour
     * @param imageDTO DTO contenant l'image (base64 ou chemin selon implémentation)
     * @return le {@link Joueur} mis à jour
     * @throws ResponseStatusException si le joueur n'existe pas
     */
    public Joueur updateJoueur(long idJoueur, ImageDTO imageDTO) {
        if (!joueurRepository.existsById(idJoueur)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le joueur n'existe pas");
        }
        Joueur joueur = getById(idJoueur);
        joueur.setPhotoJoueur(imageDTO.getImage());
        return joueurRepository.save(joueur);
    }

    /**
     * Modifie les informations nominatives d'un joueur (nom, prénom, genre) si fournies.
     *
     * @param nameDTO DTO contenant les champs modifiables
     * @param idJoueur identifiant du joueur à modifier
     * @return le {@link Joueur} mis à jour
     * @throws ResponseStatusException si le joueur n'existe pas
     */
    public Joueur editName(EditJoueurDTO nameDTO, long idJoueur) {
        Joueur joueur = joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Joueur non trouvée"));
        if (nameDTO.getNomJoueur() !=null) joueur.setNomJoueur(nameDTO.getNomJoueur());
        if (nameDTO.getPrenomJoueur() !=null) joueur.setPrenomJoueur(nameDTO.getPrenomJoueur());
        if (nameDTO.getGenre() !=null) joueur.setGenre(nameDTO.getGenre());
        return joueurRepository.save(joueur);
    }

    /**
     * Récupère les joueurs par genre.
     *
     * @param genre le genre demandé
     * @return la liste des {@link Joueur} correspondant au genre
     */
    public List<Joueur> getGenre(GenreJoueur genre) { return joueurRepository.findAllByGenre(genre);}

    /**
     * Récupère toutes les demandes d'affectation de joueurs (JoueurRequest).
     *
     * @return la liste des {@link JoueurRequest} existantes
     */
    public List<JoueurRequest> getAllRequests() { return joueurRequestRepository.findAll();}

}
