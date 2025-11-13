package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.util.List;

@Service
public class ParticipationService {

    private ParticipationRepository participationRepository;

    public ParticipationService(ParticipationRepository participationRepository) {}

    public List<Participation> getAll(){return participationRepository.findAll();}

    public Participation getParticipationById(Long idTournois){return participationRepository.findById(idTournois).orElse(null);}

    public Participation save(Participation participation){return participationRepository.save(participation);}

    public void deleteById(Long id){ participationRepository.deleteById(id);}

}