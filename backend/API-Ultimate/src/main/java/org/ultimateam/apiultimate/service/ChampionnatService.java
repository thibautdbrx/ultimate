package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Championnat;
import org.ultimateam.apiultimate.repository.ChampionnatRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.util.List;

@Service
public class ChampionnatService {

    private final ChampionnatRepository championnatRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;
    private final MatchRepository matchRepository;

    public ChampionnatService(ChampionnatRepository championnatRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository) {
        this.championnatRepository = championnatRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
    }

    public Championnat saveChampionnat(Championnat championnat) {
        return championnatRepository.save(championnat);
    }

    public List<Championnat> getAllChampionnat() {
        return championnatRepository.findAll();
    }

}
