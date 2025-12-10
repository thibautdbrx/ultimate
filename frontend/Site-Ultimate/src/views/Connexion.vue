<script setup>
import Form_connexion from '../components/form_connexion.vue'
</script>

<template>
  <div class="login_page">
    <div class="login-container">
      <h3>Connexion</h3>
      <p id="text_info">
        Connectez-vous pour gérer les équipes, compétitions et matchs
      </p>

      <Form_connexion />
    </div>
  </div>
</template>

<style scoped>
.login_page {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 2rem;
  background-color: #f5f7fb;
}

.login-container {
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

.text_info {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}
</style>
