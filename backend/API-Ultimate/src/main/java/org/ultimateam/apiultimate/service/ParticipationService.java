package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.repository.CompetitionRepository;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EquipeRepository equipeRepository;
    private final CompetitionRepository competitionRepository;
    public ParticipationService(ParticipationRepository participationRepository, EquipeRepository equipeRepository, CompetitionRepository competitionRepository) {
        this.participationRepository = participationRepository;
        this.equipeRepository = equipeRepository;
        this.competitionRepository = competitionRepository;

    }

    public List<Participation> getAll(){return participationRepository.findAll();}

    public List<Equipe> getParticipationByCompetitionId(Long idCompetition){
        List<Equipe> equipes = new java.util.ArrayList<>();
        for(Participation p : participationRepository.findById_idCompetition(idCompetition) ){
            equipes.add(
                    equipeRepository.findById(p.getId().getIdEquipe())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée (peut-être supprimée)"))
            );
        }
        return equipes;
    }

    public List<Participation> getParticipationByEquipeId(Long idEquipe){return participationRepository.findById_idEquipe(idEquipe);}

    public Participation save(Participation participation){return participationRepository.save(participation);}

    public List<Participation> deleteById(ParticipationId id){

        Competition competition = competitionRepository.findById(id.getIdCompetition())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition non trouvée"));

        if (competition.getDateDebut().isBefore(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible d'ajouter une équipe à une competition déjà commencée");
        List<Participation> participations = getAll();
        for (Participation p : participations) {
            if (p.getId().equals(id)){
                System.out.println(p.getId());
                participationRepository.delete(p);
                }
        }
        return getAll();
    }


    public Participation addParticipation(ParticipationId participationId) {

        Equipe equipe = equipeRepository.findById(participationId.getIdEquipe())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée"));

        Competition competition = competitionRepository.findById(participationId.getIdCompetition())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition non trouvée"));

        if (competition.getDateDebut().isBefore(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible d'ajouter une équipe à une competition déjà commencée");

        if (equipe.getGenre().name().equals(competition.getGenre().name())) {
            Participation participation = new Participation(equipe, competition);
            return participationRepository.save(participation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas le même genre");
        }

    }

    public List<Participation> addListParticipation(ListEquipeDTO listEquipeDTO){
        List<Long> idEquipes = listEquipeDTO.getIdEquipes();
        List<Participation> participations = new ArrayList<>();
        for (Long idEquipe : idEquipes) {
            ParticipationId id = new ParticipationId(idEquipe, listEquipeDTO.getIdCompetition());
            participations.add(addParticipation(id));

        }
        return participationRepository.saveAll(participations);
    }
}