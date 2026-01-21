<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import CardCompetition from '@/components/card/CardCompetition.vue'

const router = useRouter()
const competitions = ref([])
const loading = ref(false)
const error = ref(null)
const filtre = ref('all')

const endpoints = {
  all: '/competition',
  tournoi: '/competition/tournoi',
  championnat: '/competition/championnat'
}

function goToCompetition(id) {
  router.push({ name: 'Competition-details', params: { id } })
}

async function fetchCompetitions() {
  loading.value = true
  error.value = null
  try {
    const res = await api.get(endpoints[filtre.value])
    competitions.value = res.data
  } catch (err) {
    error.value = "Impossible de charger les compétitions."
  } finally {
    loading.value = false
  }
}

onMounted(fetchCompetitions)
watch(filtre, fetchCompetitions)
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
      <CardCompetition
          v-for="competition in competitions"
          :key="competition.idCompetition"
          :competition="competition"
          @click="goToCompetition(competition.idCompetition)"
      />
    </div>
  </main>
</template>

<style scoped>
/* On garde uniquement les styles de layout */
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
.competition-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
  width: 100%;
  max-width: 1000px;
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
.state-msg.error {
  color: #e74c3c;
}
</style>