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

let etatMatch = ref("WAITING")

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
    console.log(match.value);
    etatMatch = match.value.status;

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

  const s1 = match.value.scoreEquipe1;
  const s2 = match.value.scoreEquipe2;

  if (s1 > s2) return "vert";
  if (s1 < s2) return "rouge";
  return "or";
});

const couleurEquipe2 = computed(() => {
  if (!match.value) return "noir";
  if (match.value.status !== "FINISHED") return "noir";

  const s1 = match.value.scoreEquipe1;
  const s2 = match.value.scoreEquipe2;

  if (s2 > s1) return "vert";
  if (s2 < s1) return "rouge";
  return "or";
});

const AjoutPoint = async (numEquipe, combien) => {

  let score;
  const matchId = match.value.idMatch; 

  if (numEquipe == 1) {
    score = parseInt(match.value.scoreEquipe1) + combien;
  } else if (numEquipe == 2) {
    score = parseInt(match.value.scoreEquipe2) + combien;
  } else {
    console.error("Numéro d'équipe invalide :", numEquipe);
    return;
  }

  console.log(score);

  const point = {
    point: score
  }

  const res = await fetch(`/api/match/${matchId}/equipe/${numEquipe}/point`, { 
    method: "PATCH",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(point)
  });

  if (!res.ok) {
    const errorText = await res.text();
    console.error("Erreur API:", errorText);
    throw new Error("Erreur lors de l'ajout de point.");
  }
}

const operationMatch = async (operation) => {
  const res = await fetch(`/api/match/${matchId}/${operation}`, {
    method: "PUT"
  })

  if (!res.ok) {
    const errorText = await res.text();
    console.error("Erreur API:", errorText);
    throw new Error(`Erreur lors du ${operation} du match.`);
  }
} 


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

      <p v-if="!(etatMatch=='FINISHED' || etatMatch=='WAITING')" id="dureeMatch">00:00:00</p>

      <h1>
        Championnat régional
      </h1>

      <div class="score-box">
        <div class="score">
          <h2 :class="couleurEquipe1">{{ match.equipe1.nomEquipe }}</h2>

          <div class="affichagePoint">
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(1,1)" class="boutonScore boutonPlus">+</button>
            <p class="points">{{ match.scoreEquipe1 }}</p>
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(1,-1)" class="boutonScore boutonMoins">-</button>
          </div>
          
        </div>

        <div class="vs">VS</div>

        <div class="score">
          <h2 :class="couleurEquipe2">{{ match.equipe2.nomEquipe }}</h2>

          <div class="affichagePoint">
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(2,1)" class="boutonScore boutonPlus">+</button>
            <p class="points">{{ match.scoreEquipe2 }}</p>
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(2,-1)" class="boutonScore boutonMoins">-</button>
          </div>
          
        </div>
      </div>

      <div class="date">
        <p>Début : {{ new Date(match.dateDebut).toLocaleString() }}</p>
        <p v-if="etatMatch == 'FINISHED'">Fin :  {{ new Date(match.dateFin).toLocaleString() }}</p>
        <p v-if="etatMatch == 'PAUSED'">Début de la pause :  {{ new Date(match.datePause).toLocaleString() }}</p>
        <p v-if="etatMatch == 'PAUSED'">Durée de la pause :  {{ new Date(match.dureePause).toLocaleString() }}</p>
      </div>
      <p class="status">Status : {{ match.status }}</p>
    </section>

    <p v-if="error" class="error">{{ error }}</p>

    <!-- LAYOUT 3 colonnes -->
    <div class="layout" v-if="match">

      <!-- COLONNE GAUCHE -->
      <div class="col">
        <h3 class="subtitle">{{ match.equipe1.nomEquipe }}</h3>

        <p v-if="loadingPlayers">Chargement…</p>

        <SliderVertical v-if="!loadingPlayers" :speed=0.2>
          <Card_joueur
              v-for="j in joueursEquipe1"
              :key="j.idJoueur"
              :nom="j.nomJoueur + ' ' + j.prenomJoueur"
              :genre="j.genre"
              background="#ffdddd"
          />
        </SliderVertical>

      </div>

      <!-- COLONNE MILIEU -->
      <div class="middle">
        <h3>Informations du Match</h3>
        <p v-if="auth.isAdmin || auth.isArbitre" >Ici tu pourras ajouter les stats, durée, arbitres, etc.</p>

        <div id="actionsMatch">
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'WAITING'" @click="operationMatch('start')" class="boutonAction">Commencer le match</button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="operationMatch('end')" class="boutonAction">Terminer le match</button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="operationMatch('pause')" class="boutonAction">Pause le match</button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'PAUSED'" @click="operationMatch('resume')" class="boutonAction">Resume le match</button>
        </div>
    
      </div>

      <!-- COLONNE DROITE -->
      <div class="col">
        <h3 class="subtitle">{{ match.equipe2.nomEquipe }}</h3>

        <p v-if="loadingPlayers">Chargement…</p>

        <SliderVertical v-if="!loadingPlayers" :speed=0.2>
          <Card_joueur
              v-for="j in joueursEquipe2"
              :key="j.idJoueur"
              :nom="j.nomJoueur + ' ' + j.prenomJoueur"
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

.affichagePoint {
  display: flex;
  justify-content: center;
  align-items: center;
}

.boutonScore {
  border-radius: 20px;
  border: none;
  height: 4em;
  width: 4em;

  margin: 2em;

  font-weight: bold;
  font-size: 1em;

  padding: 0;

  transition: opacity 0.3s;
}

.boutonScore:hover {
  opacity: 0.7;
}

.boutonPlus {
  background-color: rgb(65, 133, 65);
}

.boutonMoins {
  background : rgb(194, 80, 80);
}

#actionsMatch {
  display: flex;
  justify-content: space-evenly;
}

</style>
