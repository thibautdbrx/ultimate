package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

@RestController
@RequestMapping("/api/tournois")
@Tag(name = "Tournoi", description = "Endpoints pour gérer les tournois")
public class TournoisController {


    private final TournoisService tournoisService;

    @Autowired
    public TournoisController(TournoisService tournoisService) {
        this.tournoisService = tournoisService;
    }

    @GetMapping
    public List<Tournois> findAll() { return (List<Tournois>) tournoisService.getAllTournois(); }

    @GetMapping("/{id}")
    public Tournois findById(@PathVariable Long id) { return tournoisService.getTournoisById(id); }

    @GetMapping("{idTournoi}/matchs")
    public List<Match> findMatches(@PathVariable Long idTournoi) { return tournoisService.getMatchesByTournois(idTournoi);}

    @PostMapping
    public Tournois creerTournois(@RequestBody Tournois tournois) {return tournoisService.saveTournois(tournois); }

    @PatchMapping("/{idTournoi}")
    public Tournois editTournoi(@RequestBody EquipeNameDTO nameDTO, @PathVariable long idTournoi) { return tournoisService.editTournois(nameDTO, idTournoi);}

    @PutMapping("/{idTournoi}/create")
    public List<Equipe> genererMatchs(@PathVariable Long idTournoi) { return tournoisService.genererRoundRobin(idTournoi);}

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { tournoisService.deleteTournoisById(id); }


}
