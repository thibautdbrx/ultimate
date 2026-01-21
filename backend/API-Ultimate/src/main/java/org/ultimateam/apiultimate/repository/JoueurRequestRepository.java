package org.ultimateam.apiultimate.repository;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.model.JoueurRequestId;

/**
 * Repository Spring Data JPA pour l'entité {@link JoueurRequest}.
 *
 * Cette interface hérite des opérations CRUD standards (save, findById, findAll, deleteById, etc.)
 * depuis {@link JpaRepository}. Le type de clé primaire est {@link JoueurRequestId} (clé composite).
 *
 * Utilisation typique :
 * - sauvegarder une demande de joueur via {@code save(...)},
 * - récupérer une demande par son identifiant via {@code findById(...)},
 * - lister toutes les demandes via {@code findAll()},
 * - supprimer une demande via {@code deleteById(...)}.
 *
 * Des méthodes de requête spécifiques peuvent être ajoutées ici si nécessaire.
 */
@Repository
public interface JoueurRequestRepository extends JpaRepository<JoueurRequest, JoueurRequestId> {
}
