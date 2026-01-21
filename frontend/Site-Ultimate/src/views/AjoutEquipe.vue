<script setup>
import { ref, computed, watch } from "vue"
import { useRouter } from 'vue-router'
import champs_input from "@/components/champs_input.vue"
import JoueurCardForm from "@/components/card/JoueurCardForm.vue"
import SelectJoueur from "@/components/overlay/SelectionJoueurOverlay.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import AjoutJoueurOverlay from "@/components/overlay/AjoutJoueurOverlay.vue";
import api from '@/services/api' // Import de l'instance Axios
import { useAuthStore } from "@/stores/auth";

// --- ÉTATS NOTIFICATIONS ---
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

const nomEquipe = ref("")
const descriptionEquipe = ref("")
const nombreJoueurs = ref(0)

const categorie = ref("")
const format = ref("")
const mixite = ref("")

const auth = useAuthStore();
const router = useRouter();

if (!auth.isAdmin) {
  router.push("/");
}

const maxJoueurs = computed(() => {
  if (format.value === '5v5') return 5;
  if (format.value === '7v7') return 7;
  return 0;
})

watch(format, (newVal) => {
  if (newVal === '5v5' && nombreJoueurs.value > 5) nombreJoueurs.value = 5;
  if (newVal === '7v7' && nombreJoueurs.value > 7) nombreJoueurs.value = 7;
})

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
    notify("Vous devez sélectionner une catégorie et un format avant d'ajouter des joueurs.")
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
    notify("Cette équipe est exclusivement féminine.");
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
  // --- 1) VALIDATION DE BASE ---
  if (!nomEquipe.value.trim()) {
    notify("Le nom de l'équipe est obligatoire.");
    return;
  }
  if (!categorie.value) {
    notify("Veuillez choisir une catégorie.");
    return;
  }

  // --- 2) CALCUL DU GENRE (CORRESPONDANCE ENUM) ---
  let genreFinal = "";

  if (categorie.value === 'MIXTE') {
    // Si c'est mixte, on prend la valeur de mixite (H3F2, etc.)
    if (!mixite.value) {
      notify("Veuillez sélectionner la répartition Mixte.");
      return;
    }
    genreFinal = mixite.value;
  }else{
    genreFinal = categorie.value;
  }

  // LOG DE SÉCURITÉ : Si c'est vide ici, on arrête tout
  console.log("DEBUG GENRE SELECTIONNÉ :", genreFinal);

  if (!genreFinal || genreFinal === "") {
    notify("Erreur interne : Le genre n'a pas pu être déterminé.");
    return;
  }

  try {
    // --- 3) PRÉPARATION DU PAYLOAD ---
    const equipePayload = {
      description: descriptionEquipe.value,
      genre: genreFinal,
      nbJoueurs: format.value === '5v5' ? "CINQ" : "SEPT",
      nomEquipe: nomEquipe.value
    };

    console.log("Payload envoyé au POST /equipe :", equipePayload);

    const resEquipe = await api.post("/equipe", equipePayload);
    const equipeCree = resEquipe.data;

    // --- 4) AFFECTATION DES JOUEURS ---
    const slotsVisibles = joueurs.value.slice(0, nombreJoueurs.value);
    const joueursRemplis = slotsVisibles.filter(j => j.id !== null);

    if (joueursRemplis.length > 0) {
      // On boucle un par un pour éviter les erreurs 500 de concurrence
      for (const j of joueursRemplis) {
        await api.patch(`/joueur/${j.id}/equipe/${equipeCree.idEquipe}`);
      }
    }

    notify("Équipe créée avec succès !", "success");
    setTimeout(() => router.push("/Equipe"), 1500);

  } catch (e) {
    console.error("Erreur serveur détaillée :", e.response?.data);
    const errorMsg = e.response?.data?.message || "Erreur lors de la création";
    notify(errorMsg);
  }
};

</script>

<template>
  <main v-if="auth.isAdmin" class="page-ajout">

    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

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
/* --- STYLE DU TOAST */
.toast-notification {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  z-index: 10000;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}
.toast-notification.success { background-color: #2ecc71; }
.toast-notification.error { background-color: #e74c3c; }

.toast-icon {
  background: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}
.success .toast-icon { color: #2ecc71; }
.error .toast-icon { color: #e74c3c; }

/* Animation de transition */
.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, 40px); }


.page-ajout { padding: 2rem; }
.form-block { display: flex; flex-direction: column; gap: 1rem; margin-bottom: 2rem; }
.select-genre, .select-nb { padding: 0.7rem; border-radius: 8px; border: 1px solid #ccc; background-color: white; width: 100%; max-width: 400px; }
.select-nb { width: auto; min-width: 200px; margin-right: auto; }
.joueurs-flex { display: flex; gap: 2.5rem; justify-items: center; margin-top: 1.5rem; justify-content: center; }
.joueur-wrapper { display: flex; flex-direction: column; gap: 0.8rem; width: 260px; }
.selection-categorie { display: flex; flex-direction: column; gap: 0.5rem; padding: 1rem; background-color: #f9f9f9; border-left: 4px solid #1e88e5; border-radius: 4px; }
#warning-format { font-size: 0.8rem; color: orange; font-style: italic; }
.empty-card { height: 120px; border: 2px dashed #ccc; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #999; background: #fafafa; }
.select-btn { padding: 0.6rem; border: none; border-radius: 8px; background: #1e88e5; color: white; cursor: pointer; font-weight: 500; }
.en_bas { display: flex; justify-content: center; margin-top: 3rem; }
.valider_ajout { text-align: center; padding: 1rem; border: none; border-radius: 10px; background: #1e88e5; color: white; cursor: pointer; width: 80%; font-weight: bold; font-size: 1.1rem; }
#sous-titre { font-size: 0.9rem; color: gray; text-align: center; margin-bottom: 2rem; }
h2 { text-align: center; font-size: 2.2rem; margin-bottom: 0.5rem; }
</style>