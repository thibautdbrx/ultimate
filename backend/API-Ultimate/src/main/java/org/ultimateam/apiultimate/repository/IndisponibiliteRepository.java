package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Indisponibilite;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Indisponibilite}.
 *
 * Fournit des méthodes pour récupérer les indisponibilités liées aux équipes.
 * Les méthodes CRUD standard sont héritées de {@link JpaRepository}.
 */
@Repository
public interface IndisponibiliteRepository extends JpaRepository<Indisponibilite, Long> {

    /**
     * Récupère toutes les indisponibilités déclarées pour une équipe donnée.
     *
     * Utile pour vérifier la disponibilité d'une équipe lors de la génération
     * d'un planning ou pour afficher les créneaux indisponibles côté client.
     *
     * @param idEquipe identifiant de l'équipe
     * @return liste des {@link Indisponibilite} associées à l'équipe (peut être vide)
     */
    List<Indisponibilite> findAllByEquipe_IdEquipe(Long idEquipe);
}
