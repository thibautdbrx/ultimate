<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const auth = useAuthStore();

onMounted(() => {
  auth.logout();
  router.push('/');
});

</script>

<template>
  <div class="deconnexion-message">
    <p>Déconnexion en cours...</p>
    </div>
</template>

<style scoped>
.deconnexion-message {
  padding: 5rem;
  text-align: center;
  font-size: 1.5rem;
  color: #888;
}
</style>