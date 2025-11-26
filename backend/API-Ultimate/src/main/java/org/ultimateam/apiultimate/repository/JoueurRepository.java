package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Joueur;

import java.util.List;

@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {
    List<Joueur> findAllByEquipe_IdEquipe(Long idEquipe);
    int countByEquipe_IdEquipe(Long idEquipe);
}
