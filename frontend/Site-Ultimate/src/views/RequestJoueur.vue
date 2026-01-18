<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ImageFond from "../assets/img/img_equipe.jpg"
import CarteEquipe from "@/components/card_equipe.vue"
import { useAuthStore } from "@/stores/auth.js"
const auth = useAuthStore()

const router = useRouter()

const equipes = ref([])
const loading = ref(true)
const error = ref(null)

const userRole = ref(null)
const userJoueurId = ref(null) // récupère l'id du joueur depuis le token

const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('token='))
    ?.split('=')[1]

onMounted(() => {
  // récupérer l'id du joueur et le rôle depuis le cookie ou le store
  const token = document.cookie
      .split('; ')
      .find(row => row.startsWith('token='))
      ?.split('=')[1]

  if (token) {
    const payload = JSON.parse(atob(token.split('.')[1]))
    userRole.value = payload.role
    userJoueurId.value = payload.joueurId
  }

  fetch(`/api/equipe/open?idJoueur=${userJoueurId.value}`)
      .then(res => {
        if (!res.ok) throw new Error(`Erreur HTTP: ${res.status}`)
        return res.json()
      })
      .then(data => {
        equipes.value = data
        loading.value = false
      })
      .catch(err => {
        console.error(err)
        error.value = "Impossible de charger les équipes."
        loading.value = false
      })
})

// fonction pour postuler
async function postuler(equipeId) {
  if (!userJoueurId.value || !token) {
    alert("Impossible de postuler : joueur non identifié.")
    return
  }

  try {
    const response = await fetch(`/api/joueur/request/${userJoueurId.value}/equipe/${equipeId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` // <-- ici
      }
    })

    if (!response.ok) {
      const err = await response.json()
      alert(`Erreur : ${err.message}`)
      return
    }

    const data = await response.json()

    // Supprimer l'équipe de la liste sans recharger la page
    equipes.value = equipes.value.filter(e => e.idEquipe !== equipeId)

    alert("Postulation envoyée avec succès !")
    console.log(data)
  } catch (err) {
    console.error(err)
    alert("Erreur lors de la postulation.")
  }
}

function goToEquipe(id, nom) {
  router.push({ name: 'Equipe-details', params: { id, nom } })
}
</script>

<template>
  <main class="equipes">
    <h2 class="title">Liste des équipes</h2>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else>
      <div v-if="equipes.length === 0" class="state-msg">
        Aucune équipe disponible à rejoindre pour le moment.
      </div>

      <div v-else class="competition-list">
        <div
            v-for="equipe in equipes"
            :key="equipe.idEquipe"
            class="equipe-card-container"
        >
          <CarteEquipe :equipe="equipe" :image="ImageFond" />
          <button
            v-if="auth.isVisiteur || auth.isAdmin || auth.isArbitre"
            class="postuler-btn"
            @click="postuler(equipe.idEquipe)"
          >
            Postuler
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.equipes {
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

.state-msg {
  color: #555;
}

.state-msg.error {
  color: #e74c3c;
}

.competition-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
  width: 100%;
  max-width: 1000px;
}

.equipe-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.postuler-btn {
  margin-top: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.2s;
}

.postuler-btn:hover {
  background-color: #27ae60;
}
</style>