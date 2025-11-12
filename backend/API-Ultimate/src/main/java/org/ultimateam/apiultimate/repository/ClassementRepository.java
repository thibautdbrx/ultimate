package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.Equipe;

public interface ClassementRepository extends JpaRepository<Classement, Long>  {
}
