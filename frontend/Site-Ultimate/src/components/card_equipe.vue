<!-- src/components/CarteEquipe.vue -->
<script setup>
import Image_base from "@/assets/img/img_equipe.jpg"
defineProps({
  equipe: {
    type: Object,
    required: true
  },
  image: {
    type: String,
    default: Image_base
  },
  licencie:{
    type: Boolean,
    required: false,
    default: true
  }
})
</script>

<template>
  <div class="competition-card">
    <img :src="image" alt="Image équipe" class="competition-img" />

    <div class="competition-info">
      <h3>{{ equipe.nomEquipe }}</h3>
      <p v-if="licencie">{{ equipe.nbJoueur }} licencié(e)s</p>
    </div>
  </div>
</template>

<style scoped>
.competition-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  overflow: hidden;
}

.competition-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 5px 10px rgba(0,0,0,0.15);
}

.competition-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.competition-info {
  padding: 1rem;
  text-align: center;
}
</style>
