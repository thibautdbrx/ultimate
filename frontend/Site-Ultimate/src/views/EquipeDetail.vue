<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

import Card_joueur from "@/components/card_joueur.vue";
import SliderVertical from "@/components/slider_card_vertical.vue";

// --- Récupération des paramètres ---
const route = useRoute();
const teamId = Number(route.params.id);
const teamName = String(route.params.nom);
console.log(teamName);

// --- State ---
const loading = ref(true);
const joueurs = ref([]);
const error = ref(null);

onMounted(async () => {
  loading.value = true;

  try {
    const res = await fetch(`/api/joueur/equipe/${teamId}`);
    if (!res.ok) throw new Error("Erreur API : " + res.status);

    joueurs.value = await res.json();

  } catch (err) {
    console.error(err);
    error.value = "Impossible de charger les joueurs.";
  }

  loading.value = false;
});
</script>

<template>
  <main>
    <h2 class="title">{{ teamName }}</h2>

    <div class="layout">

      <!-- Colonne gauche -->
      <div class="left">
        <p v-if="loading">Chargement…</p>
        <p v-if="error">{{ error }}</p>

        <SliderVertical v-if="!loading && joueurs.length">
          <Card_joueur
              v-for="j in joueurs"
              :nom="(j.nomJoueur+' '+j.prenomJoueur)"
              :genre="j.genre"
          />
        </SliderVertical>

        <p v-if="!loading && joueurs.length === 0">
          Aucun joueur dans cette équipe.
        </p>
      </div>

      <div class="right">
        <h3>Informations de l'équipe</h3>
        <p>blabla</p>
      </div>

    </div>
  </main>
</template>

<style scoped>
.title {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 1.8rem;
}

.layout {
  display: flex;
  gap: 2rem;
}

.left {
  width: 25%;
  min-width: 220px;
}

.right {
  flex: 1;
  padding-right: 2rem;
}
</style>
