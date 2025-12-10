package org.ultimateam.apiultimate.service;

public class CompetitionService {

    private final TournoisService tournoisService;
    private final ChampionnatService championnatService;

    public CompetitionService(TournoisService tournoisService, ChampionnatService championnatService){
        this.tournoisService = tournoisService;
        this.championnatService = championnatService;
    }

}
