<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from "@/stores/auth"

// Import des sous-composants
import TerrainList from "@/components/terrains/TerrainList.vue"
import TerrainMap from "@/components/terrains/TerrainMap.vue"
import TerrainForm from "@/components/terrains/TerrainForm.vue"
import TerrainDetail from "@/components/terrains/TerrainDetail.vue"

const auth = useAuthStore()

// --- State Global ---
const terrains = ref([])
const loading = ref(false)
const error = ref(null)

const selectedTerrain = ref(null)
const isAdding = ref(false)
const submitting = ref(false)

// Config Map
const mapZoom = ref(6)
const mapCenter = ref([46.603354, 1.888334])

// --- API ---

// 1. Charger la liste des terrains
async function fetchTerrains() {
  loading.value = true
  try {
    const response = await api.get('/terrain')
    terrains.value = response.data
  } catch (e) {
    error.value = "Impossible de charger les terrains."
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 2. Créer un terrain (Reçoit les données depuis TerrainForm)
async function handleCreateTerrain(formData) {
  submitting.value = true
  try {
    // Utilisation propre de l'instance Axios
    await api.post('/terrain', formData)

    await fetchTerrains() // Recharger la liste
    isAdding.value = false
    alert("Terrain ajouté avec succès !")
  } catch (e) {
    const msg = e.response?.data?.message || e.message
    alert("Erreur : " + msg)
  } finally {
    submitting.value = false
  }
}

// --- Interactions ---

function handleSelect(terrain) {
  selectedTerrain.value = terrain
  isAdding.value = false
  // Si TerrainDetail a besoin de charger la météo/matchs,
  // il le fera lui-même grâce à la prop "terrain" qui change.
}

function startAdding() {
  selectedTerrain.value = null
  isAdding.value = true
}

function closePanel() {
  selectedTerrain.value = null
  isAdding.value = false
}

// Init
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
  height: calc(100vh - 190px); /* Ajuste selon la hauteur de ton header/nav */
  overflow: hidden;
  box-shadow: inset 0 10px 15px -10px rgba(0,0,0,0.15);
  border-top: 1px solid rgba(0,0,0,0.05);
}

.sidebar-area {
  flex-shrink: 0; /* Garde sa largeur définie dans le composant */
  height: 100%;
}

.main-content {
  flex: 1;
  position: relative;
  height: 100%;
  background: white;
  overflow: hidden; /* Important pour que la map ne déborde pas */
}
</style>