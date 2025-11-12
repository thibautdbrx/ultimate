<script setup >
defineProps({
  nom: String,
  age: String,
  sexe: String
})
</script>

<template>
  <div class="card_joueur">
    <div class="card-content">
      <h3>{{ nom }}</h3>
      <div class="info">
        <p class="age">{{ age }}</p>
        <p class="sexe">{{ sexe }}</p>
        <div class="icon" ></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card_joueur {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 1rem 1.5rem;
  width: 220px;
  transition: transform 0.2s ease;
}

.card_joueur:hover {
  transform: translateY(4px);
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.age, .sexe {
  font-size: 1.5rem;
  font-weight: 600;
}

.icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}



h3{
  margin: 0;
}

</style>