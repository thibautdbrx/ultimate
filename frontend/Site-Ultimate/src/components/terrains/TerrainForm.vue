<script setup>
import { ref } from 'vue'

defineProps({
  submitting: { type: Boolean, default: false }
})

const emit = defineEmits(['submit', 'cancel'])

const formData = ref({
  nom: '',
  ville: '',
  latitude: null,
  longitude: null
})

const handleSubmit = () => {
  emit('submit', formData.value)
}
</script>

<template>
  <div class="details-panel form-mode">
    <button class="btn-back" @click="emit('cancel')">Annuler</button>
    <h1>Ajouter un nouveau terrain</h1>

    <form @submit.prevent="handleSubmit" class="terrain-form">
      <div class="form-group">
        <label>Nom du terrain</label>
        <input v-model="formData.nom" type="text" required placeholder="Ex: Stade de la Meinau">
      </div>

      <div class="form-group">
        <label>Ville</label>
        <input v-model="formData.ville" type="text" required placeholder="Ex: Strasbourg">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Latitude (Optionnel)</label>
          <input v-model="formData.latitude" type="number" step="any" placeholder="48.5839">
        </div>
        <div class="form-group">
          <label>Longitude (Optionnel)</label>
          <input v-model="formData.longitude" type="number" step="any" placeholder="7.7455">
        </div>
      </div>
      <p class="hint">Si les coordonnées sont vides, le serveur tentera de les trouver via la ville.</p>

      <button type="submit" class="btn-submit" :disabled="submitting">
        {{ submitting ? 'Création...' : 'Valider la création' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.form-mode {
  max-width: 600px;
  margin: 0 auto; padding: 2rem;
  background: white; height: 100%;
  overflow-y: auto;
}

.terrain-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-top: 2rem;
  background: #f9f9f9;
  padding: 2rem;
  border-radius: 12px;
  border: 1px solid #eee; }
.form-group {
  display: flex;
  flex-direction: column;

  gap: 0.5rem; }

.form-row {
  display: flex;
  gap: 1rem;
}
.form-row .form-group { flex: 1; }

label {
  font-weight: 600;
  color: #2c3e50; }

input {
  padding: 0.8rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem; }

.hint {
  font-size: 0.85rem;
  color: #666;
  font-style: italic;
  margin-top: -0.5rem; }

.btn-submit {
  background-color: #2563eb;
  color: white; border: none;
  padding: 1rem; border-radius: 6px;
  font-size: 1.1rem; font-weight: bold;
  cursor: pointer; margin-top: 1rem;
  transition: background 0.2s;

}

.btn-submit:hover {
  background-color: #1d4ed8; }

.btn-submit:disabled {

  background-color: #93c5fd;
  cursor: not-allowed; }


.btn-back { background: white;
  border: 1px solid #ccc;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 1rem; }

.btn-back:hover { background: #f0f0f0; }
</style>