package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.User;

import java.lang.ScopedValue;

public interface UserRepository extends JpaRepository<User, Long> {
    <T> ScopedValue<T> findByEmail(String username);
}
