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
                        indispo.getDateDebutIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        indispo.getDateFinIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
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
        String dateDebut = (indispo.getDateDebutIndisponibilite() != null)
                ? indispo.getDateDebutIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : null;
        String dateFin = (indispo.getDateFinIndisponibilite() != null)
                ? indispo.getDateFinIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : null;

        return new IndisponibiliteDTO(
                indispo.getIdIndisponibilite(),
                idEquipe,
                dateDebut,
                dateFin
        );
    }
    public IndisponibiliteDTO addIndisponibilite(IndisponibiliteDTO dto) {
        if (dto.getIdEquipe() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez associer à une équipe");

        Indisponibilite indispo = new Indisponibilite();

        // Dates
        if (dto.getDateDebut() != null) {
            indispo.setDateDebutIndisponibilite(
                    LocalDateTime.parse(dto.getDateDebut(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }
        if (dto.getDateFin() != null) {
            indispo.setDateFinIndisponibilite(
                    LocalDateTime.parse(dto.getDateFin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        Equipe equipe = equipeService.getById(dto.getIdEquipe());
        indispo.setEquipe(equipe);

        Indisponibilite saved = indisponibiliteRepository.save(indispo);

        return new IndisponibiliteDTO(
                saved.getIdIndisponibilite(),
                saved.getEquipe().getIdEquipe(),
                saved.getDateDebutIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                saved.getDateFinIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
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
                        indispo.getDateDebutIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        indispo.getDateFinIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                ))
                .toList();
    }

    public IndisponibiliteDTO updateIndisponibilite(IndisponibiliteDTO indisponibiliteDTO, long indisponibiliteId) {
        Indisponibilite indisponibilite = getById(indisponibiliteId);
        if (indisponibilite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'indisponibilite n'existe pas");

        if(indisponibiliteDTO.getDateDebut() != null) {
            indisponibilite.setDateDebutIndisponibilite(
                    LocalDateTime.parse(indisponibiliteDTO.getDateDebut(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        if(indisponibiliteDTO.getDateFin() != null) {
            indisponibilite.setDateFinIndisponibilite(
                    LocalDateTime.parse(indisponibiliteDTO.getDateFin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }

        Indisponibilite saved = save(indisponibilite);

        return new IndisponibiliteDTO(
                saved.getIdIndisponibilite(),
                saved.getEquipe().getIdEquipe(),
                saved.getDateDebutIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                saved.getDateFinIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

}
