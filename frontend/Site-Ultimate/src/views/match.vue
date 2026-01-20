<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import Card_joueur from "@/components/card_joueur.vue";
import SliderVertical from "@/components/slider_card_vertical.vue";
import weather_card from  "@/components/weather_card.vue";

import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';

dayjs.extend(duration);

import start from "@/assets/img/matchIcon/start.png";
import end from "@/assets/img/matchIcon/end.png";
import pause from "@/assets/img/matchIcon/pause.png";
import resume from "@/assets/img/matchIcon/resume.png";

import curseur from "@/assets/img/curseur.cur"

import "leaflet/dist/leaflet.css"
import { LMap, LTileLayer, LMarker, LTooltip } from "@vue-leaflet/vue-leaflet"

import { useAuthStore } from "@/stores/auth";
import PUB from "@/components/PUB.vue";
const auth = useAuthStore();

// --- Récupération de l'id du match ---
const route = useRoute();
const router = useRouter();
const matchId = Number(route.params.id);

// State
const loadingMatch = ref(true);
const loadingPlayers = ref(true);
const loadingActions = ref(true);

const match = ref(null);
const joueursEquipe1 = ref([]);
const joueursEquipe2 = ref([]);
const actions = ref([]);

let etatMatch = ref("WAITING");
let duree = ref(null);

const error = ref(null);

const zoom = ref(7); // zoom pour la carte
const center = ref([46.603354, 1.888334]);


//------------------------
// 0) LA METEO
//-------------------------
const WEATHER_ERROR_FALLBACK = {
  current: {
    weather_code: -1,
    temperature_2m: null,
    wind_speed_10m: null
  },
  current_units: {
    temperature_2m: "°C",
    wind_speed_10m: "km/h"
  }
};

const weather = ref(null); // --- METEO : Stockage des infos météo


// --- METEO : Fonction de récupération ---
const loadWeather = async (lat, lon) => {
  if (lat === null || lat === undefined || lon === null || lon === undefined) {
    console.warn("Coordonnées manquantes, pas de météo.");
    return;
  }

  try {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,wind_speed_10m,weather_code`;
    const res = await fetch(url);
    if (!res.ok) throw new Error("Erreur météo");
    weather.value = await res.json();
    //console.log("Météo reçue :", weather.value);

  } catch (e) {
    console.error("Impossible de charger la météo", e);
    weather.value = WEATHER_ERROR_FALLBACK;
  }
};

// Fonction pour aller vers la page de la compétition
function goToCompetition(id) {
  router.push({ name: 'Competition-details', params: { id } });
}



// ----------------------
// 1) Charger infos du match
// ----------------------
const loadMatch = async () => {
  try {
    const res = await fetch(`/api/match/${matchId}`);
    if (!res.ok) throw new Error("Erreur API match : " + res.status);

    match.value = await res.json();
    etatMatch = match.value.status;

    if (match.value.terrain && match.value.terrain.latitude) {
      center.value = [match.value.terrain.latitude, match.value.terrain.longitude];
    }

    //loadWeather(match.value.terrain.latitude, 2.3488);
    loadWeather(match.value.terrain.latitude, match.value.terrain.longitude);

  } catch (err) {
    error.value = err.message;
  } finally {
    loadingMatch.value = false;
  }


};

function ConversionPause(pause) {
  if (!pause) {
    pause = 0;
  } else if (pause.substr(0,2) == "PT") {
    pause = dayjs.duration(pause).asMilliseconds(); // Conversion en ms
  } else {
    pause = 0;
  }

  return pause;
}

// Calcul la durée du match en prenant en compte l'état et les pauses
const calculDuree = computed(() => {
  const dateDebut = match.value.dateDebut;
  if (!dateDebut) return 0;

  let pause = ConversionPause(match.value.dureePauseTotale); // Récupération d'un objet Duration à convertir en un temps en ms

  duree = 0;

  if (etatMatch == "ONGOING") {
    duree = maintenant.value - Date.parse(dateDebut) - pause;
  }
  else if (etatMatch == "FINISHED") {
    const dateFin = match.value.dateFin;
    duree = Date.parse(match.value.dateFin) - Date.parse(dateDebut) - pause;
  }
  else if (etatMatch == "PAUSED") {
    const datePause = match.value.datePause;
    duree = Date.parse(match.value.datePause) - Date.parse(dateDebut) - pause;
  }

  return formatDuree(duree);
});

// Calcul la durée de la pause quand le match est en pause
const calculDureePause = computed(() => {
  const debut = match.value.datePause;

  if (!debut) {
    return 0;
  }

  let dureePause = maintenant.value - Date.parse(debut);

  return formatDuree(dureePause)
});

// Passage d'une date à un temps pour une action
function calculTempsAction(dateAction, tempsPause=0) {
  const date = Date.parse(dateAction);
  const dateDebut = Date.parse(match.value.dateDebut);

  return formatDuree(date - dateDebut - tempsPause)
}

// Convertie un temps en ms en un temps sous format heures:minutes:secondes
function formatDuree(ms) {
  const h = Math.floor(ms / 3600000);
  const m = Math.floor((ms % 3600000) / 60000);
  const s = Math.floor((ms % 60000) / 1000);

  const format = (n) => String(n).padStart(2, '0');

  return `${format(h)}:${format(m)}:${format(s)}`;
}


// ----------------------
// 2) Charger les joueurs
// ----------------------
const loadPlayers = async () => {
  if (!match.value || !match.value.equipe1 || !match.value.equipe2) return;

  try {
    const [res1, res2] = await Promise.all([
      fetch(`/api/joueur/equipe/${match.value.equipe1.idEquipe}`), // Récupère l'équipe 1
      fetch(`/api/joueur/equipe/${match.value.equipe2.idEquipe}`) // Récupère l'équipe 2
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
// 3) Charger les actions
// ----------------------

const loadActions = async () => {
  if (!match.value) return;

  try {
    const [res1] = await Promise.all([
      fetch(`/api/action-match/match/${matchId}`)
    ]);

    if (!res1.ok)
      throw new Error("Erreur API joueurs");

    actions.value = await res1.json();


  } catch (err) {
    error.value = err.message;
  } finally {
    loadingActions.value = false;
  }
};

// ----------------------
// LOGIQUE COULEURS (Pour déterminer la couleur d'affichage des noms des équipes)
// ----------------------

// Couleur de l'équipe 1
const couleurEquipe1 = computed(() => {
  if (!match.value) return "noir";
  if (match.value.status !== "FINISHED") return "noir"; // noir tant que le match n'est pas terminé

  const s1 = match.value.scoreEquipe1;
  const s2 = match.value.scoreEquipe2;

  if (s1 > s2) return "vert"; // vert si l'équipe a plus de points
  if (s1 < s2) return "rouge"; // rouge si moins de points
  return "or"; // or sinon (correspond à une égalité)
});

// Couleur de l'équipe 2 (même fonctionnement)
const couleurEquipe2 = computed(() => {
  if (!match.value) return "noir";
  if (match.value.status !== "FINISHED") return "noir";

  const s1 = match.value.scoreEquipe1;
  const s2 = match.value.scoreEquipe2;

  if (s2 > s1) return "vert";
  if (s2 < s1) return "rouge";
  return "or";
});

// ajoute 1 point à l'équipe "numEquipe", marqué par le joueur "idJoueur"
const AjoutPoint = async (numEquipe, combien, idJoueur) => {

  const matchId = match.value.idMatch;

  const point = {
    point: combien,
    idJoueur:idJoueur
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

  await loadMatch(); // Recharge le match, les joueurs et les actions pour mettre à jour l'affichage
  await loadPlayers();
  await loadActions();
}
// ajout d'une faute pour le joueur idJoueur de l'équipe numEquipe
const AjoutFaute = async (numEquipe, idJoueur) => {

  const matchId = match.value.idMatch;

  const res = await fetch(`/api/match/${matchId}/equipe/${numEquipe}/faute`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({idJoueur : idJoueur})
  });

  if (!res.ok) {
    const errorText = await res.text();
    console.error("Erreur API:", errorText);
    throw new Error("Erreur lors de l'ajout de point.");
  }

  await loadMatch(); // Recharge le match, les joueurs et les actions pour mettre à jour l'affichage
  await loadPlayers();
  await loadActions();
}

// appel d'une opération sur le match (Resume, Start, Pause ou End)
const operationMatch = async (operation) => {
  const res = await fetch(`/api/match/${matchId}/${operation}`, {
    method: "PUT"
  })

  if (!res.ok) {
    const errorText = await res.text();
    console.error("Erreur API:", errorText);
    throw new Error(`Erreur lors du ${operation} du match.`);
  }

  await loadMatch(); // Recharge le match, les joueurs et les actions pour mettre à jour l'affichage
  await loadPlayers();
  await loadActions();
}


// ----------------------
const maintenant = ref(Date.now()); // Date actuelle pour le calcul du timer
let interval = null;


onMounted(async () => {
  await loadMatch();
  await loadPlayers();
  await loadActions();

  interval = setInterval(() => {
    maintenant.value = Date.now(); // mise à jour de la variable chaque secondes. Ce qui appelera la fonction calculDuree et calculDureePause chaque secondes
  }, 1000);


});

onUnmounted(() => {
  clearInterval(interval); // Supprime la mise à jour de la variable quand la page est fermée
});


</script>

<template>
  <main class="page">

    <!-- HEADER : Infos du match -->
    <section class="haut" v-if="match">

      <p v-if="!(etatMatch=='WAITING')" id="dureeMatch">{{ calculDuree }}</p>

      <h1
          @click="goToCompetition(match.idCompetition.idCompetition)"
          class="lien-competition"
          title="Voir la compétition"
      >
        {{match.idCompetition.nomCompetition}}
      </h1>

      <div class="score-box">
        <div class="score">
          <h2 :class="couleurEquipe1">{{ match.equipe1.nomEquipe }}</h2>

          <div class="affichagePoint">
            <p class="points">{{ match.scoreEquipe1 }}</p>
          </div>

            <div class="actions">

              <p v-if="loadingActions">Chargement…</p>

              <div v-for="i in actions">
               <p v-if="i.joueur.equipe.idEquipe == match.equipe1.idEquipe && i.type == 'POINT'">
                {{ calculTempsAction(i.dateAction) }} : {{ i.joueur.prenomJoueur }} {{ i.joueur.nomJoueur }}
              </p>
              </div>

            </div>

        </div>

        <div class="vs">VS</div>

        <div class="score">
          <h2 :class="couleurEquipe2">{{ match.equipe2.nomEquipe }}</h2>

          <div class="affichagePoint">
            <p class="points">{{ match.scoreEquipe2 }}</p>
          </div>

          <div class="actions">

            <p v-if="loadingActions">Chargement…</p>

            <div v-for="i in actions">
               <p v-if="(i.joueur.equipe.idEquipe == match.equipe2.idEquipe) && i.type == 'POINT'">
                {{ calculTempsAction(i.dateAction) }} : {{ i.joueur.prenomJoueur }} {{ i.joueur.nomJoueur }}
              </p>
              </div>

          </div>

        </div>
      </div>

      <div class="date">
        <p v-if="!(etatMatch == 'WAITING')">Début : {{ new Date(match.dateDebut).toLocaleString() }}</p>
        <p v-if="etatMatch == 'FINISHED'">Fin :  {{ new Date(match.dateFin).toLocaleString() }}</p>
        <p v-if="etatMatch == 'PAUSED'">Début de la pause :  {{ new Date(match.datePause).toLocaleString() }}</p>
        <p v-if="etatMatch == 'PAUSED'">Durée de la pause :  {{ calculDureePause }}</p>
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

        <div class="joueurDiv" v-for="j in joueursEquipe1">

          <div class="boutons">
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(match.equipe1.idEquipe,1,j.idJoueur)" class="boutonScore boutonPlus">+</button>
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutFaute(match.equipe1.idEquipe,j.idJoueur)" class="boutonScore boutonMoins">X</button>
          </div>
          <Card_joueur

              :key="j.idJoueur"
              :nom="j.nomJoueur"
              :prenom="j.prenomJoueur"
              :genre="j.genre"
              :photo="j.photoJoueur"
              background="#ffdddd"
          />

        <div class="fautes gauche">
          <template v-for="action in actions" :key="action.id">
            <p v-if="action.type === 'FAUTE' && action.joueur.idJoueur === j.idJoueur" class="faute"></p>
          </template>
        </div>

        </div>

      </div>

      <!-- COLONNE MILIEU -->
      <div class="middle">
        <div id="actionsMatch">
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'WAITING'" @click="operationMatch('start')" class="boutonAction actionStart"><img :src="resume" alt="démarrer le match"></button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="operationMatch('end')" class="boutonAction actionEnd"><img :src="end" alt="Terminer le match"></button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="operationMatch('pause')" class="boutonAction actionPause"><img :src="pause" alt="démarrer le match"></button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'PAUSED'" @click="operationMatch('resume')" class="boutonAction actionResume"><img :src="resume" alt="démarrer le match"></button>
        </div>

        <h3>Informations du Match</h3>

        <div class="Info">
          <div class="Nom-Terrain">
            <p>
              {{ match.terrain.nom }}, {{ match.terrain.ville }}
            </p>
          </div>
          <div class="map-container" v-if="match.terrain && match.terrain.latitude">
            <l-map ref="map" v-model:zoom="zoom" :center="center" :use-global-leaflet="false">
              <l-tile-layer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"></l-tile-layer>
              <l-marker :lat-lng="[match.terrain.latitude, match.terrain.longitude]">
                <l-tooltip>{{ match.terrain.nom }}</l-tooltip>
              </l-marker>
            </l-map>
          </div>
          
        </div>

        <div v-if="weather">
          <weather_card
              :code="weather.current.weather_code"
              :temp="weather.current.temperature_2m"
              :temp_unit="weather.current_units.temperature_2m"
              :wind="weather.current.wind_speed_10m"
              :wind_unit="weather.current_units.wind_speed_10m"
          />
        </div>
        <PUB id="pubMilieu"/>

      </div>

      <!-- COLONNE DROITE -->
      <div class="col">
        <h3 class="subtitle">{{ match.equipe2.nomEquipe }}</h3>

        <p v-if="loadingPlayers">Chargement…</p>

        <div class="joueurDiv" v-for="j in joueursEquipe2">

        <div class="boutons">
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(match.equipe2.idEquipe,1,j.idJoueur)" class="boutonScore boutonPlus">+</button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutFaute(match.equipe2.idEquipe,j.idJoueur)" class="boutonScore boutonMoins">X</button>
        </div>
        <Card_joueur

            :key="j.idJoueur"
            :nom="j.nomJoueur"
            :prenom="j.prenomJoueur"
            :genre="j.genre"
            :photo="j.photoJoueur"
            background="#ffdddd"
        />

        <div class="fautes droite">
          <template v-for="action in actions" :key="action.id">
            <p v-if="action.type === 'FAUTE' && action.joueur.idJoueur === j.idJoueur" class="faute"></p>
          </template>
        </div>


        </div>



      </div>

    </div>

  </main>
</template>

<style scoped>
.haut {
  text-align: center;
  margin-bottom: 3rem;
}

.haut h1 {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
}
.score-box {
  display: flex;
  justify-content: center;

  gap: 5rem;
  margin: 2rem 0;
}

.score {
  text-align: center;
  width: 260px;
  margin-top: 0;
}


.score h2 {
  min-height: 2.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  white-space: nowrap;

}


.points {
  font-size: 4.5rem;
  font-weight: 800;
  line-height: 1;
}

.vs {
  font-size: 2.2rem;
  font-weight: 700;
color: gray}

.noir { color: #222; }
.vert { color: #1b9c41; }
.rouge { color: #c0392b; }
.or { color: #c9a227; }

/* BOUTONS SCORE */
.affichagePoint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.2rem;
  margin-top: 0.5rem;
  flex-direction: column;
}

.boutons {
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  margin-bottom: 0.5rem;
}

.boutonScore {
  border-radius: 10%;
  border: none;
  height: 1.4rem;
  width: 30%;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  color: white;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.boutonPlus {
  background-color: #2ecc71;
}

.boutonMoins {
  background-color: #e74c3c;
}

.boutonScore:hover {
  transform: scale(1.15);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}




.date {
  margin-top: 1.2rem;
  font-size: 0.9rem;
  color: #666;
}

.status {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #888;
}

.layout {
  display: flex;
  gap: 3rem;
  align-items: flex-start;
}

  /* -----------------------*/
 /* -- COLONNES EQUIPES -- */
/* -----------------------*/
.col {
  width: 26%;

  display: flex;
  flex-direction: column;
  align-items: center;
}

.col .joueurDiv {
  margin-bottom: 30px;
  display: flex;
  flex-direction: column;

  position: relative;
}

.subtitle {
  text-align: center;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  padding-bottom: 0.4rem;
  border-bottom: 2px solid #eee;
}


.middle {
  flex: 1;
  padding: 1.5rem;
  border-left: 2px solid #f0f0f0;
  border-right: 2px solid #f0f0f0;
}

.middle #pubMilieu {
  margin: auto;
}

.middle h3 {
  text-align: center;
  margin-bottom: 1rem;
  font-weight: 600;
}





  /* -------------------------------- */
 /* -- Bouton action sur le match -- */
/* -------------------------------- */

#actionsMatch {
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  gap: 0.8rem;
  margin-top: 1.5rem;
}

.boutonAction {
  padding: 0.7rem;
  border-radius: 6px;
  border: none;
  background: #f4f4f4;
  font-weight: 600;
  transition: background 0.2s ease, transform 0.15s ease;

  border-radius: 50%;
  width: 2.5em;
  height: 2.5em;

  font-size: 2em;

  display: flex;
  align-items: center;
  justify-content: center;
}

.boutonAction:hover {
  transform: translateY(-1px);
  /* On utilise v-bind pour injecter l'URL de l'image importée */
  /* Note : 'auto' est obligatoire en secours */
  cursor: url("@/assets/img/curseur.cur"), pointer;
}

.boutonAction img {
  max-width: 1em;
  max-height: 1em;
}

.actionStart, .actionResume {
  background-color: #2ecc71;
}

.actionStart:hover, .actionResume:hover {
  background-color: #29ad60;
}

.actionPause:hover {
  background-color: #eaeaea;
}

.actionEnd {
  background-color: #e74c3c;
}

.actionEnd:hover {
  background-color: #b94134;
}

  /* -------------------- */
 /* -- Texte d'erreur -- */
/* -------------------- */


.error {
  color: #c0392b;
  text-align: center;
  font-weight: 600;
  margin-top: 1rem;
}





  /* -------------------------------------- */
 /* -- Affichage des fautes des joueurs -- */
/* -------------------------------------- */

.fautes {
  position: absolute;
}

.droite {
  left: -2rem;
}

.gauche {
  right: -2rem;
}

.faute {
  background-color: #e74c3c;
  width: 1.2rem;
  height: 1.7rem;

  border-radius: 15%;

  text-align: center;
  font-size: 1rem;
  font-weight: bold;
}


.Info {
  margin-bottom: 2rem;
  text-align: center;
}

.Nom-Terrain {
  font-weight: bold;
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}

/* CARTE */
.map-container {
  height: 250px;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #ddd;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
  z-index: 0;
}

.lien-competition {
  cursor: pointer;
  transition: color 0.2s ease;
}

.lien-competition:hover {
  color: #3498db; /* Un bleu pour indiquer le lien, ou la couleur de ton thème */
  text-decoration: underline;
}




</style>
