package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Championnat;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.service.ChamionnatService;

import java.util.List;

@RestController
@RequestMapping("/api/championnats")
@Tag(name = "Championnat", description = "Endpoints pour gérer les championnats")
public class ChamionnatController {


    private final ChamionnatService chamionnatService;

    @Autowired
    public ChamionnatController(ChamionnatService chamionnatService) { this.chamionnatService = chamionnatService;}

    @GetMapping
    public List<Championnat> findAll() { return (List<Championnat>) chamionnatService.getAllChampionnat(); }

    @GetMapping("/{id}")
    public Championnat findById(@PathVariable Long id) { return chamionnatService.getChampionnatById(id); }

    @GetMapping("{idChampionnat}/matchs")
    public List<Match> findMatches(@PathVariable Long idChampionnat) { return chamionnatService.getMatchesByChampionnat(idChampionnat);}

    @PostMapping
    public Championnat creerChampionnat(@RequestBody Championnat tournois) {return chamionnatService.saveChampionnat(tournois); }

    @PatchMapping("/{idChampionnat}")
    public Championnat editTournoi(@RequestBody EquipeNameDTO nameDTO, @PathVariable long idChampionnat) { return chamionnatService.editChampionnat(nameDTO, idChampionnat);}


    /**
    @PutMapping("/{idChampionnat}/create")
    public List<Equipe> genererMatchs(@PathVariable Long idChampionnat) { return chamionnatService.genererRoundRobin(idChampionnat);}
**/
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { chamionnatService.deleteChampionnatById(id); }


}
