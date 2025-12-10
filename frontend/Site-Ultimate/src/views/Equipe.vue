<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ImageFond from "../assets/img/img_equipe.jpg"
import CarteEquipe from "@/components/card_equipe.vue"
import {useAuthStore} from "@/stores/auth.js";
const auth = useAuthStore();

const router = useRouter()

const equipes = ref([])
const loading = ref(true)
const error = ref(null)

const isAdmin = ref(false)
const isArbitre = ref(false)

onMounted(() => {
  const roleCookie = document.cookie
      .split('; ')
      .find(row => row.startsWith('user_role='))

  const role = roleCookie?.split('=')[1]

  isAdmin.value = role === 'ADMIN'
  isArbitre.value = role === 'ARBITRE'

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
        error.value = "Impossible de charger les équipes."
        loading.value = false
      })
})

function goToEquipe(id, nom) {
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
          @click="goToEquipe(equipe.idEquipe, equipe.nomEquipe)"
      >
        <CarteEquipe :equipe="equipe" :image="ImageFond" />
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
</style>
