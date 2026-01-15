<script setup>
import {RouterLink, useRouter} from 'vue-router'
import frisbeeIcon from '@/assets/icons/frisbee.svg'
import PUB from "@/components/PUB.vue"
</script>

<template>
  <main class="NotFound">

    <div class="content_container">
      <div class="frisbee-wrapper">
        <component :is="frisbeeIcon" class="frisbee-icon" />
      </div>

      <h1 class="error-code">404</h1>
      <h2 class="titre_oups">Oups !</h2>

      <p class="description">
        Ce frisbee a atterri hors des limites du terrain.<br>
        La page que vous cherchez n'existe pas ou a été déplacée.
      </p>

      <RouterLink to="/" class="btn-retour">
        Retour
      </RouterLink>
    </div>
    <PUB/>
  </main>
</template>

<style scoped>
/* --- Layout Principal --- */
.NotFound {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 2rem;
  text-align: center;
  background-color: #f8fafc;
  gap: 3rem;
}

.content_container {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 600px;
}

/* --- Frisbee --- */
.frisbee-wrapper {
  margin-bottom: 1rem;
  perspective: 1000px;
}

.frisbee-icon {
  width: 150px;
  height: 150px;
  animation: spin-and-float 4s infinite linear;
  display: block;
}

@keyframes spin-and-float {
  0% { transform: rotate(0deg) translateY(0); }
  50% { transform: rotate(180deg) translateY(-15px); }
  100% { transform: rotate(360deg) translateY(0); }
}

.error-code {
  font-size: 6rem;
  font-weight: 900;
  color: #155dfc;
  margin: 0;
  line-height: 1;
}

.titre_oups {
  font-size: 2rem;
  color: #1e293b;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.description {
  font-size: 1.1rem;
  color: #64748b;
  margin-bottom: 2.5rem;
  line-height: 1.6;
}

/* --- Bouton --- */
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
</style>