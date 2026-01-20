<script setup>
defineProps({
  terrains: { type: Array, default: () => [] },
  editMode: { type: Boolean, default: false }
})

const emit = defineEmits(['open-add', 'delete-terrain'])
</script>

<template>
  <section class="terrain-section">
    <h3>Terrains sélectionnés</h3>

    <div v-if="editMode" class="edit-actions">
      <button class="btn-primary" @click="emit('open-add')">
        Ajouter un terrain
      </button>
    </div>

    <ul v-if="terrains && terrains.length > 0" class="terrain-list">
      <li v-for="(terrain, i) in terrains" :key="terrain.idTerrain" class="terrain-wrapper">
        <button v-if="editMode" class="btn-delete" @click="emit('delete-terrain', { index: i, id: terrain.idTerrain })">
          Supprimer
        </button>

        <div class="terrain-card-display">
          <span class="terrain-nom">{{ terrain.nom }}</span>
          <span class="terrain-ville">{{ terrain.ville }}</span>
        </div>
      </li>
    </ul>

    <div v-else class="info-msg">
      <p>Aucun terrain n'a été sélectionné pour cette compétition.</p>
    </div>
  </section>
</template>

<style scoped>
.terrain-section {
  margin-top: 3rem;
  width: 100%;

}
.edit-actions {
  display: flex;
  gap: 1rem;
  margin: 1rem 0;
}

.terrain-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: center;
  padding: 0;
  list-style: none;
  margin: 2rem 0;
}

.terrain-wrapper { flex: 0 1 220px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.terrain-card-display {
  background: white;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 1.5rem 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  height: 100%;
  background-image: linear-gradient(to bottom right, #ffffff, #f9f9f9);
}

.terrain-nom {
  font-weight: bold;
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.terrain-ville {
  font-size: 0.9rem;
  color: #666;
  text-transform: uppercase;
  font-weight: 500;
}

.info-msg {
  text-align: center;
  color: #666;
  font-style: italic;
}
.btn-primary {
  background-color: #333;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.btn-delete {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 8px;
  cursor: pointer; }

</style>