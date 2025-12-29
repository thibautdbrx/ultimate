<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CardJoueur from "@/components/card_joueur.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import champ_input from "@/components/champs_input.vue"
import SelectJoueur from "@/components/SelectionJoueurOverlay.vue"

const route = useRoute()
const equipeId = route.params.id
const dateDebut = ref("")
const dateFin = ref("")
const indispos = ref([])


const loading = ref(true)
const error = ref(null)
const editMode = ref(false) // pour activer l'affichage des boutons supprimer
const indispoMode = ref(false)// pour activer l'affichage d'ajout d'indispo

const equipe = ref(null)
const joueurs = ref([])
const modalShow_1 = ref(false)
const modalIndex = ref()

const nomEquipe = ref("")
const descriptionEquipe = ref("")
const genre = ref("")


onMounted(async () => {
  try {
    //Récupérer l'équipe
    const resEquipe = await fetch(`/api/equipe/${equipeId}`)
    if (!resEquipe.ok) throw new Error("Erreur API équipe")
    equipe.value = await resEquipe.json()
    nomEquipe.value = equipe.value.nomEquipe
    genre.value = equipe.value.genre
    //Récupérer les joueurs de l'équipe
    const resJoueurs = await fetch(`/api/joueur/equipe/${equipeId}`)
    if (!resJoueurs.ok) throw new Error("Erreur API joueurs")
    joueurs.value = await resJoueurs.json()
    await get_indispo()
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

const get_indispo = async () => {
  try {
    const res = await fetch(`/api/indisponibilite/equipe/${equipeId}`)
    if (!res.ok) throw new Error("Erreur API indisponibilités")
    indispos.value = await res.json()
  } catch (err) {
    console.error(err)
    error.value = "Impossible de charger les indisponibilités."
  }
}

const ajouterIndispo = async () => {
  if (!dateDebut.value || !dateFin.value) {
    alert("Veuillez renseigner les deux dates")
    return
  }

  const dateDebutFormatted = formatDateTimeSafe(dateDebut.value)
  const dateFinFormatted = formatDateTimeSafe(dateFin.value)

  if (!dateDebutFormatted || !dateFinFormatted) return

  try {
    const res = await fetch(`/api/indisponibilite`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        idEquipe: equipeId,
        dateDebut: dateDebutFormatted,
        dateFin: dateFinFormatted
      })
    })

    if (!res.ok) throw new Error("Erreur ajout indisponibilité")

    const newIndispo = await res.json()
    indispos.value.push(newIndispo)

    dateDebut.value = ""
    dateFin.value = ""
  } catch (err) {
    console.error(err)
    alert("Impossible d'ajouter l'indisponibilité")
  }
}


  const supprimerIndispo = async (id, index) => {
    if (!confirm("Supprimer cette indisponibilité ?")) return

    try {
      const res = await fetch(`/api/indisponibilite/${id}`, {
        method: "DELETE"
      })

      if (!res.ok) throw new Error("Erreur suppression")

      indispos.value.splice(index, 1)
    } catch (err) {
      console.error(err)
      alert("Impossible de supprimer l'indisponibilité")
    }
  }


  const valider_titre_desc = async () => {
    if (confirm(`changer le nom ou la desciption de l'équipe ?`)) {
      console.log(nomEquipe.value)
      try {
        const modif_nom = await fetch(`/api/equipe/${equipeId}/name`, {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            nom: nomEquipe.value,
            description: descriptionEquipe.value
          })
        });
        equipe.value.nomEquipe = nomEquipe.value;
        equipe.value.descriptionEquipe = descriptionEquipe.value;

        if (!modif_nom.ok) {
          throw new Error("Erreur lors de la modification du nom ou de la description de l'équipe")
        }
      } catch (err) {
        console.error(err)
      }
    }
  }


  const openModal_1 = () => {
    modalIndex.value = joueurs.length
    modalShow_1.value = true
  }


// Activer / désactiver le mode édition
  const toggleEditMode = () => {
    editMode.value = !editMode.value
  }

  const toggleindispoMode = () => {
    indispoMode.value = !indispoMode.value
  }

  const selectExisting = async (joueur) => {
    try {
      // 1. Appeler l’API pour associer le joueur à l’équipe
      const res = await fetch(`/api/joueur/${joueur.idJoueur}/equipe/${equipeId}`, {
        method: "PATCH"
      })

      if (!res.ok) {
        throw new Error("Erreur lors de l'ajout du joueur à l'équipe")
      }

      // 2. Ajouter le joueur dans la liste locale
      joueurs.value.push({
        idJoueur: joueur.idJoueur,
        nomJoueur: joueur.nomJoueur,
        prenomJoueur: joueur.prenomJoueur,
        genre: joueur.genre,
        photoJoueur: joueur.photoJoueur
      })
      alert("joueur bien ajouté dans la base de donnée")
      // 3. Fermer la modale
      modalShow_1.value = false

    } catch (err) {
      console.error(err)
      alert("Impossible d’ajouter le joueur à l’équipe.")
    }
  }

  const formatDateTimeSafe = (value) => {
    const regex = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/

    if (!regex.test(value)) {
      alert("Format de date invalide (YYYY-MM-DD HH:mm attendu)")
      return null
    }

    return value.replace("T", " ")

}


</script>

<template>
  <main class="equipe-page">

    <!-- États -->
    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else>
      <button class="btn" @click="toggleEditMode">
        {{ editMode ? "Quitter la modification" : "Modifier" }}
      </button>
      <button class="btn" @click="toggleindispoMode">
        {{ indispoMode ? "Quitter l'ajout d'indisponibilité " : "ajouter une indisponibilité" }}
      </button>


      <h2 v-if="!editMode" class="titre">{{ equipe.nomEquipe }}</h2>
      <div class="modif">
        <champ_input
            class="input"
            label="Nom équipe"
            v-if="editMode"
            v-model="nomEquipe"
            :placeholder="equipe.nomEquipe"
            :icon="UserIcon"
        />

        <button
            v-show="editMode"
            class="btn"
            @click="valider_titre_desc"
        >
          Valider
        </button>

      </div>


      <p v-if="!editMode" class="description">
        {{ equipe.descriptionEquipe || "Aucune description disponible." }}
      </p>


      <div v-if="indispoMode" class="date_indispo">
        <p>Veuillez choisir une date de début et une date de fin d'indisponibilité</p>

        <label >Date début :</label>
        <input
            type="datetime-local"
            v-model="dateDebut"
            class="select-format"
        />

        <label>Date fin :</label>
        <input
            type="datetime-local"
            v-model="dateFin"
            class="select-format"
        />

        <button class="btn" @click="ajouterIndispo">
          Ajouter
        </button>



        <h3>Indisponibilités existantes</h3>

        <div
            v-for="(indispo, i) in indispos"
            :key="indispo.idIndisponibilite"
            class="joueur-wrapper"
        >
          <p>
             {{ indispo.dateDebut }} → {{ indispo.dateFin }}
          </p>

          <button
              class="btn-supprimer"
              @click="supprimerIndispo(indispo.idIndisponibilite, i)"
          >
            Supprimer
          </button>
        </div>
      </div>














      <div class="modif">
        <champ_input
            class="input"
            label="Description"
            v-if="editMode"
            v-model="descriptionEquipe"
            :placeholder="equipe.descriptionEquipe"
            :icon="UserIcon"
        />

        <button
            v-show="editMode"
            class="btn"
            @click="valider_titre_desc"
        >
          Valider
        </button>

      </div>
      <button v-if="editMode" class="btn" id="ajouter_j" @click="openModal_1()">
        Ajouter un joueur
      </button>
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
              :photo="j.photoJoueur"
          />
        </div>
        <SelectJoueur
          :show="modalShow_1"
          :genre="genre"
          @close="modalShow_1 = false"
          @select="selectExisting"
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
  align-items: center;
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

.btn {
  background-color: #333;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
  margin: 0.5rem;
}

.btn:hover {
  background-color: #555;
}

.modif{
  display: flex;
  flex-direction: row;
  align-items: center;
  align-items: flex-end;
  justify-content: center;
}
.input {
  width: 30%;
}

#ajouter_j{
  width: 10rem;
  height: 3rem;
}

.date_indispo{
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
