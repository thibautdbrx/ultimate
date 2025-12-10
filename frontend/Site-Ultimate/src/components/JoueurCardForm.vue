<script setup>
import champs_input from "@/components/champs_input.vue"
import UserIcon from "@/assets/icons/avatar.svg"

const props = defineProps({
  joueur: Object,
})
</script>

<template>
  <div class="joueur-card">

    <champs_input
        label="Nom"
        placeholder="Nom"
        v-model="joueur.nom"
        :icon="UserIcon"
        :clickable = joueur.clickable
    />

    <champs_input
        label="Prénom"
        placeholder="Prénom"
        v-model="joueur.prenom"
        :icon="UserIcon"
        :clickable = joueur.clickable
    />

    <label class="photo-label">
      Genre :
      <select v-if="joueur.clickable" class="genre-select" v-model="joueur.genre">
        <option value="MALE">MALE</option>
        <option value="FEMALE">FEMALE</option>
      </select>

      <select v-else class="genre-select" v-model="joueur.genre" disabled>
        <option value="MALE">MALE</option>
        <option value="FEMALE">FEMALE</option>
      </select>
    </label>

      <label v-if="joueur.clickable" class="photo-label">
        Photo du joueur :
        <input class="photo_input" type="file" @change="e => joueur.photo = e.target.files[0]" />
      </label>






  </div>
</template>

<style scoped>
.joueur-card {
  padding: 1rem;
  border-radius: 10px;
  border: 1px solid #ddd;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  width: 260px;
}





.genre-select {
  border-radius: 8px;
  padding: 0.6rem;
  border: 1px solid #ccc;
  margin-right: 50%;
}
.photo-label {
  font-size: 0.85rem;
  color: #444;
}
</style>
