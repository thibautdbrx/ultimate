<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CardJoueur from "@/components/card/card_joueur.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import champ_input from "@/components/champs_input.vue"
import SelectJoueur from "@/components/overlay/SelectionJoueurOverlay.vue"

import api from '@/services/api' // Ajout de l'import api
import { useAuthStore } from "@/stores/auth";
const auth = useAuthStore();

const route = useRoute()
const equipeId = route.params.id
const dateDebut = ref("")
const dateFin = ref("")
const indispos = ref([])

// --- LOGIQUE TOAST (EXISTANTE) ---
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

// --- LOGIQUE MODALE DE CONFIRMATION (NOUVEAU) ---
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

const loading = ref(true)
const error = ref(null)
const editMode = ref(false)
const indispoMode = ref(false)

const equipe = ref(null)
const joueurs = ref([])
const modalShow_1 = ref(false)
const modalIndex = ref()

const nomEquipe = ref("")
const descriptionEquipe = ref("")
const genre = ref("")


onMounted(async () => {
  try {
    // Remplacement des fetch par api.get
    const resEquipe = await api.get(`/equipe/${equipeId}`)
    equipe.value = resEquipe.data
    nomEquipe.value = equipe.value.nomEquipe
    genre.value = equipe.value.genre

    const resJoueurs = await api.get(`/joueur/equipe/${equipeId}`)
    joueurs.value = resJoueurs.data

    await get_indispo()
    loading.value = false

  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les informations de l'équipe."
    loading.value = false
  }
})

// --- FONCTIONS MODIFIÉES POUR UTILISER LA MODALE ---

const supprimerJoueur = (index) => {
  askConfirmation(`Supprimer ${joueurs.value[index].nomJoueur} de l'équipe ?`, async () => {
    try {
      await api.delete(`/joueur/${joueurs.value[index].idJoueur}/equipe/${equipeId}`);
      joueurs.value.splice(index, 1)
      notify("Joueur supprimé", "success")
    } catch (err) {
      notify("Erreur lors de la suppression")
    }
  })
}

const get_indispo = async () => {
  try {
    const res = await api.get(`/indisponibilite/equipe/${equipeId}`)
    indispos.value = res.data
  } catch (err) {
    console.error(err)
  }
}

const ajouterIndispo = async () => {
  if (!dateDebut.value || !dateFin.value) {
    notify("Veuillez renseigner les deux dates")
    return
  }
  const dateDebutFormatted = formatDateTimeSafe(dateDebut.value)
  const dateFinFormatted = formatDateTimeSafe(dateFin.value)
  if (!dateDebutFormatted || !dateFinFormatted) return
  try {
    const res = await api.post(`/indisponibilite`, {
      idEquipe: equipeId,
      dateDebut: dateDebutFormatted,
      dateFin: dateFinFormatted
    })
    const newIndispo = res.data
    indispos.value.push(newIndispo)
    dateDebut.value = ""; dateFin.value = ""
    notify("Indisponibilité ajoutée", "success")
  } catch (err) {
    notify("Erreur lors de l'ajout")
  }
}

const supprimerIndispo = (id, index) => {
  askConfirmation("Supprimer cette indisponibilité ?", async () => {
    try {
      await api.delete(`/indisponibilite/${id}`)
      indispos.value.splice(index, 1)
      notify("Indisponibilité supprimée", "success")
    } catch (err) {
      notify("Erreur lors de la suppression")
    }
  })
}

const valider_titre_desc = () => {
  askConfirmation("Changer le nom ou la description de l'équipe ?", async () => {
    try {
      await api.patch(`/equipe/${equipeId}/name`, {
        nom: nomEquipe.value,
        description: descriptionEquipe.value
      });
      equipe.value.nomEquipe = nomEquipe.value;
      equipe.value.descriptionEquipe = descriptionEquipe.value;
      notify("Modifications enregistrées", "success")
    } catch (err) {
      notify("Erreur lors de la modification")
    }
  })
}

const openModal_1 = () => {
  modalIndex.value = joueurs.value.length
  modalShow_1.value = true
}

const toggleEditMode = () => editMode.value = !editMode.value
const toggleindispoMode = () => indispoMode.value = !indispoMode.value

const selectExisting = async (joueur) => {
  try {
    await api.patch(`/joueur/${joueur.idJoueur}/equipe/${equipeId}`)
    joueurs.value.push({ ...joueur })
    notify("Joueur ajouté", "success")
    modalShow_1.value = false
  } catch (err) {
    if (err.response && err.response.status === 401) {
      notify("L'équipe est déjà complète !", "error")
    } else {
      notify("Impossible d'ajouter le joueur")
    }
    modalShow_1.value = false
  }
}

const formatDateTimeSafe = (value) => {
  const regex = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/
  if (!regex.test(value)) { notify("Format de date invalide"); return null }
  return value.replace("T", " ")
}
</script>

<template>
  <main class="equipe-page">

    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

    <Transition name="fade">
      <div v-if="showConfirm" class="confirm-overlay">
        <div class="confirm-box">
          <p>{{ confirmMsg }}</p>
          <div class="confirm-btns">
            <button class="btn-no" @click="showConfirm = false">Annuler</button>
            <button class="btn-yes" @click="confirmYes">Confirmer</button>
          </div>
        </div>
      </div>
    </Transition>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else>
      <button v-if="auth.isAdmin" class="btn" @click="toggleEditMode">
        {{ editMode ? "Quitter la modification" : "Modifier" }}
      </button>
      <button v-if="auth.isAdmin" class="btn" @click="toggleindispoMode">
        {{ indispoMode ? "Quitter l'ajout d'indisponibilité " : "ajouter une indisponibilité" }}
      </button>

      <h2 v-if="!editMode" class="titre">{{ equipe.nomEquipe }}</h2>
      <div class="modif">
        <champ_input class="input" label="Nom équipe" v-if="editMode" v-model="nomEquipe" :placeholder="equipe.nomEquipe" :icon="UserIcon" />
        <button v-show="editMode" class="btn" @click="valider_titre_desc">Valider</button>
      </div>

      <p v-if="!editMode" class="description">{{ equipe.descriptionEquipe || "Aucune description disponible." }}</p>

      <div v-if="indispoMode" class="date_indispo">
        <p>Veuillez choisir une date de début et une date de fin d'indisponibilité</p>
        <label>Date début :</label>
        <input type="datetime-local" v-model="dateDebut" class="select-format" />
        <label>Date fin :</label>
        <input type="datetime-local" v-model="dateFin" class="select-format" />
        <button class="btn" @click="ajouterIndispo">Ajouter</button>

        <h3>Indisponibilités existantes</h3>
        <div v-for="(indispo, i) in indispos" :key="indispo.idIndisponibilite" class="joueur-wrapper">
          <p>{{ indispo.dateDebut }} → {{ indispo.dateFin }}</p>
          <button class="btn-supprimer" @click="supprimerIndispo(indispo.idIndisponibilite, i)">Supprimer</button>
        </div>
      </div>

      <div class="modif">
        <champ_input class="input" label="Description" v-if="editMode" v-model="descriptionEquipe" :placeholder="equipe.descriptionEquipe" :icon="UserIcon" />
        <button v-show="editMode" class="btn" @click="valider_titre_desc">Valider</button>
      </div>

      <button v-if="editMode" class="btn" id="ajouter_j" @click="openModal_1()">Ajouter un joueur</button>

      <div class="joueurs-grid">
        <div v-for="(j, i) in joueurs" :key="i" class="joueur-wrapper">
          <button v-show="editMode" class="btn-supprimer" @click="supprimerJoueur(i)">Supprimer</button>
          <CardJoueur :nom="j.nomJoueur" :prenom="j.prenomJoueur" :genre="j.genre" :photo="j.photoJoueur" />
        </div>
        <Teleport to="body">
        <SelectJoueur :show="modalShow_1" :genre="genre" :id_equipe="equipeId" @close="modalShow_1 = false" @select="selectExisting" />
        </Teleport>
      </div>
    </div>
  </main>
</template>

<style scoped>
/* --- TES STYLES EXISTANTS --- */
.equipe-page { padding: 2rem; text-align: center; }
.titre { font-size: 2rem; margin-bottom: 0.5rem; }
.description { max-width: 600px; margin: 0 auto 2rem auto; color: #666; }
.joueurs-grid { display: flex; justify-content: center; flex-wrap: wrap; gap: 1rem; margin-top: 2rem; align-items: center; }
.joueur-wrapper { display: flex; flex-direction: column; align-items: center; gap: 0.5rem; }
.btn-supprimer { background: #e53935; color: white; border: none; padding: 0.3rem 0.6rem; border-radius: 8px; cursor: pointer; }
.btn { background-color: #333; color: white; padding: 0.5rem 1rem; border: none; border-radius: 8px; cursor: pointer; margin: 0.5rem; }
.btn:hover { background-color: #555; }
.modif{ display: flex; flex-direction: row; align-items: center; align-items: flex-end; justify-content: center; }
.input { width: 30%; }
#ajouter_j{ width: 10rem; height: 3rem; }
.date_indispo{ display: flex; flex-direction: column; align-items: center; }

/* --- STYLE DU TOAST --- */
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

/* --- STYLE DE LA MODALE DE CONFIRMATION (NOUVEAU) --- */
.confirm-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  backdrop-filter: blur(2px); display: flex; align-items: center; justify-content: center; z-index: 11000;
}
.confirm-box {
  background: white; padding: 2rem; border-radius: 15px; width: 90%; max-width: 400px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.2); text-align: center;
}
.confirm-box p { font-weight: 600; font-size: 1.1rem; margin-bottom: 1.5rem; color: #333; }
.confirm-btns { display: flex; gap: 1rem; justify-content: center; }
.btn-no, .btn-yes { padding: 0.6rem 1.2rem; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-no { background: #eee; color: #666; }
.btn-yes { background: #1e88e5; color: white; }
.btn-no:hover { background: #ddd; }
.btn-yes:hover { background: #1565c0; transform: scale(1.05); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>