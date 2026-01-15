package org.ultimateam.apiultimate.repository;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.model.JoueurRequestId;

@Repository
public interface JoueurRequestRepository extends JpaRepository<JoueurRequest, JoueurRequestId> {
}
