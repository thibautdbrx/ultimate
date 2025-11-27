<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import ConnexionBoutton from './ConnexionBoutton.vue'
import Champs_input from "@/components/champs_input.vue"
import CadenaIcon from "@/assets/icons/cadena.svg"
import EmailIcon from "@/assets/icons/email.svg"
import InscriptionBoutton from "@/components/InscriptionBoutton.vue"

const router = useRouter()

// Variables réactives pour le formulaire
// CORRECTION : On utilise bien 'email' ici
const email = ref('')
const password = ref('')
const errorMessage = ref(null)

const submitForm = async () => {
  errorMessage.value = null // Réinitialise l'erreur au début du clic

  try {
    const response = await axios.post('http://localhost:8080/api/auth/login',
        {
          // CORRECTION : La clé est 'email' et la valeur vient de la variable email
          email: email.value,
          password: password.value
        },
        { withCredentials: true }
    )

    if (response.status === 200) {
      console.log("Réuissi")
      //const role = response.data.role || 'VISITEUR';
      // Cookie valide 3h
      //document.cookie = `user_role=${role}; path=/; max-age=10800; SameSite=Lax`;
      document.cookie = `token=${response.data.token}; path=/; max-age=10800; SameSite=Lax`;
      console.log(JSON.parse(atob(document.cookie.split('.')[1])).role) //.split('=')[1]
      //const roles = JSON.parse(atob(token.split('.')[1])).roles
      // Redirection vers l'accueil (ou 'Equipes' selon ton router)
      await router.push("/")
    }

  } catch (error) {
    console.error("Erreur de connexion:", error);

    password.value = ''; // On efface le mot de passe en cas d'erreur

    // Gestion des erreurs
    if (error.response && error.response.status === 401) {
      errorMessage.value = "Email ou mot de passe incorrect.";
    } else if (error.code === "ERR_NETWORK") {
      errorMessage.value = "Impossible de contacter le serveur. Vérifiez qu'il est lancé.";
    } else {
      errorMessage.value = "Une erreur inattendue est survenue.";
    }
  }
}
</script>

<template>
  <form class="login_form" @submit.prevent="submitForm">
    
    <champs_input
        label="Email"
        type="name"
        placeholder="exemple@mail.com"
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

    <p v-if="errorMessage" class="error-text">
      {{ errorMessage }}
    </p>

    <ConnexionBoutton texte="Se connecter"/>
    <InscriptionBoutton texte="S'inscrire" direction="page"/>

    <p class="demo-text">
      Utilisez vos identifiants pour continuer.
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

.error-text {
  color: #e74c3c;
  font-size: 0.9rem;
  text-align: center;
  margin-top: -0.5rem;
  font-weight: 600;
  background-color: #fdecea;
  padding: 0.5rem;
  border-radius: 4px;
}
</style>