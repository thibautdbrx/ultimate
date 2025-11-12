<script setup lang="ts">
import { ref, onMounted } from 'vue'

import LiveIcon from '@/assets/icons/live.svg'
import TrophyIcon from '@/assets/icons/trophy.svg'
import CalendarIcon from '@/assets/icons/calendar.svg'

import SliderCardHorizontal from '../components/Slider_card_horizontal.vue'
import CardInfo from '../components/Card_info.vue'
import CardRes from '../components/Card_resultat.vue'

const stats = ref({ //valeur de base en attendant API
  live: 999,
  upcoming: 999,
  competitions: 999
})

onMounted(() => {
  fetch('https://api.exemple.com/matchs/stats')
      .then(res => res.json())
      .then(data => {
        stats.value = {
          live: data.liveCount,
          upcoming: data.upcomingCount,
          competitions: data.competitionCount
        }
      })
      .catch(err => console.error('Erreur lors du fetch', err))
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
      <SliderCardHorizontal :autoScroll="true" :autoScrollDelay="500">
        <CardRes title="Championnat National" nom1="Polytech" nom2="Mines" :points1="13" :points2="2" />
        <CardRes title="Championnat National" nom1="Polytech" nom2="Mines" :points1="13" :points2="2" />
        <CardRes title="Championnat National" nom1="Polytech" nom2="Mines" :points1="13" :points2="2" />
        <CardRes title="Championnat National" nom1="Polytech" nom2="Mines" :points1="13" :points2="2" />
      </SliderCardHorizontal>
    </div>
  </main>
</template>

<style scoped>

.titre_acceuil {
  text-align: center;
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
</style>