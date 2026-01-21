<script setup>
import {useRouter} from "vue-router";
import {ref} from 'vue'
import Champs_input from "@/components/champs_input.vue";
import CadenaIcon from "@/assets/icons/cadena.svg"
import EmailIcon from "@/assets/icons/email.svg"
import AvatarIcon from "@/assets/icons/avatar.svg"
import InscriptionBoutton from "@/components/InscriptionBoutton.vue";
import api from '@/services/api' // Ajout de l'import api

// --- LOGIQUE TOAST ---
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

const email = ref('')
const password = ref('')
const nom = ref('')
const prenom = ref('')
const sexe = ref('')
const errorMessage = ref('')
const router = useRouter()

const submitForminscription = async () => {
  try {
    const response = await api.post('/auth/register', {
      prenom: prenom.value,
      nom: nom.value,
      email: email.value,
      password: password.value,
      genre: sexe.value
    });

    const data = response.data;
    console.log("ROLE_VISITEUR");
    document.cookie = `token=${data.token}; path=/; max-age=10800; SameSite=Lax`;

    notify("Compte créé avec succès !", "success");

    setTimeout(async () => {
      await router.back();
    }, 1500);

  } catch (err) {
    console.error(err);
    const message = err.response?.data?.message || "Erreur lors de la création du compte.";
    notify(`Erreur : ${message}`, "error");
  }
};
</script>

<template>
  <form class="login_form" @submit.prevent="submitForminscription">

    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

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
    <p v-if="errorMessage" class="error-text">
      {{ errorMessage }}
    </p>
    <InscriptionBoutton texte="S'inscrire" direction="form"/>

    <p class="demo-text">
    </p>
  </form>
</template>

<style scoped>
/* --- STYLE DU TOAST (AJOUTÉ) --- */
.toast-notification {
  position: fixed;
  top: 20px; /* En haut c'est mieux pour une page de login/inscription */
  left: 50%;
  transform: translateX(-50%);
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  z-index: 10000;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}
.toast-notification.success { background-color: #2ecc71; }
.toast-notification.error { background-color: #e74c3c; }

.toast-icon {
  background: white;
  width: 20px; height: 20px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: bold;
}
.success .toast-icon { color: #2ecc71; }
.error .toast-icon { color: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, -40px); }

/* --- TES STYLES EXISTANTS --- */
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