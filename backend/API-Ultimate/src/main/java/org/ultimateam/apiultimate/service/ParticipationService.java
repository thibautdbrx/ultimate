package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.repository.CompetitionRespository;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EquipeRepository equipeRepository;
    private final CompetitionRespository competitionRespository;
    public ParticipationService(ParticipationRepository participationRepository, EquipeRepository equipeRepository, CompetitionRespository competitionRespository) {
        this.participationRepository = participationRepository;
        this.equipeRepository = equipeRepository;
        this.competitionRespository = competitionRespository;

    }

    public List<Participation> getAll(){return participationRepository.findAll();}

    public List<Equipe> getParticipationByCompetitionId(Long idCompetition){
        List<Equipe> equipes = new java.util.ArrayList<>();
        for(Participation p : participationRepository.findById_idCompetition(idCompetition) ){
            equipes.add(
                    equipeRepository.findById(p.getId().getIdEquipe())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée"))
            );
        }
        return equipes;
    }

    public List<Participation> getParticipationByEquipeId(Long idEquipe){return participationRepository.findById_idEquipe(idEquipe);}

    public Participation save(Participation participation){return participationRepository.save(participation);}

    public List<Participation> deleteById(ParticipationId id){
        List<Participation> participations = getAll();
        participations.removeIf(p -> p.getId().equals(id));
        return participationRepository.saveAll(participations);
    }


    public Participation addParticipation(ParticipationId participationId) {

        Equipe equipe = equipeRepository.findById(participationId.getIdEquipe())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée"));

        Competition competition = competitionRespository.findById(participationId.getIdCompetition())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition non trouvée"));

        if (equipe.getGenre().name().equals(competition.getGenre().name())) {
            Participation participation = new Participation(equipe, competition);
            return participationRepository.save(participation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas le même genre");
        }

    }

    public List<Participation> addListParticipation(long idCompetition, ListEquipeDTO listEquipeDTO){
        List<Long> idEquipes = listEquipeDTO.getIdEquipes();
        List<Participation> participations = new ArrayList<>();
        for (Long idEquipe : idEquipes) {
            ParticipationId id = new ParticipationId(idEquipe, idCompetition);
            participations.add(addParticipation(id));

        }
        return participationRepository.saveAll(participations);
    }
}