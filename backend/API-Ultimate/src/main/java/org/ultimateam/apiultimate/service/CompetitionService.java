package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ScheduleResult;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service gérant les opérations liées aux compétitions.
 *
 * Ce service fournit des méthodes pour récupérer, créer, supprimer des compétitions,
 * générer les matchs d'une compétition (tournoi ou championnat) et récupérer les matchs
 * liés à une compétition.
 */
@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;
    private final RoundRobinSchedulerService scheduler;
    private final IndisponibiliteRepository indisponibiliteRepository;
    private final ClassementRepository classementRepository;

    /**
     * Constructeur avec injection des dépendances nécessaires au service.
     *
     * @param competitionRepository repository pour accéder aux compétitions
     * @param matchRepository repository pour accéder aux matchs
     * @param participationRepository repository pour accéder aux participations
     * @param equipeService service utilisé pour récupérer les informations des équipes
     * @param scheduler service responsable de la génération des plannings (round-robin / tournoi)
     * @param indisponibiliteRepository repository pour persister les indisponibilités
     * @param classementRepository repository pour créer les classements associés
     */
    public CompetitionService(
            CompetitionRepository competitionRepository,
            MatchRepository matchRepository,
            ParticipationRepository participationRepository,
            EquipeService equipeService,
            RoundRobinSchedulerService scheduler,
            IndisponibiliteRepository indisponibiliteRepository,
            ClassementRepository classementRepository
    ) {
        this.competitionRepository = competitionRepository;
        this.matchRepository = matchRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.scheduler = scheduler;
        this.indisponibiliteRepository = indisponibiliteRepository;
        this.classementRepository = classementRepository;
    }

    /**
     * Récupère toutes les compétitions présentes en base de données.
     *
     * @return une liste de toutes les {@link Competition}
     */
    public List<Competition> getAllCompetition() {
        return competitionRepository.findAll();
    }

    /**
     * Récupère une compétition par son identifiant.
     *
     * @param id l'identifiant de la compétition recherchée
     * @return l'objet {@link Competition} si trouvé, sinon {@code null}
     */
    public Competition getCompetitionById(Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    /**
     * Persiste une compétition en base (création ou mise à jour).
     *
     * @param Competition l'entité {@link Competition} à sauvegarder
     * @return la compétition persistée
     */
    public Competition saveCompetition(Competition Competition) {
        return competitionRepository.save(Competition);
    }

    /**
     * Supprime une compétition identifiée par son id.
     *
     * @param id identifiant de la compétition à supprimer
     */
    public void deleteCompetitionById(Long id) {
        competitionRepository.deleteById(id);
    }


    /**
     * Génère les matchs d'une compétition (tournoi ou championnat) et crée les entrées de classement.
     *
     * <p>Le service :
     * <ol>
     *   <li>vérifie l'existence de la compétition;</li>
     *   <li>récupère les participations et les équipes participantes;</li>
     *   <li>construit les classements initiaux pour chaque participation;</li>
     *   <li>appelle le scheduler pour générer les horaires en respectant les indisponibilités;</li>
     *   <li>associe la compétition aux matchs générés et persiste les matchs et indisponibilités.</li>
     * </ol>
     * </p>
     *
     * @param idCompeptition identifiant de la compétition pour laquelle générer le planning
     * @return la liste des {@link Match} générés et persistés
     * @throws ResponseStatusException si la compétition n'existe pas ou si le type de compétition est invalide
     */
    public List<Match> genererCompetition(Long idCompeptition) {

        Competition competition = getCompetitionById(idCompeptition);
        if (competition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compétition n'existe pas");
        }
        List<Participation> participations = participationRepository.findById_idCompetition(idCompeptition);
        List<Equipe> equipes = new ArrayList<>();
        List<Indisponibilite> indispo = new ArrayList<>();
        for (Participation participation : participations) {
            Equipe equipe = equipeService.getById(participation.getId().getIdEquipe());
            equipes.add(equipe);
            indispo.addAll(equipeService.getIndisponibilites(equipe.getIdEquipe()));
            Classement classement = new Classement(participation.getId());
            classement.setCompetition(competition);
            classement.setEquipe(equipe);
            classementRepository.save(classement);

        }
        ScheduleResult scheduleResult;
        if (Objects.equals(competition.getTypeCompetition(), "Tournoi")) {
            scheduleResult = scheduler.generateSchedule(equipes, competition.getDateDebut(), competition.getDateFin(), true, indispo);
        }
        else if (Objects.equals(competition.getTypeCompetition(), "Championnat")){
            scheduleResult = scheduler.generateSchedule(equipes, competition.getDateDebut(), competition.getDateFin(), false, indispo);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "pas une competition valide");
        }
        List<Match> matchs = scheduleResult.getMatchs();
        for (Match match : matchs) {
            match.setIdCompetition(competition);
        }
        List<Indisponibilite> indisponibilites = scheduleResult.getIndisponibilites();

        matchRepository.saveAll(matchs);
        indisponibiliteRepository.saveAll(indisponibilites);


        return matchs;
    }

    /**
     * Récupère la liste des matchs d'une compétition triés par date de match croissante.
     *
     * @param idCompetition identifiant de la compétition
     * @return liste des {@link Match} correspondant à la compétition
     */
    public List<Match> getMatchesByCompetition(Long idCompetition) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idCompetition);
    }


}
