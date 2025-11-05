package org.ultimateam.apiultimate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Joueur;

@Repository
public interface JoueurRepository extends CrudRepository<Joueur, Long> {
}
