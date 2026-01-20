<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ImageFond from "../assets/img/img_equipe.jpg"
import CarteEquipe from "@/components/card_equipe.vue"
import { useAuthStore } from "@/stores/auth.js"

const auth = useAuthStore()
const router = useRouter()

// --- STATES ---
const equipes = ref([])
const loading = ref(true)
const error = ref(null)
const submittingId = ref(null)
const existingTeam = ref(null)

// --- STATE POUR LE BANDEAU (TOAST) ---
const showToast = ref(false)
const toastMessage = ref("")

// Fonction pour afficher le toast et le cacher automatiquement
function triggerToast(message) {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 4000) // Disparaît après 4 secondes
}

function getJoueurIdFromCookie() {
  const token = document.cookie.split('; ').find(row => row.startsWith('token='))?.split('=')[1]
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.joueurId
    } catch (e) { return null }
  }
  return null
}

onMounted(async () => {
  let currentJoueurId = auth.joueurId || getJoueurIdFromCookie()
  if (!currentJoueurId) {
    router.push('/Connexion')
    return
  }

  try {
    loading.value = true
    const resJoueur = await fetch(`/api/joueur/${currentJoueurId}`)
    if (!resJoueur.ok) throw new Error("Impossible de récupérer le profil")
    const joueur = await resJoueur.json()

    if (joueur.equipe) {
      existingTeam.value = joueur.equipe
      loading.value = false
      return
    }

    const resEquipes = await fetch(`/api/equipe/open?idJoueur=${currentJoueurId}`)
    if (!resEquipes.ok) throw new Error(`Erreur HTTP: ${resEquipes.status}`)
    equipes.value = await resEquipes.json()
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
})

async function postuler(equipeId) {
  let currentJoueurId = auth.joueurId || getJoueurIdFromCookie()
  let token = auth.token
  if (!token) {
    const cookie = document.cookie.split('; ').find(row => row.startsWith('token='))
    if(cookie) token = cookie.split('=')[1]
  }

  if (!currentJoueurId || !token) {
    triggerToast("Erreur d'authentification.")
    return
  }

  submittingId.value = equipeId
  try {
    const response = await fetch(`/api/joueur/request/${currentJoueurId}/equipe/${equipeId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })

    if (!response.ok) {
      const err = await response.json().catch(() => ({}))
      throw new Error(err.message || "Erreur lors de la demande.")
    }

    equipes.value = equipes.value.filter(e => e.idEquipe !== equipeId)

    // --- REMPLACEMENT DE L'ALERT PAR LE TOAST ---
    triggerToast("Votre postulation a bien été envoyée !")

  } catch (err) {
    console.error(err)
    triggerToast("Erreur : " + err.message)
  } finally {
    submittingId.value = null
  }
}
</script>

<template>
  <main class="equipes-page">

    <Transition name="toast">
      <div v-if="showToast" class="toast-notification">
        ✔ {{ toastMessage }}
      </div>
    </Transition>

    <h2 class="title" v-if="existingTeam">Votre Équipe</h2>
    <h2 class="title" v-else>Rejoindre une équipe</h2>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else>
      <div v-if="existingTeam" class="already-team-container">
        <p class="info-text">Vous faites déjà partie d'une équipe !</p>
        <div class="my-team-card">
          <CarteEquipe :equipe="existingTeam" :image="ImageFond" />
        </div>
        <router-link to="/mon-compte" class="action-btn-primary">Aller sur mon espace</router-link>
      </div>

      <div v-else>
        <p class="subtitle">Voici les équipes qui recrutent.</p>
        <div v-if="equipes.length === 0" class="state-msg info">Aucune équipe disponible.</div>
        <div v-else class="equipes-list">
          <div v-for="equipe in equipes" :key="equipe.idEquipe" class="equipe-wrapper">
            <router-link :to="{ name: 'Equipe-details', params: { id: equipe.idEquipe, nom: equipe.nomEquipe } }" class="card-link">
              <CarteEquipe :equipe="equipe" :image="ImageFond" />
            </router-link>
            <button class="postuler-btn" :disabled="submittingId === equipe.idEquipe" @click="postuler(equipe.idEquipe)">
              {{ submittingId === equipe.idEquipe ? 'Envoi...' : 'Postuler' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
/* --- STYLE DU TOAST (BANDEAU VERT) --- */
.toast-notification {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #2ecc71;
  color: white;
  padding: 1rem 2rem;
  border-radius: 50px;
  box-shadow: 0 10px 25px rgba(46, 204, 113, 0.3);
  z-index: 9999;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  min-width: 300px;
  justify-content: center;
}

/* Animation de transition Vue */
.toast-enter-active, .toast-leave-active {
  transition: all 0.4s ease;
}
.toast-enter-from, .toast-leave-to {
  opacity: 0;
  bottom: 0px;
}

/* --- LE RESTE DES STYLES --- */
.equipes-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.title { text-align: center; font-size: 2rem; margin-bottom: 1rem; color: #2c3e50; }
.subtitle { color: #7f8c8d; margin-bottom: 2rem; text-align: center; }

.already-team-container {
  display: flex; flex-direction: column; align-items: center; background-color: #f8f9fa;
  padding: 2.5rem; border-radius: 12px; border: 1px solid #e0e0e0; text-align: center; gap: 1.5rem; max-width: 600px;
}

.action-btn-primary {
  padding: 0.8rem 1.5rem; background-color: #3498db; color: white; text-decoration: none; border-radius: 6px; font-weight: 600;
}

.equipes-list {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 2rem; width: 100%;
}

.equipe-wrapper { display: flex; flex-direction: column; align-items: center; }

.postuler-btn {
  margin-top: 1rem; width: 100%; padding: 0.6rem 1rem; background-color: #27ae60; color: white;
  border: none; border-radius: 6px; cursor: pointer; font-weight: 600; transition: all 0.2s;
}

.postuler-btn:hover:not(:disabled) { background-color: #219150; transform: scale(1.02); }
.postuler-btn:disabled { background-color: #95a5a6; cursor: not-allowed; opacity: 0.7; }

.state-msg { text-align: center; font-size: 1.1rem; color: #555; margin-top: 2rem; }
.state-msg.error { color: #e74c3c; }
.state-msg.info { background: #eef2ff; padding: 2rem; border-radius: 8px; color: #333; }
</style>