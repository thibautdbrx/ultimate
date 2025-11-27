<script setup>
import Button from './Bouton.vue'
import { RouterLink } from 'vue-router'

const clickhome = () => {
  router.push("/")
}

</script>

<template>
  <header class="header">
    <router-link to="/" active-class="no-active-style">
      <h1 class="logo">UltiMATE</h1>
    </router-link>
    <nav>
      <RouterLink to="/AjouterEquipe" class="nav-link">Ajouter Equipe</RouterLink>
      <RouterLink to="/" class="nav-link">Accueil</RouterLink>
      <RouterLink to="/Equipe" class="nav-link">Equipe</RouterLink>
      <RouterLink to="/Competition" class="nav-link">Compétition</RouterLink>
      <Button active-class="no-active-style" label="Connexion" />
    </nav>
  </header>
</template>

<style scoped>
.header {
  background-color: white;
  padding: 16px 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;}
nav {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-link {
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: #7a0ca7;
}

.router-link-active {
  color: #515151;
  font-weight: bold;
}

a {
  text-decoration: none;
  color: inherit;
}


</style>