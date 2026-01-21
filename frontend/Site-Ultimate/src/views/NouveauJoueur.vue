<script setup>
import { ref } from "vue"
import JoueurCardForm from "@/components/card/JoueurCardForm.vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth";
import api from '@/services/api' 

const router = useRouter();
const auth = useAuthStore();

// ... (Le code des notifications reste identique) ...
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

if (!auth.isAdmin) {
  router.push("/");
}

const joueur = ref({
  idJoueur: null,
  nomJoueur: "",
  prenomJoueur: "",
  genre: "",
  photoJoueur: null,
  clickable: true
})

// --- CORRECTION ICI : ON UTILISE LA MEME LOGIQUE QUE DANS COMPTE.VUE ---
const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  // On récupère le token du store
  let token = auth.token;
  
  // Préparation des headers (SANS Content-Type, le navigateur le fera)
  const headers = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  // Utilisation de fetch au lieu de api.post pour éviter les conflits de configuration Axios
  const res = await fetch(`/api/files/upload`, {
    method: "POST",
    headers: headers,
    body: formData
  });

  if (!res.ok) {
    throw new Error("Erreur lors de l'upload de l'image");
  }

  const data = await res.json();
  return data.url;
}

const validerCreation = async () => {
  // --- VALIDATION ---
  if (!joueur.value.nomJoueur.trim() || !joueur.value.prenomJoueur.trim()) {
    notify("Le nom et prénom sont obligatoires.")
    return
  }

  if (!joueur.value.genre) {
    notify("Le genre est obligatoire pour créer un joueur.")
    return
  }

  let photoUrl = "/api/files/pnj.jpg"; // Image par défaut

  try {
    // Si l'utilisateur a uploadé un fichier
    if (joueur.value.photoJoueur instanceof File) {
      photoUrl = await uploadFile(joueur.value.photoJoueur);
    } 
    // Si c'est déjà une string (ex: modif), on garde l'ancienne, sinon défaut
    else if (typeof joueur.value.photoJoueur === 'string') {
       photoUrl = joueur.value.photoJoueur;
    }

    const joueurPayload = {
      nomJoueur: joueur.value.nomJoueur,
      prenomJoueur: joueur.value.prenomJoueur,
      genre: joueur.value.genre,
      photoJoueur: photoUrl
    };

    // Pour le JSON classique, on garde api.post (Axios), c'est parfait
    await api.post("/joueur", joueurPayload);

    notify("Joueur créé avec succès !", "success");

    setTimeout(() => {
      router.push("/AjouterEquipe");
    }, 1500);

  } catch (error) {
    console.error(error);
    notify("Impossible de créer le joueur : " + (error.response?.data?.message || error.message));
  }
}
</script>

<template>
  <main v-if="auth.isAdmin" class="page">

    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

    <h2>Nouveau joueur</h2>
    <p id="sous-titre">Ajouter un nouveau joueur afin de l'inscrire dans une équipe puis dans une compétition</p>

    <JoueurCardForm :joueur="joueur" />

    <button class="btn" @click="validerCreation">Créer le joueur</button>
  </main>
</template>

<style scoped>
/* --- STYLE DU TOAST (ALERTES FINES) --- */
.toast-notification {
  position: fixed;
  bottom: 30px;
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

.page {
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.btn {
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  border: none;
  background: #1e88e5;
  color: white;
  cursor: pointer;
  width: 20%;
  min-width: 150px;
  font-weight: bold;
  transition: background 0.3s;
}

.btn:hover { background: #1565c0; }

h2 { text-align: center; font-size: 2rem; margin-bottom: 0.5rem; }

#sous-titre {
  font-size: 0.9rem;
  color: gray;
  text-align: center;
  margin-bottom: 1.5rem;
}
</style>