<script setup>
import { ref, onMounted } from 'vue'
import "leaflet/dist/leaflet.css"
import { LMap, LTileLayer, LMarker, LTooltip } from "@vue-leaflet/vue-leaflet"

// Import des composants
import weather_card from "@/components/weather_card.vue"
import PUB from "@/components/PUB.vue"
import CarteMatch from "@/components/card_match.vue" 
import { useAuthStore } from "@/stores/auth"

const auth = useAuthStore()

// State
const terrains = ref([])
const selectedTerrain = ref(null)
const loading = ref(false)
const error = ref(null)
const weather = ref(null)

// State pour les matchs du terrain
const terrainMatches = ref([])
const loadingMatches = ref(false)

// Configuration carte
const zoom = ref(6)
const center = ref([46.603354, 1.888334]) // centre de france

// State Ajout Terrain
const isAdding = ref(false)
const newTerrain = ref({
  nom: '',
  ville: '',
  latitude: null,
  longitude: null
})
const submitting = ref(false)

// ==================
//      LOGIQUE 
// ==================

const formatDate = (isoString) => {
  if (!isoString) return 'Date à définir'
  return new Date(isoString).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Météo
const loadWeather = async (lat, lon) => {
  weather.value = null; 
  if (lat === null || lat === undefined || lon === null || lon === undefined) return;

  try {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,wind_speed_10m,weather_code`;
    const res = await fetch(url);
    if (!res.ok) throw new Error("Erreur météo");
    weather.value = await res.json();
  } catch (e) {
    console.error("Impossible de charger la météo", e);
  }
};

// Matchs des terrains
const loadTerrainMatches = async (idTerrain) => {
  loadingMatches.value = true;
  terrainMatches.value = []; 

  try {
    const res = await fetch(`/api/match/terrains/${idTerrain}`);
    if (!res.ok) throw new Error("Erreur lors du chargement des matchs");
    
    terrainMatches.value = await res.json();
    terrainMatches.value.sort((a, b) => new Date(b.dateDebut) - new Date(a.dateDebut)); 
  }
  catch (e) { console.error(e); }
  finally {loadingMatches.value = false;}
}

// terrains
async function fetchTerrains() {
  loading.value = true
  try {
    const response = await fetch('/api/terrain')
    if (!response.ok) throw new Error('Impossible de charger les terrains')
    terrains.value = await response.json()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

function selectTerrain(terrain) {
  selectedTerrain.value = terrain
  isAdding.value = false; // CORRECTION: .value
  loadWeather(terrain.latitude, terrain.longitude)
  loadTerrainMatches(terrain.id_terrain)
}

// CORRECTION: Renommé en startAdding pour correspondre au template
function startAdding() {
  selectedTerrain.value = null; // CORRECTION: selectedTerrain (variable) et non selectTerrain (fonction)
  isAdding.value = true;
  newTerrain.value = { nom: '', ville: '', latitude: null, longitude: null };
}

function closeDetail() {
  selectedTerrain.value = null
  isAdding.value = false;
  weather.value = null
  terrainMatches.value = []
}

async function createTerrain() {
  submitting.value = true
  try {
    const res = await fetch('/api/terrain', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newTerrain.value)
    })
    
    if (!res.ok) throw new Error("Erreur lors de la création")
    
    await fetchTerrains()
    isAdding.value = false
    alert("Terrain ajouté avec succès !")
  } catch (e) {
    alert(e.message)
  } finally {
    submitting.value = false
  }
}

onMounted(fetchTerrains)
</script>

<template>
  <div class="page-container">
    
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>Nos Terrains</h2>
            <button v-if="auth.isAdmin" @click="startAdding" class="btn-add"> + Ajouter </button>
        </div>

      <div v-if="loading">Chargement...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else class="list-container">
        <div 
          v-for="terrain in terrains" 
          :key="terrain.id_terrain" 
          class="list-item"
          :class="{ active: selectedTerrain?.id_terrain === terrain.id_terrain }"
          @click="selectTerrain(terrain)"
        >
          <div class="item-header">
            <strong>{{ terrain.nom }}</strong>
            <span class="city">{{ terrain.ville }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">

      <div v-if="isAdding" class="details-panel form-mode">
        <button class="btn-back" @click="closeDetail">Annuler</button>
        <h1>Ajouter un nouveau terrain</h1>
        
        <form @submit.prevent="createTerrain" class="terrain-form">
          <div class="form-group">
            <label>Nom du terrain</label>
            <input v-model="newTerrain.nom" type="text" required placeholder="Ex: Stade de la Meinau">
          </div>
          
          <div class="form-group">
            <label>Ville</label>
            <input v-model="newTerrain.ville" type="text" required placeholder="Ex: Strasbourg">
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Latitude (Optionnel)</label>
              <input v-model="newTerrain.latitude" type="number" step="any" placeholder="48.5839">
            </div>
            <div class="form-group">
              <label>Longitude (Optionnel)</label>
              <input v-model="newTerrain.longitude" type="number" step="any" placeholder="7.7455">
            </div>
          </div>
          <p class="hint">Si les coordonnées sont vides, le serveur tentera de les trouver via la ville.</p>

          <button type="submit" class="btn-submit" :disabled="submitting">
            {{ submitting ? 'Création...' : 'Valider la création' }}
          </button>
        </form>
      </div>
      
      <div v-else-if="!selectedTerrain" class="map-wrapper">
        <l-map ref="map" v-model:zoom="zoom" :center="center" :use-global-leaflet="false">
          <l-tile-layer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"></l-tile-layer>
          <l-marker 
            v-for="terrain in terrains"
            :key="terrain.id_terrain"
            :lat-lng="[terrain.latitude, terrain.longitude]"
            @click="selectTerrain(terrain)"
          >
            <l-tooltip>{{ terrain.nom }}</l-tooltip>
          </l-marker>
        </l-map>
      </div>

      <div v-else class="details-panel">
        <button class="btn-back" @click="closeDetail">Retour à la carte</button>
        
        <div class="terrain-header">
          <h1>{{ selectedTerrain.nom }}</h1>
          <p class="location">📍 {{ selectedTerrain.ville }}</p>
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
           
           <div v-else-if="terrainMatches.length === 0" class="no-match">
             Aucun match programmé pour le moment.
           </div>

           <div v-else class="match-list">
             <div class="match-item" v-for="match in terrainMatches" :key="match.idMatch">
                <CarteMatch
                  :match="match"
                  :title="match.dateDebut ? formatDate(match.dateDebut) : 'Date à venir'"
                />
             </div>
           </div>
        </div>

        <PUB style="margin-top: 3rem; display: flex; justify-content: center;" />

      </div>

    </div>
  </div>
</template>

<style scoped>
/* global */
.page-container { 
  display: flex; 
  height: calc(100vh - 190px); 
  overflow: hidden; 
  margin-bottom: 0;
  box-shadow: inset 0 10px 15px -10px rgba(0,0,0,0.15); /* Ombre ajoutée */
  border-top: 1px solid rgba(0,0,0,0.05);
}

/* coté gacuhe : liste */
.sidebar { 
  width: 350px; 
  background: #f8f9fa; 
  border-right: 1px solid #ddd; 
  display: flex; 
  flex-direction: column; 
  padding: 1rem; 
  overflow-y: auto; 
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.sidebar-header h2 {
  margin: 0;
  color: #2c3e50;
  font-weight: bold;
  font-size: 1.5rem;
}

.btn-add {
  background-color: #2ecc71;
  color: white;
  border: none;
  padding: 0.5rem 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.2s;
}

.btn-add:hover {
  background-color: #27ae60;
}

.title {
  margin-bottom: 1rem;
  color: #2c3e50;
  font-weight: bold;
}

.list-container { 
  display: flex; 
  flex-direction: column; 
  gap: 0.5rem; 
}

.list-item { 
  background: white; 
  padding: 1rem; 
  border-radius: 8px; 
  cursor: pointer; 
  border: 1px solid #eee; 
  transition: all 0.2s;
}

.list-item:hover { 
  background: #eef2ff; 
  border-color: #2563eb; 
  transform: translateX(4px);
}

.list-item.active { 
  background: #2563eb; 
  color: white; 
  border-color: #2563eb;
}

.list-item.active .city {
  color: #e0e0e0;
}

.item-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center;
}

.city { 
  font-size: 0.85rem; 
  color: #666; 
}

/* Coté droit */
.main-content { 
  flex: 1; 
  position: relative; 
  height: 100%; 
}

.map-wrapper { 
  height: 100%; 
  width: 100%; 
}

.details-panel { 
  padding: 2rem; 
  height: 100%; 
  overflow-y: auto; 
  background: white; 
  animation: fadeIn 0.3s ease;
  box-sizing: border-box;
}

/* --- STYLES DU FORMULAIRE D'AJOUT --- */
.form-mode {
  max-width: 600px;
  margin: 0 auto;
}

.terrain-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-top: 2rem;
  background: #f9f9f9;
  padding: 2rem;
  border-radius: 12px;
  border: 1px solid #eee;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

label {
  font-weight: 600;
  color: #2c3e50;
}

input {
  padding: 0.8rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
}

.hint {
  font-size: 0.85rem;
  color: #666;
  font-style: italic;
  margin-top: -0.5rem;
}

.btn-submit {
  background-color: #2563eb;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 6px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 1rem;
  transition: background 0.2s;
}

.btn-submit:hover {
  background-color: #1d4ed8;
}

.btn-submit:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

/* --- FIN STYLES FORMULAIRE --- */

.btn-back { 
  background: white; 
  border: 1px solid #ccc; 
  padding: 0.5rem 1rem; 
  cursor: pointer; 
  border-radius: 4px; 
  margin-bottom: 1rem; 
  transition: background 0.2s;
}
.btn-back:hover {
  background: #f0f0f0;
}

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
  font-size: 1.2rem; 
}

.weather-widget { margin-bottom: 2rem; }
.weather-widget h3 { margin-top: 0; margin-bottom: 1rem; color: #34495e;}
.weather-loading { text-align: center; color: #7f8c8d; font-style: italic; }

.terrain-info h3 {
  margin-bottom: 1.5rem;
  color: #34495e;
}

.match-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding-bottom: 2rem; 
}

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
  border-radius: 8px;
}

.match-loading {
  text-align: center;
  padding: 1rem;
}

.state-msg { text-align: center; margin-top: 1rem; color: #7f8c8d;}
.error { color: red; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>