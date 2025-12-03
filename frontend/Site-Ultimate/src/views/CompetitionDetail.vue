<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import SliderCardHorizontal from "@/components/Slider_card_horizontal.vue";
import CardMatch from "@/components/card_match.vue";

const route = useRoute()
const competitionId = route.params.id

const competition = ref(null)

const matches = ref([])
const loading = ref(true)
const error = ref(null)

const auth = useAuthStore();

onMounted(() => {
  fetch(`api/competitions/${competitionId}`)
      .then(res => {
        if (!res.ok) throw new Error(`Erreur HTTP: ${res.status}`)
        return res.json()
      })
      .then(data => {
        competition.value = data.competition
        matches.value = data.matches
        loading.value = false
      })
      .catch(err => {
        console.error(err)
        //error.value = "Impossible de charger les matchs de cette compétition."
        //loading.value = false
      })
})

</script>


<template>
  <main class="competition-details">

    <h2 v-if="competition">
      "NOM" - {{ competition.format }} - {{ competition.genre }} - {{nbTeams}} équipes inscrites
    </h2>

    <div v-if="loading" class="state-msg">Chargement...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else class="prochain_matches">

      <!-- Matchs prochains -->
      <h3>Matchs prochains</h3>
      <SliderCardHorizontal>
        <div v-for="match in upcomingMatches" :key="match.idMatch">
          <CardMatch
              :title="match.dateMatch"
              :nom1="match.equipe1.nomEquipe"
              :nom2="match.equipe2.nomEquipe"
              :points1="match.scoreEquipe1"
              :points2="match.scoreEquipe2"
              :fini="false"
          />
        </div>
      </SliderCardHorizontal>

      <!-- Matchs terminés -->
      <h3>Matchs finis</h3>
      <SliderCardHorizontal>
        <div v-for="match in finishedMatches" :key="match.idMatch">
          <CardMatch
              :title="match.dateMatch"
              :nom1="match.equipe1.nomEquipe"
              :nom2="match.equipe2.nomEquipe"
              :points1="match.scoreEquipe1"
              :points2="match.scoreEquipe2"
              :fini="true"
          />
        </div>
      </SliderCardHorizontal>

    </div>
  </main>
</template>



<style scoped>
.competition-details {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}

h2 {
  margin-bottom: 1.5rem;
}

.state-msg {
  color: #555;
}

.state-msg.error {
  color: #e74c3c;
}

.prochain_matches {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 80%;
  max-width: max-content;
}



.match-title {
  font-weight: bold;
}

.match-score {
  font-size: 1.2rem;
  margin: 0.5rem 0;
}
</style>
