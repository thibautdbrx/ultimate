package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.Equipe;

import java.util.List;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findAllByGenre(Genre genre);
    @Query("""
    SELECT e
    FROM Equipe e
    JOIN e.joueurs j
    GROUP BY e
    HAVING 
        SUM(CASE WHEN j.genre = 'HOMME' THEN 1 ELSE 0 END) >= :nbHommes
        AND
        SUM(CASE WHEN j.genre = 'FEMME' THEN 1 ELSE 0 END) >= :nbFemmes
""")
    List<Equipe> findEquipesAvecHommesEtFemmes(int nbHommes, int nbFemmes);

}
