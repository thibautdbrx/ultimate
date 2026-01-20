<script setup>
import { ref, watch, computed } from "vue"

// On attend ici la liste des terrains déjà présents dans la compétition pour les filtrer
const props = defineProps({
  show: Boolean,
  all: {
    type: Boolean,
    required: false,
    default: true,
  },
  terrain_utilise: {
    type: Array, // Attention, c'est un Array, pas un Object
    required: false,
    default: () => [],
  }
})

const emit = defineEmits(["close", "select", "nvt"]) // nvt = nouveau terrain

const loading = ref(false)
const search = ref("")
const terrains = ref([])

// Chargement des terrains depuis l'API
async function loadTerrains() {
  loading.value = true
  terrains.value = []

  try {
    const res = await fetch(`/api/terrain`) // Pas de filtre genre ici
    if (!res.ok) throw new Error("Erreur chargement terrains")
    terrains.value = await res.json()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// Rafraîchir la liste quand on ouvre l'overlay
watch(
    () => props.show,
    async (newVal) => {
      if (newVal) {
        await loadTerrains()
        search.value = "" // Reset recherche à l'ouverture
      }
    },
    { immediate: true }
)

// ID des terrains déjà sélectionnés dans la compétition
const usedIds = computed(() =>
    new Set((props.terrain_utilise ?? []).map(t => t.idTerrain))
)

// Filtrage (Exclusion des déjà pris + Recherche texte)
const filtered = computed(() => {
  const searchLower = search.value.toLowerCase()

  return terrains.value
      .filter(t => !usedIds.value.has(t.idTerrain)) // On cache ceux déjà présents
      .filter(t =>
          searchLower === "" ||
          t.nom.toLowerCase().includes(searchLower) ||
          t.ville.toLowerCase().includes(searchLower)
      )
})
</script>

<template>
  <div v-if="show" class="overlay-bg">

    <div class="overlay">
      <div class="titre">
        <h3>Choisir un terrain</h3>
        <button id="close-btt-haut" @click="emit('close')">Fermer</button>
        <button v-if="all" id="close-btt-haut" @click="emit('nvt')">Nouveau terrain</button>
      </div>

      <input
          type="text"
          class="search-input"
          placeholder="Rechercher par nom ou ville..."
          v-model="search"
      />

      <div class="terrains-grid">
        <div v-if="loading" class="loading">
          Chargement des terrains...
        </div>

        <div v-else-if="filtered.length === 0" class="no-result">
          Aucun terrain disponible ou correspondant à la recherche.
        </div>

        <div
            v-else
            v-for="t in filtered"
            :key="t.idTerrain"
            @click="emit('select', t)"
            class="terrain-card selectable"
        >
          <div class="card-content">
            <span class="t-nom">{{ t.nom }}</span>
            <span class="t-ville">{{ t.ville }}</span>
          </div>
        </div>
      </div>

      <button class="close-btn" @click="emit('close')">Fermer</button>

    </div>
  </div>
</template>

<style scoped>
.overlay-bg {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999; /* Important pour être au dessus de tout */
}

.overlay {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}

.titre {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

h3 {
  margin: 0;
}

#close-btt-haut {
  background: #ddd;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
}

.search-input {
  width: 100%;
  padding: 0.8rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  margin-bottom: 1rem;
  box-sizing: border-box; /* Pour éviter que ça dépasse */
}

.terrains-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
}

.loading, .no-result {
  width: 100%;
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
}

/* Style de la "Carte Terrain" sans composant externe */
.terrain-card {
  background: #f8f9fa;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  width: 200px; /* Largeur fixe pour faire une grille propre */
  padding: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  transition: transform 0.2s, box-shadow 0.2s;
}

.t-nom {
  font-weight: bold;
  font-size: 1.1rem;
  color: #333;
  display: block;
  margin-bottom: 0.4rem;
}

.t-ville {
  font-size: 0.9rem;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.selectable {
  cursor: pointer;
}

.selectable:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  border-color: #bbb;
  background: white;
}

.close-btn {
  margin-top: 1.5rem;
  padding: 0.7rem 1rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  width: 100%;
  background: #ddd;
  font-weight: bold;
}
</style>