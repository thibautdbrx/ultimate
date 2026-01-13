<script setup>
import {RouterLink} from "vue-router";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();

defineProps({
  label: String
})
</script>

<template>
  <RouterLink v-if="!auth.isAuthenticated" class="btn">{{ label }}</RouterLink>
  <RouterLink v-else to="/Deconnexion" class="btn">{{ label }}</RouterLink>
</template>

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