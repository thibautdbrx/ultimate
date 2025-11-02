package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.repository.EquipeRepository;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;

    public EquipeService(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    public Iterable<Equipe> findAll() { return equipeRepository.findAll(); }
    public Equipe findById(Long id) { return equipeRepository.findById(id).orElse(null); }
    public Equipe save(Equipe equipe) { return equipeRepository.save(equipe);}
    public void deleteById(Long id) { equipeRepository.deleteById(id); }
}
