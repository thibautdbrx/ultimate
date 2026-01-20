package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.model.Terrain;
import org.ultimateam.apiultimate.repository.IndisponibiliteTerrainRepository;

import java.util.List;

@Service
public class IndisponibiliteTerrainService {

    private final IndisponibiliteTerrainRepository repository;
    private final TerrainService terrainService;

    public IndisponibiliteTerrainService(IndisponibiliteTerrainRepository repository, TerrainService terrainService) {
        this.repository = repository;
        this.terrainService = terrainService;
    }

    public List<IndisponibiliteTerrain> findAll() {
        return repository.findAll();
    }

    public List<IndisponibiliteTerrain> findByTerrainId(Long idTerrain) {
        if (terrainService.getById(idTerrain) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Terrain introuvable");
        }
        return repository.findByTerrain_IdTerrain(idTerrain);
    }

    public IndisponibiliteTerrain save(Long idTerrain, IndisponibiliteTerrain indispo) {
        Terrain terrain = terrainService.getById(idTerrain);
        if (terrain == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Terrain introuvable avec l'id : " + idTerrain);
        }

        if (indispo.getDateDebutIndisponibilite() == null || indispo.getDateFinIndisponibilite() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates de début et fin obligatoire");
        }

        if (indispo.getDateDebutIndisponibilite().isAfter(indispo.getDateFinIndisponibilite())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de début doit être avant la date de fin");
        }

        indispo.setTerrain(terrain);
        return repository.save(indispo);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indisponibilité introuvable");
        }
        repository.deleteById(id);
    }
}