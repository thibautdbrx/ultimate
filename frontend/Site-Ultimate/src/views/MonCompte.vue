<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from "@/stores/auth.js"

import CarteEquipe from "@/components/card_equipe.vue"
import CarteMatch from "@/components/card_match.vue"
import CardJoueur from "@/components/card_joueur.vue"
import ImageEquipeFond from "../assets/img/img_equipe.jpg"

const router = useRouter()
const auth = useAuthStore()

// --- STATES ---
const loading = ref(true)
const error = ref(null)
const joueur = ref(null)
const prochainsMatchs = ref([])

// --- STATES MODALE (Pour la photo) ---
const showModal = ref(false)
const selectedFile = ref(null)
const previewImage = ref(null)
const loadingUpload = ref(false)

// --- FORMATAGE DATE ---
const formatDate = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// --- NAVIGATION ---
function goToMatch(id) {
  router.push(`/match/${id}`)
}

function goToEquipe(id,nom) {
  router.push({ name: 'Equipe-details', params: { id, nom } })
}

function goToTrouverEquipe() {
  router.push('/request') // Chemin adapté selon votre code
}

// --- AUTH / COOKIE ---
function getJoueurIdFromCookie() {
  const token = document.cookie
      .split('; ')
      .find(row => row.startsWith('token='))
      ?.split('=')[1]

  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.joueurId
    } catch (e) {
      console.error("Erreur lors du décodage du token", e)
      return null
    }
  }
  return null
}

// --- LOGIQUE UPLOAD & MODIFICATION PHOTO ---

const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  // On récupère le token du store ou du cookie si le store est vide
  let token = auth.token;
  if (!token) {
    const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('token='));
    if (tokenCookie) token = tokenCookie.split('=')[1];
  }

  const headers = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const uploadRes = await fetch(`/api/files/upload`, {
    method: "POST",
    headers: headers,
    body: formData
  });

  if (!uploadRes.ok) throw new Error("Erreur lors de l'upload de l'image.");

  const uploadData = await uploadRes.json();
  return uploadData.url;
}

const openEditModal = () => {
  selectedFile.value = null
  previewImage.value = joueur.value.photoJoueur
  showModal.value = true
}

const closeEditModal = () => {
  showModal.value = false
  selectedFile.value = null
}

const onFileSelected = (event) => {
  const file = event.target.files[0]
  if (file) {
    selectedFile.value = file
    // Prévisualisation immédiate
    previewImage.value = URL.createObjectURL(file)
  }
}

const validerChangementPhoto = async () => {
  if (!selectedFile.value) {
    alert("Veuillez sélectionner une image.")
    return
  }

  loadingUpload.value = true

  try {
    // 1. Upload
    const newPhotoUrl = await uploadFile(selectedFile.value)

    // 2. Patch API (Mise à jour du joueur)
    const res = await fetch(`/api/joueur/${joueur.value.idJoueur}`, {
      method: 'PATCH', // Endpoint PATCH pour l'image
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ image: newPhotoUrl }) // Correspond au DTO ImageDTO
    })

    if(!res.ok) throw new Error("Erreur lors de la mise à jour du profil.")

    // 3. Mise à jour locale
    joueur.value.photoJoueur = newPhotoUrl

    closeEditModal()
    alert("Photo de profil mise à jour !")

  } catch (e) {
    console.error(e)
    alert(e.message)
  } finally {
    loadingUpload.value = false
  }
}

// --- CHARGEMENT DONNÉES (ON MOUNTED) ---
onMounted(async () => {
  loading.value = true

  // 1. Récupération ID
  let currentJoueurId = auth.joueurId
  if (!currentJoueurId) {
    currentJoueurId = getJoueurIdFromCookie()
  }

  if (!currentJoueurId) {
    router.push('/Connexion') // Chemin adapté selon votre code
    return
  }

  try {
    // 2. Récupérer les infos du joueur
    const resJoueur = await fetch(`/api/joueur/${currentJoueurId}`)
    if (!resJoueur.ok) {
      if(resJoueur.status === 404) throw new Error("Joueur introuvable.")
      throw new Error("Impossible de récupérer les informations du joueur.")
    }
    joueur.value = await resJoueur.json()

    // 3. Récupérer les matchs (Si équipe existe)
    if (!joueur.value.equipe) {
      console.log("Le joueur n'a pas d'équipe, on ne charge pas les matchs.")
      prochainsMatchs.value = []
    }
    else {
      const resMatchs = await fetch(`/api/match/joueur/${currentJoueurId}`)

      if (!resMatchs.ok) {
        throw new Error("Impossible de récupérer les matchs.")
      }

      const tousLesMatchs = await resMatchs.json()
      const maintenant = new Date()

      prochainsMatchs.value = tousLesMatchs
          .filter(m => new Date(m.dateDebut) > maintenant && m.status !== 'FINISHED')
          .sort((a, b) => new Date(a.dateDebut) - new Date(b.dateDebut))
    }

  } catch (err) {
    console.error(err)
    error.value = err.message || "Une erreur est survenue."
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <main class="page-compte">
    <h2 class="title">Mon Espace Joueur</h2>

    <div v-if="loading" class="state-msg">Chargement des informations...</div>
    <div v-else-if="error" class="state-msg error">{{ error }}</div>

    <div v-else class="content-wrapper">

      <section class="section-profil">
        <h3>Mes Informations</h3>

        <div class="profil-wrapper">
          <div class="profil-card">
            <CardJoueur
                :nom="joueur.nomJoueur"
                :prenom="joueur.prenomJoueur"
                :genre="joueur.genre"
                :photo="joueur.photoJoueur"
            />
          </div>
          <button class="btn-edit" @click="openEditModal">
            Modifier ma photo
          </button>
        </div>
      </section>

      <hr class="divider" />

      <section class="section-equipe">
        <h3>Mon Équipe</h3>

        <div
            v-if="joueur.equipe"
            class="equipe-container"
            @click="goToEquipe(joueur.equipe.idEquipe, joueur.equipe.nomEquipe)"
        >
          <CarteEquipe :equipe="joueur.equipe" :image="ImageEquipeFond" />
        </div>

        <div v-else class="no-data-msg">
          <p>Vous ne faites partie d'aucune équipe pour le moment.</p>
          <button class="btn-action" @click="goToTrouverEquipe">Rejoindre une équipe</button>
        </div>
      </section>

      <hr class="divider" />

      <section class="section-matchs">
        <h3>Mes Prochains Matchs</h3>

        <div v-if="prochainsMatchs.length > 0" class="match-list">
          <div
              v-for="match in prochainsMatchs"
              :key="match.idMatch"
              class="match-wrapper"
              @click="goToMatch(match.idMatch)"
          >
            <CarteMatch
                :title="formatDate(match.dateDebut)"
                :match="match"
            />
          </div>
        </div>

        <div v-else class="no-data-msg">
          <p>Aucun match prévu prochainement.</p>
        </div>
      </section>

    </div>

    <div v-if="showModal" class="overlay-bg" @click.self="closeEditModal">
      <div class="overlay">
        <div class="header">
          <h3>Changer ma photo</h3>
          <button class="btn-close" @click="closeEditModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="preview-container">
            <img :src="previewImage || joueur.photoJoueur" alt="Aperçu" class="img-preview" />
          </div>

          <label for="fileInput" class="custom-file-upload">
            Choisir une nouvelle image
          </label>
          <input id="fileInput" type="file" @change="onFileSelected" accept="image/*" />
        </div>

        <button class="btn-valider" @click="validerChangementPhoto" :disabled="loadingUpload">
          {{ loadingUpload ? 'Envoi en cours...' : 'Enregistrer la photo' }}
        </button>
      </div>
    </div>

  </main>
</template>

<style scoped>
/* --- Styles de base --- */
.page-compte {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
  font-weight: 700;
  color: #2c3e50;
}

.content-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

section h3 {
  font-size: 1.5rem;
  color: #34495e;
  margin-bottom: 1.5rem;
  padding-left: 1rem;
  border-left: 4px solid #2563eb;
}

.divider {
  border: 0;
  height: 1px;
  background: #e0e0e0;
  margin: 1rem 0;
}

/* --- Profil Wrapper --- */
.profil-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}
.profil-card { display: flex; justify-content: center; }

.btn-edit {
  padding: 0.5rem 1rem;
  background-color: #34495e;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.2s;
}
.btn-edit:hover { background-color: #2c3e50; }

/* --- Equipes & Matchs --- */
.equipe-container {
  display: flex;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;
}
.equipe-container:hover { transform: translateY(-5px); }

.match-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  width: 100%;
}
.match-wrapper { cursor: pointer; }

.no-data-msg {
  text-align: center;
  background-color: #f8f9fa;
  padding: 2rem;
  border-radius: 8px;
  border: 1px dashed #ccc;
  color: #666;
}

.btn-action {
  margin-top: 1rem;
  padding: 0.7rem 1.5rem;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: background-color 0.2s;
}
.btn-action:hover { background-color: #27ae60; }

.state-msg { text-align: center; font-size: 1.2rem; color: #555; margin-top: 3rem; }
.state-msg.error { color: #e74c3c; }

/* --- Styles MODALE (Identique à votre style Overlay) --- */
.overlay-bg {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.overlay {
  background: white;
  width: 400px;
  padding: 1.5rem;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}

.header { display: flex; justify-content: space-between; align-items: center; }
.header h3 { margin: 0; font-size: 1.3rem; }

.btn-close {
  background: #eee; border: none; padding: 0.4rem 0.8rem;
  border-radius: 8px; cursor: pointer; font-size: 1rem;
}
.btn-close:hover { background: #ddd; }

.modal-body {
  display: flex; flex-direction: column; align-items: center; gap: 1rem;
}

.preview-container {
  width: 120px; height: 120px; border-radius: 50%; overflow: hidden;
  border: 3px solid #2563eb; box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.img-preview { width: 100%; height: 100%; object-fit: cover; }

input[type="file"] { display: none; }
.custom-file-upload {
  border: 1px solid #ccc; display: inline-block; padding: 6px 12px;
  cursor: pointer; border-radius: 6px; background: #f9f9f9; font-size: 0.9rem;
}
.custom-file-upload:hover { background: #e9e9e9; }

.btn-valider {
  padding: 0.8rem; border: none; border-radius: 8px;
  background: #1e88e5; color: white; cursor: pointer; width: 100%; font-weight: bold;
}
.btn-valider:hover { background: #1565c0; }
.btn-valider:disabled { background: #90caf9; cursor: not-allowed; }
</style>