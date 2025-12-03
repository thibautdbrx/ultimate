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
const teams = ref([])

const loading = ref(true)
const error = ref(null)

// Matchs ?
const hasMatches = computed(() => matches.value.length > 0)

// Filtres dates
function getFinishedMatches(m) {
  const now = new Date()
  return m.filter(x => x.status === "FINISHED" || new Date(x.dateFin) <= now)
}

function getUpcomingMatches(m) {
  const now = new Date()
  return m.filter(x => x.status !== "FINISHED" && new Date(x.dateFin) > now)
}

const finishedMatches = computed(() => getFinishedMatches(matches.value))
const upcomingMatches = computed(() => getUpcomingMatches(matches.value))

// Nombre d’équipes
const nbTeams = computed(() => teams.value.length)

// Récupération des équipes via participation
async function fetchTeams() {
  try {
    const res = await fetch(`/api/participation/competition/${competitionId}`)
    if (!res.ok) throw new Error("Erreur HTTP équipes")

    teams.value = await res.json()
  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les équipes."
  }
}

// Page logic
onMounted(async () => {
  try {
    // 1) Récup matchs
    const resM = await fetch(`/api/tournois/${competitionId}/matchs`)
    if (!resM.ok) throw new Error("Erreur HTTP matchs")

    const dataM = await resM.json()
    matches.value = dataM

    // 2) Si des matchs -> récupérer la compétition ET les équipes via les matchs
    if (dataM.length > 0) {
      competition.value = dataM[0].idCompetition

      // Récup teams via les matchs
      const map = new Map()
      dataM.forEach(m => {
        map.set(m.equipe1.idEquipe, m.equipe1)
        map.set(m.equipe2.idEquipe, m.equipe2)
      })
      teams.value = Array.from(map.values())
    } else {
      // 3) Sinon -> récupérer les équipes via participation
      await fetchTeams()

      // Et récupérer la compétition séparément si nécessaire
      const resC = await fetch(`/api/competition/${competitionId}`)
      if (resC.ok) competition.value = await resC.json()
    }

    loading.value = false
  } catch (err) {
    console.error(err)
    error.value = "Erreur de chargement."
    loading.value = false
  }
})

function goToEdit() {
  console.log("Modifier compétition")
}

function genererMatchs() {
  console.log("Générer les poules")
}
</script>


<template>
  <div class="competition-details">

    <div v-if="loading">Chargement...</div>
    <div v-if="error" class="state-msg error">{{ error }}</div>

    <!-- Aucun match -->
    <div v-if="!hasMatches && !loading" class="no-matches">
      <p>Aucun match n’a encore été généré pour cette compétition.</p>
      <button class="btn-primary" @click="goToEdit">Modifier la compétition</button>
      <button class="btn-secondary" @click="genererMatchs">Générer les poules et créer les matchs</button>
    </div>

    <!-- Affichage principal -->
    <div v-if="competition">

      <h2>
        {{ competition.nomCompetition }} —
        {{ competition.format }} —
        {{ competition.genre }} —
        {{ nbTeams }} équipes
      </h2>

      <!-- ÉQUIPES -->
      <section class="equipes-section">
        <h3>Équipes engagées</h3>

        <div class="teams-grid">
          <div v-for="t in teams" :key="t.idEquipe" class="team-card-wrapper">
            <CarteEquipe :equipe="t" :image="ImageFond" :licencie="false  "/>
          </div>
        </div>
      </section>

      <!-- MATCHS -->
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
  color: #000;
}

.state-msg.error {
  color: #e74c3c;
}

.teams-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: center;
  width: 100%;
}

.team-card-wrapper {
  flex: 0 1 220px;
}

.prochain_matches {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  width: 100%;
  max-width: 1000px;
}
</style>
