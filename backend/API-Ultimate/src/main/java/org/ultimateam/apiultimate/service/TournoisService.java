package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class TournoisService {

    private final TournoisRepository tournoisRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;
    private final MatchRepository matchRepository;
    private final RoundRobinSchedulerService scheduler;
    private final IndisponibiliteRepository indisponibiliteRepository;
    private final ClassementRepository classementRepository;

    /**
     * Constructeur du service de gestion des tournois.
     *
     * @param tournoisRepository repository pour l'entité Tournoi
     * @param participationRepository repository pour l'entité Participation
     * @param equipeService service fournissant des opérations sur les équipes
     * @param matchRepository repository pour l'entité Match
     * @param scheduler service de génération de planning Round Robin
     * @param indisponibiliteRepository repository pour les indisponibilités
     * @param classementRepository repository pour les classements
     */
    public TournoisService(TournoisRepository tournoisRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository, RoundRobinSchedulerService scheduler, IndisponibiliteRepository indisponibiliteRepository,
                           ClassementRepository classementRepository) {
        this.tournoisRepository = tournoisRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
        this.scheduler = scheduler;
        this.indisponibiliteRepository = indisponibiliteRepository;
        this.classementRepository = classementRepository;
    }

    /**
     * Retourne la liste de tous les tournois en base.
     *
     * @return liste de {@link Tournoi}
     */
    public List<Tournoi> getAllTournois() {
        return tournoisRepository.findAll();
    }

    /**
     * Récupère un tournoi par son identifiant.
     *
     * @param id identifiant du tournoi
     * @return le {@link Tournoi} correspondant ou null si non trouvé
     */
    public Tournoi getTournoisById(Long id) {
        return tournoisRepository.findById(id).orElse(null);
    }

    /**
     * Sauvegarde (création ou mise à jour) d'un tournoi.
     *
     * @param tournoi objet {@link Tournoi} à sauvegarder
     * @return le tournoi sauvegardé
     */
    public Tournoi saveTournois(Tournoi tournoi) {
        return tournoisRepository.save(tournoi);
    }

    /**
     * Supprime un tournoi en base par son identifiant.
     *
     * @param id identifiant du tournoi à supprimer
     */
    public void deleteTournoisById(Long id) {
        tournoisRepository.deleteById(id);
    }

/*
    public void genererTournois(Long idTournois) {
        genererRoundRobin(idTournois);
    }
*/
    /**
     * Récupère la liste des matchs associés à un tournoi, triés par date croissante.
     *
     * @param idTournois identifiant du tournoi
     * @return liste des {@link Match} ordonnée par date
     */
    public List<Match> getMatchesByTournois(Long idTournois) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idTournois);
    }


/*
    //Pour le moment genererRoundRobin renvoie la liste des equipes qui participent à la competition.
    public List<Equipe> genererRoundRobin(Long idTournois) {

        Tournoi tournoi = getTournoisById(idTournois);
        if (tournoi == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournois n'existe pas");
        }
        List<Participation> participations = participationRepository.findById_idCompetition(idTournois);
        List<Equipe> equipes = new ArrayList<>();
        List<Indisponibilite> indispo = new ArrayList<>();
        for (Participation participation : participations) {
            Equipe equipe = equipeService.getById(participation.getId().getIdEquipe());
            equipes.add(equipe);
            indispo.addAll(equipeService.getIndisponibilites(equipe.getIdEquipe()));
            Classement classement = new Classement(participation.getId());
            classement.setCompetition(tournoi);
            classement.setEquipe(equipe);
            classementRepository.save(classement);

        }

        ScheduleResult scheduleResult = scheduler.generateSchedule(equipes, tournoi.getDateDebut(), tournoi.getDateFin(), true, indispo);
        List<Match> matchs = scheduleResult.getMatchs();
        for (Match match : matchs) {
            match.setIdCompetition(tournoi);
        }
        //System.out.println(matchs.get(0).getIdMatch());
        List<Indisponibilite> indisponibilites = scheduleResult.getIndisponibilites();

        matchRepository.saveAll(matchs);
        indisponibiliteRepository.saveAll(indisponibilites);


        return equipes;
    }
*/
    /**
     * Modifie certaines propriétés d'un tournoi existant (nom et description) si fournies.
     *
     * @param nameDTO DTO contenant les nouveaux champs (nom, description)
     * @param idTournoi identifiant du tournoi à éditer
     * @return le {@link Tournoi} mis à jour
     * @throws ResponseStatusException si le tournoi n'est pas trouvé
     */
    public Tournoi editTournois(EquipeNameDTO nameDTO, Long idTournoi) {
        Tournoi tournoi = tournoisRepository.findById(idTournoi)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournoi non trouvée"));
        if (nameDTO.getNom() != null) {
            tournoi.setNomCompetition(nameDTO.getNom());
        }
        if (nameDTO.getDescription() != null) {
            tournoi.setDescriptionCompetition(nameDTO.getDescription());
        }
        return tournoisRepository.save(tournoi);
    }

}
