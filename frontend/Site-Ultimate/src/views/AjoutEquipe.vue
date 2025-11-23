<script setup>
import { ref } from "vue"
import champs_input from "@/components/champs_input.vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import SelectJoueur from "@/components/SelectionJoueurOverlay.vue"
import UserIcon from "@/assets/icons/avatar.svg"

const nomEquipe = ref("")
const descriptionEquipe = ref("")
const nombreJoueurs = ref(0)

// 20 joueurs
const joueurs = ref(
    Array.from({ length: 20 }, () => ({
      id: null, // joueur existant
      nom: "",
      prenom: "",
      genre: "",
      photo: null,
      clickable: false
    }))
)

// Gestion du modal
const modalIndex = ref(null) // quel joueur on a selectionner donc lequel modifier
const modalShow = ref(false) //bool qui dit si c'est affiché ou non

const openModal = (i) => {
  modalIndex.value = i
  modalShow.value = true
}

const selectExisting = (joueur) => {
  const j = joueurs.value[modalIndex.value] //je prend la carte du joueru sur lequel j'avais cliquer sur modifier
  j.id = joueur.id // et j'ajoute les valeurs du joueur selectionner dans l'overlay
  j.nom = joueur.nomJoueur
  j.prenom = joueur.prenomJoueur
  j.genre = joueur.genre
  j.clickable = false
  modalShow.value = false
}

</script>

<template>
  <main class="page-ajout">

    <h2>Ajouter une équipe</h2>

    <div class="form-block">
      <champs_input
          label="Nom de l'équipe"
          v-model="nomEquipe"
          placeholder="Ex: Les Dragons"
          :icon="UserIcon"
      />

      <champs_input
          label="Description"
          v-model="descriptionEquipe"
          placeholder="Description de l'équipe"
      />

      <label>Nombre de joueurs :</label>
      <select v-model="nombreJoueurs" class="select-nb">
        <option v-for="n in 20" :value="n">{{ n }}</option>
      </select>
    </div>

    <div class="joueurs-grid">
      <div
          v-for="(j, i) in joueurs"
          :key="i"
          v-show="i < nombreJoueurs"
          class="joueur-wrapper"
      >
        <button class="select-btn" @click="openModal(i)">
          Sélectionner un joueur
        </button>

        <JoueurCardForm :joueur="j" />
      </div>
    </div>
    <!-- les @ c les variables qu'on recoit de l'overlay quand il "emit" une donnée, donc une des trois -->.
    <SelectJoueur
        :show="modalShow"
        @close="modalShow = false"
        @select="selectExisting"
    />

  </main>
</template>

<style scoped>
.page-ajout {
  padding: 2rem;
}
.form-block {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}
.select-nb {
  padding: 0.6rem;
  margin-right: 90%;
  border-radius: 8px;

}
.joueurs-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
}
.joueur-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.select-btn {
  padding: 0.5rem;
  border: none;
  border-radius: 8px;
  background: #1e88e5;
  color: white;
  cursor: pointer;
}
</style>
