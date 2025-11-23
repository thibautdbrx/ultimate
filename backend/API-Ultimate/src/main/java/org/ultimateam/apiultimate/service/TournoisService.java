package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;
import org.ultimateam.apiultimate.repository.TournoisRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TournoisService {

    private final TournoisRepository tournoisRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;

    public TournoisService(TournoisRepository tournoisRepository, ParticipationRepository participationRepository, EquipeService equipeService) {
        this.tournoisRepository = tournoisRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
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

