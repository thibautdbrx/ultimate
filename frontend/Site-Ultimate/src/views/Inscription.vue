<script setup>
import Form_inscription from '../components/Form_inscription.vue'
import {onMounted} from "vue";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();

</script>

<template>
  <div class="signup_page">
    <div class="signup-container">
      <h3>Inscription Joueur</h3>
      <p id="text_info">
        Créez un compte pour intégrer une équipe
      </p>

      <Form_inscription />
    </div>
  </div>
</template>

<style scoped>
.signup_page {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 2rem;
  background-color: #f5f7fb;
}

.signup-container {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  padding: 2rem 2.5rem;
  text-align: left;
}

h3 {
  font-weight: 600;
  font-size: 1.2rem;
  margin-bottom: 0.3rem;
  color: #222;
}

#text_info {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}
</style>
