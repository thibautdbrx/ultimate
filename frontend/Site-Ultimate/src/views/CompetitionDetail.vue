<script setup>
import { ref, onMounted, computed } from 'vue'
import {stringifyQuery, useRoute, useRouter} from 'vue-router'

import SliderCardHorizontal from "@/components/Slider_card_horizontal.vue"
import CardMatch from "@/components/card_match.vue"
import CarteEquipe from "@/components/card_equipe.vue"
import ImageFond from "@/assets/img/img_equipe.jpg"
import SelectEquipe from "@/components/SelectionEquipeOverlay.vue"
import SelectionTerrainOverlay from "@/components/SelectionTerrainOverlay.vue"

import { useAuthStore } from "@/stores/auth";
const auth = useAuthStore();


const route = useRoute()
const router = useRouter()

const classement = ref([])


const competitionId = route.params.id

const competition = ref(null)
const matches = ref([])
const teams = ref([])

const loading = ref(true)
const error = ref(null)

const modalShow_1 =ref(false)
const editMode = ref(false)

const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}


const showConfirm = ref(false)
const confirmMsg = ref("")
const pendingAction = ref(null)

const askConfirmation = (message, action) => {
  confirmMsg.value = message
  pendingAction.value = action
  showConfirm.value = true
}

const confirmYes = () => {
  if (pendingAction.value) pendingAction.value()
  showConfirm.value = false
  pendingAction.value = null
}

async function fetchTeams() {
  const res = await fetch(`/api/participation/competition/${competitionId}`)
  if (!res.ok) throw new Error("Erreur HTTP équipes")
  teams.value = await res.json()
}

async function fetchCompetitionInfo() {
  const res = await fetch(`/api/competition/${competitionId}`)
  if (res.ok) {
    competition.value = await res.json()
    GENRE_API_MAP[competition.genre] ?? ""
  }

}

async function fetchMatches() {
  const res = await fetch(`/api/competition/${competitionId}/matchs`)
  if (!res.ok) throw new Error("Erreur HTTP matchs")
  matches.value = await res.json()
  //console.log(matches.value)
}

async function fetchClassement() {
  const res = await fetch(`/api/classement/competition/${competitionId}`)
  if (!res.ok) throw new Error("Erreur HTTP classement")
  classement.value = await res.json()
}

const classementTrie = computed(() => {
  return [...classement.value].sort((a, b) => a.rang - b.rang)
})



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
    await fetchClassement()


  } catch (err) {
    console.error(err)
    error.value = "Erreur de chargement."
  } finally {
    loading.value = false
  }
})



const selectExisting = async (equipe) => {
  try {
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
    notify("Équipe ajoutée avec succès", "success")
    modalShow_1.value = false

  } catch (err) {
    console.error(err)
    notify("Impossible d’ajouter l'équipe.")
  }
}


const allowEdit = computed(() => matches.value.length === 0)

const hasMatches = computed(() => matches.value.length > 0)

const finishedMatches = computed(() => {
  return matches.value.filter(m => m.status === "FINISHED")
})

const upcomingMatches = computed(() => {
  return matches.value.filter(m => m.status !== "FINISHED")
})

const nbTeams = computed(() => teams.value.length)

function toggleEditMode() {
  editMode.value = !editMode.value
}


function goToEquipe(id, nom) {
  router.push({ name: 'Equipe-details', params: { id, nom } })
}

const openModal_1 = () => {
  if (competitionDejaCommencee.value) {
    notify("La compétition a déjà commencé.", "error")
    return
  }
  modalShow_1.value = true
}

const supprimerEquipe = async (index, id) => {
  askConfirmation(`Supprimer ${teams.value[index].nomEquipe} ?`, async () => {
    const suppJ = await fetch(`/api/participation`, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ idEquipe: id, idCompetition: competitionId })
    });
    teams.value.splice(index, 1)
    notify("Équipe supprimée", "success")
  })
}

const format_bien_aff = computed(() => {
  return (competition.value?.format || "").toUpperCase();
});

const formatDate = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

const GenererMatch = async () => {
  const message = "⚠️ Attention :\n\n" +
      "Une fois les matchs générés, vous ne pourrez PLUS modifier la compétition " +
      "(ajout/suppression d'équipes impossible).\n\n" +
      "Voulez-vous continuer ?"

  askConfirmation(message, async () => {
    try {
      const idCompetition = route.params.id;
      const response = await fetch(`/api/competition/${idCompetition}/create`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
      });

      if (!response.ok) {
        throw new Error("Erreur lors de la génération des matchs");
      }

      notify("Matchs générés avec succès !", "success");
      setTimeout(() => { router.push(`/Competitions/${competitionId}`) }, 1500)

    } catch (error) {
      console.error(error);
      notify("Une erreur est survenue lors de la génération")
    }
  })
};

const competitionDejaCommencee = computed(() => {
  if (!competition.value?.dateDebut) return false

  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const debut = new Date(competition.value.dateDebut)
  debut.setHours(0, 0, 0, 0)

  return debut < today
})





const modalTerrainShow = ref(false)

const openTerrainModal = () => {
  // On réutilise la logique pour empêcher la modif si c'est commencé
  if (competitionDejaCommencee.value) {
    alert("La compétition a déjà commencé.")
    return
  }
  modalTerrainShow.value = true
}
const supprimerTerrain = async (index, idTerrain) => {
  // 1. Confirmation
  if (!confirm("Retirer ce terrain de la compétition ?")) {
    return
  }

  try {
    // 2. Appel API avec l'ID
    const res = await fetch(`/api/competition/${competitionId}/terrain/${idTerrain}`, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" }
    })

    if (!res.ok) throw new Error("Erreur suppression terrain")

    // 3. Mise à jour locale avec l'INDEEEEX
    competition.value.terrains.splice(index, 1)

    console.log("Terrain supprimé")

  } catch (err) {
    console.error(err)
    alert("Impossible de supprimer le terrain.")
  }
}



const selectTerrain = async (terrain) => {
  try {

    const res = await fetch(`/api/competition/${competitionId}/terrain/${terrain.id_terrain}`, {
      method: "POST", // ou PUT selon ton API
      headers: { "Content-Type": "application/json" }
    })

    if (!res.ok) throw new Error("Erreur ajout terrain")

    // Mise à jour locale
    if(!competition.value.terrains) competition.value.terrains = []

    competition.value.terrains.push(terrain)

    // Fermer modal
    modalTerrainShow.value = false
    alert("Terrain ajouté !")

  } catch (err) {
    console.error(err)
    alert("Impossible d'ajouter le terrain.")
  }
}

function nouveau_push_router(){
  console.log("Nouveau push_router")
  router.push('/Terrains')
}
</script>

<template>
  <main>
    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

    <Transition name="fade">
      <div v-if="showConfirm" class="confirm-overlay">
        <div class="confirm-box">
          <p style="white-space: pre-line;">{{ confirmMsg }}</p>
          <div class="confirm-btns">
            <button class="btn-no" @click="showConfirm = false">Annuler</button>
            <button class="btn-yes" @click="confirmYes">Confirmer</button>
          </div>
        </div>
      </div>
    </Transition>

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

        <div v-if="allowEdit && !loading" class="no-matches">
          <p>Aucun match n’a encore été généré pour cette compétition.</p>

          <button v-if="auth.isAdmin" class="btn-primary" @click="toggleEditMode">
            {{ editMode ? "Quitter la modification" : "Modifier" }}
          </button>

          <button v-if="auth.isAdmin || auth.isArbitre" class="btn-primary" @click="GenererMatch">
            Générer les poules et créer les matchs
          </button>
        </div>

        <section class="equipes-section">
          <h3>Équipes engagées</h3>

          <div v-if="allowEdit && editMode" class="edit-actions">

            <button
                v-if="!competitionDejaCommencee"
                class="btn-primary"
                @click="openModal_1()"
            >
              Ajouter une équipe
            </button>

            <p v-else class="competition-deja-commencee">
              La compétition a déjà commencé, il n’est plus possible d’ajouter des équipes.
            </p>

          </div>

          <div class="teams-grid">
            <div v-for="(t,i) in teams" :key="t.idEquipe" class="team-card-wrapper">

              <button
                  v-if="allowEdit && editMode && !competitionDejaCommencee"
                  class="btn-delete"
                  @click="supprimerEquipe(i, t.idEquipe)"
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
                :genre="competition.genre"
                :all="false"
                :equipe_utilise="teams"
                @close="modalShow_1 = false"
                @select="selectExisting"
            />
          </div>
        </section>

        <div v-if="hasMatches" class="prochain_matches">

          <h3>Matchs prochains</h3>
          <SliderCardHorizontal v-if="upcomingMatches.length > 0">
            <div v-for="match in upcomingMatches" :key="match.idMatch">
              <CardMatch
                  :title="formatDate(match.dateMatch)"
                  :match ="match"

              />
            </div>
          </SliderCardHorizontal>

          <h3>Matchs finis</h3>
          <SliderCardHorizontal v-if="finishedMatches.length > 0">
            <div v-for="match in finishedMatches" :key="match.idMatch" @click="goToMatch(match.idMatch)">
              <CardMatch
                  :title="formatDate(match.dateMatch)"
                  :match = "match"
              />
            </div>
          </SliderCardHorizontal>
        </div>

        <section class="classement-section">
          <h3>Classement du tournoi</h3>

          <p v-if="!hasMatches" class="classement-info">
            Le classement sera mis à jour automatiquement dès que les matchs auront été générés et joués.
          </p>

          <ul v-else-if="classementTrie.length" class="classement-list">
            <li
                v-for="c in classementTrie"
                :key="c.idClassement.idEquipe"
                :class="['classement-item', `rang-${c.rang}`]"
            >
              <span class="rang">{{ c.rang }}</span>

              <span
                  class="equipe"
                  @click="goToEquipe(c.equipe.idEquipe, c.equipe.nomEquipe)"
              >
                  {{ c.equipe.nomEquipe }}
              </span>

              <span class="score">{{ c.score }} pts</span>
            </li>
          </ul>
        </section>


        <section class="terrain-section">
          <h3>Terrains sélectionnés</h3>

          <div v-if="allowEdit && editMode" class="edit-actions">
            <button
                class="btn-primary"
                @click="openTerrainModal"
            >
              Ajouter un terrain
            </button>
          </div>

          <ul v-if="competition.terrains && competition.terrains.length > 0" class="terrain-list">
            <li
                v-for="(terrain, i) in competition.terrains"
                :key="terrain.id_terrain"
                class="terrain-wrapper"
            >
              <button
                  v-if="allowEdit && editMode"
                  class="btn-delete"
                  @click="supprimerTerrain(i, terrain.id_terrain)"
              >
                Supprimer
              </button>
              <div class="terrain-card-display">
                <span class="terrain-nom">{{ terrain.nom }}</span>
                <span class="terrain-ville">{{ terrain.ville }}</span>
              </div>
            </li>
          </ul>

          <div v-else class="no-data-msg">
            <p>Aucun terrain n'a été sélectionné pour cette compétition.</p>
          </div>

          <SelectionTerrainOverlay
              :show="modalTerrainShow"
              :terrain_utilise="competition.terrains"
              @close="modalTerrainShow = false"
              @select="selectTerrain"
              @nvt="nouveau_push_router"
          />
        </section>

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

/* 3. LE SLIDER */
.prochain_matches {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  width: 100%;

  /* --- MODIFICATIONS POUR CENTRER --- */
  max-width: 1200px;       /* 1. On remet la limite de largeur (comme tes h2) */
  margin: 1rem auto 0 auto;/* 2. 'auto' à gauche et à droite centre le bloc */
  box-sizing: border-box;  /* 4. Pour que le padding ne dépasse pas les 100% */
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
.competition-deja-commencee {
  color: #d32f2f;
  font-size: 0.9rem;
  text-align: center;
}

.classement-section {
  margin-top: 3rem;
  display: flex;
  flex-direction: column;

}

.classement-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.classement-item {
  display: grid;
  grid-template-columns: 40px 1fr 80px;
  align-items: center;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
  border-radius: 10px;
  background: #f4f4f4;
  font-weight: 500;
}

.classement-item .rang {
  font-weight: bold;
  text-align: center;
}

.classement-item .equipe {
  cursor: pointer;
}

.classement-item .score {
  text-align: right;
  font-weight: bold;
}


.rang-1 {
  background: linear-gradient(90deg, #ffd700, #fff4b0);
}

.rang-2 {
  background: linear-gradient(90deg, #c0c0c0, #eeeeee);
}

.rang-3 {
  background: linear-gradient(90deg, #cd7f32, #f1d1b3);
}

.classement-info {
  text-align: center;
  font-size: 0.95rem;
  color: #666;
  margin-top: 1rem;
  font-style: italic;
}
/* --- STYLE TERRAINS --- */
.terrain-section {
  margin-top: 3rem;
  width: 100%;
  /* Le width 100% permet au h3 de prendre toute la largeur et de s'aligner à gauche par défaut */
}

/* Espacement pour le bouton d'ajout (similaire à teams) */
.edit-actions {
  margin: 1rem 0;
}

/* Grille identique à .teams-grid */
.terrain-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: center;
  padding: 0;
  list-style: none;
  margin: 2rem 0;
}

/* Wrapper identique à .team-card-wrapper */
.terrain-wrapper {
  flex: 0 1 220px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* Le visuel de la carte (boite blanche) */
.terrain-card-display {
  background: white;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 1.5rem 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  height: 100%;
  background-image: linear-gradient(to bottom right, #ffffff, #f9f9f9);
}

.terrain-nom {
  font-weight: bold;
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.terrain-ville {
  font-size: 0.9rem;
  color: #666;
  text-transform: uppercase;
  font-weight: 500;
}

.no-data-msg {
  text-align: center;
  color: #666;
  font-style: italic;
  margin-top: 1rem;
  border: 1px dashed #ccc;
  padding: 2rem;
  border-radius: 8px;
}

.toast-notification {
  position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%);
  color: white; padding: 12px 24px; border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2); z-index: 10000;
  display: flex; align-items: center; gap: 12px; font-weight: 600;
}
.toast-notification.success { background-color: #2ecc71; }
.toast-notification.error { background-color: #e74c3c; }
.toast-icon { background: white; width: 20px; height: 20px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; }
.success .toast-icon { color: #2ecc71; }
.error .toast-icon { color: #e74c3c; }
.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, 40px); }

.confirm-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  backdrop-filter: blur(2px); display: flex; align-items: center; justify-content: center; z-index: 11000;
}
.confirm-box {
  background: white; padding: 2rem; border-radius: 15px; width: 90%; max-width: 400px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.2); text-align: center;
}
.confirm-box p { font-weight: 600; font-size: 1rem; margin-bottom: 1.5rem; color: #333; line-height: 1.5; }
.confirm-btns { display: flex; gap: 1rem; justify-content: center; }
.btn-no, .btn-yes { padding: 0.6rem 1.2rem; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-no { background: #eee; color: #666; }
.btn-yes { background: #1e88e5; color: white; }
.btn-no:hover { background: #ddd; }
.btn-yes:hover { background: #1565c0; transform: scale(1.05); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }


</style>
