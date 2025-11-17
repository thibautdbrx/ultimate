package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.IndisponibiliteService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "Indisponibilite", description = "Endpoints pour gérer les indisponibilités des équipes")
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
    public IndisponibiliteDTO createIndisponibilite(@RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.addIndisponibilite(dto);
    }

    //Json = "dateDebut" : "yyyy-MM-dd HH:mm", "dateFin" : "yyyy-MM-dd HH:mm"
    @PutMapping("/{idIndisponibilite}")
    public IndisponibiliteDTO updateIndisponibilite(@PathVariable Long idIndisponibilite, @RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.updateIndisponibilite(dto, idIndisponibilite);
    }

    @DeleteMapping("/{idIndisponibilite}")
    public void deleteIndisponibilite(@PathVariable Long idIndisponibilite) {
        indisponibiliteService.deleteIndisponibilite(idIndisponibilite);
    }


}
