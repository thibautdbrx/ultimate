<script setup>
import { ref } from "vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import { useAuthStore } from "@/stores/auth";
import api from '@/services/api' // Import de l'instance Axios

const emit = defineEmits(["close", "created"])

// --- ÉTATS NOTIFICATIONS (TOAST) ---
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

const auth = useAuthStore();

const joueur = ref({
  idJoueur: null,
  nomJoueur: "",
  prenomJoueur: "",
  genre: "HOMME",
  photoJoueur: null,
  clickable: true
})

const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  // Avec Axios, plus besoin de gérer le token ou le Content-Type manuellement ici
  const uploadRes = await api.post(`/files/upload`, formData);

  return uploadRes.data.url;
}

const validerCreation = async () => {
  // --- VALIDATION ---
  if (!joueur.value.nomJoueur.trim() || !joueur.value.prenomJoueur.trim()) {
    notify("Le nom et prénom sont obligatoires.")
    return
  }

  if (!joueur.value.genre) {
    notify("Le genre est obligatoire.")
    return
  }

  let photoUrl = "/api/files/pnj.jpg";

  try {
    if (joueur.value.photoJoueur instanceof File) {
      photoUrl = await uploadFile(joueur.value.photoJoueur);
    }

    const joueurPayload = {
      nomJoueur: joueur.value.nomJoueur,
      prenomJoueur: joueur.value.prenomJoueur,
      genre: joueur.value.genre,
      photoJoueur: photoUrl
    };

    // Remplacement du fetch par api.post
    await api.post("/joueur", joueurPayload);

    // --- SUCCÈS ---
    notify("Joueur créé avec succès !", "success");

    // On attend un peu que le toast soit visible avant de fermer l'overlay
    setTimeout(() => {
      emit("created")
    }, 1500);

  } catch (error) {
    console.error(error);
    notify("Impossible de créer le joueur : " + (error.response?.data?.message || error.message));
  }
}
</script>

<template>
  <div class="overlay-bg">

    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

    <div class="overlay">
      <div class="header">
        <h3>Ajouter un joueur</h3>
        <button class="btn" @click="emit('close')">✕</button>
      </div>

      <div class="form">
        <JoueurCardForm :joueur="joueur" />
      </div>

      <button class="valid-btn" @click="validerCreation">Créer le joueur</button>
    </div>
  </div>
</template>

<style scoped>
/* --- STYLE DU TOAST (ALERTES) --- */
.toast-notification {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  z-index: 10000; /* Toujours au dessus de l'overlay */
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}
.toast-notification.success { background-color: #2ecc71; }
.toast-notification.error { background-color: #e74c3c; }

.toast-icon {
  background: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}
.success .toast-icon { color: #2ecc71; }
.error .toast-icon { color: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, 40px); }

/* --- STYLE DE L'OVERLAY (INCHANGÉ) --- */
.overlay-bg {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
.overlay {
  background: white;
  width: 420px;
  padding: 1.5rem;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn {
  background: #ddd;
  border: none;
  padding: 0.4rem 0.7rem;
  border-radius: 8px;
  cursor: pointer;
}

.valid-btn {
  margin-top: 1rem;
  padding: 0.7rem;
  border: none;
  border-radius: 8px;
  background: #1e88e5;
  color: white;
  cursor: pointer;
  width: 100%;
  font-weight: bold;
}

.form {
  display: flex;
  justify-content: center;
}
</style>