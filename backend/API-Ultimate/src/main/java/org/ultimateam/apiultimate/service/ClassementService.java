package org.ultimateam.apiultimate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ClassementService {
    private Map<Equipe, Integer> scores;
    private Tournoi tournoi;
    private String type;

    private final MatchRepository matchRepo;

    public Classement(Tournoi tournoi, String type) {
        this.tournoi = tournoi;
        this.type = type;
        this.scores = new HashMap<>();
    }

    public int calculerDifferencePoints(Classement classement, long idEquipe) {
        int pointsMarques = 0;
        int pointsEncaisses = 0;

        List<Match> matchs = matchRepo.findAll();

        for (Match match : matchs) {
            if (match.getEquipe1().getIdEquipe() == idEquipe) {
                pointsMarques += match.getScore_equipe1();
                pointsEncaisses += match.getScore_equipe2();
            } else if (match.getEquipe2().getIdEquipe() == idEquipe) {
                pointsMarques += match.getScore_equipe2();
                pointsEncaisses += match.getScore_equipe1();
            }
        }

        return pointsMarques - pointsEncaisses;
    }

    public void mettreAJourScore(Equipe equipe) {
        if (scores.containsKey(equipe)) {
            scores.put(equipe, equipe.calculerScore());
        }
    }

    public List<Equipe> getClassementTrie() {
        List<Map.Entry<Equipe, Integer>> entries = new ArrayList<>(scores.entrySet());
        // Tri par score décroissant
        entries.sort(e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        // Extraction des équipes dans l'ordre
        List<Equipe> classement = new ArrayList<>();
        for (Map.Entry<Equipe, Integer> entry : entries) {
            classement.add(entry.getKey());
        }
        return classement;
    }
    public void enregistrerResultatMatch(Match match) {
        match.appliquerResultat();
        mettreAJourScore(match.getEquipe1());
        mettreAJourScore(match.getEquipe2());
    }

    public void afficherClassement() {
        System.out.println("Classement " + type + " pour le tournoi " + tournoi.getNom());
        List<Equipe> classement = getClassementTrie();
        for (int i = 0; i < classement.size(); i++) {
            Equipe equipe = classement.get(i);
            System.out.println((i + 1) + ". " + equipe.getNom() + " - " + scores.get(equipe) + " pts");
        }
    }


}
