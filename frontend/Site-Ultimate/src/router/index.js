import { createRouter, createWebHistory } from 'vue-router'
import Accueil from '../views/Accueil.vue'
import Connexion from '../views/Connexion.vue'
import Competition from '../views/Competition.vue'
import Equipe from '../views/Equipe.vue'
import CompetitionDetail from '../views/CompetitionDetail.vue'



const routes = [
    { path: '/', component: Accueil },
    { path: '/Connexion', component: Connexion },
    { path: '/Equipe', component: Equipe },
    { path: '/Competition', component: Competition },
    { path: '/Competitions/:id', name: 'Competition-details', component:CompetitionDetail } ,

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
