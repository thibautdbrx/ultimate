<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from "@/stores/auth";
import SliderCardHorizontal from "@/components/Slider_card_horizontal.vue"
import CardMatch from "@/components/card_match.vue"
import CarteEquipe from "@/components/card_equipe.vue"
import ImageFond from "@/assets/img/img_equipe.jpg"

const route = useRoute()
const competitionId = route.params.id

const competition = ref(null)
const matches = ref([])
const loading = ref(true)
const error = ref(null)
const teams = ref([]) // toutes les équipes à afficher

// Computed
const hasMatches = computed(() => matches.value && matches.value.length > 0)
const upcomingMatches = computed(() => matches.value.filter(m => m.status !== 'FINISHED' && new Date(m.dateFin) > new Date()))
const finishedMatches = computed(() => matches.value.filter(m => m.status === 'FINISHED' || new Date(m.dateFin) <= new Date()))
const nbTeams = computed(() => teams.value.length)

// Récupérer les équipes uniques depuis les matchs
function getTeamsFromMatches(matches) {
  const map = new Map()
  matches.forEach(m => {
    map.set(m.equipe1.idEquipe, m.equipe1)
    map.set(m.equipe2.idEquipe, m.equipe2)
  })
  return Array.from(map.values())
}

// Récupérer les équipes via la participation si aucun match
async function fetchTeamsFromParticipation() {
  try {
    const res = await fetch(`/api/participation/competition/${competitionId}`)
    if (!res.ok) throw new Error("Erreur HTTP participation")
    const data = await res.json() // [{id: {idEquipe, idCompetition}}, ...]

    // Pour chaque idEquipe, fetch les infos complètes
    const teamPromises = data.map(async p => {
      const r = await fetch(`/api/equipe/${p.id.idEquipe}`)
      if (!r.ok) throw new Error("Erreur HTTP équipe")
      return r.json()
    })
    teams.value = await Promise.all(teamPromises)
  } catch (err) {
    console.error(err)
    teams.value = []
  }
}

onMounted(async () => {
  try {
    // 1️⃣ fetch des matchs
    const res = await fetch(`/api/tournois/${competitionId}/matchs`)
    if (!res.ok) throw new Error(`Erreur HTTP matchs: ${res.status}`)
    const data = await res.json()
    matches.value = data

    if (data.length > 0) {
      // On récupère la compétition depuis le premier match
      competition.value = data[0].idCompetition
      // Et les équipes depuis les matchs
      teams.value = getTeamsFromMatches(data)
    } else {
      // Si aucun match, récupérer les équipes inscrites
      await fetchTeamsFromParticipation()
    }

    loading.value = false
  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger la compétition"
    loading.value = false
  }
})

// Fonctions boutons
function goToEdit() { console.log("Modifier la compétition") }
function genererMatchs() { console.log("Générer les matchs") }
</script>

<template>
  <div class="competition-details">
    <div v-if="loading">Chargement...</div>
    <div v-if="error" class="state-msg error">{{ error }}</div>

    <div v-if="competition || teams.length">
      <h2>
        {{ competition?.nomCompetition || 'Compétition inconnue' }} -
        {{ competition?.format || '-' }} -
        {{ competition?.genre || '-' }} -
        {{ nbTeams }} équipes
      </h2>

      <!-- Équipes engagées -->
      <section class="equipes-section">
        <h3>Équipes engagées</h3>
        <div class="teams-grid">
          <div v-for="t in teams" :key="t.idEquipe" class="team-card-wrapper">
            <CarteEquipe :equipe="t" :image="ImageFond" />
          </div>
        </div>
      </section>

      <!-- Matchs si disponibles -->
      <div v-if="hasMatches" class="prochain_matches">
        <button class="btn-primary" @click="goToEdit">Modifier la compétition</button>

        <h3>Matchs prochains</h3>
        <SliderCardHorizontal>
          <div v-for="match in upcomingMatches" :key="match.idMatch">
            <CardMatch
                :title="match.dateMatch"
                :nom1="match.equipe1.nomEquipe"
                :nom2="match.equipe2.nomEquipe"
                :points1="match.scoreEquipe1"
                :points2="match.scoreEquipe2"
                :fini="false"
            />
          </div>
        </SliderCardHorizontal>

        <h3>Matchs finis</h3>
        <SliderCardHorizontal>
          <div v-for="match in finishedMatches" :key="match.idMatch">
            <CardMatch
                :title="match.dateMatch"
                :nom1="match.equipe1.nomEquipe"
                :nom2="match.equipe2.nomEquipe"
                :points1="match.scoreEquipe1"
                :points2="match.scoreEquipe2"
                :fini="true"
            />
          </div>
        </SliderCardHorizontal>
      </div>

      <!-- Aucun match -->
      <div v-else class="no-matches">
        <p>Aucun match n’a encore été généré pour cette compétition.</p>
        <button class="btn-primary" @click="goToEdit">Modifier la compétition</button>
        <button class="btn-secondary" @click="genererMatchs">Générer les poules et créer les matchs</button>
      </div>
    </div>
  </div>
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
  text-align: center;
}

.state-msg { color: #555; }
.state-msg.error { color: #e74c3c; }

.teams-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: center;
  width: 100%;
  max-width: 1000px;
}

.team-card-wrapper {
  flex: 0 1 220px;
}

.prochain_matches {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 80%;
  max-width: max-content;
}
</style>
