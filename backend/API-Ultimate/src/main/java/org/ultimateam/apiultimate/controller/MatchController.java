package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private MatchService matchService;

    public MatchController(MatchService matchService) { this.matchService = matchService; }

    @GetMapping
    public List<Match> getAllMatch() { return (List<Match>) matchService.getAll();}

    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) { return matchService.getById(id);}

    @PostMapping
    public Match createMatch(@RequestBody Match match) { return matchService.save(match);}

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) { matchService.deleteById(id);}

}

