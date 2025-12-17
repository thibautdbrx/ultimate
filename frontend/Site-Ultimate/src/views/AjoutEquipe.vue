<script setup>
import { ref } from "vue"
import { useRouter } from 'vue-router'
import champs_input from "@/components/champs_input.vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import SelectJoueur from "@/components/SelectionJoueurOverlay.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import AjoutJoueurOverlay from "@/components/AjoutJoueurOverlay.vue";
import { useAuthStore } from "@/stores/auth";

const nomEquipe = ref("")
const descriptionEquipe = ref("")
const nombreJoueurs = ref(1)

const auth = useAuthStore();

if (!auth.isAdmin) {
  router.push("/");
}

// 20 joueurs
const joueurs = ref(
    Array.from({ length: 20 }, () => ({
      id: null, // joueur existant
      nom: "",
      prenom: "",
      genre: "",
      photoJoueur: null,
      clickable: false
    }))
)

// Gestion du modal
const modalIndex = ref(null) // quel joueur on a selectionner donc lequel modifier
const modalShow_1 = ref(false) //bool qui dit si c'est affiché ou non
const modalShow_2 = ref(false)

const openModal_1 = (i) => {
  if (!genre.value) {
    alert("Vous devez sélectionner un genre avant d'ajouter des équipes")
    return
  }
  modalIndex.value = i
  modalShow_1.value = true
  modalShow_2.value = false
}

const openModal_2 = () => {
  modalShow_1.value = false
  modalShow_2.value = true
}

const closemodalShow_2 = () => {
  modalShow_2.value = false

}

const selectExisting = (joueur) => {
  const j = joueurs.value[modalIndex.value] //je prend la carte du joueru sur lequel j'avais cliquer sur modifier
  j.id = joueur.idJoueur // et j'ajoute les valeurs du joueur selectionner dans l'overlay
  j.nom = joueur.nomJoueur
  j.prenom = joueur.prenomJoueur
  j.genre = joueur.genre
  j.photoJoueur = joueur.photoJoueur
  j.clickable = false
  modalShow_1.value = false

}

const router = useRouter()

const genre = ref("")

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

    for (let k = 0; k < joueursSelectionnes.length; k++) {
      if (k !== i && joueursSelectionnes[k].id === j.id) {
        alert(`Le joueur n°${i + 1} a été sélectionné deux fois.`);
        return;
      }
    }

  }

  try {
    // 3) Création de l'équipe
    const equipePayload = {
      nomEquipe: nomEquipe.value,
      description: descriptionEquipe.value,
      genre: genre.value
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
    router.push("/Equipe");

  } catch (e) {
    console.error(e);
    alert("Impossible de contacter le serveur");
  }
};


</script>

<template>
  <main v-if="auth.isAdmin" class="page-ajout">

    <h2>Ajouter une équipe</h2>
    <p id="sous-titre">Creer une équipe sans minimum un joueur dedans, vous pourrais la modifier plus tard</p>

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

      <label>Genre :</label>
      <select v-model="genre" class="select-genre">
        <option value="HOMME">HOMME</option>
        <option value="FEMME">FEMME</option>
        <option value="MIXTE">MIXTE</option>
      </select>


      <label>Nombre de joueurs :</label>
      <select v-model="nombreJoueurs" class="select-nb">
        <option v-for="n in 20" :value="n" >{{ n }}</option>
      </select>
    </div>

    <div class="joueurs-grid">
      <div
          v-for="(j, i) in joueurs"
          :key="i"
          v-show="i < nombreJoueurs"
          class="joueur-wrapper"
      >
        <button class="select-btn" @click="openModal_1(i)">
          Sélectionner un joueur
        </button>

        <JoueurCardForm :joueur="j" />
      </div>
    </div>
    <!-- les @ c les variables qu'on recoit de l'overlay quand il "emit" une donnée, donc une des trois -->.
    <SelectJoueur
        :show="modalShow_1"
        :genre="genre"
        @close="modalShow_1 = false"
        @select="selectExisting"
        @nvj="openModal_2"
    />
    <AjoutJoueurOverlay
        v-if="modalShow_2"
        @close="modalShow_2 = false"
        @created="closemodalShow_2"
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
  background: #1e88e5;
  color: white;
  cursor: pointer;
  width: 80%;

}

#sous-titre{
  font-size: 0.8rem;
  color: gray;
  text-align: center;
}

h2{
  text-align: center;
  font-size: 2rem;
  margin-bottom: 0rem;
}
</style>
