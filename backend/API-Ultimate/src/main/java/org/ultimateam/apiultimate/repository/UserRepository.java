package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.User;

/**
 * Repository Spring Data JPA pour l'entité {@link User}.
 *
 * Fournit les opérations CRUD standards pour les utilisateurs via {@link JpaRepository}.
 * Les méthodes dérivées peuvent être ajoutées pour des requêtes simples.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email adresse email de l'utilisateur recherchée
     * @return l'entité {@link User} correspondante si trouvée, sinon null
     */
    User findByEmail(String email);
}
