<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from "@/stores/auth";

// Imports des sous-composants
import CompetitionTeams from "@/components/competition_detail/CompetitionTeams.vue"
import CompetitionMatches from "@/components/competition_detail/CompetitionMatches.vue"
import CompetitionClassement from "@/components/competition_detail/CompetitionClassement.vue"
import CompetitionTerrains from "@/components/competition_detail/CompetitionTerrains.vue"

// Imports des Overlays
import SelectEquipe from "@/components/SelectionEquipeOverlay.vue"
import SelectionTerrainOverlay from "@/components/SelectionTerrainOverlay.vue"

const auth = useAuthStore();
const route = useRoute()
const router = useRouter()
const competitionId = route.params.id

// --- STATE ---
const competition = ref(null)
const matches = ref([])
const teams = ref([])
const classement = ref([])

const loading = ref(true)
const error = ref(null)
const editMode = ref(false)

// Modales
const modalShow_Teams = ref(false)
const modalShow_Terrains = ref(false)

// Toast & Confirm
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")
const showConfirm = ref(false)
const confirmMsg = ref("")
const pendingAction = ref(null)

// --- COMPUTED HELPERS ---
const allowEdit = computed(() => matches.value.length === 0)
const hasMatches = computed(() => matches.value.length > 0)
const nbTeams = computed(() => teams.value.length)

const competitionDejaCommencee = computed(() => {
  if (!competition.value?.dateDebut) return false
  const today = new Date(); today.setHours(0, 0, 0, 0)
  const debut = new Date(competition.value.dateDebut); debut.setHours(0, 0, 0, 0)
  return debut < today
})

const canGenerate = computed(() => {
  const hasEnoughTeams = teams.value.length >= 2
  const hasTerrain = competition.value?.terrains?.length >= 1
  return hasEnoughTeams && hasTerrain
})

const format_bien_aff = computed(() => (competition.value?.format || "").toUpperCase());

// --- NOTIFICATIONS ---
const notify = (msg, type = "error") => {
  toastMessage.value = msg; toastType.value = type; showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

const askConfirmation = (message, action) => {
  confirmMsg.value = message; pendingAction.value = action; showConfirm.value = true
}
const confirmYes = () => { if (pendingAction.value) pendingAction.value(); showConfirm.value = false; pendingAction.value = null }

// --- API FETCHING ---
async function fetchData() {
  loading.value = true
  try {
    const [resTeams, resMatches, resInfo, resClass] = await Promise.all([
      fetch(`/api/participation/competition/${competitionId}`),
      fetch(`/api/competition/${competitionId}/matchs`),
      fetch(`/api/competition/${competitionId}`),
      fetch(`/api/classement/competition/${competitionId}`)
    ])

    if (resTeams.ok) teams.value = await resTeams.json()
    if (resMatches.ok) matches.value = await resMatches.json()
    if (resInfo.ok) competition.value = await resInfo.json()
    if (resClass.ok) classement.value = await resClass.json()

  } catch (err) {
    console.error(err); error.value = "Erreur de chargement des données."
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)

// --- ACTIONS ÉQUIPES ---
const addTeam = async (equipe) => {
  try {
    const res = await fetch(`/api/participation`, {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ "idEquipe": equipe.idEquipe, "idCompetition": competitionId })
    })
    if (!res.ok) throw new Error("Erreur ajout")

    teams.value.push({ ...equipe }) // On ajoute localement pour éviter un re-fetch
    notify("Équipe ajoutée", "success")
    modalShow_Teams.value = false
  } catch (err) { notify("Impossible d’ajouter l'équipe.") }
}

const removeTeam = (index, id) => {
  askConfirmation(`Supprimer cette équipe ?`, async () => {
    await fetch(`/api/participation`, {
      method: "DELETE", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ idEquipe: id, idCompetition: competitionId })
    });
    teams.value.splice(index, 1)
    notify("Équipe supprimée", "success")
  })
}

// --- ACTIONS TERRAINS ---
const addTerrain = async (terrain) => {
  try {
    const res = await fetch(`/api/competition/${competitionId}/terrain/${terrain.idTerrain}`, {
      method: "POST", headers: { "Content-Type": "application/json" }
    })
    if (!res.ok) throw new Error("Erreur ajout terrain")

    if(!competition.value.terrains) competition.value.terrains = []
    competition.value.terrains.push(terrain)
    modalShow_Terrains.value = false
    notify("Terrain ajouté !", "success")
  } catch (err) { notify("Impossible d'ajouter le terrain.") }
}

const removeTerrain = (index, idTerrain) => {
  askConfirmation("Retirer ce terrain ?", async () => {
    const res = await fetch(`/api/competition/${competitionId}/terrain/${idTerrain}`, { method: "DELETE" })
    if (!res.ok) return notify("Impossible de supprimer")
    competition.value.terrains.splice(index, 1)
    notify("Terrain supprimé", "success")
  })
}

// --- ACTIONS MATCHS ---
const GenererMatch = async () => {
  if (!canGenerate.value){
    return askConfirmation("⚠️ Impossible : Il faut au moins 2 équipes et 1 terrain.")
  }
  askConfirmation("Une fois générés, les équipes ne sont plus modifiables.\nContinuer ?", async () => {
    try {
      const res = await fetch(`/api/competition/${competitionId}/create`, {
        method: "PUT", headers: { "Content-Type": "application/json" },
      });
      if (!res.ok) throw new Error("Erreur génération");
      notify("Matchs générés !", "success");
      // Petit reload pour afficher les matchs
      setTimeout(() => { window.location.reload() }, 1000)
    } catch (error) { notify("Erreur lors de la génération") }
  })
};

const supprimerMatch = async () => {
  // Placeholder car la fonction manquait dans votre code original
  askConfirmation("Voulez-vous vraiment supprimer tous les matchs ?", async () => {
    // Logique API à mettre ici
    notify("Fonctionnalité en cours de développement", "error")
  })
}

// --- NAVIGATION ---
const goToEquipe = (t) => { router.push({ name: 'Equipe-details', params: { id: t.idEquipe, nom: t.nomEquipe } }) }
const openTeamsModal = () => {
  if (competitionDejaCommencee.value) return notify("Compétition commencée.", "error")
  modalShow_Teams.value = true
}
const openTerrainsModal = () => {
  if (competitionDejaCommencee.value) return notify("Compétition commencée.", "error")
  modalShow_Terrains.value = true
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
          {{ competition.nomCompetition }} — {{ format_bien_aff }} — {{ competition.genre }} — {{ nbTeams }} équipes
        </h2>

        <div v-if="allowEdit && !loading" class="no-matches">
          <p class="info-msg">Aucun match n’a encore été généré.</p>

          <button v-if="auth.isAdmin" class="btn-primary" @click="editMode = !editMode">
            {{ editMode ? "Quitter la modification" : "Modifier" }}
          </button>

          <button v-if="auth.isAdmin || auth.isArbitre" class="btn-primary" @click="GenererMatch">
            Générer les poules et créer les matchs
          </button>
        </div>

        <button v-if="auth.isAdmin && !allowEdit" class="btn-primary" @click="supprimerMatch">
          Supprimer tous les matchs
        </button>

        <CompetitionTeams
            :teams="teams"
            :edit-mode="editMode"
            :is-started="competitionDejaCommencee"
            @open-add="openTeamsModal"
            @delete-team="({index, id}) => removeTeam(index, id)"
            @go-to-team="goToEquipe"
        />

        <CompetitionMatches
            :matches="matches"
        />

        <CompetitionClassement
            :classement="classement"
            :has-matches="hasMatches"
            @go-to-team="goToEquipe"
        />

        <CompetitionTerrains
            :terrains="competition.terrains"
            :edit-mode="editMode"
            @open-add="openTerrainsModal"
            @delete-terrain="({index, id}) => removeTerrain(index, id)"
        />

        <SelectEquipe
            :show="modalShow_Teams"
            :genre="competition.genre"
            :all="false"
            :equipe_utilise="teams"
            @close="modalShow_Teams = false"
            @select="addTeam"
        />

        <SelectionTerrainOverlay
            :show="modalShow_Terrains"
            :terrain_utilise="competition.terrains"
            @close="modalShow_Terrains = false"
            @select="addTerrain"
            @nvt="router.push('/Terrains')"
        />

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

.btn-primary {
  background-color: #333;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
}

.info-msg {
  font-size: 0.95rem;
  color: #666;
  margin-top: 1rem;
  font-style: italic;
}

/* TOAST & CONFIRM STYLES */

.toast-notification {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  z-index: 10000;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}

.toast-notification.success {
  background-color: #2ecc71;
}

.toast-notification.error {
  background-color: #e74c3c;
}

.toast-icon {
  background: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.success .toast-icon {
  color: #2ecc71;
}

.error .toast-icon {
  color: #e74c3c;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, 40px);
}

.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 11000;
}

.confirm-box {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.confirm-box p {
  font-weight: 600;
  font-size: 1rem;
  margin-bottom: 1.5rem;
  color: #333;
  line-height: 1.5;
}

.confirm-btns {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-no,
.btn-yes {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s;
}

.btn-no {
  background: #eee;
  color: #666;
}

.btn-yes {
  background: #1e88e5;
  color: white;
}

.btn-no:hover {
  background: #ddd;
}

.btn-yes:hover {
  background: #1565c0;
  transform: scale(1.05);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>