<script setup lang="ts">
import { ref, onMounted } from 'vue'

import LiveIcon from '@/assets/icons/live.svg'
import TrophyIcon from '@/assets/icons/trophy.svg'
import CalendarIcon from '@/assets/icons/calendar.svg'

import CardInfo from '../components/Card_info.vue'
import CardRes from '../components/Card_resultat.vue'

const stats = ref({
  live: 999,
  upcoming: 999,
  competitions: 999
})

const slider_card = ref(null)

let isDown = false // Vrai si le bouton de la souris est enfoncé
let startX // Position de la souris au début du clic
let scrollLeft // Position de défilement de l'élément au début du clic

// Fonction pour démarrer le glissement gerer par vue
const handleMouseDown = (e: MouseEvent) => {
  isDown = true
  if (slider_card.value) {
    slider_card.value.classList.add('active-drag')
    startX = e.pageX - slider_card.value.offsetLeft
    scrollLeft = slider_card.value.scrollLeft
  }
}

// Fonction pour arrêter le glissement gerer par javascrpit
const handleMouseUp = () => {
  isDown = false
  slider_card.value.classList.remove('active-drag')
}

// Fonction pour gérer le mouvement du glissement gerer par vue
const handleMouseMove = (e: MouseEvent) => { // Ajouter : MouseEvent
  if (!isDown || !slider_card.value) return

  e.preventDefault()
  const x = e.pageX - slider_card.value.offsetLeft
  const walk = (x - startX) * 1.5
  slider_card.value.scrollLeft = scrollLeft - walk
}

onMounted(() => {

  fetch('https://api.exemple.com/matchs/stats')
      .then(res => {
        if (!res.ok) {
          // erreur si la réponse HTTP n'est pas OK (404, 500)
          throw new Error(`Erreur HTTP: ${res.status}`)
        }
        return res.json()
      })

      .then(data => {
        stats.value = {
          live: data.liveCount,
          upcoming: data.upcomingCount,
          competitions: data.competitionCount
        }
      })
      .catch(error => { //msg erreur si la requete passe pas
        console.error("Échec de la récupération des statistiques :", error)

      })

  if (slider_card.value) {
    document.addEventListener('mouseup', handleMouseUp);
  }
})

</script>

<template>
  <main class="Acceuil">
    <div class="accueil_section">
      <h2 class="titre_acceuil">Gerez vos match en toute simplicité</h2>
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
      <section class="slider_card" ref="slider_card" @mousedown="handleMouseDown" @mouseup="handleMouseUp" @mousemove="handleMouseMove" @mouseleave="handleMouseUp">
      <CardRes
        title= "Championnat National"
        nom1 ="Polytech"
        nom2 = "Mines"
        :points1 = 13
        :points2 = 2 />
      <CardRes
          title= "Championnat National"
          nom1 ="Polytech"
          nom2 = "Mines"
          :points1 = 13
          :points2 = 2 />
      <CardRes
          title= "Championnat National"
          nom1 ="Polytech"
          nom2 = "Mines"
          :points1 = 13
          :points2 = 2 />
      <CardRes
          title= "Championnat National"
          nom1 ="Polytech"
          nom2 = "Mines"
          :points1 = 13
          :points2 = 2 />
      </section>
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

.slider_card{
  display: flex;
  flex-direction: row;
  gap: 1.5rem;
  padding-top: 0rem;
  padding-left : 1rem;
  padding-right : 1rem;
  padding: 2rem;
  flex-wrap: nowrap;

  /* défilement horizontal */
  overflow-x: auto;
  scrollbar-width: none; /* Pour Firefox */
  -ms-overflow-style: none; /* Pour IE et Edge */
  cursor: grab;
}

.slider_card.active-drag {
  /* Curseur quand le clic est maintenu */
  cursor: grabbing;
  user-select: none; /* Empêche la sélection de texte lors du glissement */
}

.slider_card::-webkit-scrollbar {
  display: none;
}

.cards_info{
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  align-items: center;
  margin-bottom: 2rem;
}
</style>