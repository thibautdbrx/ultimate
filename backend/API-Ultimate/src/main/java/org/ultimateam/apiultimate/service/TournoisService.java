package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.repository.TournoisRepository;

import java.util.List;

@Service
public class TournoisService {

    private final TournoisRepository tournoisRepository;

    public TournoisService(TournoisRepository tournoisRepository) {
        this.tournoisRepository = tournoisRepository;
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

    public void genererTournois() {
        genererRoundRobin();
    }

    public void genererRoundRobin() {
    }
}
