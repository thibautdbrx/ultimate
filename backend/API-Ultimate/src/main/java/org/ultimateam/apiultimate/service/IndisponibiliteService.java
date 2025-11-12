package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.repository.IndisponibiliteRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IndisponibiliteService {
    public final IndisponibiliteRepository indisponibiliteRepository;
    public final EquipeService equipeService;

    public IndisponibiliteService(IndisponibiliteRepository indisponibiliteRepository, EquipeService equipeService) {
        this.indisponibiliteRepository = indisponibiliteRepository;
        this.equipeService = equipeService;
    }
    public List<Indisponibilite> getAll() {

        return indisponibiliteRepository.findAll();
    }
    public List<IndisponibiliteDTO> getAllDTO() {
        return indisponibiliteRepository.findAll().stream()
                .map(indispo -> new IndisponibiliteDTO(
                        indispo.getIdIndisponibilite(),
                        indispo.getEquipe().getIdEquipe(),
                        indispo.getDate_debut_indisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        indispo.getDate_fin_indisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                ))
                .toList();
    }

    public Indisponibilite getById(Long id) {
        // retourne l'entité (utilisé par update/edit dans le service)
        return indisponibiliteRepository.findById(id).orElse(null);
    }

    public IndisponibiliteDTO getByIdDTO(Long id) {
        Indisponibilite indispo = getById(id);
        if (indispo == null) return null;

        Long idEquipe = (indispo.getEquipe() != null) ? indispo.getEquipe().getIdEquipe() : null;
        String dateDebut = (indispo.getDate_debut_indisponibilite() != null)
                ? indispo.getDate_debut_indisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : null;
        String dateFin = (indispo.getDate_fin_indisponibilite() != null)
                ? indispo.getDate_fin_indisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : null;

        return new IndisponibiliteDTO(
                indispo.getIdIndisponibilite(),
                idEquipe,
                dateDebut,
                dateFin
        );
    }
    public Indisponibilite addIndisponibilite(IndisponibiliteDTO dto) {
        if (dto.getIdIndisponibilite() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez associer à une équipe");;
        Indisponibilite indisponibilite = new Indisponibilite();


        // Conversion des dates
        if (dto.getDateDebut() != null) {
            indisponibilite.setDate_debut_indisponibilite(
                    LocalDateTime.parse(dto.getDateDebut(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        if (dto.getDateFin() != null) {
            indisponibilite.setDate_fin_indisponibilite(
                    LocalDateTime.parse(dto.getDateFin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        // Récupérer l'équipe si idEquipe est fourni
        if (dto.getIdEquipe() != 0) {  // ou null selon ton DTO
            indisponibilite.setEquipe(equipeService.getById(dto.getIdEquipe()));
        }


        return indisponibiliteRepository.save(indisponibilite);
    }
    public Indisponibilite save(Indisponibilite indisponibilite) {
        return indisponibiliteRepository.save(indisponibilite);
    }

    public void deleteIndisponibilite(Long id) {

        indisponibiliteRepository.deleteById(id);
    }

    public List<IndisponibiliteDTO> getEquipeIndisponibilite(Long id) {
        return indisponibiliteRepository.findAllByEquipe_IdEquipe(id)
                .stream()
                .map(indispo -> new IndisponibiliteDTO(
                        indispo.getIdIndisponibilite(),
                        indispo.getEquipe().getIdEquipe(),
                        indispo.getDate_debut_indisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        indispo.getDate_fin_indisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                ))
                .toList();
    }

    public Indisponibilite editEquipe(Long id, Long id_equipe) {
        Indisponibilite indisponibilite = getById(id);
        if (indisponibilite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'indisponibilite n'existe pas");
        Equipe equipe = equipeService.getById(id_equipe);
        indisponibilite.setEquipe(equipe);
        return save(indisponibilite);
    }
    public Indisponibilite updateIndisponibilite(Long id, IndisponibiliteDTO dto) {
        Indisponibilite indisponibilite = getById(id);

        if(dto.getDateDebut() != null) {
            indisponibilite.setDate_debut_indisponibilite(
                    LocalDateTime.parse(dto.getDateDebut(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        if(dto.getDateFin() != null) {
            indisponibilite.setDate_fin_indisponibilite(
                    LocalDateTime.parse(dto.getDateFin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        return save(indisponibilite);
    }

}
