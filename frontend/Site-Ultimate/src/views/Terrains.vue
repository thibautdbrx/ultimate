<script setup>
import { ref, onMounted } from 'vue'
import "leaflet/dist/leaflet.css"
import { LMap, LTileLayer, LMarker, LTooltip } from "@vue-leaflet/vue-leaflet"
import axios from 'axios' // Import nécessaire pour l'appel externe

// Import des composants
import weather_card from "@/components/weather_card.vue"
import PUB from "@/components/PUB.vue"
import CarteMatch from "@/components/card_match.vue"
import api from '@/services/api' // Import de l'instance Axios
import { useAuthStore } from "@/stores/auth"

// Import des nouveaux sous-composants
import TerrainList from "@/components/terrains/TerrainList.vue"
import TerrainMap from "@/components/terrains/TerrainMap.vue"
import TerrainForm from "@/components/terrains/TerrainForm.vue"
import TerrainDetail from "@/components/terrains/TerrainDetail.vue"

const auth = useAuthStore()

// --- State Global de la page ---
const terrains = ref([])
const loading = ref(false)
const error = ref(null)

const selectedTerrain = ref(null)
const isAdding = ref(false)
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

// Météo (API Externe : utilisation de axios pour remplacer fetch)
const loadWeather = async (lat, lon) => {
  weather.value = null;
  if (lat === null || lat === undefined || lon === null || lon === undefined) return;

  try {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,wind_speed_10m,weather_code`;
    const res = await axios.get(url);
    weather.value = res.data;
  } catch (e) {
    console.error("Impossible de charger la météo", e);
  }
};

// Matchs des terrains
const loadTerrainMatches = async (idTerrain) => {
  loadingMatches.value = true;
  terrainMatches.value = [];
// Config Map
const mapZoom = ref(6)
const mapCenter = ref([46.603354, 1.888334])

// --- API ---
async function fetchTerrains() {
  loading.value = true
  try {
    const response = await api.get('/terrain')
    terrains.value = response.data
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function handleCreateTerrain(formData) {
  submitting.value = true
  try {
    const res = await fetch('/api/terrain', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    })

    if (!res.ok) throw new Error("Erreur lors de la création")

    await api.post('/terrain', newTerrain.value)
    await fetchTerrains()
    isAdding.value = false
    alert("Terrain ajouté avec succès !")
  } catch (e) {
    alert(e.message)
  } finally {
    submitting.value = false
  }
}

// --- Interactions ---
function handleSelect(terrain) {
  selectedTerrain.value = terrain
  isAdding.value = false
}

function startAdding() {
  selectedTerrain.value = null
  isAdding.value = true
}

function closePanel() {
  selectedTerrain.value = null
  isAdding.value = false
}

onMounted(fetchTerrains)
</script>

<template>
  <div class="page-container">

    <TerrainList
        class="sidebar-area"
        :terrains="terrains"
        :selected-id="selectedTerrain?.id_terrain"
        :loading="loading"
        :error="error"
        :is-admin="auth.isAdmin"
        @select="handleSelect"
        @add-click="startAdding"
    />

    <div class="main-content">

      <TerrainForm
          v-if="isAdding"
          :submitting="submitting"
          @submit="handleCreateTerrain"
          @cancel="closePanel"
      />

      <TerrainDetail
          v-else-if="selectedTerrain"
          :terrain="selectedTerrain"
          @close="closePanel"
      />

      <TerrainMap
          v-else
          :terrains="terrains"
          v-model:zoom="mapZoom"
          v-model:center="mapCenter"
          @select-marker="handleSelect"
      />

    </div>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  height: calc(100vh - 190px); /* Ajuste selon ton header */
  overflow: hidden;
  box-shadow: inset 0 10px 15px -10px rgba(0,0,0,0.15);
  border-top: 1px solid rgba(0,0,0,0.05);
}

.sidebar-area {
  flex-shrink: 0; /* Empêche la sidebar de s'écraser */
}

.main-content {
  flex: 1;
  position: relative;
  height: 100%;
  background: white;
}
</style>