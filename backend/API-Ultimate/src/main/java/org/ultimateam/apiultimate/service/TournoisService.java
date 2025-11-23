package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.Tournois;
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

    public TournoisService(TournoisRepository tournoisRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository) {
        this.tournoisRepository = tournoisRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
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
        for (Participation participation : participations) {
            equipes.add(equipeService.getById(participation.getId().getIdEquipe()));
        }
        int nbEquipes = equipes.size();

        //Génération de match :
        /**
        Match match = new Match();
        match.setIdCompetition(tournoi);
        Equipe equipe1 = null;
        match.setEquipe1(equipe1);

        Equipe equipe2 = null;
        match.setEquipe2(equipe2);

        LocalDateTime datePrevison = LocalDateTime.now();
        match.setDateMatch(datePrevison);
        matchRepository.save(match);
        */

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

