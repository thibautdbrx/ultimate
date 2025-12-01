import { createRouter, createWebHistory } from 'vue-router'
import Accueil from '../views/Accueil.vue'
import Connexion from '../views/Connexion.vue'
import Competition from '../views/Competition.vue'
import Equipe from '../views/Equipe.vue'
import CompetitionDetail from '../views/CompetitionDetail.vue'
import EquipeDetail from '../views/EquipeDetail.vue'
import Inscription from "@/views/Inscription.vue";
import Match from "@/views/match.vue";
import AjoutEquipe from "@/views/AjoutEquipe.vue";
import NouveauJoueur from "../views/NouveauJoueur.vue";
import AjoutCompetition from "@/views/AjoutCompetition.vue";



const routes = [
    { path: '/', component: Accueil },
    { path: '/Connexion', component: Connexion },
    { path: '/Inscription', component: Inscription },
    { path: '/Equipe', component: Equipe },
    { path: '/Competition', component: Competition },
    { path: '/Competitions/:id', name: 'Competition-details', component:CompetitionDetail } ,
    { path: '/Equipe/:id/:nom',name:'Equipe-details', component: EquipeDetail } ,
    { path: '/match/:id', component: Match },
    { path: '/AjouterEquipe', component: AjoutEquipe },
    { path: '/NouveauJoueur', component: NouveauJoueur },
    { path: '/AjouterCompetition', component: AjoutCompetition },

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
