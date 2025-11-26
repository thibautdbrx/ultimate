package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;
import org.ultimateam.apiultimate.repository.TournoisRepository;

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

    public TournoisService(TournoisRepository tournoisRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository, RoundRobinSchedulerService scheduler) {
        this.tournoisRepository = tournoisRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
        this.scheduler = scheduler;
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


        List<Match> matches = scheduler.generateSchedule(equipes, tournoi.getDateDebut(), tournoi.getDateFin(), true, indispo);

        matchRepository.saveAll(matches);


        return equipes;
/**
        Tournois tournois = getTournoisById(idTournois);
        List<Equipe> liste_equipe = participationRepository.findById_TournoisId(idTournois).get(0);
        int nb_equipe = participationRepository.findById_TournoisId(idTournois).size();

        if (nb_equipe % 2 != 0) {
            nb_equipe = nb_equipe + 1;
        }


        int nb_round = nb_equipe/2*(nb_equipe-1);

        for (int x = 1; x <= nb_round/2+1; x++) {
            Match match = new Match();
        } */
    }

}

