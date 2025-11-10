<script setup lang="ts">
defineProps({
  title: String,
  nom1: String,
  nom2: String,
  points1: Number,
  points2: Number,
})
</script>

<template>
  <div class="card_res">
    <div class="card-content">
      <h3>{{ title }}</h3>
      <div class="info">
        <div class="equipe">
          <p :style="{color: points1 > points2 ? 'Red' : 'Green'}" class="nom_equipe">{{ nom1 }}</p>
          <p class="points">{{ points1 }}</p>
        </div>

        <div class="equipe">
          <p :style="{color: points1 > points2 ? 'green' : 'Red'}" class="nom_equipe">{{ nom2 }}</p>
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
  padding: 1rem 7rem;
  width: 220px;
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

.nom_equipe{
  margin: 0.5rem;
  font-weight: bold;
}

</style>