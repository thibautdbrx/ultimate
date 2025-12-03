<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CardJoueur from "@/components/card_joueur.vue"
import UserIcon from "@/assets/icons/avatar.svg"

const route = useRoute()
const equipeId = route.params.id

const loading = ref(true)
const error = ref(null)
const editMode = ref(false) // pour activer l'affichage des boutons supprimer

const equipe = ref(null)
const joueurs = ref([])


const nomEquipe = ref("")

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
    console.log(joueurs.value)
    loading.value = false
  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les informations de l'équipe."
    loading.value = false
  }
})

// Supprimer un joueur
const supprimerJoueur = async (index) => {
  if (confirm(`Supprimer ${joueurs.value[index].nomJoueur} ?`)) {
    const suppJ = await fetch(`/api/joueur/${joueurs.value[index].idJoueur}/equipe/${equipeId}`, {
      method: "DELETE",
    });
    joueurs.value.splice(index, 1)
  }
}

// Activer / désactiver le mode édition
const toggleEditMode = () => {
  editMode.value = !editMode.value
}
</script>

<template>
  <main class="equipe-page">

    <!-- États -->
    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else>
      <button @click="toggleEditMode">
        {{ editMode ? "Annuler la modification" : "Modifier" }}
      </button>


      <h2 v-if="!editMode" class="titre">{{ equipe.nomEquipe }}</h2>
      <champs_input
          label=""
          v-if="editMode"
          v-model="nomEquipe"
          :placeholder="equipe.nomEquipe"
          :icon="UserIcon"
          class="titre"
      />

      <p class="description">
        {{ equipe.descriptionEquipe || "Aucune description disponible." }}
      </p>

      <div class="joueurs-grid">
        <div v-for="(j, i) in joueurs" :key="i" class="joueur-wrapper">
          <button
              v-show="editMode"
              class="btn-supprimer"
              @click="supprimerJoueur(i)"
          >
            Supprimer
          </button>

          <CardJoueur
              :nom="j.nomJoueur"
              :prenom="j.prenomJoueur"
              :genre="j.genre"
          />
        </div>
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

.joueur-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.btn-supprimer {
  background: #e53935;
  color: white;
  border: none;
  padding: 0.3rem 0.6rem;
  border-radius: 8px;
  cursor: pointer;
}

.state-msg {
  color: #666;
}
.state-msg.error {
  color: #c0392b;
}
</style>
