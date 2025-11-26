<script setup>
import {computed} from "vue";

const props = defineProps({
  title: String,
  nom1: [Number, String],
  nom2: [Number, String],
  points1: [Number, String],
  points2: [Number, String],
})

const couleurPointsNom1 = computed(() => {
  if (props.points1 > props.points2) return 'vert'
  if (props.points1 < props.points2) return 'rouge'
  return 'or'
})

const couleurPointsNom2 = computed(() => {
  if (props.points2 > props.points1) return 'vert'
  if (props.points2 < props.points1) return 'rouge'
  return 'or'
})
</script>

<template>
  <div class="card_res">
    <div class="card-content">
      <h3>{{ title }}</h3>
      <div class="info">
        <div class="equipe">
          <p  :class="['nom_equipe', couleurPointsNom1]">{{ nom1 }}</p>
          <p class="points">{{ points1 }}</p>
        </div>

        <div class="equipe">
          <p :class="['nom_equipe', couleurPointsNom2]">{{ nom2 }}</p>
          <p class="points">{{ points2 }}</p>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.card_res {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 1rem 3rem;
  transition: transform 0.2s ease;
}

.card_res:hover {
  transform: translateY(4px);
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-left: 2rem;
  margin-right: 2rem;
}

.info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: row;
  gap: 8rem;
}

h3{
  text-align: center;
  margin:0;
  white-space: nowrap;
}
.equipe{
  display: flex;
  flex-direction: column;
  align-items: center;
}

.points{
  margin: 0;
}

.nom_equipe {
  margin: 0.5rem;
  font-weight: bold;
  white-space: nowrap;
}

.vert {
  color: green;
  margin: 0.5rem;
  font-weight: bold;
  white-space: nowrap;
}

.rouge {
  color: red;
  margin: 0.5rem;
  font-weight: bold;
  white-space: nowrap;
}

.or {
  color: gold;
  margin: 0.5rem;
  font-weight: bold;
  white-space: nowrap;
}
</style>