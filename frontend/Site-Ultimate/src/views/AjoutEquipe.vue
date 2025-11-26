<script setup>
import { ref } from "vue"
import { useRouter } from 'vue-router'
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
  j.id = joueur.idJoueur // et j'ajoute les valeurs du joueur selectionner dans l'overlay
  j.nom = joueur.nomJoueur
  j.prenom = joueur.prenomJoueur
  j.genre = joueur.genre
  j.clickable = false
  modalShow.value = false
}

const router = useRouter()

const goToNouveauJoueur = () =>{
  router.push('NouveauJoueur')
}

const valider_ajout_equipe = async () => {

  // --- 1) VALIDATION DES CHAMPS SIMPLES ---
  if (!nomEquipe.value.trim()) {
    alert("Le nom de l'équipe est obligatoire.");
    return;
  }

  if (nombreJoueurs.value <= 0) {
    alert("Vous devez sélectionner un nombre de joueurs.");
    return;
  }

  // --- 2) VALIDATION DES JOUEURS ---
  // On garde que les joueurs visibles (i < nombreJoueurs) le mx c'est 20
  const joueursSelectionnes = joueurs.value.slice(0, nombreJoueurs.value);

  for (let i = 0; i < joueursSelectionnes.length; i++) {
    const j = joueursSelectionnes[i];

    // Il faut un joueur existant
    if (!j.id) {
      alert(`Le joueur n°${i + 1} n'a pas été sélectionné.`);
      return;
    }
  }

  try {
    // 3) Création de l'équipe
    const equipePayload = {
      nomEquipe: nomEquipe.value,
      description: descriptionEquipe.value
    };

    const resEquipe = await fetch("/api/equipe", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(equipePayload)
    });

    if (!resEquipe.ok) {
      const err = await resEquipe.json();
      alert("Erreur création équipe : " + err.message);
      return;
    }

    const equipeCree = await resEquipe.json();
    const idEquipe = equipeCree.idEquipe;

    // 4) Affecter les joueurs à l'équipe
    await Promise.all(joueursSelectionnes.map(j =>
        fetch(`/api/joueur/${j.id}/equipe/${idEquipe}`, {
          method: "PATCH"
        })
    ));

    alert("Équipe et joueurs mis à jour avec succès !");
    router.push("/Equipes");

  } catch (e) {
    console.error(e);
    alert("Impossible de contacter le serveur");
  }
};


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
        @nvj="goToNouveauJoueur"
    />
    <div class="en_bas">
      <button class="valider_ajout" @click="valider_ajout_equipe">Ajouter l'équipe</button>

    </div>

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

.en_bas{
  display: flex;
  align-items: center;
  justify-content: center;
}

.valider_ajout{
  text-align: center;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  background: #f8e72f;
  color: white;
  cursor: pointer;
  width: 80%;

}
</style>
