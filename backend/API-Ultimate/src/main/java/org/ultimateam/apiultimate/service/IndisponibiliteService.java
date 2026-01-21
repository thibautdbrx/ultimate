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

/**
 * Service gérant les indisponibilités des équipes.
 *
 * <p>Ce service permet de récupérer, convertir en DTO, créer, modifier et supprimer
 * des {@link Indisponibilite} ainsi que d'effectuer les validations liées aux dates.</p>
 */
@Service
public class IndisponibiliteService {
    public final IndisponibiliteRepository indisponibiliteRepository;
    public final EquipeService equipeService;

    /**
     * Constructeur pour l'injection des dépendances du service.
     *
     * @param indisponibiliteRepository repository pour accéder aux indisponibilités
     * @param equipeService service pour accéder aux informations des équipes
     */
    public IndisponibiliteService(IndisponibiliteRepository indisponibiliteRepository, EquipeService equipeService) {
        this.indisponibiliteRepository = indisponibiliteRepository;
        this.equipeService = equipeService;
    }

    /**
     * Récupère toutes les indisponibilités en base.
     *
     * @return une liste de toutes les entités {@link Indisponibilite}
     */
    public List<Indisponibilite> getAll() {

        return indisponibiliteRepository.findAll();
    }

    /**
     * Récupère toutes les indisponibilités et les transforme en DTO lisibles par l'API.
     *
     * <p>Chaque DTO contient l'identifiant de l'indisponibilité, l'id de l'équipe associée
     * et les dates formatées en "yyyy-MM-dd HH:mm".</p>
     *
     * @return liste des {@link IndisponibiliteDTO}
     */
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

    /**
     * Récupère une indisponibilité par son identifiant.
     *
     * @param id identifiant de l'indisponibilité
     * @return l'entité {@link Indisponibilite} trouvée ou {@code null} si inexistante
     */
    public Indisponibilite getById(Long id) {
        // retourne l'entité (utilisé par update/edit dans le service)
        return indisponibiliteRepository.findById(id).orElse(null);
    }

    /**
     * Récupère une indisponibilité par son identifiant et la transforme en DTO.
     *
     * <p>Cette méthode gère les champs potentiellement nuls (équipe, dates) et renvoie
     * {@code null} si l'indisponibilité n'existe pas.</p>
     *
     * @param id identifiant de l'indisponibilité
     * @return {@link IndisponibiliteDTO} ou {@code null} si introuvable
     */
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

    /**
     * Crée une nouvelle indisponibilité à partir d'un DTO et la persiste en base.
     *
     * <p>La méthode valide la présence d'une équipe associée, convertit les dates,
     * associe l'entité Equipe et sauvegarde l'indisponibilité. Elle renvoie ensuite le DTO
     * représentant l'entité persistée.</p>
     *
     * @param dto DTO contenant les informations de l'indisponibilité à créer
     * @return le {@link IndisponibiliteDTO} de l'entité créée
     * @throws ResponseStatusException si l'id de l'équipe est absent
     */
    public IndisponibiliteDTO addIndisponibilite(IndisponibiliteDTO dto) {
        if (dto.getIdEquipe() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Veuillez associer à une équipe");

        Indisponibilite indispo = new Indisponibilite();

        checkDate(dto, indispo);

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

    /**
     * Persiste une entité {@link Indisponibilite} fournie.
     *
     * @param indisponibilite entité à sauvegarder
     * @return l'entité persistée
     */
    public Indisponibilite save(Indisponibilite indisponibilite) {
        return indisponibiliteRepository.save(indisponibilite);
    }

    /**
     * Supprime une indisponibilité par son identifiant.
     *
     * @param id identifiant de l'indisponibilité à supprimer
     * @throws ResponseStatusException si l'indisponibilité n'existe pas
     */
    public void deleteIndisponibilite(Long id) {
        if (!indisponibiliteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'indisponibilité n'existe pas");
        }
        indisponibiliteRepository.deleteById(id);
    }

    /**
     * Récupère les indisponibilités d'une équipe et les transforme en DTO.
     *
     * @param id identifiant de l'équipe
     * @return liste des {@link IndisponibiliteDTO} pour l'équipe fournie
     */
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

    /**
     * Met à jour une indisponibilité existante à partir d'un DTO.
     *
     * <p>La méthode récupère l'entité existante, vérifie son existence, met à jour
     * les dates via {@link #checkDate(IndisponibiliteDTO, Indisponibilite)} et persiste
     * l'entité mise à jour. Elle renvoie ensuite le DTO correspondant.</p>
     *
     * @param indisponibiliteDTO DTO contenant les nouvelles valeurs
     * @param indisponibiliteId identifiant de l'indisponibilité à mettre à jour
     * @return le {@link IndisponibiliteDTO} de l'entité mise à jour
     * @throws ResponseStatusException si l'indisponibilité n'existe pas
     */
    public IndisponibiliteDTO updateIndisponibilite(IndisponibiliteDTO indisponibiliteDTO, long indisponibiliteId) {
        Indisponibilite indisponibilite = getById(indisponibiliteId);
        if (indisponibilite == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'indisponibilite n'existe pas");

        checkDate(indisponibiliteDTO, indisponibilite);

        Indisponibilite saved = save(indisponibilite);

        return new IndisponibiliteDTO(
                saved.getIdIndisponibilite(),
                saved.getEquipe().getIdEquipe(),
                saved.getDateDebutIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                saved.getDateFinIndisponibilite().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    /**
     * Valide et met à jour les dates d'une entité {@link Indisponibilite} à partir du DTO.
     *
     * <p>Si le DTO contient une date de début ou de fin, elles sont parsées et affectées
     * à l'entité. Ensuite la méthode vérifie que la date de début est antérieure à la date de fin.</p>
     *
     * @param dto le DTO contenant les dates (format attendu "yyyy-MM-dd HH:mm")
     * @param indispo l'entité à mettre à jour
     * @throws ResponseStatusException si la validation des dates échoue
     */
    private void checkDate(IndisponibiliteDTO dto, Indisponibilite indispo) {
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

        // Validation: dateDebut < dateFin
        LocalDateTime dateDebut = indispo.getDateDebutIndisponibilite();
        LocalDateTime dateFin = indispo.getDateFinIndisponibilite();
        if (dateDebut != null && dateFin != null && !dateDebut.isBefore(dateFin)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La date de début doit être avant la date de fin."
            );
        }
    }

}
