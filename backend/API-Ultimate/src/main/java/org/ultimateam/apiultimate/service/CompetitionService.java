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

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;
    private final RoundRobinSchedulerService scheduler;
    private final IndisponibiliteRepository indisponibiliteRepository;
    private final ClassementRepository classementRepository;

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

    public List<Competition> getAllCompetition() {
        return competitionRepository.findAll();
    }

    public Competition getCompetitionById(Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    public Competition saveCompetition(Competition Competition) {
        return competitionRepository.save(Competition);
    }

    public void deleteCompetitionById(Long id) {
        competitionRepository.deleteById(id);
    }


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
            List<Match> matchs = scheduleResult.getMatchs();
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

    public List<Match> getMatchesByCompetition(Long idCompetition) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idCompetition);
    }


}
