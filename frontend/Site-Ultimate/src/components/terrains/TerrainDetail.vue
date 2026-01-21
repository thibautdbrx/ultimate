<script setup>
import { ref, watch, onMounted } from 'vue'
import weather_card from "@/components/card/weather_card.vue"
import CarteMatch from "@/components/card/card_match.vue"
import PUB from "@/components/PUB.vue"

const props = defineProps({
  terrain: { type: Object, required: true }
})

const emit = defineEmits(['close'])

const weather = ref(null)
const matches = ref([])
const loadingMatches = ref(false)

// --- Fonctions de chargement ---
const loadWeather = async () => {
  weather.value = null;
  const { latitude, longitude } = props.terrain;
  if (!latitude || !longitude) return;

  try {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&current=temperature_2m,wind_speed_10m,weather_code`;
    const res = await fetch(url);
    if (!res.ok) throw new Error("Erreur météo");
    weather.value = await res.json();
  } catch (e) { console.error(e); }
};

const loadMatches = async () => {
  loadingMatches.value = true;
  matches.value = [];
  try {
    const res = await fetch(`/api/match/terrains/${props.terrain.idTerrain}`);
    if (res.ok) {
      const data = await res.json();
      matches.value = data.sort((a, b) => new Date(b.dateDebut) - new Date(a.dateDebut));
    }
  } catch (e) { console.error(e); }
  finally { loadingMatches.value = false; }
}

const formatDate = (isoString) => {
  if (!isoString) return 'Date à définir'
  return new Date(isoString).toLocaleDateString('fr-FR', {
    day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit'
  })
}

// Recharger les données si le terrain change
watch(() => props.terrain, () => {
  loadWeather()
  loadMatches()
}, { immediate: true })

</script>

<template>
  <div class="details-panel">
    <button class="btn-back" @click="emit('close')">Retour à la carte</button>

    <div class="terrain-header">
      <h1>{{ terrain.nom }}</h1>
      <p class="location">📍 {{ terrain.ville }}</p>
    </div>

    <div class="weather-widget">
      <h3>Météo actuelle</h3>
      <div v-if="weather">
        <weather_card
            :code="weather.current.weather_code"
            :temp="weather.current.temperature_2m"
            :temp_unit="weather.current_units.temperature_2m"
            :wind="weather.current.wind_speed_10m"
            :wind_unit="weather.current_units.wind_speed_10m"
        />
      </div>
      <div v-else class="weather-loading">Chargement de la météo...</div>
    </div>

    <div class="terrain-info">
      <h3>Matchs sur ce terrain</h3>

      <div v-if="loadingMatches" class="match-loading">Chargement des matchs...</div>
      <div v-else-if="matches.length === 0" class="no-match">Aucun match programmé.</div>

      <div v-else class="match-list">
        <div class="match-item" v-for="match in matches" :key="match.idMatch">
          <CarteMatch
              :match="match"
              :title="match.dateDebut ? formatDate(match.dateDebut) : 'Date à venir'"
          />
        </div>
      </div>
    </div>

    <PUB style="margin-top: 3rem; display: flex; justify-content: center;" />
  </div>
</template>

<style scoped>
.details-panel {
  padding: 2rem;
  height: 100%;
  overflow-y: auto;
  background: white;
  box-sizing: border-box;
  animation: fadeIn 0.3s ease;
}
.btn-back {
  background: white;
  border: 1px solid #ccc;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.btn-back:hover {
  background: #f0f0f0; }

.terrain-header {
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
}
.terrain-header h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
}
.location {
  color: #666;
  font-size: 1.2rem; }

.weather-widget {
  margin-bottom: 2rem;

}
.weather-widget h3 { margin-top: 0;
  margin-bottom: 1rem;
  color: #34495e;}

.terrain-info h3 {
  margin-bottom: 1.5rem;
  color: #34495e;
}

.match-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding-bottom: 2rem; }

.match-item {
  transform: scale(0.95);
  margin: -5px 0;
  width: 100%;
  display: flex;
  justify-content: center;
}
.no-match {
  text-align: center;
  color: #7f8c8d;
  font-style: italic;
  padding: 2rem;
  background: #f9f9f9;
  border-radius: 8px; }

.match-loading {
  text-align: center;
  padding: 1rem; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>