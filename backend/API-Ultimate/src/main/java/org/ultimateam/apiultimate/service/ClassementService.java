package org.ultimateam.apiultimate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.ClassementRepository;

import java.util.List;

@Service
public class ClassementService {

    private final ClassementRepository classementRepository;

    public ClassementService(ClassementRepository classementRepository ) {
        this.classementRepository = classementRepository;
    }

    public Iterable<Classement> getAll() {return classementRepository.findAll();}
    public Classement save(Classement classement) { return classementRepository.save(classement); }
    public List<Classement> deleteByIdCompetition(Long idCompetition) {
        List<Classement> classements = classementRepository.findAllByCompetition_IdCompetition(idCompetition);
        if (classements.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Classement not found");
        }
        classementRepository.deleteAll(classements);
        return classements;
    }
    public List<Classement> deleteByIdEquipe(Long idEquipe) {
        List<Classement> classements = classementRepository.findAllByEquipe_IdEquipe(idEquipe);
        if (classements.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe not found");
        }
        classementRepository.deleteAll(classements);
        return classements;

    }


    public void mettreAJourClassement(Match match) {
        if (match==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le match n'existe pas.");

        if (match.getStatus() != Match.Status.FINISHED) {
            return;
        }

        Competition competition = match.getIdCompetition();
        if (competition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le competition n'existe pas.");
        }
        Equipe equipe1 = match.getEquipe1();
        Equipe equipe2 = match.getEquipe2();

        Classement classement1 = classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(competition.getIdCompetition(),equipe1.getIdEquipe());
        if (classement1 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le classement n'existe pas.");
        }
        Classement classement2 = classementRepository.findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(competition.getIdCompetition(),equipe2.getIdEquipe());
        if (classement2 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le classement n'existe pas.");
        }

        long score1 = match.getScoreEquipe1();
        long score2 = match.getScoreEquipe2();

        updateStats(classement1, score1, score2);
        updateStats(classement2, score2, score1);


    }

    private void updateStats(Classement classement, long score1, long score2) {
        classement.setPoint_marque(classement.getPoint_marque() + score1);
        classement.setPoint_encaisse(classement.getPoint_encaisse() + score2);
        classement.setDifference_points(classement.getPoint_marque() - classement.getPoint_encaisse());

        if (score1 > score2) {
            classement.setScore(classement.getScore() + 3 );
            classement.setVictoires(classement.getVictoires() +1);
        }
        else if (score2 == score1) {
            classement.setScore(classement.getScore() +1 );
            classement.setEgalites(classement.getEgalites() + 1);
        } else {
            classement.setDefaites(classement.getDefaites() + 1);
        }
        classementRepository.save(classement);
    }


    public List<Classement> triClassement(Long idCompetition) {
        List<Classement> classements = classementRepository.findAllByCompetitionIdOrderByRank(idCompetition);
        for(int i=0; i<classements.size(); i++) {
            classements.get(i).setRang(i+1);
        }

        classementRepository.saveAll(classements);

        return classements;
    }


}
