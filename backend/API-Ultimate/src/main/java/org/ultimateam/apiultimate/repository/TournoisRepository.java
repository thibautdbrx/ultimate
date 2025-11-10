package org.ultimateam.apiultimate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Tournois;

@Repository
interface TournoisRepository extends CrudRepository<Tournois, Long> {
}
