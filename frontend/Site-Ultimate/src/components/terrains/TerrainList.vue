<script setup>
defineProps({
  terrains: { type: Array, required: true },
  selectedId: { type: [Number, String], default: null },
  loading: { type: Boolean, default: false },
  error: { type: String, default: null },
  isAdmin: { type: Boolean, default: false }
})

const emit = defineEmits(['select', 'add-click'])
</script>

<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <h2>Nos Terrains</h2>
      <button v-if="isAdmin" @click="emit('add-click')" class="btn-add"> + Ajouter </button>
    </div>

    <div v-if="loading">Chargement...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="list-container">
      <div
          v-for="terrain in terrains"
          :key="terrain.id_terrain"
          class="list-item"
          :class="{ active: selectedId === terrain.id_terrain }"
          @click="emit('select', terrain)"
      >
        <div class="item-header">
          <strong>{{ terrain.nom }}</strong>
          <span class="city">{{ terrain.ville }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;
}
.sidebar-header h2 { margin: 0; color: #2c3e50; font-size: 1.5rem; font-weight: bold; }

.btn-add {
  background-color: #2ecc71; color: white; border: none; padding: 0.5rem 0.8rem;
  border-radius: 6px; cursor: pointer; font-weight: bold; transition: background 0.2s;
}
.btn-add:hover {
  background-color: #27ae60; }

.list-container {
  display: flex; flex-direction: column; gap: 0.5rem; }
.list-item {
  background: white; padding: 1rem; border-radius: 8px; cursor: pointer;
  border: 1px solid #eee; transition: all 0.2s;
}
.list-item:hover {
  background: #eef2ff;
  border-color: #2563eb;
  transform: translateX(4px);
}
.list-item.active {
  background: #2563eb;
  color: white;
  border-color: #2563eb; }

.list-item.active .city { color: #e0e0e0; }

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;}

.city { font-size: 0.85rem; color: #666; }
.error { color: red; }
</style>