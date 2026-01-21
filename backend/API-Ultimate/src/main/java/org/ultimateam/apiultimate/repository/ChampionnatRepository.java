package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Championnat;

/**
 * Repository Spring Data JPA pour l'entité {@link Championnat}.
 *
 * Fournit l'accès aux opérations CRUD et aux fonctionnalités de pagination/tri
 * via l'interface {@link JpaRepository}. Aucune méthode personnalisée n'est
 * définie ici, mais les méthodes standards (save, findById, findAll, delete, ...)
 * sont disponibles via l'héritage de {@code JpaRepository} et documentées dans
 * la Javadoc de Spring Data JPA.
 */
@Repository
public interface ChampionnatRepository extends JpaRepository<Championnat, Long> {
}
