<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ImageFond from "../assets/img/img_equipe.jpg"
import CarteEquipe from "@/components/card/card_equipe.vue"
import api from '@/services/api' // On utilise ton instance Axios configurée
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

// Fonction pour afficher le toast
function triggerToast(message) {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 4000)
}

// Fonction de secours pour récupérer l'ID si le store est vide (page refresh)
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
  // Récupération de l'ID via le store ou le cookie
  let currentJoueurId = auth.joueurId || getJoueurIdFromCookie()

  if (!currentJoueurId) {
    router.push('/Connexion')
    return
  }

  try {
    loading.value = true

    // 1. On récupère les infos du joueur (avec Axios)
    const resJoueur = await api.get(`/joueur/${currentJoueurId}`)
    const joueur = resJoueur.data

    // Si le joueur a déjà une équipe
    if (joueur.equipe) {
      existingTeam.value = joueur.equipe
      loading.value = false
      return
    }

    // 2. Sinon, on récupère les équipes ouvertes (avec Axios)
    // On passe l'ID en paramètre propre (params)
    const resEquipes = await api.get('/equipe/open', {
      params: { idJoueur: currentJoueurId }
    })
    equipes.value = resEquipes.data

  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les données."
  } finally {
    loading.value = false
  }
})

async function postuler(equipeId) {
  let currentJoueurId = auth.joueurId || getJoueurIdFromCookie()

  // 1. On récupère le token proprement depuis les cookies
  let token = auth.token
  if (!token) {
    const cookieToken = document.cookie.split('; ').find(row => row.startsWith('token='))
    if (cookieToken) token = cookieToken.split('=')[1]
  }

  if (!currentJoueurId || !token) {
    triggerToast("Erreur d'authentification : Token manquant.")
    return
  }

  submittingId.value = equipeId

  try {
    // 2. On fait l'appel AXIOS en forçant le header ici
    // Syntaxe : api.post(URL, BODY, CONFIG)
    await api.post(
        `/joueur/request/${currentJoueurId}/equipe/${equipeId}`,
        {}, // <--- Body vide obligatoire
        {
          headers: {
            'Authorization': `Bearer ${token}` // <--- On force le token ici
          }
        }
    )

    // Mise à jour locale
    equipes.value = equipes.value.filter(e => e.idEquipe !== equipeId)
    triggerToast("Votre postulation a bien été envoyée !")

  } catch (err) {
    console.error(err)
    // On affiche le vrai message d'erreur du serveur s'il existe
    const msg = err.response?.data?.message || err.message || "Erreur inconnue"
    triggerToast("Erreur : " + msg)
  } finally {
    submittingId.value = null
  }
}
</script>

<template>
  <main class="equipes-page">

    <Transition name="toast">
      <div v-if="showToast" class="toast-notification">
        <span v-if="toastMessage.includes('Erreur')">✖</span>
        <span v-else>✔</span>
        {{ toastMessage }}
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

            <button
                class="postuler-btn"
                :disabled="submittingId === equipe.idEquipe"
                @click="postuler(equipe.idEquipe)"
            >
              {{ submittingId === equipe.idEquipe ? 'Envoi...' : 'Postuler' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
/* --- STYLE DU TOAST (BANDEAU) --- */
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
  transform: translate(-50%, 20px);
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
  display: grid;
  grid-template-columns: repeat(4, 1fr); 
  gap: 1.5rem;
  width: 100%;
  max-width: 1000px;
  align-items: stretch; 
}

.equipe-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  /* Permet au wrapper de prendre toute la hauteur de la ligne de la grille */
  height: 100%; 
  background: white; /* Optionnel : si vous voulez un fond uniforme */
  padding-bottom: 1rem; /* Espace sous le bouton */
}

.card-link {
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s;
  /* Ces deux lignes sont cruciales : */
  flex-grow: 1; 
  display: flex;
  flex-direction: column;
  width: 100%;
}

.card-link :deep(.card) { 
  height: 100%;
}

.card-link:hover { transform: translateY(-5px); }

.postuler-btn {
  margin-top: 1rem; width: 100%; padding: 0.6rem 1rem; background-color: #27ae60; color: white;
  border: none; border-radius: 6px; cursor: pointer; font-weight: 600; transition: all 0.2s;
  margin-bottom: 2em;
}

.postuler-btn:hover:not(:disabled) { background-color: #219150; transform: scale(1.02); }
.postuler-btn:disabled { background-color: #95a5a6; cursor: not-allowed; opacity: 0.7; }

.state-msg { text-align: center; font-size: 1.1rem; color: #555; margin-top: 2rem; }
.state-msg.error { color: #e74c3c; }
.state-msg.info { background: #eef2ff; padding: 2rem; border-radius: 8px; color: #333; width: 100%; }
</style>