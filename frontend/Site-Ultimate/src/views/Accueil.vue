<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import LiveIcon from '@/assets/icons/live.svg'
import TrophyIcon from '@/assets/icons/trophy.svg'
import CalendarIcon from '@/assets/icons/calendar.svg'
import { useAuthStore } from "@/stores/auth";

import api from '@/services/api' // Ajout de l'import api
import SliderCardHorizontal from '../components/Slider_card_horizontal.vue'
import CardInfo from '../components/Card_info.vue'
import CardMatch from '../components/card_match.vue'
import PUB from "@/components/PUB.vue"

const router = useRouter()
const stats = ref({
  live: 0,
  upcoming: 0,
  competitions: 0
})

const auth = useAuthStore();

let compData = ref([])
const derniersMatchs = ref([])
const errorMsg = ref('')

const formatDate = (isoString) => {
  if (!isoString) return "";

  return new Date(isoString).toLocaleDateString("fr-FR", {
    day: "numeric",
    month: "long",
    year: "numeric"
  });
};

onMounted(async () => {
  try {
    // Remplacement des fetch par api.get
    // Les données sont directement accessibles dans .data

    // Matchs en direct
    const liveRes = await api.get(`match/started`)
    stats.value.live = liveRes.data.length

    // Matchs à venir
    const upcomingRes = await api.get(`match/notstarted`)
    stats.value.upcoming = upcomingRes.data.length

    // Compétitions
    const compRes = await api.get(`competition`)
    compData = compRes.data
    stats.value.competitions = compData.length

    // Récupérer tous les matchs pour les derniers résultats
    const matchsRes = await api.get('match')
    const matchsData = matchsRes.data

    const isMatchJoue = (match) => {
      return match.status === "FINISHED"
    }

    const matchs = matchsData
        .filter(isMatchJoue)
        .sort((a, b) => new Date(b.dateDebut) - new Date(a.dateDebut))

    // Garder les 5 derniers
    derniersMatchs.value = matchs.slice(0, 5)

  } catch (err) {
    errorMsg.value = "Impossible de récupérer les matchs."
    console.error("Erreur lors du chargement des données:", err)
  }
})

function getCompetitionName(MatchInfo) {
  // compData étant maintenant le tableau direct issu de res.data
  const comp = compData.find(c => c.idCompetition === MatchInfo.idCompetition)
  return comp ? comp.nomCompetition : ''
}

</script>


<template>
  <main class="Acceuil">
    <div class="accueil_section">
      <h2 class="titre_acceuil">Gerez vos matchs en toute simplicité</h2>
      <section class="cards_info">
        <CardInfo
            title="Matchs en direct"
            :value="stats.live"
            :icon="LiveIcon"
            color1="#FFD6D6"
            color2="#d31a42"
            :tp= "{ path: '/Matchs', query: { filtre: 'started'} }"

        />
        <CardInfo
            title="Matchs à venir"
            :value="stats.upcoming"
            :icon="CalendarIcon"
            color1="#dbeafe"
            color2="#155dfc"
            :tp="{ path: '/Matchs', query: { filtre: 'notstarted'} }"

        />
        <CardInfo
            title="Compétitions"
            :value="stats.competitions"
            :icon="TrophyIcon"
            color1="#f3e8ff"
            color2="#9810fa"
            tp="Competition"
        />
      </section>

    </div>
    <div class="accueil_section">
      <h2 class="titre_acceuil">Dernier resultats</h2>
      <p v-if="errorMsg" class="error-text">{{ errorMsg }}</p>
      <SliderCardHorizontal :autoScroll="true" :autoScrollDelay="500">
        <div v-for="match in derniersMatchs" :key="match.idMatch" class="match-card" >
          <CardMatch
              :title="`${formatDate(match.dateMatch)} - ${getCompetitionName(match.idCompetition)}`"
              :match="match"
          />
        </div>
      </SliderCardHorizontal>
    </div>
  </main>
</template>

<style scoped>

.titre_acceuil {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
}


.accueil_section{
  display: flex;
  flex-direction: column;
}

.cards_info{
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  align-items: center;
  margin-bottom: 2rem;
}

.Acceuil{
  padding: 2rem 0rem;
}
</style>