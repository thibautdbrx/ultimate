package org.ultimateam.apiultimate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.repository.CompetitionRespository;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;
import org.ultimateam.apiultimate.repository.TournoisRepository;

import java.util.List;

@Service
public class ParticipationService {

    private ParticipationRepository participationRepository;
    private EquipeRepository equipeRepository;
    private CompetitionRespository competitionRespository;
    public ParticipationService(ParticipationRepository participationRepository, EquipeRepository equipeRepository, CompetitionRespository competitionRespository) {
        this.participationRepository = participationRepository;
        this.equipeRepository = equipeRepository;
        this.competitionRespository = competitionRespository;

    }

    public List<Participation> getAll(){return participationRepository.findAll();}

    public List<Participation> getParticipationByCompetitionId(Long idCompetition){return participationRepository.findById_idCompetition(idCompetition);}

    public List<Participation> getParticipationByEquipeId(Long idEquipe){return participationRepository.findById_idEquipe(idEquipe);}

    public Participation save(Participation participation){return participationRepository.save(participation);}

    public void deleteById(Long id){ participationRepository.deleteById(id);}


    public Participation addParticipation(Long idEquipe, Long idCompetition) {

        Equipe equipe = equipeRepository.findById(idEquipe)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée"));

        Competition competition = competitionRespository.findById(idCompetition)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition non trouvée"));

        if (equipe.getGenre().name().equals(competition.getGenre().name())) {
            Participation participation = new Participation(equipe, competition);
            return participationRepository.save(participation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas le même genre");
        }

    }
}