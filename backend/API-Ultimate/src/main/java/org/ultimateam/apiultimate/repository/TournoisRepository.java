package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Tournoi;

/**
 * Repository Spring Data JPA pour l'entité {@link Tournoi}.
 *
 * Fournit les opérations CRUD et de pagination pour les objets {@link Tournoi}.
 * Cette interface hérite des méthodes standards de {@link JpaRepository} (save, findById, findAll, delete, etc.).
 * N'ajoutez des méthodes personnalisées ici que si vous avez besoin de requêtes spécifiques.
 *
 * @see org.ultimateam.apiultimate.model.Tournoi
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface TournoisRepository extends JpaRepository<Tournoi, Long> {
}
