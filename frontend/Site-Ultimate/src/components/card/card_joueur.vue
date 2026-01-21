<script setup>
import {computed} from "vue";

const props = defineProps({
  nom: String,
  prenom: String,
  genre: String,
  photo: String
})

import Photo_pnj from "@/assets/img_joueur/pnj.jpg";
const name = computed(() => props.nom.toUpperCase())
const prename = computed(() => props.prenom.toLowerCase())
</script>

<template>
  <div class="carte">
    <img :src="Photo_pnj" alt="photo joueur" class="photo" v-if="photo==null"/>
    <img :src="`http://localhost:8080${photo}`"  alt="photo joueur" class="photo" v-else/>
    <h3>{{prename}} {{ name }} </h3>
    <p class="role">{{ genre }}</p>
  </div>
</template>

<style scoped>
.carte {
  width: 10rem;
  height: 14rem;
  background: #ececec;
  border-radius: 12px;
  padding: 1rem;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.photo {
  min-height: 5rem;
  max-height: 5rem;

  border-radius: 10px;
  margin-bottom: 1rem;
}
.role { color: #777; font-size: .9rem; }
</style>
