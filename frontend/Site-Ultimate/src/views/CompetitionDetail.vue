<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SliderCardHorizontal from "@/components/Slider_card_horizontal.vue";
import CardMatch from "@/components/card_match.vue";

const route = useRoute()
const competitionId = route.params.id // JS pur, plus de 'as string'

const competition = ref(null) // JS pur, infère null ou objet
const matches = ref([])       // JS pur, tableau vide initial
const loading = ref(true)
const error = ref(null)       // JS pur

onMounted(() => {
  fetch(`https://api.exemple.com/competitions/${competitionId}`)
      .then(res => {
        if (!res.ok) throw new Error(`Erreur HTTP: ${res.status}`)
        return res.json()
      })
      .then(data => {
        competition.value = data.competition
        matches.value = data.matches
        loading.value = false
      })
      .catch(err => {
        console.error(err)
        //error.value = "Impossible de charger les matchs de cette compétition."
        //loading.value = false
      })
})

// Valeurs par défaut pour tests
matches.value = [
  { id: 1, team1: 'Polytech', team2: 'Mines', score1: 3, score2: 1, date: '2025-11-05' },
  { id: 2, team1: 'Centrale', team2: 'INSA', score1: 2, score2: 2, date: '2025-11-07' },
  { id: 3, team1: 'ESIEA', team2: 'Polytech', score1: null, score2: null, date: '2025-11-15' },
  { id: 4, team1: 'CACA', team2: 'Polytech', score1: 20, score2: 3, date: '2025-10-15' },
]
competition.value = { name: "Nom de la compet" }
loading.value = false

const now = new Date()
</script>


<template>
  <main class="competition-details">
    <h2 v-if="competition">{{ competition.name }}</h2>
    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>
    <div v-else class="prochain_matches">
      <h3>Matchs prochain</h3>
      <SliderCardHorizontal :autoScroll="false" :autoScrollDelay="0">
      <div v-for="match in matches" :key="match.id" class="match-card">

          <CardMatch  v-if="(new Date(match.date) > now)" :title=match.date :nom1=match.team1 :nom2=match.team2 :points1=match.score1 :points2=match.score2 :fini=false />

      </div>
      </SliderCardHorizontal>


      <h3>Matchs finis</h3>
      <SliderCardHorizontal :autoScroll="false" :autoScrollDelay="0">
        <div v-for="match in matches" :key="match.id" class="match-card">
          <CardMatch v-if="(new Date(match.date) <= now)" :title=match.date :nom1=match.team1 :nom2=match.team2 :points1=match.score1 :points2=match.score2 :fini=true />
        </div>
      </SliderCardHorizontal>
    </div>
  </main>
</template>

<style scoped>
.competition-details {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}

h2 {
  margin-bottom: 1.5rem;
}

.state-msg {
  color: #555;
}

.state-msg.error {
  color: #e74c3c;
}

.prochain_matches {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 80%;
  max-width: max-content;
}



.match-title {
  font-weight: bold;
}

.match-score {
  font-size: 1.2rem;
  margin: 0.5rem 0;
}
</style>
