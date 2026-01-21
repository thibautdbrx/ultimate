package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.ClassementRepository;

import java.util.List;

/**
 * Service responsable de la gestion des classements des compétitions.
 *
 * <p>Ce service expose des opérations pour récupérer, sauvegarder, supprimer
 * et mettre à jour les {@link Classement} liés à des compétitions et équipes.</p>
 */
@Service
public class ClassementService {

    private final ClassementRepository classementRepository;

    /**
     * Constructeur pour l'injection du repository de classement.
     *
     * @param classementRepository repository utilisé pour accéder aux données de classement
     */
    public ClassementService(ClassementRepository classementRepository ) {
        this.classementRepository = classementRepository;
    }

    /**
     * Récupère tous les classements existants.
     *
     * @return un itérable contenant tous les {@link Classement} en base
     */
    public Iterable<Classement> getAll() {return classementRepository.findAll();}

    /**
     * Sauvegarde ou met à jour un classement en base.
     *
     * <p>Si l'objet {@code classement} possède déjà un identifiant existant, il sera mis à jour,
     * sinon il sera créé.</p>
     *
     * @param classement l'entité {@link Classement} à persister
     * @return le {@link Classement} persistant
     */
    public Classement save(Classement classement) { return classementRepository.save(classement); }

    /**
     * Supprime tous les classements associés à une compétition donnée.
     *
     * <p>Si aucun classement n'est trouvé pour la compétition, une exception {@link ResponseStatusException}
     * avec le statut NOT_FOUND est levée.</p>
     *
     * @param idCompetition l'identifiant de la compétition dont il faut supprimer les classements
     * @return la liste des {@link Classement} supprimés
     * @throws ResponseStatusException si aucun classement n'est trouvé pour la compétition
     */
    public List<Classement> deleteByIdCompetition(Long idCompetition) {
        List<Classement> classements = classementRepository.findAllByCompetition_IdCompetition(idCompetition);
        if (classements.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Classement not found");
        }
        classementRepository.deleteAll(classements);
        return classements;
    }

    /**
     * Supprime tous les classements associés à une équipe donnée.
     *
     * <p>Si aucun classement n'est trouvé pour l'équipe, une exception {@link ResponseStatusException}
     * avec le statut NOT_FOUND est levée.</p>
     *
     * @param idEquipe l'identifiant de l'équipe dont il faut supprimer les classements
     * @return la liste des {@link Classement} supprimés
     * @throws ResponseStatusException si aucun classement n'est trouvé pour l'équipe
     */
    public List<Classement> deleteByIdEquipe(Long idEquipe) {
        List<Classement> classements = classementRepository.findAllByEquipe_IdEquipe(idEquipe);
        if (classements.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe not found");
        }
        classementRepository.deleteAll(classements);
        return classements;

    }


    /**
     * Met à jour les statistiques de classement en fonction du résultat d'un {@link Match} terminé.
     *
     * <p>La méthode vérifie que le match existe et qu'il est terminé. Elle récupère ensuite la
     * compétition et les deux équipes impliquées, récupère leurs entrées de classement existantes
     * et met à jour leurs statistiques (points marqués/encaissés, différence, victoires/égalité/défaites,
     * points de classement).</p>
     *
     * @param match le {@link Match} utilisé pour mettre à jour les classements
     * @throws ResponseStatusException si le match ou la compétition ou un classement attendu n'existe pas
     */
    public void mettreAJourClassement(Match match) {
        if (match==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas.");

        if (match.getStatus() != Match.Status.FINISHED) {
            return;
        }

        Competition competition = match.getIdCompetition();
        if (competition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le competition n'existe pas.");
        }
        Equipe equipe1 = match.getEquipe1();
        Equipe equipe2 = match.getEquipe2();

        Classement classement1 = classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(competition.getIdCompetition(),equipe1.getIdEquipe());
        if (classement1 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le classement n'existe pas.");
        }
        Classement classement2 = classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(competition.getIdCompetition(),equipe2.getIdEquipe());
        if (classement2 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le classement n'existe pas.");
        }

        long score1 = match.getScoreEquipe1();
        long score2 = match.getScoreEquipe2();

        updateStats(classement1, score1, score2);
        updateStats(classement2, score2, score1);


    }

    /**
     * Met à jour les statistiques d'un {@link Classement} donné en fonction des points marqués et encaissés.
     *
     * <p>Cette méthode modifie le nombre de points marqués/encaissés, la différence de points,
     * ainsi que le nombre de victoires, égalités ou défaites et le score de classement.
     * Le classement est ensuite persisté en base.</p>
     *
     * @param classement l'entité {@link Classement} à mettre à jour
     * @param score1 le nombre de points marqués par l'équipe considérée
     * @param score2 le nombre de points marqués par l'adversaire
     */
    private void updateStats(Classement classement, long score1, long score2) {
        classement.setPoint_marque(classement.getPoint_marque() + score1);
        classement.setPoint_encaisse(classement.getPoint_encaisse() + score2);
        classement.setDifference_points(classement.getPoint_marque() - classement.getPoint_encaisse());

        if (score1 > score2) {
            classement.setScore(classement.getScore() + 3 );
            classement.setVictoires(classement.getVictoires() +1);
        }
        else if (score2 == score1) {
            classement.setScore(classement.getScore() +1 );
            classement.setEgalites(classement.getEgalites() + 1);
        } else {
            classement.setDefaites(classement.getDefaites() + 1);
        }
        classementRepository.save(classement);
    }


    /**
     * Trie les classements d'une compétition par rang et réaffecte les rangs séquentiels.
     *
     * <p>La méthode récupère la liste des {@link Classement} ordonnée par rang pour la compétition,
     * met à jour le champ de rang (1..N) en fonction de l'ordre courant, persiste les modifications
     * et renvoie la liste mise à jour.</p>
     *
     * @param idCompetition l'identifiant de la compétition dont on veut trier les classements
     * @return la liste des {@link Classement} triés et avec rangs recalculés
     */
    public List<Classement> triClassement(Long idCompetition) {
        List<Classement> classements = classementRepository.findAllByCompetitionIdOrderByRank(idCompetition);
        for(int i=0; i<classements.size(); i++) {
            classements.get(i).setRang(i+1);
        }

        classementRepository.saveAll(classements);

        return classements;
    }


}
