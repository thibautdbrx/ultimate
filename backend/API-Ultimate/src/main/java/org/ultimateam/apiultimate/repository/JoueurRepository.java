package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.DTO.GenreJoueur;
import org.ultimateam.apiultimate.model.Joueur;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Joueur}.
 *
 * Fournit des méthodes de recherche pour récupérer les joueurs selon leur équipe ou leur genre.
 * Les méthodes CRUD standard sont héritées de {@link JpaRepository}.
 */
@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    /**
     * Récupère tous les joueurs appartenant à l'équipe identifiée par {@code idEquipe}.
     *
     * @param idEquipe identifiant de l'équipe
     * @return liste des {@link Joueur} associés à l'équipe (peut être vide)
     */
    List<Joueur> findAllByEquipe_IdEquipe(Long idEquipe);

    /**
     * Compte le nombre de joueurs affectés à une équipe donnée.
     *
     * Utile pour vérifier la taille d'une équipe sans charger tous les joueurs en mémoire.
     *
     * @param idEquipe identifiant de l'équipe
     * @return nombre de joueurs liés à l'équipe
     */
    int countByEquipe_IdEquipe(Long idEquipe);

    /**
     * Récupère tous les joueurs qui ne sont assignés à aucune équipe.
     *
     * @return liste des {@link Joueur} sans équipe (peut être vide)
     */
    List<Joueur> findAllByEquipe_IdEquipeIsNull();

    /**
     * Récupère tous les joueurs non assignés à une équipe et dont le genre correspond
     * au genre fourni.
     *
     * @param genre genre recherché (valeur de {@link GenreJoueur})
     * @return liste des {@link Joueur} correspondants
     */
    List<Joueur> findAllByEquipe_IdEquipeIsNullAndGenre(GenreJoueur genre);

    /**
     * Récupère tous les joueurs ayant le genre spécifié.
     *
     * @param genre genre recherché
     * @return liste des {@link Joueur} du genre donné
     */
    List<Joueur> findAllByGenre(GenreJoueur genre);
}
