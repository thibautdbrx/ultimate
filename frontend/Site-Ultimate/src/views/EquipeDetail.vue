<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CardJoueur from "@/components/card_joueur.vue"

const route = useRoute()
const equipeId = route.params.id

const loading = ref(true)
const error = ref(null)

const equipe = ref(null)
const joueurs = ref([])

const auth = useAuthStore();

onMounted(async () => {
  try {
    //Récupérer l'équipe
    const resEquipe = await fetch(`/api/equipe/${equipeId}`)
    if (!resEquipe.ok) throw new Error("Erreur API équipe")

    equipe.value = await resEquipe.json()

    //Récupérer les joueurs de l'équipe
    const resJoueurs = await fetch(`/api/joueur/equipe/${equipeId}`)
    if (!resJoueurs.ok) throw new Error("Erreur API joueurs")

    joueurs.value = await resJoueurs.json()

    loading.value = false

  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les informations de l'équipe."
    loading.value = false
  }
})
</script>

<template>
  <main class="equipe-page">

    <!-- États -->
    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else>
      <!-- Titre -->
      <h2 class="titre">{{ equipe.nomEquipe }}</h2>

      <!-- Description -->
      <p class="description">
        {{ equipe.descriptionEquipe || "Aucune description disponible." }}
      </p>

      <!-- Grille des joueurs -->
      <div class="joueurs-grid">
        <CardJoueur
            v-for="j in joueurs"

            :nom="j.nomJoueur"
            :prenom="j.prenomJoueur"
            :genre="j.genre"
        />
      </div>
    </div>
  </main>
</template>

<style scoped>
.equipe-page {
  padding: 2rem;
  text-align: center;
}

.titre {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.description {
  max-width: 600px;
  margin: 0 auto 2rem auto;
  color: #666;
}

/* Grille responsive */
.joueurs-grid {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 2rem;
}

.state-msg {
  color: #666;
}
.state-msg.error {
  color: #c0392b;
}
</style>
