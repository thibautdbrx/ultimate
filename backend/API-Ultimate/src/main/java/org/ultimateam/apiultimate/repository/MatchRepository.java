package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Match;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Match}.
 *
 * Fournit des requêtes dérivées utiles pour récupérer des matchs selon leur état
 * (non démarrés, démarrés mais non terminés, terminés), pour obtenir les matchs
 * d'une compétition ordonnés par date, ou pour filtrer des matchs programmés
 * après une date donnée.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Retourne la liste des matchs dont la date de début est nulle (matchs non encore démarrés).
     *
     * @return liste des {@link Match} avec dateDebut == null
     */
    List<Match> findByDateDebutIsNull();

    /**
     * Retourne la liste des matchs qui ont une date de début renseignée mais
     * dont la date de fin est encore nulle (matchs en cours).
     *
     * @return liste des {@link Match} démarrés mais non terminés
     */
    List<Match> findByDateDebutIsNotNullAndDateFinIsNull();

    /**
     * Retourne tous les matchs d'une compétition triés par date de match croissante.
     *
     * @param competitionId identifiant de la compétition
     * @return liste des {@link Match} pour la compétition, ordonnée par dateMatch asc
     */
    List<Match> findByIdCompetition_IdCompetitionOrderByDateMatchAsc(long competitionId);

    /**
     * Retourne les matchs programmés après la date fournie et dont la date de début est nulle
     * (donc non démarrés et programmés plus tard que {@code dateMatchBefore}).
     *
     * @param dateMatchBefore seuil de date à partir duquel récupérer les matchs
     * @return liste des {@link Match} programmés après la date donnée et non démarrés
     */
    List<Match> findByDateMatchAfterAndDateDebutIsNull(LocalDateTime dateMatchBefore);

    /**
     * Retourne la liste des matchs dont la date de fin est renseignée (matchs terminés).
     *
     * @return liste des {@link Match} terminés (dateFin != null)
     */
    List<Match> findByDateFinIsNotNull();
        @Query("""
        SELECT m
        FROM Match m
        WHERE m.equipe1.idEquipe = :idEquipe
           OR m.equipe2.idEquipe = :idEquipe
    """)
        List<Match> findMatchesByEquipe(@Param("idEquipe") long idEquipe);
    @Query("SELECT m FROM Match m WHERE m.terrain.idTerrain = :idTerrain")
    List<Match> findByTerrain_Id_terrain(@Param("idTerrain") Long idTerrain);

    List<Match>findByIdCompetition_IdCompetition(Long idCompetition);
}