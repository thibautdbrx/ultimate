package org.ultimateam.apiultimate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Long> {
}