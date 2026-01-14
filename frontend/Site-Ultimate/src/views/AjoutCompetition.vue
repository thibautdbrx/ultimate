<script setup>
import { ref, computed } from "vue"
import { useRouter } from 'vue-router'
import champs_input from "@/components/champs_input.vue"
import SelectEquipe from "@/components/SelectionEquipeOverlay.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import imgEquipeDefault from "@/assets/img/img_equipe.jpg"
import { useAuthStore } from "@/stores/auth";

const nomCompetition = ref("")
const descriptionCompetition = ref("")
const typeCompetition = ref("")
const dateDebut = ref("")
const dateFin = ref("")

const categorie = ref("")
const format = ref("")
const mixite = ref("")
const nombreEquipe = ref(0)

const auth = useAuthStore();
const router = useRouter();

if (!auth.isAdmin) {
  router.push("/")
}

// Emplacements pour 20 équipes
const equipes = ref(
    Array.from({ length: 20 }, () => ({
      idEquipe: null,
      nomEquipe: "Vide",
    }))
)

const modalIndex = ref(null)
const modalShow_1 = ref(false)

const genreFilter = computed(() => {
  if (categorie.value === 'MIXTE') return mixite.value;
  return categorie.value;
})

const openModal_1 = (i) => {
  if (!categorie.value || !format.value) {
    alert("Veuillez sélectionner une catégorie et un format avant d'ajouter des équipes.")
    return
  }
  modalIndex.value = i
  modalShow_1.value = true
}

const selectExisting = (equipe) => {
  const e = equipes.value[modalIndex.value]
  e.idEquipe = equipe.idEquipe
  e.nomEquipe = equipe.nomEquipe
  modalShow_1.value = false
}

const valider_ajout_competition = async () => {
  // --- 1) VALIDATIONS DES CHAMPS OBLIGATOIRES ---
  if (!nomCompetition.value.trim()) {
    alert("Le nom de la compétition est obligatoire.");
    return;
  }
  if (!typeCompetition.value) {
    alert("Veuillez choisir le type de compétition (Tournoi ou Championnat).");
    return;
  }
  if (!categorie.value) {
    alert("Veuillez sélectionner une catégorie.");
    return;
  }
  if (!format.value) {
    alert("Veuillez sélectionner un format de jeu.");
    return;
  }
  if (categorie.value === 'MIXTE' && !mixite.value) {
    alert("Pour une compétition MIXTE, veuillez préciser la répartition imposée.");
    return;
  }

  // --- 2) VALIDATION DES DATES ---
  if (!dateDebut.value || !dateFin?.value || dateDebut.value === "" || dateFin.value === "") {
    alert("Les dates de début et de fin sont obligatoires.");
    return;
  }

  const debut = new Date(dateDebut.value);
  const fin = new Date(dateFin.value);

  if (fin < debut) {
    alert("La date de fin ne peut pas être antérieure à la date de début.");
    return;
  }

  // --- 3) VALIDATION DES ÉQUIPES ---
  const equipesSelectionnees = equipes.value.slice(0, nombreEquipe.value);

  // On ne valide que s'il y a des slots d'équipes affichés (nombreEquipe > 0)
  if (nombreEquipe.value > 0) {
    for (let i = 0; i < equipesSelectionnees.length; i++) {
      const e = equipesSelectionnees[i];
      if (!e.idEquipe) {
        alert(`L'équipe n°${i + 1} n'a pas été sélectionnée.`);
        return;
      }

      // Vérification des doublons
      for (let k = 0; k < equipesSelectionnees.length; k++) {
        if (k !== i && equipesSelectionnees[k].idEquipe === e.idEquipe) {
          alert(`L'équipe "${e.nomEquipe}" est sélectionnée plusieurs fois.`);
          return;
        }
      }
    }
  }

  // --- 4) ENVOI DES DONNÉES ---
  try {
    const genreFinal = categorie.value === 'MIXTE' ? mixite.value : categorie.value;
    const formatTexte = format.value === '5v5' ? 'V5' : 'v7';

    const payload = {
      genre: genreFinal,
      format: formatTexte,
      dateDebut: dateDebut.value,
      dateFin: dateFin.value,
      nomCompetition: nomCompetition.value,
      descriptionCompetition: descriptionCompetition.value
    };

    const endpoint = typeCompetition.value === "TOURNOI"
        ? "/api/competition/tournoi"
        : "/api/competition/championnat";

    const res = await fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message);
    }

    const competitionCree = await res.json();
    const idComp = competitionCree.idCompetition;

    // Ajout des participations
    const equipesRemplies = equipesSelectionnees.filter(e => e.idEquipe !== null);
    if (equipesRemplies.length > 0) {
      await Promise.all(
          equipesRemplies.map(e =>
              fetch(`/api/participation/equipe/${e.idEquipe}/competition/${idComp}`, {
                method: "POST"
              })
          )
      );
    }

    alert("Compétition créée avec succès !");
    router.push("/Competition");
  } catch (e) {
    alert("Erreur : " + e.message);
  }
};
</script>
<template>
  <main v-if="auth.isAdmin" class="page-ajout">
    <h2>Ajouter une compétition</h2>
    <p id="sous-titre">Configurez les paramètres et engagez les équipes.</p>

    <div class="form-block">
      <champs_input label="Nom de la compétition" v-model="nomCompetition" placeholder="Ex: Open de France" :icon="UserIcon" />
      <champs_input label="Description" v-model="descriptionCompetition" placeholder="Détails de l'événement..." :icon="UserIcon" />

      <label>Type :</label>
      <select v-model="typeCompetition" class="select-genre">
        <option disabled value="">Choisir un type</option>
        <option value="TOURNOI">Tournoi</option>
        <option value="CHAMPIONNAT">Championnat</option>
      </select>

      <label>Catégorie :</label>
      <select v-model="categorie" class="select-genre">
        <option disabled value="">Choisir une catégorie</option>
        <option value="OPEN">OPEN</option>
        <option value="MIXTE">MIXTE</option>
        <option value="FEMME">FEMME</option>
      </select>

      <label>Format :</label>
      <select v-model="format" class="select-genre">
        <option disabled value="">Choisir un format</option>
        <option value="5v5">5 vs 5</option>
        <option value="7v7">7 vs 7</option>
      </select>

      <div v-if="categorie === 'MIXTE'" class="selection-categorie">
        <label>Mixité imposée :</label>
        <select v-if="format === '5v5'" v-model="mixite" class="select-genre">
          <option value="H3F2">3 Hommes / 2 Femmes (H3F2)</option>
          <option value="H2F3">2 Hommes / 3 Femmes (H2F3)</option>
        </select>
        <select v-else-if="format === '7v7'" v-model="mixite" class="select-genre">
          <option value="H3F4">3 Hommes / 4 Femmes (H3F4)</option>
          <option value="H4F3">4 Hommes / 3 Femmes (H4F3)</option>
        </select>
        <p v-else id="sous-titre" style="color: orange;">Veuillez choisir un format.</p>
      </div>

      <div class="dates-row">
        <div class="date-item">
          <label>Date début :</label>
          <input type="date" v-model="dateDebut" class="select-genre" />
        </div>
        <div class="date-item">
          <label>Date fin :</label>
          <input type="date" v-model="dateFin" class="select-genre" />
        </div>
      </div>

      <label>Nombre d'équipes engagées (OPTIONNEL) :</label>
      <select v-model="nombreEquipe" class="select-nb">
        <option :value="0">Plus tard</option>
        <option v-for="n in 20" :key="n" :value="n">{{ n }} équipes</option>
      </select>
    </div>

    <div class="joueurs-flex">
      <div v-for="(e, i) in equipes" :key="i" v-show="i < nombreEquipe" class="joueur-wrapper">
        <button class="select-btn" @click="openModal_1(i)">
          {{ e.idEquipe ? 'Changer' : 'Sélectionner équipe ' + (i+1) }}
        </button>
        <div class="competition-card" :class="{ 'empty-card': !e.idEquipe }">
          <img :src="imgEquipeDefault" class="competition-img" />
          <div class="competition-info"><h3>{{ e.nomEquipe }}</h3></div>
        </div>
      </div>
    </div>

    <SelectEquipe :show="modalShow_1" :genre="genreFilter" @close="modalShow_1 = false" @select="selectExisting" />

    <div class="en_bas">
      <button class="valider_ajout" @click="valider_ajout_competition">Créer la compétition</button>
    </div>
  </main>
</template>

<style scoped>
.page-ajout { padding: 2rem; }
.form-block { display: flex; flex-direction: column; gap: 1rem; margin-bottom: 2rem; }

/* Sélecteurs arrondis */
.select-genre, .select-nb {
  padding: 0.6rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: white;
  width: 100%;
  max-width: 400px;
}
.select-nb { margin-right: 90%; width: auto; min-width: 180px; }

/* Grid stable pour les cartes */
.joueurs-flex{
  display: flex;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  justify-items: center;
  justify-self: center;
  margin-top: 1.5rem;
}

.joueur-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  width: 260px;
}

/* Bloc Mixité */
.selection-categorie {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background-color: #f9f9f9;
  border-left: 4px solid #1e88e5;
  border-radius: 4px;
}

/* Design des cartes */
.competition-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  overflow: hidden;
  border: 1px solid #eee;
}
.competition-img { width: 100%; height: 130px; object-fit: cover; }
.competition-info { padding: 1rem; text-align: center; }
.empty-card { border: 2px dashed #ccc; box-shadow: none; background: #fafafa; }

/* Boutons */
.select-btn {
  padding: 0.6rem;
  border: none;
  border-radius: 8px;
  background: #1e88e5;
  color: white;
  cursor: pointer;
  font-weight: 500;
}
.en_bas { display: flex; justify-content: center; margin-top: 3rem; }
.valider_ajout {
  padding: 1rem;
  border: none;
  border-radius: 10px;
  background: #1e88e5;
  color: white;
  cursor: pointer;
  width: 80%;
  font-weight: bold;
  font-size: 1.1rem;
}

/* Dates */
.dates-row { display: flex; gap: 2rem; max-width: 850px; }
.date-item { flex: 1; display: flex; flex-direction: column; gap: 0.5rem; }

#sous-titre { font-size: 0.9rem; color: gray; text-align: center; margin-bottom: 2rem; }
h2 { text-align: center; font-size: 2rem; margin-bottom: 0rem; }
</style>