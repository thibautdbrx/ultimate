package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.service.EquipeService;

import java.util.List;

@RestController
@RequestMapping("/api/equipe")
public class EquipeController {

    private EquipeService equipeService;

    public EquipeController(EquipeService equipeService) { this.equipeService = equipeService; }

    @GetMapping
    public List<Equipe> findAll() { return (List<Equipe>) equipeService.findAll(); }

    @GetMapping("/{id}")
    public Equipe findById(@PathVariable Long id) { return equipeService.getById(id); }

    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) { return equipeService.save(equipe); }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { equipeService.deleteById(id); }
}
