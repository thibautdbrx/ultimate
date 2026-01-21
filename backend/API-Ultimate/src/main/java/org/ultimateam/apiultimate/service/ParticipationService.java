package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.repository.CompetitionRepository;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service gérant les participations des équipes aux compétitions.
 *
 * <p>Fournit des opérations pour lister, ajouter, supprimer et récupérer
 * les participations (liens équipe & compétition) ainsi que des helpers
 * pour ajouter une liste d'équipes.</p>
 */
@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EquipeRepository equipeRepository;
    private final CompetitionRepository competitionRepository;

    /**
     * Constructeur pour l'injection des repositories nécessaires.
     *
     * @param participationRepository repository pour les participations
     * @param equipeRepository repository pour les équipes
     * @param competitionRepository repository pour les compétitions
     */
    public ParticipationService(ParticipationRepository participationRepository, EquipeRepository equipeRepository, CompetitionRepository competitionRepository) {
        this.participationRepository = participationRepository;
        this.equipeRepository = equipeRepository;
        this.competitionRepository = competitionRepository;

    }

    /**
     * Récupère toutes les participations enregistrées.
     *
     * @return liste de toutes les {@link Participation}
     */
    public List<Participation> getAll(){return participationRepository.findAll();}

    /**
     * Récupère les équipes participantes d'une compétition donnée.
     *
     * <p>Pour chaque {@link Participation} liée à la compétition, la méthode
     * récupère l'entité {@link Equipe}. Si une équipe référencée n'existe plus,
     * une {@link ResponseStatusException} avec statut 404 est levée.</p>
     *
     * @param idCompetition identifiant de la compétition
     * @return liste des {@link Equipe} participant à la compétition
     * @throws ResponseStatusException si une équipe référencée est introuvable
     */
    public List<Equipe> getParticipationByCompetitionId(Long idCompetition){
        List<Equipe> equipes = new java.util.ArrayList<>();
        for(Participation p : participationRepository.findById_idCompetition(idCompetition) ){
            equipes.add(
                    equipeRepository.findById(p.getId().getIdEquipe())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée (peut-être supprimée)"))
            );
        }
        return equipes;
    }

    /**
     * Récupère les participations associées à une équipe donnée.
     *
     * @param idEquipe identifiant de l'équipe
     * @return liste des {@link Participation} pour l'équipe
     */
    public List<Participation> getParticipationByEquipeId(Long idEquipe){return participationRepository.findById_idEquipe(idEquipe);}

    /**
     * Persiste une participation (création ou mise à jour).
     *
     * @param participation l'entité {@link Participation} à sauvegarder
     * @return la participation persistée
     */
    public Participation save(Participation participation){return participationRepository.save(participation);}

    /**
     * Supprime une participation identifiée par sa clé composite.
     *
     * <p>Vérifie d'abord que la compétition existe et que sa date de début
     * n'est pas passée (on ne peut pas retirer une équipe d'une compétition
     * déjà commencée). Supprime ensuite la participation correspondante.</p>
     *
     * @param id clé composite {@link ParticipationId} (idEquipe + idCompetition)
     * @return la liste des participations restante après suppression
     * @throws ResponseStatusException si la compétition n'existe pas ou si la suppression est interdite
     */
    public List<Participation> deleteById(ParticipationId id){

        Competition competition = competitionRepository.findById(id.getIdCompetition())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition non trouvée"));

        if (competition.getDateDebut().isBefore(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible de supprimer une équipe à une competition déjà commencée");
        List<Participation> participations = getAll();
        for (Participation p : participations) {
            if (p.getId().equals(id)){
                participationRepository.delete(p);
                }
        }
        return getAll();
    }


    /**
     * Ajoute une participation après validation de l'équipe et de la compétition.
     *
     * <p>Vérifie que l'équipe et la compétition existent, que la compétition
     * n'a pas commencé, et que le genre de l'équipe correspond au genre de la compétition.
     * Si tout est valide, crée et persiste la participation.</p>
     *
     * @param participationId clé composite contenant idEquipe et idCompetition
     * @return la {@link Participation} créée
     * @throws ResponseStatusException si l'équipe ou la compétition est introuvable, si la compétition a commencé, ou si les genres diffèrent
     */
    public Participation addParticipation(ParticipationId participationId) {

        Equipe equipe = equipeRepository.findById(participationId.getIdEquipe())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Équipe non trouvée"));

        Competition competition = competitionRepository.findById(participationId.getIdCompetition())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition non trouvée"));

        if (competition.getDateDebut().isBefore(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible d'ajouter une équipe à une competition déjà commencée");

        if (!equipe.isFull())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "L'équipe n'est pas pleine");
        else if (equipe.getGenre().name().equals(competition.getGenre().name())) {
            Participation participation = new Participation(equipe, competition);
            return participationRepository.save(participation);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas le même genre");
        }

    }

    /**
     * Ajoute une liste d'équipes à une compétition en utilisant {@link #addParticipation(ParticipationId)}.
     *
     * @param listEquipeDTO DTO contenant la liste des id d'équipes et l'id de la compétition
     * @return la liste des {@link Participation} créées et persistées
     */
    public List<Participation> addListParticipation(ListEquipeDTO listEquipeDTO){
        List<Long> idEquipes = listEquipeDTO.getIdEquipes();
        List<Participation> participations = new ArrayList<>();
        for (Long idEquipe : idEquipes) {
            ParticipationId id = new ParticipationId(idEquipe, listEquipeDTO.getIdCompetition());
            participations.add(addParticipation(id));

        }
        return participationRepository.saveAll(participations);
    }
}