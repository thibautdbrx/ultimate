<script setup>
defineProps({
  nom: String,
  age: String,
  sexe: String
})
</script>

<template>
  <div class="card_joueur">
    <div class="header">
      <h3>{{ nom }}</h3>
      <span class="tag">{{ sexe }}</span>
    </div>

    <div class="footer">
      <span class="age">{{ age }} ans</span>
    </div>
  </div>
</template>

<style scoped>
.card_joueur {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e4e4e4;
  padding: 0.8rem 1rem;
  width: 180px;                /* plus fin pour empilement */
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  /* pour slider vertical : fine ombre */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);

  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card_joueur:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}

/* --- Header --- */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h3 {
  font-size: 1.2rem;
  margin: 0;
  font-weight: 600;
}

/* Badge du sexe */
.tag {
  background: #ececec;
  padding: 3px 8px;
  border-radius: 8px;
  font-size: 0.8rem;
  text-transform: capitalize;
}

/* --- Footer --- */
.footer {
  display: flex;
  justify-content: flex-start;
}

.age {
  font-size: 0.95rem;
  font-weight: 500;
  color: #444;
}
</style>
