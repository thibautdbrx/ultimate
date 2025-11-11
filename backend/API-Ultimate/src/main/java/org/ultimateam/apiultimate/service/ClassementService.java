package org.ultimateam.apiultimate.service;

@Service
@RequiredArgsConstructor
public class ClassementService {

    private final MatchRepository matchRepo;

    public void calculerDifferencePoints(Classement classement) {
        int idEquipe = classement.getId_equipe();
        int pointsMarques = 0;
        int pointsPris = 0;

        List<Match> matchs = matchRepo.findAllByEquipe(idEquipe);

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
    }
}
