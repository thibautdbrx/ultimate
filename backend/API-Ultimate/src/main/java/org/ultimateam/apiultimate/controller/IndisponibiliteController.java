package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.IndisponibiliteService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/indisponibilite")
public class IndisponibiliteController {

    private final IndisponibiliteService indisponibiliteService;

    public IndisponibiliteController(IndisponibiliteService indisponibiliteService) {
        this.indisponibiliteService = indisponibiliteService;
    }
    @GetMapping
    public List<IndisponibiliteDTO> getAllIndisponibilites() {
        return indisponibiliteService.getAllDTO();
    }
    @GetMapping("/{id}")
    public IndisponibiliteDTO getIndisponibiliteById(@PathVariable Long id) {
        return indisponibiliteService.getByIdDTO(id);
    }

    @GetMapping("/equipe/{id}")
    public List<IndisponibiliteDTO> getEquipeIndisponibilite(@PathVariable Long id){
        return indisponibiliteService.getEquipeIndisponibilite(id);
    }

    @PostMapping
    public Indisponibilite createIndisponibilite(@RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.addIndisponibilite(dto);
    }

    @PutMapping("/{id}/{id_equipe}")
    public Indisponibilite editEquipe(@PathVariable Long id, @PathVariable Long id_equipe) {
        return indisponibiliteService.editEquipe(id, id_equipe);
    }

    //Json = "dateDebut" : "yyyy-MM-dd HH:mm", "dateFin" : "yyyy-MM-dd HH:mm"
    @PutMapping("/{id}")
    public Indisponibilite updateIndisponibilite(
            @PathVariable Long id,
            @RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.updateIndisponibilite(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteIndisponibilite(@PathVariable Long id) {
        indisponibiliteService.deleteIndisponibilite(id);
    }


}
