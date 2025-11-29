package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChamionnatService {

    private final ChampionnatRepository championnatRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;
    private final MatchRepository matchRepository;
    private final RoundRobinSchedulerService scheduler;
    private final IndisponibiliteRepository indisponibiliteRepository;

    public ChamionnatService(ChampionnatRepository championnatRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository, RoundRobinSchedulerService scheduler, IndisponibiliteRepository indisponibiliteRepository) {
        this.championnatRepository = championnatRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
        this.scheduler = scheduler;
        this.indisponibiliteRepository = indisponibiliteRepository;
    }

    public List<Championnat> getAllChampionnat() {
        return championnatRepository.findAll();
    }

    public Championnat getChampionnatById(Long id) {
        return championnatRepository.findById(id).orElse(null);
    }

    public Championnat saveChampionnat(Championnat tournois) {
        return championnatRepository.save(tournois);
    }

    public void deleteChampionnatById(Long id) {
        championnatRepository.deleteById(id);
    }

    public List<Match> getMatchesByChampionnat(Long idChampionnat) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idChampionnat);
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
/**

    //Pour le moment genererRoundRobin renvoie la liste des equipes qui participent à la competition.
    public List<Equipe> genererRoundRobin(Long idChampionnat) {


        Championnat tournoi = getChampionnatById(idChampionnat);
        if (tournoi == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Championnat n'existe pas");
        }
        List<Participation> participations = participationRepository.findById_idCompetition(idChampionnat);
        List<Equipe> equipes = new ArrayList<>();
        List<Indisponibilite> indispo = new ArrayList<>();
        for (Participation participation : participations) {
            Equipe equipe = equipeService.getById(participation.getId().getIdEquipe());
            equipes.add(equipe);
            indispo.addAll(equipeService.getIndisponibilites(equipe.getIdEquipe()));

        }

        ScheduleResult scheduleResult = scheduler.generateSchedule(equipes, tournoi.getDateDebut(), tournoi.getDateFin(), true, indispo);
        List<Match> matchs = scheduleResult.getMatchs();
        List<Indisponibilite> indisponibilites = scheduleResult.getIndisponibilites();

        matchRepository.saveAll(matchs);
        indisponibiliteRepository.saveAll(indisponibilites);


        return equipes;
    }
 **/

}

