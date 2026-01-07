<script setup>
import { RouterLink } from "vue-router";
import chien_prankeur from "@/assets/img/chien_prankeur.png"
</script>

<template>
  <main>
    <div class="emoji">
      <img :src="chien_prankeur">
    </div>
    <h1>FAUSSE PUB</h1>
    <p>On rigole !</p>


    <RouterLink to="/" class="btn-retour">
      Retour
    </RouterLink>
  </main>
</template>

<style scoped>
main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  font-family: sans-serif;
  padding: 20px;
}

.emoji {
  font-size: 5rem;
  margin-bottom: 1rem;
  animation: bounce 2s infinite;
}

h1 {
  font-size: 4rem;
  font-weight: 900;
  color: #ff4757; /* Rouge un peu flashy */
  margin: 0;
}

p {
  font-size: 1.5rem;
  color: #57606f;
  margin-top: 1rem;
  margin-bottom: 3rem;
}

.btn-retour {
  background-color: #333;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
}

.btn-retour:hover {
  background-color: #555;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
  40% {transform: translateY(-20px);}
  60% {transform: translateY(-10px);}
}
</style>