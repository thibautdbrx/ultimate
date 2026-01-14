<script setup>
import { ref, computed, watch } from "vue"
import { useRouter } from 'vue-router'
import champs_input from "@/components/champs_input.vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import SelectJoueur from "@/components/SelectionJoueurOverlay.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import AjoutJoueurOverlay from "@/components/AjoutJoueurOverlay.vue";
import { useAuthStore } from "@/stores/auth";

const nomEquipe = ref("")
const descriptionEquipe = ref("")
const nombreJoueurs = ref(0)

const categorie = ref("") // OPEN, MIXTE, FEMME
const format = ref("")    // 5v5, 7v7
const mixite = ref("")    // H3F2, H2F3, etc

const auth = useAuthStore();
const router = useRouter();

if (!auth.isAdmin) {
  router.push("/");
}

// --- LOGIQUE DE LIMITE DE JOUEURS (0 à X) ---
const maxJoueurs = computed(() => {
  if (format.value === '5v5') return 5;
  if (format.value === '7v7') return 7;
  return 0;
})

watch(format, (newVal) => {
  if (newVal === '5v5' && nombreJoueurs.value > 5) {
    nombreJoueurs.value = 5;
  }
  if (newVal === '7v7' && nombreJoueurs.value > 7) {
    nombreJoueurs.value = 7;
  }
})

// Initialisation du roster (7 slots max par défaut)
const joueurs = ref(
    Array.from({ length: 7 }, () => ({
      id: null,
      nomJoueur: "",
      prenomJoueur: "",
      genre: "",
      photoJoueur: null,
      clickable: false
    }))
)

// Gestion des modaux
const modalIndex = ref(null)
const modalShow_1 = ref(false)
const modalShow_2 = ref(false)

const genreFilter = computed(() => {
  if (categorie.value === 'FEMME') return 'FEMME';
  return '';
})

const openModal_1 = (i) => {
  if (!categorie.value || !format.value) {
    alert("Vous devez sélectionner une catégorie et un format avant d'ajouter des joueurs.")
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
  const j = joueurs.value[modalIndex.value]

  if (categorie.value === 'FEMME' && joueur.genre !== 'FEMME') {
    alert("Cette équipe est exclusivement féminine.");
    return;
  }

  j.id = joueur.idJoueur;
  j.nomJoueur = joueur.nomJoueur;
  j.prenomJoueur = joueur.prenomJoueur;
  j.genre = joueur.genre;
  j.photoJoueur = joueur.photoJoueur;
  j.clickable = false;
  modalShow_1.value = false;
}

const valider_ajout_equipe = async () => {
  // --- 1) VALIDATION ---
  if (!nomEquipe.value.trim() || !categorie.value || !format.value) {
    alert("Le nom, la catégorie et le format sont obligatoires.");
    return;
  }
  if (categorie.value === 'MIXTE' && !mixite.value) {
    alert("Veuillez sélectionner la répartition (Mixité).");
    return;
  }

  // --- 2) RÉCUPÉRATION ET COMPTAGE DES JOUEURS ---
  const slotsVisibles = joueurs.value.slice(0, nombreJoueurs.value);
  const joueursRemplis = slotsVisibles.filter(j => j.id !== null);

  if (joueursRemplis.length > 0) {
    const countH = joueursRemplis.filter(j => j.genre === 'HOMME').length;
    const countF = joueursRemplis.filter(j => j.genre === 'FEMME').length;

    if (categorie.value === 'FEMME' && countH > 0) {
      alert("Erreur : Une équipe 'FEMME' ne peut contenir que des joueuses.");
      return;
    }

    if (categorie.value === 'MIXTE' && mixite.value) {
      const maxH = parseInt(mixite.value.charAt(1));
      const maxF = parseInt(mixite.value.charAt(3));
      if (countH > maxH) { alert(`Trop d'hommes (Max : ${maxH}).`); return; }
      if (countF > maxF) { alert(`Trop de femmes (Max : ${maxF}).`); return; }
    }
  }

  try {
    // --- 3) PRÉPARATION DU PAYLOAD ---
    const genreFinal = categorie.value === 'MIXTE' ? mixite.value : categorie.value;
    const formatTexte = format.value === '5v5' ? 'CINQ' : 'SEPT';

    const equipePayload = {
      nomEquipe: nomEquipe.value,
      description: descriptionEquipe.value,
      genre: genreFinal,
      nbJoueur: formatTexte,
    };

    const resEquipe = await fetch("/api/equipe", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(equipePayload)
    });

    if (!resEquipe.ok) {
      const err = await resEquipe.json();
      alert("Erreur : " + err.message);
      return;
    }

    const equipeCree = await resEquipe.json();

    // --- 4) AFFECTATION DES JOUEURS ---
    if (joueursRemplis.length > 0) {
      await Promise.all(joueursRemplis.map(j =>
          fetch(`/api/joueur/${j.id}/equipe/${equipeCree.idEquipe}`, {
            method: "PATCH"
          })
      ));
    }

    alert("Équipe créée avec succès !");
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
    <p id="sous-titre">
      Renseignez les informations de l'équipe. L'ajout de joueurs est optionnel à cette étape.
    </p>

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

      <label>Catégorie :</label>
      <select v-model="categorie" class="select-genre">
        <option disabled value="">Choisir une catégorie</option>
        <option value="OPEN">OPEN (Tout genre)</option>
        <option value="MIXTE">MIXTE (Hommes & Femmes)</option>
        <option value="FEMME">FEMME (Uniquement Femmes)</option>
      </select>

      <label>Format de jeu :</label>
      <select v-model="format" class="select-genre">
        <option disabled value="">Choisir un format</option>
        <option value="5v5">5 vs 5</option>
        <option value="7v7">7 vs 7</option>
      </select>

      <div v-if="categorie === 'MIXTE'" class="selection-categorie">
        <label>Répartition Mixte imposée :</label>
        <select v-if="format === '5v5'" v-model="mixite" class="select-genre">
          <option disabled value="">Choisir une mixité</option>
          <option value="H3F2">3 Hommes / 2 Femmes (H3F2)</option>
          <option value="H2F3">2 Hommes / 3 Femmes (H2F3)</option>
        </select>
        <select v-else-if="format === '7v7'" v-model="mixite" class="select-genre">
          <option disabled value="">Choisir une mixité</option>
          <option value="H3F4">3 Hommes / 4 Femmes (H3F4)</option>
          <option value="H4F3">4 Hommes / 3 Femmes (H4F3)</option>
        </select>
        <p v-else id="warning-format">Veuillez d'abord choisir un format ci-dessus.</p>
      </div>

      <label>Nombre de joueurs à ajouter maintenant (OPTIONNEL) :</label>
      <select v-model="nombreJoueurs" class="select-nb">
        <option :value="0">Plus tard</option>
        <option v-for="n in maxJoueurs" :value="n" :key="n">
          {{ n }} joueur{{ n > 1 ? 's' : '' }}
        </option>
      </select>
    </div>

    <div class="joueurs-flex">
      <div
          v-for="(j, i) in joueurs"
          :key="i"
          v-show="i < nombreJoueurs"
          class="joueur-wrapper"
      >
        <button class="select-btn" @click="openModal_1(i)">
          {{ j.id ? 'Modifier ' + j.prenomJoueur : 'Sélectionner joueur ' + (i + 1) }}
        </button>

        <JoueurCardForm v-if="j.id" :joueur="j" />
        <div v-else class="empty-card">vide</div>
      </div>
    </div>

    <SelectJoueur
        :show="modalShow_1"
        :genre="genreFilter"
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
      <button class="valider_ajout" @click="valider_ajout_equipe">
        {{ joueurs.slice(0, nombreJoueurs).some(j => j.id) ? "Sauvegarder l'équipe et les joueurs" : "Créer l'équipe vide" }}
      </button>
    </div>

  </main>
</template>

<style scoped>
.page-ajout { padding: 2rem; }
.form-block { display: flex; flex-direction: column; gap: 1rem; margin-bottom: 2rem; }

/* Sélecteurs stylisés */
.select-genre, .select-nb {
  padding: 0.7rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: white;
  width: 100%;
  max-width: 400px;
}
.select-nb { width: auto; min-width: 200px; margin-right: auto; }

/* Grid stable pour l'affichage des cartes */
.joueurs-flex {
  display: flex;
  gap: 2.5rem;
  justify-items: center;
  margin-top: 1.5rem;
  justify-content: center;
}

.joueur-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  width: 260px;
}

/* Bloc Mixité Stylisé */
.selection-categorie {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background-color: #f9f9f9;
  border-left: 4px solid #1e88e5;
  border-radius: 4px;
}

#warning-format { font-size: 0.8rem; color: orange; font-style: italic; }

.empty-card {
  height: 120px;
  border: 2px dashed #ccc;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #fafafa;
}

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
  text-align: center;
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

#sous-titre { font-size: 0.9rem; color: gray; text-align: center; margin-bottom: 2rem; }
h2 { text-align: center; font-size: 2.2rem; margin-bottom: 0.5rem; }
</style>