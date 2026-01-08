<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
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
const loadingActions = ref(true);

const match = ref(null);
const joueursEquipe1 = ref([]);
const joueursEquipe2 = ref([]);
const actions = ref([]);

let etatMatch = ref("WAITING")

const error = ref(null);

const auth = useAuthStore();

let duree = ref(null);

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

const calculDuree = computed(() => {
  const dateDebut = match.value.dateDebut;
  if (!dateDebut) return 0;

  let dureePause;
  dureePause = match.value.dureePause;
  if (!dureePause) {
    dureePause = 0;
  }

  duree = 0;

  if (etatMatch == "ONGOING") {
    duree = maintenant.value - Date.parse(dateDebut) - dureePause;
  }
  else if (etatMatch == "FINISHED") {
    const dateFin = match.value.dateFin;
    duree = Date.parse(match.value.dateFin) - Date.parse(dateDebut);
  }
  else if (etatMatch == "PAUSED") {
    const datePause = match.value.datePause;
    duree = Date.parse(match.value.datePause) - Date.parse(dateDebut);
  }
  


  return formatDuree(duree);
});

function calculTemps(dateAction) {
  const date = Date.parse(dateAction);
  const dateDebut = Date.parse(match.value.dateDebut);

  return formatDuree(date - dateDebut)
}

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

  await loadMatch();
  await loadPlayers();
  await loadActions();
}

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

  await loadMatch();
  await loadPlayers();
  await loadActions();
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

  await loadMatch();
  await loadPlayers();
  await loadActions();
} 


// ----------------------
const maintenant = ref(Date.now());
let interval = null;


onMounted(async () => {
  await loadMatch();
  await loadPlayers();
  await loadActions();

  interval = setInterval(() => {
    maintenant.value = Date.now();
  }, 1000);
});

onUnmounted(() => {
  clearInterval(interval);
});


</script>

<template>
  <main class="page">

    <!-- HEADER : Infos du match -->
    <section class="haut" v-if="match">

      <p v-if="!(etatMatch=='WAITING')" id="dureeMatch">{{ calculDuree }}</p>

      <h1>
        Championnat régional
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
                {{ calculTemps(i.dateAction) }} : {{ i.joueur.prenomJoueur }} {{ i.joueur.nomJoueur }}
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
                {{ calculTemps(i.dateAction) }} : {{ i.joueur.prenomJoueur }} {{ i.joueur.nomJoueur }}
              </p>
              </div>
            
          </div>
          
        </div>
      </div>

      <div class="date">
        <p v-if="!(etatMatch == 'WAITING')">Début : {{ new Date(match.dateDebut).toLocaleString() }}</p>
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
        
        <div class="joueurDiv" v-for="j in joueursEquipe1">

          <div class="boutons">
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(match.equipe1.idEquipe,1,j.idJoueur)" class="boutonScore boutonPlus">+</button>
            <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutFaute(match.equipe1.idEquipe,j.idJoueur)" class="boutonScore boutonMoins">X</button>
          </div>
          <Card_joueur
              
              :key="j.idJoueur"
              :nom="j.nomJoueur + ' ' + j.prenomJoueur"
              :genre="j.genre"
              :photo="j.photoJoueur"
              background="#ffdddd"
          />

          <div class="fautes gauche" v-for="action in actions">
          <p v-if="action.type=='FAUTE' && action.joueur.idJoueur == j.idJoueur" class="faute"></p>
          </div>
        
        </div>
      
      </div>

      <!-- COLONNE MILIEU -->
      <div class="middle">
        <h3>Informations du Match</h3>
        <p v-if="auth.isAdmin || auth.isArbitre" ></p>

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
        
        <div class="joueurDiv" v-for="j in joueursEquipe2">

        <div class="boutons">
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutPoint(match.equipe2.idEquipe,1,j.idJoueur)" class="boutonScore boutonPlus">+</button>
          <button v-if="(auth.isAdmin || auth.isArbitre) && etatMatch == 'ONGOING'" @click="AjoutFaute(match.equipe2.idEquipe,j.idJoueur)" class="boutonScore boutonMoins">X</button>
        </div>
        <Card_joueur
            
            :key="j.idJoueur"
            :nom="j.nomJoueur + ' ' + j.prenomJoueur"
            :genre="j.genre"
            :photo="j.photoJoueur"
            background="#ffdddd"
        />

        <div class="fautes droite" v-for="action in actions">
          <p v-if="action.type=='FAUTE' && action.joueur.idJoueur == j.idJoueur" class="faute"></p> 
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

/* COLONNES EQUIPES */
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

.middle h3 {
  text-align: center;
  margin-bottom: 1rem;
  font-weight: 600;
}


#actionsMatch {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  margin-top: 1.5rem;
}

.boutonAction {
  padding: 0.7rem;
  border-radius: 6px;
  border: none;
  background: #f4f4f4;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.15s ease;
}

.boutonAction:hover {
  background: #eaeaea;
  transform: translateY(-1px);
}


.error {
  color: #c0392b;
  text-align: center;
  font-weight: 600;
  margin-top: 1rem;
}


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
  background-color: red;
  width: 1.2rem;
  height: 1.7rem;

  border-radius: 15%;

  text-align: center;
  font-size: 1rem;
  font-weight: bold;
}



</style>
