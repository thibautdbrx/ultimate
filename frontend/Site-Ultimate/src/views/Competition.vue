<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import ImageTournoi from "../assets/img/img_tournois.jpg"
import ImageChampionnat from "../assets/img/img_championnat.png"

const router = useRouter()

const competitions = ref([])
const loading = ref(false)
const error = ref(null)

const filtre = ref('all') // 'all', 'tournoi', 'championnat'

const endpoints = {
  all: '/api/competition',
  tournoi: '/api/competition/tournois',
  championnat: '/api/competition/championnat'
}

function goToCompetition(id) {
  router.push({ name: 'Competition-details', params: { id } })
}

const format_bien_aff = (format) => (format || "").toUpperCase()

async function fetchCompetitions() {
  loading.value = true
  error.value = null

  try {
    const res = await fetch(endpoints[filtre.value])
    if (!res.ok) throw new Error(`Erreur HTTP: ${res.status}`)
    competitions.value = await res.json()
  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les compétitions."
  } finally {
    loading.value = false
  }
}

onMounted(fetchCompetitions)
watch(filtre, fetchCompetitions, { immediate: true })

// Retourne l'image en fonction du type
function getCompetitionImage(type) {
  if (type === 'tournoi') return ImageTournoi
  if (type === 'championnat') return ImageChampionnat
  return ImageTournoi // valeur par défaut
}
</script>

<template>
  <main class="competitions">
    <h2 class="title">Liste des Compétitions</h2>

    <div class="filters">
      <button :class="{ active: filtre === 'all' }" @click="filtre = 'all'">Toutes</button>
      <button :class="{ active: filtre === 'tournoi' }" @click="filtre = 'tournoi'">Tournois</button>
      <button :class="{ active: filtre === 'championnat' }" @click="filtre = 'championnat'">Championnats</button>
    </div>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else class="competition-list">
      <div
          v-for="competition in competitions"
          :key="competition.idCompetition"
          class="competition-card"
          @click="goToCompetition(competition.idCompetition)"
      >
        <img :src="getCompetitionImage(competition.type)" alt="Image compétition" class="competition-img" />
        <div class="competition-info">
          <h3>{{ competition.nomCompetition }}</h3>
          <p>{{ format_bien_aff(competition.format) + " - " + competition.genre }}</p>
        </div>
      </div>
    </div>
  </main>
</template>



<style scoped>
.competitions {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}

.title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
}

.state-msg {
  color: #555;
}

.state-msg.error {
  color: #e74c3c;
}

.competition-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem; width: 100%; max-width: 1000px;
}

.competition-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease; overflow: hidden;
}
.competition-card:hover {
  transform: translateY(-4px); box-shadow: 0 5px 10px rgba(0,0,0,0.15);
}
.competition-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}
.competition-info {
  padding: 1rem;
  text-align: center;
}

.filters {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.filters button {
  padding: 0.5rem 1rem;
  border: none;
  cursor: pointer;
  background: #eee;
  border-radius: 4px;
}

.filters button.active {
  background: #2563eb;
  color: white;
}
</style>