<script setup>
import { computed, onMounted } from 'vue'
import {useRouter} from 'vue-router'
const router = useRouter()

const props = defineProps({
  match: Object,
  title: [Number, String],

})

const couleurPointsNom1 = computed(() => {
  if (props.match.status !=="FINISHED") return 'noir'
  if (props.match.scoreEquipe1 > props.match.scoreEquipe2) return 'vert'
  if (props.match.scoreEquipe1 < props.match.scoreEquipe2) return 'rouge'
  return 'or'
})

const couleurPointsNom2 = computed(() => {
    if (props.match.status !== "FINISHED") return 'noir'
  if (props.match.scoreEquipe2 > props.match.scoreEquipe1) return 'vert'
  if (props.match.scoreEquipe2 < props.match.scoreEquipe1) return 'rouge'
  return 'or'
})

const afficherPoints = computed(() => {
  return (props.match.status === "FINISHED")
})

function goToMatch(id) {
  router.push(`/match/${id}`)
}
</script>



<template>
  <div class="card_res" @click="goToMatch(props.match.idMatch)">
    <div class="card-content">
      <h3>{{ title }}</h3>
      <div class="info">
        <div class="equipe">
          <p :class="['nom_equipe', couleurPointsNom1]">{{ props.match.equipe1.nomEquipe }}</p>
          <p v-if="afficherPoints" :class="['points', couleurPointsNom1]">{{ props.match.scoreEquipe1 }}</p>
          <p v-else>---</p>
        </div>

        <div class="equipe">
          <p :class="['nom_equipe', couleurPointsNom2]">{{ props.match.equipe2.nomEquipe }}</p>
          <p v-if="afficherPoints" :class="['points', couleurPointsNom2]">{{ props.match.scoreEquipe2 }}</p>
          <p v-else>---</p>
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
  padding: 1rem 2rem;
  transition: transform 0.2s ease;
  height: 7rem;
  width: 35rem;
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

.noir {
  color: black;
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
  color: goldenrod;
  margin: 0.5rem;
  font-weight: bold;
  white-space: nowrap;
}


</style>