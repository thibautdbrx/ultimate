package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
