<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";

import Card_joueur from "@/components/card_joueur.vue";
import SliderVertical from "@/components/slider_card_vertical.vue";

import { useAuthStore } from "@/stores/auth";

// --- Récupération de l'id du match ---
const route = useRoute();
const matchId = Number(route.params.id);

// State
const loadingMatch = ref(true);
const loadingPlayers = ref(true);

const match = ref(null);
const joueursEquipe1 = ref([]);
const joueursEquipe2 = ref([]);

const error = ref(null);

const auth = useAuthStore();

// ----------------------
// 1) Charger infos du match
// ----------------------
const loadMatch = async () => {
  try {
    const res = await fetch(`/api/match/${matchId}`);
    if (!res.ok) throw new Error("Erreur API match : " + res.status);

    match.value = await res.json();
  } catch (err) {
    error.value = err.message;
  } finally {
    loadingMatch.value = false;
  }
};

// ----------------------
// 2) Charger les joueurs
// ----------------------
const loadPlayers = async () => {
  if (!match.value) return;

  try {
    const [res1, res2] = await Promise.all([
      fetch(`/api/joueur/equipe/${match.value.equipe1.idEquipe}`),
      fetch(`/api/joueur/equipe/${match.value.equipe2.idEquipe}`)
    ]);

    if (!res1.ok || !res2.ok)
      throw new Error("Erreur API joueurs");

    joueursEquipe1.value = await res1.json();
    joueursEquipe2.value = await res2.json();

  } catch (err) {
    error.value = err.message;
  } finally {
    loadingPlayers.value = false;
  }
};

// ----------------------
// LOGIQUE COULEURS
// ----------------------

const couleurEquipe1 = computed(() => {
  if (!match.value) return "noir";
  if (match.value.status !== "FINISHED") return "noir";

  const s1 = match.value.score_equipe1;
  const s2 = match.value.score_equipe2;

  if (s1 > s2) return "vert";
  if (s1 < s2) return "rouge";
  return "or";
});

const couleurEquipe2 = computed(() => {
  if (!match.value) return "noir";
  if (match.value.status !== "FINISHED") return "noir";

  const s1 = match.value.score_equipe1;
  const s2 = match.value.score_equipe2;

  if (s2 > s1) return "vert";
  if (s2 < s1) return "rouge";
  return "or";
});

// ----------------------
onMounted(async () => {
  await loadMatch();
  await loadPlayers();
});
</script>

<template>
  <main class="page">

    <!-- HEADER : Infos du match -->
    <section class="header" v-if="match">
      <h1>
        Championnat régional
      </h1>

      <div class="score-box">
        <div class="score">
          <h2 :class="couleurEquipe1">{{ match.equipe1.nom_equipe }}</h2>
          <p class="points">{{ match.score_equipe1 }}</p>
        </div>

        <div class="vs">VS</div>

        <div class="score">
          <h2 :class="couleurEquipe2">{{ match.equipe2.nom_equipe }}</h2>
          <p class="points">{{ match.score_equipe2 }}</p>
        </div>
      </div>

      <p class="date">
        Début : {{ new Date(match.dateDebut).toLocaleString() }}
      </p>
      <p class="status">Status : {{ match.status }}</p>
    </section>

    <p v-if="error" class="error">{{ error }}</p>

    <!-- LAYOUT 3 colonnes -->
    <div class="layout" v-if="match">

      <!-- COLONNE GAUCHE -->
      <div class="col">
        <h3 class="subtitle">{{ match.equipe1.nom_equipe }}</h3>

        <p v-if="loadingPlayers">Chargement…</p>

        <SliderVertical v-if="!loadingPlayers" :speed=0.2>
          <Card_joueur
              v-for="j in joueursEquipe1"
              :key="j.idJoueur"
              :nom="j.nom_joueur + ' ' + j.prenom_joueur"
              :genre="j.genre"
              background="#ffdddd"
          />
        </SliderVertical>

      </div>

      <!-- COLONNE MILIEU -->
      <div class="middle">
        <h3>Informations du Match</h3>
        <p>Ici tu pourras ajouter les stats, durée, arbitres, etc.</p>
      </div>

      <!-- COLONNE DROITE -->
      <div class="col">
        <h3 class="subtitle">{{ match.equipe2.nom_equipe }}</h3>

        <p v-if="loadingPlayers">Chargement…</p>

        <SliderVertical v-if="!loadingPlayers" :speed=0.2>
          <Card_joueur
              v-for="j in joueursEquipe2"
              :key="j.idJoueur"
              :nom="j.nom_joueur + ' ' + j.prenom_joueur"
              :genre="j.genre"
              background="#dde8ff"
          />
        </SliderVertical>
      </div>

    </div>

  </main>
</template>

<style scoped>
.page {
  padding: 2rem;
  font-family: "Poppins", sans-serif;
}

/* --- HEADER --- */
.header {
  text-align: center;
  margin-bottom: 2rem;
}

.team-name {
  font-weight: 700;
}

/* COULEURS */
.noir { color: black; }
.vert { color: green; }
.rouge { color: red; }
.or   { color: goldenrod; }

.score-box {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 3rem;
  margin: 1rem 0;
}

.score {
  text-align: center;
  flex: 1;

}

.points {
  font-size: 2.4rem;
  font-weight: 700;
}

.vs {
  font-size: 2rem;
  font-weight: 700;
  flex: 0;
  text-align: center;
}

.layout {
  display: flex;
  gap: 2rem;
}

.col {
  width: 28%;
}

.middle {
  flex: 1;
  padding: 1rem;
  background: #fafafa;
  border-radius: 10px;
}

.subtitle {
  text-align: center;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.error {
  color: red;
  text-align: center;
}
</style>
