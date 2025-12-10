<script setup>
import { ref } from "vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import { useAuthStore } from "@/stores/auth";

const emit = defineEmits(["close", "created"])

const nom = ref("")
const prenom = ref("")
const genre = ref("MALE")
const photo = ref(null)

const auth = useAuthStore();

const joueur = ref({
  idJoueur: null,
  nomJoueur: "",
  prenomJoueur: "",
  genre: "",
  photoJoueur: null,
  clickable: true
})


const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const token = auth.token;
  const headers = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const uploadRes = await fetch(`/api/files/upload`, {
    method: "POST",
    headers: headers,
    body: formData
  });

  if (!uploadRes.ok) {
    throw new Error("Erreur lors de l'upload de l'image.");
  }

  const uploadData = await uploadRes.json();
  return uploadData.url;
}

const validerCreation = async () => {

  if (!joueur.value.nomJoueur.trim() || !joueur.value.prenomJoueur.trim()) {
    alert("Le nom et prénom sont obligatoires.")
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

    const res = await fetch("/api/joueur", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(joueurPayload)
    });

    if (!res.ok) {
      alert("Erreur : impossible de créer le joueur.")
      return

    }

    alert("Joueur créé avec succès !");
    emit("created") // renvoie le joueur créé


  } catch (error) {
    console.error(error);
    alert("Impossible de créer le joueur : " + error.message);
  }
}
</script>

<template>
  <div class="overlay-bg">
    <div class="overlay">

      <div class="header">
        <h3>Ajouter un joueur</h3>
        <button class="btn" @click="emit('close')">✕</button>
      </div>

      <div class="form">
        <JoueurCardForm :joueur="joueur" />
      </div>

      <button class="btn" @click="validerCreation">Créer le joueur</button>

    </div>
  </div>
</template>

<style scoped>
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

.label {
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.select {
  width: 100%;
  padding: 0.6rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  margin-bottom: 0.5rem;
}

.file {
  margin-top: 0.3rem;
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
}
.form{
  display: flex;
  justify-content: center;
}

</style>
