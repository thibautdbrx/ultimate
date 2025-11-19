<script setup>
import {ref} from 'vue'
import ConnexionBoutton from './ConnexionBoutton.vue'
import Champs_input from "@/components/champs_input.vue";
import CadenaIcon from "@/assets/icons/cadena.svg"
import EmailIcon from "@/assets/icons/email.svg"
import InscriptionBoutton from "@/components/InscriptionBoutton.vue";

const email = ref('')
const password = ref('')

const submitForm = () => {
  alert(`mettre l'api ici de connexion`)
}
</script>



<template>
  <form class="login_form" @submit.prevent="submitForm">
    <champs_input
        label="Email"
        type="email"
        placeholder="jean.peuplu@example.com"
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

    <ConnexionBoutton texte="Se connecter"/>
    <InscriptionBoutton texte="S'inscrire" direction="page"/>

    <p class="demo-text">
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
