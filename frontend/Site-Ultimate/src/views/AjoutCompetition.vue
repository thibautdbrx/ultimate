<script setup>
import { ref } from "vue"
import { useRouter } from 'vue-router'
import champs_input from "@/components/champs_input.vue"
import SelectEqiupe from "@/components/SelectionEquipeOverlay.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import image from "@/assets/img/img_equipe.jpg"
import { useAuthStore } from "@/stores/auth";


const nomCompetition = ref("")
const nomEquipe = ref("")
const dateDebut = ref("")
const dateFin = ref("")
const DescriptionCompetition = ref("")
const nombreEquipe = ref(0)
const format = ref("")
const genre = ref("")

const auth = useAuthStore();

// 20 joueurs
const equipes = ref(
    Array.from({ length: 20 }, () => ({
      idEquipe: null,
      nomEquipe: "Equipe",
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

const selectExisting = (equipe) => {
  const e = equipes.value[modalIndex.value] //je prend la carte du joueru sur lequel j'avais cliquer sur modifier
  e.idEquipe = equipe.idEquipe // et j'ajoute les valeurs du joueur selectionner dans l'overlay
  e.nomEquipe = equipe.nomEquipe
  modalShow_1.value = false
}

const router = useRouter()

function toSimpleDate(dateString) {
  return dateString ?? null;
}


const valider_ajout_equipe = async () => {

  // --- 1) VALIDATION DES CHAMPS SIMPLES ---
  if (!nomCompetition.value.trim()) {
    alert("Le nom de l'équipe est obligatoire.");
    return;
  }

  if (nombreEquipe.value <= 0) {
    alert("Vous devez sélectionner une équipe.");
    return;
  }

  // On garde que les equipe visibles (i < equipe) le mx c'est 20
  const equipesSelectionnes = equipes.value.slice(0, nombreEquipe.value);

  for (let i = 0; i < equipesSelectionnes.length; i++) {
    const e = equipesSelectionnes[i];

    // Il faut une equipe existante
    if (!e.idEquipe) {
      alert(`L'équipe n°${i + 1} n'a pas été sélectionné.`);
      return;
    }

    for (let k = 0; k < equipesSelectionnes.length; k++) {
      if (k !== i && equipesSelectionnes[k].idEquipe === e.idEquipe) {
        alert(`L'équipe' n°${i + 1} a été sélectionné deux fois.`);
        return;
      }
    }

  }

  try {
    // 3) Création de la compétition
    const tournoisPayload = {
      genre: genre.value,
      format: format.value,
      dateDebut: toSimpleDate(dateDebut.value),
      dateFin :  toSimpleDate(dateFin.value),
      nomCompetition: nomCompetition.value,
      descriptionCompetition: DescriptionCompetition.value
    };

    const resTournois = await fetch("/api/tournois", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(tournoisPayload)
    });

    if (!resTournois.ok) {
      const err = await resTournois.json();
      alert("Erreur création équipe : " + err.message);
      return;
    }

    const tournoisCree = await resTournois.json();
    const idCompetition = tournoisCree.idCompetition;

    // 4) Affecter les équipe à la competitions
    await Promise.all(equipesSelectionnes.map(e =>
        fetch(`api/participation/equipe/${e.idEquipe}/competition/${idCompetition}`, {
          method: "POST"
        })
    ));

    alert("Équipe et Competitions mis à jour avec succès !");
    router.push("/Competition");

  } catch (e) {
    console.error(e);
    alert("Impossible de contacter le serveur");
  }
};


</script>

<template>
  <main v-if="auth.isAdmin" class="page-ajout">

    <h2>Ajouter une compétitions</h2>
    <p id="sous-titre">Creer une compétitions, vous pourrais la modifier plus tard</p>

    <div class="form-block">
      <champs_input
          label="Nom de compétitions"
          v-model="nomCompetition"
          placeholder="Ex: Tournois Saint Gobain cup"
          :icon="UserIcon"
      />
      <champs_input
          label="Description de la compétitions"
          v-model="DescriptionCompetition"
          placeholder="Ex: Tournois Ultimate National école d'ingenieur"
          :icon="UserIcon"
      />

      <label>Format :</label>
      <select v-model="format" class="select-format">
        <option value="V5">V5</option>
        <option value="V7">V7</option>
      </select>

      <label>Genre :</label>
      <select v-model="genre" class="select-genre">
        <option value="MALE">HOMME</option>
        <option value="FEMALE">FEMME</option>
        <option value="MIXTE">MIXTE</option>
      </select>

      <label>Date debut :</label>

      <input
          type="date"
          v-model="dateDebut"
          class="select-format"
      />


      <label>Date fin :</label>
      <input
          type="date"
          v-model="dateFin"
          class="select-format"
      />
      <label>Nombre équipes engagées (modifiable plus tard):</label>
      <select v-model="nombreEquipe" class="select-nb">
        <option v-for="n in 20" :value="n">{{ n }}</option>
      </select>

    </div>

    <div class="joueurs-grid">
      <div
          v-for="(equipe, i) in equipes"
          :key="i"
          v-show="i < nombreEquipe"
          class="joueur-wrapper"
      >
        <button class="select-btn" @click="openModal_1(i)">
          Sélectionner une équipe
        </button>

        <div class="competition-card">
          <img :src="image" alt="Image équipe" class="competition-img" />

          <div class="competition-info">
            <h3>{{ equipe.nomEquipe }}</h3>
          </div>
        </div>
      </div>

    </div>
    <SelectEqiupe
        :show="modalShow_1"
        :genre="genre"
        @close="modalShow_1 = false"
        @select="selectExisting"
        @nvj="openModal_2"
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
