<template>
  <RouterLink to="/Connexion" class="btn">{{ label }}</RouterLink>
</template>

<script setup>
import {RouterLink} from "vue-router";

defineProps({
  label: String
})
</script>

<style scoped>
.btn {
  background-color: #333;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
}

.btn:hover {
  background-color: #555;
}
</style>