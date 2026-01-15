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
import Deconnexion from '@/views/Deconnexion.vue'
import Matchs from '@/views/Matchs.vue'
import Contact from  '../views/Contact.vue'
import page_introuvable from  '../views/404.vue'
import Fausse_pub from '../views/fausse_pub.vue'
import RequestJoueur from "@/views/RequestJoueur.vue";
import RequestAdmin from "@/views/RequestAdmin.vue";



const routes = [
    { path: '/', component: Accueil },
    { path: '/Connexion', component: Connexion },
    { path: '/Deconnexion', component: Deconnexion },
    { path: '/Inscription', component: Inscription },
    { path: '/Equipe', component: Equipe },
    { path: '/Competition', component: Competition },
    { path: '/Competitions/:id', name: 'Competition-details', component:CompetitionDetail } ,
    { path: '/Equipe/:id/:nom',name:'Equipe-details', component: EquipeDetail } ,
    { path: '/match/:id', component: Match },
    { path: '/AjouterEquipe', component: AjoutEquipe },
    { path: '/NouveauJoueur', component: NouveauJoueur },
    { path: '/AjouterCompetition', component: AjoutCompetition },
    { path: '/Matchs', component: Matchs },
    {path : '/Contact', component: Contact },
    {path : '/fausse_pub', component: Fausse_pub },
    {path : '/request', component: RequestJoueur},
    {path : '/requestadmin', component: RequestAdmin},

    { path: '/:pathMatch(.*)*', component: page_introuvable }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
