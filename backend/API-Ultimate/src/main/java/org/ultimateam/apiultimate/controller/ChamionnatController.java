package org.ultimateam.apiultimate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Championnat;
import org.ultimateam.apiultimate.service.ChamionnatService;

import java.util.List;

@RestController
@RequestMapping("/api/championnats")
public class ChamionnatController {


    private final ChamionnatService chamionnatService;

    @Autowired
    public ChamionnatController(ChamionnatService chamionnatService) { this.chamionnatService = chamionnatService;}

    @GetMapping
    public List<Championnat> findAll() { return (List<Championnat>) chamionnatService.getAllChampionnat(); }

    @GetMapping("/{id}")
    public Championnat findById(@PathVariable Long id) { return chamionnatService.getChampionnatById(id); }

    @GetMapping("{idTournoi}/matchs")
    public List<Match> findMatches(@PathVariable Long idTournoi) { return chamionnatService.getMatchesByChampionnat(idTournoi);}

    @PostMapping
    public Championnat creerChampionnat(@RequestBody Championnat tournois) {return chamionnatService.saveChampionnat(tournois); }

    /**
    @PutMapping("/{idTournoi}/create")
    public List<Equipe> genererMatchs(@PathVariable Long idTournoi) { return chamionnatService.genererRoundRobin(idTournoi);}
**/
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { chamionnatService.deleteChampionnatById(id); }


}
