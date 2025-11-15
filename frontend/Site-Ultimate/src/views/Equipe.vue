<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ImageFond from "../assets/img/img_equipe.jpg"

const router = useRouter()

// Liste des compétitions récupérées depuis ton API
const equipes = ref<{ id: number; name: string; nb_joueur: number; image: string }[]>([])

const loading = ref(true)
const error = ref<string | null>(null)

// Récupération des compétitions
onMounted(() => {
  fetch('https://api.exemple.com/equipes')
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
        //error.value = "Impossible de charger les compétitions."
        //loading.value = false
      })
})

equipes.value = [
  {id: 1, name: 'Polypote', nb_joueur:20, image: ImageFond},
  {id: 2, name: 'PSG',nb_joueur:79,image: ImageFond},
  {id: 3, name: 'prout deluxe',nb_joueur:1, image: ImageFond},
  {id: 4, name: 'tim2spor', nb_joueur: 10, image: ImageFond}

]
loading.value = false

// Redirection vers la page d'une compétition
function goToEquipe(id: number) {
  router.push({ name: 'Equipe-details', params: { id } })
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
          :key="equipe.id"
          class="competition-card"
          @click="goToEquipe(equipe.id)"
      >
        <img :src="equipe.image" alt="Image compétition" class="competition-img" />
        <div class="competition-info">
          <h3>{{ equipe.name }}</h3>
          <p>{{ equipe.nb_joueur }} licencié(e)s</p>
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
