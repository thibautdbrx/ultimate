<script setup>
import {ref} from 'vue'
import LoginButton from './LogginButton.vue'
import Champs_input from "@/components/champs_input.vue";
import CadenaIcon from "@/assets/icons/cadena.svg"
import EmailIcon from "@/assets/icons/email.svg"

const email = ref('')
const password = ref('')

const submitForm = () => {
  alert(`mettre l'api ici ${email.value}`)
}
</script>



<template>
  <form class="login_form" @submit.prevent="submitForm">
    <champs_input
        label="Email"
        type="email"
        placeholder="admin@example.com"
        v-model="email"
        :icon="EmailIcon"
    />
    <champs_input
        label="Mot de passe"
        type="password"
        placeholder="••••••••"
        v-model="password"
        :icon="CadenaIcon"
    />

    <LoginButton texte="Se connecter"/>

    <p class="demo-text">
      n’importe quel email et mot de passe
    </p>
  </form>
</template>



<style scoped>
.login_form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.demo-text {
  text-align: center;
  font-size: 0.8rem;
  color: #888;
  margin-top: 0.6rem;
}


</style>
