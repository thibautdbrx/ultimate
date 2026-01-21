package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.Equipe;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Equipe}.
 *
 * Fournit des méthodes pour récupérer des équipes filtrées par genre et une requête
 * personnalisée pour retrouver les équipes contenant au moins un certain nombre
 * de joueurs hommes et femmes.
 */
public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    /**
     * Récupère toutes les équipes dont le genre principal correspond au genre fourni.
     *
     * @param genre le genre recherché (par ex. Genre.HOMME ou Genre.FEMME)
     * @return liste d'équipes correspondant au genre (peut être vide)
     */
    List<Equipe> findAllByGenre(Genre genre);

    /**
     * Requête personnalisée : récupère les équipes qui contiennent au moins
     * {@code nbHommes} joueurs de genre HOMME et au moins {@code nbFemmes} joueuses de genre FEMME.
     *
     * La requête effectue un JOIN sur les joueurs de chaque équipe et utilise
     * des agrégations conditionnelles (SUM CASE) pour compter les joueurs par genre.
     *
     * @param nbHommes nombre minimum de joueurs de genre HOMME requis
     * @param nbFemmes nombre minimum de joueuses de genre FEMME requis
     * @return liste des équipes satisfaisant les contraintes de genre
     */
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
