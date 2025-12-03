package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.time.LocalDateTime;
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

    public TournoisService(TournoisRepository tournoisRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository, RoundRobinSchedulerService scheduler, IndisponibiliteRepository indisponibiliteRepository) {
        this.tournoisRepository = tournoisRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
        this.scheduler = scheduler;
        this.indisponibiliteRepository = indisponibiliteRepository;
    }

    public List<Tournois> getAllTournois() {
        return tournoisRepository.findAll();
    }

    public Tournois getTournoisById(Long id) {
        return tournoisRepository.findById(id).orElse(null);
    }

    public Tournois saveTournois(Tournois tournois) {
        return tournoisRepository.save(tournois);
    }

    public void deleteTournoisById(Long id) {
        tournoisRepository.deleteById(id);
    }


    public void genererTournois(Long idTournois) {
        genererRoundRobin(idTournois);
    }

    public List<Match> getMatchesByTournois(Long idTournois) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idTournois);
    }

    public record ScheduleResult(
            List<Match> matchs,
            List<Indisponibilite> indisponibilites
    ) {
        public List<Match> getMatchs() {
            return matchs;
        }

        public List<Indisponibilite> getIndisponibilites() {
            return indisponibilites;
        }

        public void addMatch(Match match) {
            matchs.add(match);
        }

        public void addIndisponibilite(Indisponibilite indisponibilite) {
            indisponibilites.add(indisponibilite);
        }
    }


    //Pour le moment genererRoundRobin renvoie la liste des equipes qui participent à la competition.
    public List<Equipe> genererRoundRobin(Long idTournois) {


        Tournois tournoi = getTournoisById(idTournois);
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

        }

        ScheduleResult scheduleResult = scheduler.generateSchedule(equipes, tournoi.getDateDebut(), tournoi.getDateFin(), true, indispo);
        List<Match> matchs = scheduleResult.getMatchs();
        System.out.println(matchs.get(0).getIdMatch());
        List<Indisponibilite> indisponibilites = scheduleResult.getIndisponibilites();

        matchRepository.saveAll(matchs);
        indisponibiliteRepository.saveAll(indisponibilites);


        return equipes;
    }

}

