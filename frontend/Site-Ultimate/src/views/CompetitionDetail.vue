<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import SliderCardHorizontal from "@/components/Slider_card_horizontal.vue"
import CardMatch from "@/components/card_match.vue"
import CarteEquipe from "@/components/card_equipe.vue"
import ImageFond from "@/assets/img/img_equipe.jpg"
import SelectEquipe from "@/components/SelectionEquipeOverlay.vue"

const route = useRoute()
const router = useRouter()

const competitionId = route.params.id

const competition = ref(null)
const matches = ref([])
const teams = ref([])

const loading = ref(true)
const error = ref(null)

const modalShow_1 =ref(false)
const editMode = ref(false)

async function fetchTeams() {
  const res = await fetch(`/api/participation/competition/${competitionId}`)
  if (!res.ok) throw new Error("Erreur HTTP équipes")
  teams.value = await res.json()
}

async function fetchCompetitionInfo() {
  const res = await fetch(`/api/tournois/${competitionId}`)
  if (res.ok) {
    competition.value = await res.json()
    GENRE_API_MAP[competition.genre] ?? ""
  }

}

async function fetchMatches() {
  const res = await fetch(`/api/tournois/${competitionId}/matchs`)
  if (!res.ok) throw new Error("Erreur HTTP matchs")
  matches.value = await res.json()
}



const GENRE_API_MAP = {
  HOMME: "MALE",
  FEMMME: "FEMALE",
  MIXTE: "MIXTE",
  MALE: "MALE",
  FEMALE: "FEMALE"
}

const genreApi = computed(() => {
  return GENRE_API_MAP[competition.value?.genre] ?? ""
})


onMounted(async () => {
  try {
    loading.value = true

    await fetchTeams()
    await fetchMatches()
    await fetchCompetitionInfo()

  } catch (err) {
    console.error(err)
    error.value = "Erreur de chargement."
  } finally {
    loading.value = false
  }
})


const selectExisting = async (equipe) => {
  try {
    // 1. Appeler l’API pour assicier equipe au tournois
      const res = await fetch(`/api/participation`, {
      method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          "idEquipe": equipe.idEquipe,
          "idCompetition": competitionId
        })
    })

    if (!res.ok) {
      throw new Error("Erreur lors de l'ajout de l'équie à la competition")
    }

    // 2. Ajouter le joueur dans la liste locale
    teams.value.push({
      idEquipe: equipe.idEquipe,
      nomEquipe: equipe.nomEquipe,
      description: equipe.descriptionEquipe,
      genre: equipe.genre
    })
    alert("équipe bien ajouté dans la base de donnée")
    // 3. Fermer la modale
    modalShow_1.value = false

  } catch (err) {
    console.error(err)
    alert("Impossible d’ajouter le joueur à l’équipe.")
  }
}

// Logique affichage
const allowEdit = computed(() => matches.value.length === 0)

const hasMatches = computed(() => matches.value.length > 0)

const finishedMatches = computed(() => {
  const now = new Date()
  return matches.value.filter(m =>
      m.status === "FINISHED" || new Date(m.dateFin) <= now
  )
})

const upcomingMatches = computed(() => {
  const now = new Date()
  return matches.value.filter(m =>
      m.status !== "FINISHED" && new Date(m.dateFin) > now
  )
})

const nbTeams = computed(() => teams.value.length)

function toggleEditMode() {
  editMode.value = !editMode.value
}

function genererMatchs() {
  console.log("Générer les poules…")
}

function goToEquipe(id, nom) {
  router.push({ name: 'Equipe-details', params: { id, nom } })
}

const openModal_1 = () => {
  modalShow_1.value = true
}
const supprimerEquipe = async (index) => {
  if (confirm(`Supprimer ${teams.value[index].nomEquipe} ?`)) {
    const suppJ = await fetch(`/api/participation//equipe/${equipeId}`, {
      method: "DELETE",
    });
    joueurs.value.splice(index, 1)
  }
}

const format_bien_aff = computed(() => {
  return (competition.value?.format || "").toUpperCase();
});



</script>

<template>
  <main>
    <div class="competition-details">

      <div v-if="loading">Chargement...</div>
      <div v-if="error" class="state-msg error">{{ error }}</div>

      <div v-if="competition">

        <h2>
          {{ competition.nomCompetition }} —
          {{ format_bien_aff }} —
          {{ competition.genre }} —
          {{ nbTeams }} équipes
        </h2>

        <!-- 🔥 MODE SANS MATCHS = ÉDITION ACTIVÉE -->
        <div v-if="allowEdit && !loading" class="no-matches">
          <p>Aucun match n’a encore été généré pour cette compétition.</p>

          <button class="btn-primary" @click="toggleEditMode">
            {{ editMode ? "Quitter la modification" : "Modifier" }}
          </button>

          <button class="btn-primary" @click="genererMatchs">
            Générer les poules et créer les matchs
          </button>
        </div>

        <!-- ÉQUIPES TOUJOURS AFFICHÉES -->
        <section class="equipes-section">
          <h3>Équipes engagées</h3>

          <!-- Bouton d'ajout uniquement en mode edition -->
          <div v-if="allowEdit && editMode" class="edit-actions">
            <button class="btn-primary" @click="openModal_1()">Ajouter une équipe</button>
          </div>

          <div class="teams-grid">
            <div v-for="t in teams" :key="t.idEquipe" class="team-card-wrapper">

              <!-- Bouton supprimer affiché uniquement en édition -->
              <button
                  v-if="allowEdit && editMode"
                  class="btn-delete"
                  @click="() => console.log('Supprimer', t.idEquipe)"
              >
                Supprimer
              </button>

              <CarteEquipe
                  :equipe="t"
                  :image="ImageFond"
                  :licencie="false"
                  @click="goToEquipe(t.idEquipe, t.nomEquipe)"
              />
            </div>


          <SelectEquipe
              :show="modalShow_1"
              :genre="genreApi"
              :all="false"
              @close="modalShow_1 = false"
              @select="selectExisting"
          />
          </div>
        </section>

        <!-- AFFICHAGE DES MATCHS UNIQUEMENT S'IL Y EN A -->
        <div v-if="hasMatches" class="prochain_matches">

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
  text-align: center;
  color: #000;
}

.state-msg.error {
  color: #e74c3c;
}

.no-matches {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.teams-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: center;
  width: 100%;
  margin: 2rem 0;
}

.team-card-wrapper {
  flex: 0 1 220px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.btn-delete {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 8px;
  cursor: pointer;
}

.prochain_matches {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  width: 100%;
  max-width: 1000px;
}

.btn-primary{
  background-color: #333;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
}
</style>
