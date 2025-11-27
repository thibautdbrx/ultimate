  <script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ImageFond from "../assets/img/img_equipe.jpg"

const router = useRouter()

// Liste des compétitions récupérées depuis ton API
const equipes = ref([])

const loading = ref(true)
const error = ref(null)
const isAdmin = ref(false)
const isArbitre = ref(false)

// Récupération des compétitions
onMounted(() => {

  const cookies = document.cookie.split('; ');
  const roleCookie = cookies.find(row => row.startsWith('user_role='));

  if (roleCookie && roleCookie.split('=')[1] === 'ADMIN') {
    isAdmin.value = true;
  } else if (roleCookie && roleCookie.split('=')[1] === 'ARBITRE'){
    isArbitre.value = true;
  }

  fetch('/api/equipe')
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
        error.value = "Impossible de charger les compétitions."
        loading.value = false
      })
})



// Redirection vers la page d'une compétition
function goToEquipe(id,nom) {
  router.push({ name: 'Equipe-details', params: { id, nom } })
}
</script>


<template>
  <main class="equipes">
    <h2 class="title">Liste des équipes</h2>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>
    <div v-else class="competition-list">
      <div
          v-for="equipe in equipes"
          :key="equipe.idEquipe"
          class="competition-card"
          @click="goToEquipe(equipe.idEquipe, equipe.nomEquipe)"
      >
        <img :src="ImageFond" alt="Image compétition" class="competition-img" />
        <div class="competition-info">
          <h3>{{ equipe.nomEquipe }}</h3>
          <p>{{ equipe.nbJoueur }} licencié(e)s</p>
        </div>
      </div>
    </div>
    <p v-if="isAdmin">Admin</p>
    <p v-if="isArbitre">Arbitre</p>
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

.competition-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  overflow: hidden;
}

.competition-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 5px 10px rgba(0,0,0,0.15);
}

.competition-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.competition-info {
  padding: 1rem;
  text-align: center;
}
</style>
