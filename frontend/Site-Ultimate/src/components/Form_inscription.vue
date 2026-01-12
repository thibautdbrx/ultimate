<script setup>

import {useRouter} from "vue-router";

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
const sexe = ref('')
const router = useRouter()

const submitForminscription = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        prenom: prenom.value,
        nom: nom.value,
        email: email.value,
        password: password.value,
        genre: sexe.value
      })
    });

    if (!response.ok) {
      const error = await response.json();
      alert(`Erreur : ${error.message}`);
      return;
    }

    const data = await response.json();
    console.log("ROLE_VISITEUR"); // juste pour debug
    document.cookie = `token=${data.token}; path=/; max-age=10800; SameSite=Lax`;

    // auth.loadToken(); // à décommenter si tu importes ton service auth
    // router.back();   // redirection après inscription

    alert("Compte créé avec succès !");
    await router.back();
  } catch (err) {
    console.error(err);
    alert("Erreur lors de la création du compte.");
  }
};
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
    <div class="sexe_select">
      <label for="sexe">Sexe</label>
      <select id="sexe" v-model="sexe" required>
        <option value="" disabled selected>Choisir le sexe</option>
        <option value="HOMME">Homme</option>
        <option value="FEMME">Femme</option>
      </select>
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

.sexe_select {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  margin-bottom: 0.5rem;
}

</style>
