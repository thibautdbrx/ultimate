<script setup>
import {ref} from 'vue'
import ConnexionBoutton from './ConnexionBoutton.vue'
import Champs_input from "@/components/champs_input.vue";
import CadenaIcon from "@/assets/icons/cadena.svg"
import EmailIcon from "@/assets/icons/email.svg"
import AvatarIcon from "@/assets/icons/avatar.svg"
import InscriptionBoutton from "@/components/InscriptionBoutton.vue";

const email = ref('')
const password = ref('')
const nom = ref('')
const prenom = ref('')

const submitForminscription = () => {
  alert(`mettre l'api ici d'inscription `)
}
</script>



<template>
  <form class="login_form" @submit.prevent="submitForminscription">
    <div class="nom_prenom">
      <champs_input
          label="Prenom"
          type="text"
          placeholder="Jean"
          v-model="prenom"
          :icon="AvatarIcon"
      />
      <champs_input
          label="Nom"
          type="text"
          placeholder="Bonboeur"
          v-model="nom"
          :icon="AvatarIcon"
      />
    </div>
    <champs_input
        label="Email"
        type="email"
        placeholder="Yves.vapabien@example.com"
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

    <InscriptionBoutton texte="S'inscrire" direction="form"/>

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
.nom_prenom{
  display: flex;
  flex-direction: row;
  gap: 1rem;
  justify-content: center;
}

</style>
