package org.ultimateam.apiultimate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassementService {

    private final MatchRepository matchRepo;

    public void calculerDifferencePoints(Classement classement) {
        int idEquipe = classement.getIdEquipe();
        int pointsMarques = 0;
        int pointsPris = 0;

        //List<Match> matchs = matchRepo.findById(idEquipe);
        /**
        for (Match match : matchs) {
            if (match.getId_equipe1() == idEquipe) {
                pointsMarques += match.getScore_equipe1();
                pointsPris += match.getScore_equipe2();
            } else {
                pointsMarques += match.getScore_equipe2();
                pointsPris += match.getScore_equipe1();
            }
        }

        classement.setDifference_points(pointsMarques - pointsPris);
         **/
    }
}
