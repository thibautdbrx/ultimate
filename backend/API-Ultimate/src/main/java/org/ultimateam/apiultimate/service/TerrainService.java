package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.ultimateam.apiultimate.DTO.Weather_location.*;
import org.ultimateam.apiultimate.model.Terrain;
import org.ultimateam.apiultimate.repository.TerrainRepository;

import java.util.List;


@Service
public class TerrainService {

    private final TerrainRepository terrainRepository;
    private final RestClient restClient;

    public TerrainService(TerrainRepository terrainRepository) {
        this.terrainRepository = terrainRepository;
        this.restClient = RestClient.builder()
                .defaultHeader("User-Agent", "Ultimateam-App-2026")
                .build();
    }

    public Terrain saveTerrain(Terrain terrain) {
        if (terrain.getVille() != null && (terrain.getLatitude() == null || terrain.getLongitude() == null || terrain.getLatitude() == 0 || terrain.getLongitude() == 0)) {
            completerCoordonnees(terrain);
        }
        else if (terrain.getLatitude() != null && terrain.getLongitude() != null && terrain.getVille() == null) {
            completerVille(terrain);
        }
        return terrainRepository.save(terrain);
    }

    public Terrain getById(Long id) {return terrainRepository.findById(id).orElse(null);}

    public List<Terrain> getAll() {return terrainRepository.findAll();}

    private void completerCoordonnees(Terrain terrain) {
        try {
            String url = "https://geocoding-api.open-meteo.com/v1/search?name={ville}&count=1&language=fr&format=json";

            GeoResult res = restClient.get()
                    .uri(url, terrain.getVille())
                    .retrieve()
                    .body(GeoResult.class);

            if (res != null && res.results() != null && !res.results().isEmpty()) {
                terrain.setLatitude(res.results().get(0).latitude());
                terrain.setLongitude(res.results().get(0).longitude());
            }
        } catch (Exception e) {
            System.err.println("Erreur Geocoding : " + e.getMessage());
        }
    }

    private void completerVille(Terrain terrain) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}";

            ReverseResult res = restClient.get()
                    .uri(url, terrain.getLatitude(), terrain.getLongitude())
                    .retrieve()
                    .body(ReverseResult.class);

            if (res != null && res.getCityName() != null) {
                terrain.setVille(res.getCityName());
            }
        } catch (Exception e) {
            System.err.println("Erreur Reverse Geocoding : " + e.getMessage());
        }
    }

    public void deleteById(long id) {
        terrainRepository.deleteById(id);
    }
}