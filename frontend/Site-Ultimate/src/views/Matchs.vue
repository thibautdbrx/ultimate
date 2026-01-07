<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import CarteMatch from '@/components/card_match.vue'

const router = useRouter()
const route = useRoute()
const matchs = ref([])
const loading = ref(false)
const error = ref(null)

const filtre = ref(route.query.filtre || 'all')

const endpoints = {
  all: '/api/match',
  started: '/api/match/started',
  notstarted: '/api/match/notstarted',
  finished: '/api/match/finished'
}

function goToMatch(id) {
  router.push(`/match/${id}`)
}

const formatDate = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

async function fetchMatchs() {
  loading.value = true
  error.value = null

  try {
    const response = await fetch(endpoints[filtre.value])
    if (!response.ok) throw new Error('Erreur lors du chargement des matchs')
    matchs.value = await response.json()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(fetchMatchs)

// recharge automatique quand on change le filtre
watch(filtre, fetchMatchs, { immediate: true })
</script>


<template>
  <main class="matchs">
    <h2 class="title">Liste des Matchs</h2>

    <div class="filters">
      <button :class="{ active: filtre === 'all' }" @click="filtre = 'all'">
        Tous
      </button>
      <button :class="{ active: filtre === 'started' }" @click="filtre = 'started'">
        En cours
      </button>
      <button :class="{ active: filtre === 'notstarted' }" @click="filtre = 'notstarted'">
        Prochainement
      </button>
      <button :class="{ active: filtre === 'finished' }" @click="filtre = 'finished'">
        Fini
      </button>
    </div>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else class="competition-list">
      <div
          v-for="match in matchs"
          :key="match.idMatch"
          class="match-card"
          @click="goToMatch(match.idMatch)"
      >
        <CarteMatch
            :title="formatDate(match.dateMatch)"
            :match="match"
        />
      </div>
    </div>
  </main>
</template>


<style scoped>
.matchs {
  padding: 1rem;
}

.title {
  margin-bottom: 1rem;
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

.state-msg {
  margin-top: 2rem;
  text-align: center;
}

.state-msg.error {
  color: red;
}

.competition-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
}

.match-card {
  cursor: pointer;
}
</style>
