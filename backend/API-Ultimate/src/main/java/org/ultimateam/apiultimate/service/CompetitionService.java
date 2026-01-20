package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ScheduleResult;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Competition> getAllCompetition() {
        return competitionRepository.findAll();
    }

    public Competition getCompetitionById(Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    public Competition saveCompetition(Competition Competition) {
        if (Competition.getDateDebut() == null || Competition.getDateFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez renseigner les dates");
        }
        return competitionRepository.save(Competition);
    }

    public void deleteCompetitionById(Long id) {
        competitionRepository.deleteById(id);
    }


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

    public List<Match> getMatchesByCompetition(Long idCompetition) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idCompetition);
    }

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
     * Nettoie TOUT : Matchs, Indispos Terrains ET Indispos Équipes
     */
    private void nettoyerMatchsEtIndispos(Long idCompetition) {
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

    public Competition checkCommencer(Long idCompetition) {
        Competition competition = getCompetitionById(idCompetition);
        if (competition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compétition n'existe pas");
        }

        List<Match> matchs = matchRepository.findByIdCompetition_IdCompetition(idCompetition);
        if(matchs != null || !matchs.isEmpty())
            for (Match match : matchs) {
                if (match.getStatus() != Match.Status.WAITING)
                    competition.setCommencer(true);
            }
        else{
            competition.setCommencer(false);
        }
        return saveCompetition(competition);
    }

}
