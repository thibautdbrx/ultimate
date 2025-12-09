package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.Equipe;

import java.util.List;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findAllByGenre(Equipe.Genre genre);
}
