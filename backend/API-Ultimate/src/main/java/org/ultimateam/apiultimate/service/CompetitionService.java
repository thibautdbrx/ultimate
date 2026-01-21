package org.ultimateam.apiultimate.service;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ScheduleResult;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service gérant le cycle de vie des compétitions.
 *
 * Ce service permet de gérer la création, la suppression et la configuration des compétitions.
 * Sa fonction principale est l'orchestration de la génération automatique des calendriers de matchs
 * (Tournois ou Championnats) en prenant en compte les contraintes d'indisponibilité des équipes
 * et des terrains via un algorithme de planification.
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
    private final TerrainService terrainService;
    private final IndisponibiliteTerrainRepository indisponibiliteTerrainRepository;
    private final IndisponibiliteTerrainService indisponibiliteTerrainService;

    /**
     * Constructeur pour l'injection de toutes les dépendances nécessaires à la gestion des compétitions.
     */
    public CompetitionService(
            CompetitionRepository competitionRepository,
            MatchRepository matchRepository,
            ParticipationRepository participationRepository,
            EquipeService equipeService,
            RoundRobinSchedulerService scheduler,
            IndisponibiliteRepository indisponibiliteRepository,
            ClassementRepository classementRepository,
            TerrainService terrainService,
            IndisponibiliteTerrainRepository indisponibiliteTerrainRepository, IndisponibiliteTerrainService indisponibiliteTerrainService) {

        this.competitionRepository = competitionRepository;
        this.matchRepository = matchRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.scheduler = scheduler;
        this.indisponibiliteRepository = indisponibiliteRepository;
        this.classementRepository = classementRepository;
        this.terrainService = terrainService;
        this.indisponibiliteTerrainRepository = indisponibiliteTerrainRepository;
        this.indisponibiliteTerrainService = indisponibiliteTerrainService;
    }

    /**
     * Récupère la liste de toutes les compétitions enregistrées.
     *
     * @return une liste de {@link Competition}
     */
    public List<Competition> getAllCompetition() {
        return competitionRepository.findAll();
    }

    /**
     * Récupère une compétition par son identifiant.
     *
     * @param id identifiant de la compétition
     * @return la {@link Competition} trouvée ou {@code null}
     */
    public Competition getCompetitionById(Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    /**
     * Sauvegarde ou met à jour une compétition.
     *
     * @param Competition l'objet compétition à persister
     * @return la compétition sauvegardée
     * @throws ResponseStatusException si les dates de début ou de fin sont manquantes
     */
    public Competition saveCompetition(Competition Competition) {
        if (Competition.getDateDebut() == null || Competition.getDateFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez renseigner les dates");
        }
        return competitionRepository.save(Competition);
    }

    /**
     * Supprime une compétition par son identifiant.
     *
     * @param id identifiant de la compétition à supprimer
     */
    public void deleteCompetitionById(Long id) {
        competitionRepository.deleteById(id);
    }

    /**
     * Génère automatiquement le calendrier des matchs pour une compétition donnée.
     *
     * Le processus inclut :
     * 1. Le nettoyage des anciens matchs et indisponibilités.
     * 2. La collecte des équipes participantes et de leurs contraintes.
     * 3. L'initialisation du classement.
     * 4. L'appel au {@link RoundRobinSchedulerService} selon le type (Tournoi/Championnat).
     * 5. La persistence des matchs et des nouvelles indisponibilités générées.</p>
     *
     * @param idCompetition identifiant de la compétition à générer
     * @return la liste des {@link Match} générés
     * @throws ResponseStatusException si la compétition n'existe pas, n'a pas de terrain, ou si le type est invalide
     */
    public List<Match> genererCompetition(Long idCompetition) {

        Competition competition = getCompetitionById(idCompetition);
        if (competition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compétition n'existe pas");
        }

        nettoyerMatchsEtIndispos(idCompetition);

        List<Terrain> terrains = competition.getTerrains()
                .stream()
                .map(t -> terrainService.getById(t.getIdTerrain()))
                .toList();
        if (terrains.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossible de générer la compétition : aucun terrain trouvé");
        }

        List<Participation> participations = participationRepository.findById_idCompetition(idCompetition);
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

        List<IndisponibiliteTerrain> indispoTerrains = indisponibiliteTerrainRepository.findAll();

        ScheduleResult scheduleResult;
        if (Objects.equals(competition.getTypeCompetition(), "Tournoi")) {
            scheduleResult = scheduler.generateSchedule(equipes, terrains, competition.getDateDebut(), competition.getDateFin(), true, indispo, indispoTerrains);
        }
        else if (Objects.equals(competition.getTypeCompetition(), "Championnat")){
            scheduleResult = scheduler.generateSchedule(equipes, terrains, competition.getDateDebut(), competition.getDateFin(), false, indispo, indispoTerrains);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "pas une competition valide");
        }
        List<Match> matchs = scheduleResult.getMatchs();
        for (Match match : matchs) {
            if (match.getTerrain() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ALERTE : Le match entre " + match.getEquipe1().getIdEquipe() + " et " + match.getEquipe2().getIdEquipe() + " n'a pas de terrain !");
            }
            match.setIdCompetition(competition);
        }

        List<Indisponibilite> indisponibilites = scheduleResult.getIndisponibilites();

        matchRepository.saveAll(matchs);
        indisponibiliteRepository.saveAll(indisponibilites);
        indisponibiliteTerrainRepository.saveAll(indispoTerrains);


        return matchs;
    }

    /**
     * Récupère tous les matchs d'une compétition triés par date ascendante.
     *
     * @param idCompetition identifiant de la compétition
     * @return liste ordonnée de {@link Match}
     */
    public List<Match> getMatchesByCompetition(Long idCompetition) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idCompetition);
    }

    /**
     * Associe un terrain à une compétition.
     *
     * @param idCompetition identifiant de la compétition
     * @param idTerrain     identifiant du terrain à ajouter
     * @return la {@link Competition} mise à jour
     */
    public Competition ajouterTerrainACompetition(Long idCompetition, Long idTerrain) {
        Competition competition = competitionRepository.findById(idCompetition)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compétition introuvable"));

        Terrain terrain = terrainService.getById(idTerrain);
        if (terrain == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Terrain introuvable");
        }

        if (!competition.getTerrains().contains(terrain)) {
            competition.getTerrains().add(terrain);
        }
        return competitionRepository.save(competition);
    }

    /**
     * Retire un terrain d'une compétition.
     *
     * @param idCompetition identifiant de la compétition
     * @param idTerrain     identifiant du terrain à retirer
     * @return la {@link Competition} mise à jour
     */
    public Competition retirerTerrainDeCompetition(Long idCompetition, Long idTerrain) {
        Competition competition = competitionRepository.findById(idCompetition)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compétition introuvable"));
        Terrain terrain = terrainService.getById(idTerrain);
        if (terrain == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Terrain introuvable");
        }
        competition.getTerrains().remove(terrain);

        return competitionRepository.save(competition);
    }

    /**
     * Supprime tous les matchs et les indisponibilités associés à une compétition.
     *
     * Cette opération n'est possible que si la compétition n'a pas encore commencé.
     *
     * @param idCompetition identifiant de la compétition à nettoyer
     * @throws ResponseStatusException si la compétition est déjà commencée
     */
    public void nettoyerMatchsEtIndispos(Long idCompetition) {
        Competition competition = getCompetitionById(idCompetition);
        if (competition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compétition n'existe pas");
        }

        checkCommencer(idCompetition);

        if(!competition.isCommencer()){

            List<Match> anciensMatchs = matchRepository.findByIdCompetition_IdCompetition(idCompetition);


            if (anciensMatchs.isEmpty()) return;



            for (Match match : anciensMatchs) {
                IndisponibiliteTerrain terrain = indisponibiliteTerrainRepository.findByMatch(match);
                List<Indisponibilite> indisponibilites = indisponibiliteRepository.findByMatch(match);
                if(terrain != null)
                    indisponibiliteTerrainRepository.delete(terrain);
                if(!indisponibilites.isEmpty())
                    indisponibiliteRepository.deleteAll(indisponibilites);
            }

            matchRepository.deleteAll(anciensMatchs);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Compétition déjà commencer");
        }

    }

    /**
     * Vérifie et met à jour le statut "commencer" d'une compétition.
     *
     * Une compétition est considérée comme commencée si :
     * 1. Le flag est déjà à {@code true}.
     * 2. La date de début est dépassée.
     * 3. Au moins un match de la compétition a un statut différent de 'WAITING'.
     *
     * @param idCompetition identifiant de la compétition
     * @return la {@link Competition} avec son statut mis à jour
     */
    public Competition checkCommencer(Long idCompetition) {
        Competition competition = getCompetitionById(idCompetition);
        if (competition == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Compétition n'existe pas"
            );
        }


        if (Boolean.TRUE.equals(competition.isCommencer())) {
            return competition;
        }


        LocalDate today = LocalDate.now();
        competition.setCommencer(false);


        if (competition.getDateDebut().isBefore(today)) {
            competition.setCommencer(true);
            return competition;
        }



        List<Match> matchs =
                matchRepository.findByIdCompetition_IdCompetition(idCompetition);

        if (matchs != null && !matchs.isEmpty()) {
            for (Match match : matchs) {
                if (match.getStatus() != Match.Status.WAITING) {
                    competition.setCommencer(true);
                    break;
                }
            }
        }

        return saveCompetition(competition);
    }

}
