package org.ultimateam.apiultimate.repository;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.JoueurRequest;
import org.ultimateam.apiultimate.model.JoueurRequestId;

import java.util.List;

@Repository
public interface JoueurRequestRepository extends JpaRepository<JoueurRequest, JoueurRequestId> {
    List<JoueurRequest> getByJoueur(Joueur joueur);
}
