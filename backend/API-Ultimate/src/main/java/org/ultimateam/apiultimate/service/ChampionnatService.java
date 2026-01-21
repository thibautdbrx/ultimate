package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Championnat;
import org.ultimateam.apiultimate.repository.ChampionnatRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.repository.ParticipationRepository;

import java.util.List;

/**
 * Service gérant les opérations liées aux championnats.
 *
 * <p>Ce service expose des méthodes pour créer et récupérer des championnats
 * ainsi que pour accéder aux dépendances nécessaires (participations, équipes, matchs).</p>
 */
@Service
public class ChampionnatService {

    private final ChampionnatRepository championnatRepository;
    private final ParticipationRepository participationRepository;
    private final EquipeService equipeService;
    private final MatchRepository matchRepository;

    /**
     * Constructeur pour l'injection des dépendances du service.
     *
     * @param championnatRepository repository pour l'entité {@link Championnat}
     * @param participationRepository repository pour les participations aux championnats
     * @param equipeService service permettant d'accéder aux informations des équipes
     * @param matchRepository repository pour les matchs liés aux championnats
     */
    public ChampionnatService(ChampionnatRepository championnatRepository, ParticipationRepository participationRepository, EquipeService equipeService,
                           MatchRepository matchRepository) {
        this.championnatRepository = championnatRepository;
        this.participationRepository = participationRepository;
        this.equipeService = equipeService;
        this.matchRepository = matchRepository;
    }

    /**
     * Sauvegarde un championnat en base de données.
     *
     * <p>Si l'objet {@code championnat} contient un identifiant existant, l'entité sera mise à jour,
     * sinon elle sera créée.</p>
     *
     * @param championnat l'objet {@link Championnat} à sauvegarder
     * @return l'entité {@link Championnat} persistée
     */
    public Championnat saveChampionnat(Championnat championnat) {
        return championnatRepository.save(championnat);
    }

    /**
     * Récupère la liste de tous les championnats stockés en base.
     *
     * @return une liste de {@link Championnat} (peut être vide si aucun championnat trouvé)
     */
    public List<Championnat> getAllChampionnat() {
        return championnatRepository.findAll();
    }

}
