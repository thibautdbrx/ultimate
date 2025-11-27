<script setup>
import { ref, onMounted } from 'vue'

import LiveIcon from '@/assets/icons/live.svg'
import TrophyIcon from '@/assets/icons/trophy.svg'
import CalendarIcon from '@/assets/icons/calendar.svg'
import { useAuthStore } from "@/stores/auth";

import SliderCardHorizontal from '../components/Slider_card_horizontal.vue'
import CardInfo from '../components/Card_info.vue'
import CardRes from '../components/Card_resultat.vue'

const stats = ref({
  live: 0,
  upcoming: 0,
  competitions: 0
})
const auth = useAuthStore();

const derniersMatchs = ref([])
const errorMsg = ref('')

onMounted(async () => {
  try {
    //console.log(auth.role)
    // Matchs en direct
    const liveRes = await fetch(`/api/match/started`)
    const liveData = await liveRes.json()
    stats.value.live = liveData.length    // ou liveData.count selon ton API

    // Matchs à venir
    const upcomingRes = await fetch(`/api/match/notstarted`)
    const upcomingData = await upcomingRes.json()
    stats.value.upcoming = upcomingData.length

    // Compétitions
    const compRes = await fetch(`/api/tournois`)
    const compData = await compRes.json()
    stats.value.competitions = compData.length


    //ajouter les match fini uniquement.

    const matchsRes = await fetch(`/api/match`)
    const matchsData = await matchsRes.json()
    const matchs = matchsData.sort((a, b) => {
          if (a.dateDebut && b.dateDebut) {
            return new Date(b.dateDebut) - new Date(a.dateDebut); // les plus récents en premier
          } else if (a.dateDebut) {
            return -1; // a a une date, b non => a avant b
          } else if (b.dateDebut) {
            return 1;  // b a une date, a non => b avant a
          } else {
            return 0;  // aucun des deux n'a de date => pas de changement
          }
        })
    derniersMatchs.value = matchs.slice(0, 5)

  } catch (err) {
    errorMsg.value = "Impossible de récupérer les matchs."
    console.error("Erreur lors du chargement des données:", err)
  }
})
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
        />
        <CardInfo
            title="Matchs à venir"
            :value="stats.upcoming"
            :icon="CalendarIcon"
            color1="#dbeafe"
            color2="#155dfc"
        />
        <CardInfo
            title="Compétitions"
            :value="stats.competitions"
            :icon="TrophyIcon"
            color1="#f3e8ff"
            color2="#9810fa"
        />
      </section>

    </div>
    <div class="accueil_section">
      <h2 class="titre_acceuil">Dernier resultats</h2>
      <p v-if="errorMsg" class="error-text">{{ errorMsg }}</p>
      <SliderCardHorizontal :autoScroll="true" :autoScrollDelay="500">
        <div v-for="match in derniersMatchs" :key="match.idMatch" class="match-card">
          <CardRes :title="String(match.idMatch)" :nom1="match.equipe1.nomEquipe" :nom2="match.equipe2.nomEquipe" :points1="match.scoreEquipe1" :points2="match.scoreEquipe2"
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
  padding: 2rem;
}
</style>