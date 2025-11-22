<script setup>
defineProps({
  nom: String,
  prenom: String,
  genre: String,
  photo: String
})

import Photo_pnj from "@/assets/img_joueur/pnj.jpg";
</script>

<template>
  <div class="carte">
    <img :src="photo || Photo_pnj" alt="photo joueur" class="photo" />
    <h3>{{ nom }} {{prenom}}</h3>
    <p class="role">{{ genre }}</p>
  </div>
</template>

<style scoped>
.carte {
  background: #f7f7f7;
  border-radius: 12px;
  padding: 1rem;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.photo {
  width: 5rem;
  border-radius: 10px;
  margin-bottom: 1rem;
}
.role { color: #777; font-size: .9rem; }
</style>
