<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

import Card_joueur from "@/components/card_joueur.vue";
import SliderVertical from "@/components/slider_card_vertical.vue";

// --- Récupération de l'ID dans l'URL ---
const route = useRoute();
const teamId = route.params.id;

// --- Informations générales ---
const teamName = ref("Équipe #" + teamId);
const loading = ref(true);

// --- Données des joueurs ---
const joueurs = ref<any[]>([]);

// --- Données par défaut (mock) ---
const mockJoueurs = [
  { id: 1, nom: "Loup Gicquel", age: 21, sexe: "H" },
  { id: 2, nom: "Alice Martin", age: 19, sexe: "F" },
  { id: 3, nom: "Théo Pommier", age: 22, sexe: "H" },
  { id: 4, nom: "Maya Lefèvre", age: 20, sexe: "F" },
  { id: 5, nom: "Rayan Dupont", age: 23, sexe: "H" },
];

onMounted(async () => {
  loading.value = true;

  /* ------------------------------------------------------------------
   * 🌐 APPEL API (désactivé)
   * Quand ton API sera prête :
   * - supprime les "//"
   * - supprime l'affectation mock juste en-dessous
   ------------------------------------------------------------------ */

  /*
  try {
    const response = await fetch("https://ton-api.com/equipes/" + teamId);
    const data = await response.json();
    joueurs.value = data.joueurs;
    teamName.value = data.nom;
  } catch (error) {
    console.error("Erreur API :", error);
  }
  */

  // --- Valeurs mock en attendant l'API ---
  joueurs.value = mockJoueurs;

  loading.value = false;
});
</script>

<template>
  <main>
    <h2 class="title">Détails – {{ teamName }}</h2>

    <div class="layout">
      <!-- Colonne gauche : SLIDER -->
      <div class="left">
        <p v-if="loading">Chargement…</p>

        <SliderVertical :autoScroll="true" >
          <Card_joueur
              v-for="j in joueurs"
              :key="j.id"
              :nom="j.nom"
              :age="j.age"
              :sexe="j.sexe"
          />
        </SliderVertical>
      </div>

      <div class="right">
        <h3>Informations de l'équipe</h3>
        <p>blabla</p>
      </div>
    </div>
  </main>
</template>

<style scoped>
/* Titre centré */
.title {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 1.8rem;
}

/* Layout global */
.layout {
  display: flex;
  gap: 2rem;
}

/* Colonne gauche : slider */
.left {
  width: 25%; /* slider = 1/4 de la fenêtre */
  min-width: 220px;
}

/* Colonne droite */
.right {
  flex: 1; /* prend tout le reste */
  padding-right: 2rem;
}
</style>
