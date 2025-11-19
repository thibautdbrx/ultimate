package org.ultimateam.apiultimate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.util.List;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;

    public ParticipationService(ParticipationRepository participationRepository) {}

    public List<Participation> getAll(){return participationRepository.findAll();}

    public List<Participation> getParticipationByTournoisId(Long idCompetition){return participationRepository.findById_idCompetition(idCompetition);}

    public List<Participation> getParticipationByEquipeId(Long idEquipe){return participationRepository.findById_idEquipe(idEquipe);}

    public Participation save(Participation participation){return participationRepository.save(participation);}

    public void deleteById(Long id){ participationRepository.deleteById(id);}

}