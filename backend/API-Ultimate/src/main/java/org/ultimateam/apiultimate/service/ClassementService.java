package org.ultimateam.apiultimate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ClassementService {
    private Map<Equipe, Integer> scores;
    private Tournois tournois;
    private String type;

    private final MatchRepository matchRepo;

    public void Classement(Tournois tournois, String type) {
        this.tournois = tournois;
        this.type = type;
        this.scores = new HashMap<>();
    }

    public int calculerDifferencePoints(Classement classement) {
        int idEquipe = classement.getIdEquipe();
        int pointsMarques = 0;
        int pointsEncaissees = 0;

        // Récupérer tous les matchs
        List<Match> matchs = matchRepo.findAll();

        for (Match match : matchs) {
            if (match.getStatus() == Match.Status.FINISHED) {
                if (match.getEquipe1().getIdEquipe() == idEquipe) {
                    pointsMarques += match.getScoreEquipe1();
                    pointsEncaissees += match.getScoreEquipe2();
                } else if (match.getEquipe2().getIdEquipe() == idEquipe) {
                    pointsMarques += match.getScoreEquipe2();
                    pointsEncaissees += match.getScoreEquipe1();
                }
            }
        }

        int differencePoints = pointsMarques - pointsEncaissees;
        classement.setDifference_points(differencePoints);

        return differencePoints;
    }


    public void mettreAJourScore(Equipe equipe) {
        int points = 0;
        List<Match> matchsTermines = matchRepo.findAll().stream()
                .filter(match -> match.getStatus() == Match.Status.FINISHED)
                .toList();

        for (Match match : matchsTermines) {
            if (match.getEquipe1().equals(equipe)) {
                if (match.getScoreEquipe1() > match.getScoreEquipe2()) {
                    points += 3; // Victoire
                } else if (match.getScoreEquipe1() == match.getScoreEquipe2()) {
                    points += 1; // Match nul
                }
            } else if (match.getEquipe2().equals(equipe)) {
                if (match.getScoreEquipe2() > match.getScoreEquipe1()) {
                    points += 3; // Victoire
                } else if (match.getScoreEquipe1() == match.getScoreEquipe2()) {
                    points += 1; // Match nul
                }
            }
        }
        scores.put(equipe, points);
    }


    public List<Equipe> getClassementTrie() {
        // Récupérer toutes les équipes et leurs scores
        List<Map.Entry<Equipe, Integer>> entries = new ArrayList<>(scores.entrySet());
        entries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // Trier par score décroissant
        List<Equipe> classement = new ArrayList<>();
        for (Map.Entry<Equipe, Integer> entry : entries) {
            classement.add(entry.getKey());
        }

        return classement;
    }

    public void calculerPointsClassement() {
        List<Match> matchsTermines = matchRepo.findAll().stream()
                .filter(match -> match.getStatus() == Match.Status.FINISHED)
                .toList();

        for (Match match : matchsTermines) {
            Equipe equipe1 = match.getEquipe1();
            Equipe equipe2 = match.getEquipe2();

            // Initialiser les scores si nécessaire
            scores.putIfAbsent(equipe1, 0);
            scores.putIfAbsent(equipe2, 0);

            if (match.getScoreEquipe1() > match.getScoreEquipe2()) {
                scores.put(equipe1, scores.get(equipe1) + 3); // Victoire équipe 1
            } else if (match.getScoreEquipe1() < match.getScoreEquipe2()) {
                scores.put(equipe2, scores.get(equipe2) + 3); // Victoire équipe 2
            } else {
                scores.put(equipe1, scores.get(equipe1) + 1); // Match nul
                scores.put(equipe2, scores.get(equipe2) + 1);
            }
        }
    }


    public void enregistrerResultatMatch(Match match) {
        mettreAJourScore(match.getEquipe1());
        mettreAJourScore(match.getEquipe2());
    }

    public void afficherClassement() {
        System.out.println("Classement " + type + " pour le tournoi d'ID n°" + tournois.getIdCompetition());
        List<Equipe> classement = getClassementTrie();

        for (int i = 0; i < classement.size(); i++) {
            Equipe equipe = classement.get(i);
            int score = scores.get(equipe);
            System.out.println((i + 1) + ". " + equipe.getNomEquipe() + " - " + score + " points.");
        }
    }

}
