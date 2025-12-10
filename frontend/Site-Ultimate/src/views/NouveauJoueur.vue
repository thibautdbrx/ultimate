<script setup>
import { ref } from "vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth";

const router = useRouter()
const auth = useAuthStore();

const joueur = ref({
  idJoueur: null,
  nomJoueur: "",
  prenomJoueur: "",
  genre: "MALE",
  photoJoueur: null, 
  clickable: true
})

const apiBaseUrl = "http://localhost:8080";

const uploadFile = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    const token = auth.token;
    const headers = {};
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    // Requête Multipart (pas de Content-Type manuel ici)
    const uploadRes = await fetch(`${apiBaseUrl}/api/files/upload`, {
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
    router.push("/AjouterEquipe");

  } catch (error) {
    console.error(error);
    alert("Impossible de créer le joueur : " + error.message);
  }
}
</script>

<template>
  <main v-if="auth.isAdmin" class="page">
    <h2>Nouveau joueur</h2>
    <p id="sous-titre">Ajouter un nouveau joueur afin de l'inscrire dans une équipe puis dans une compétition</p>

    <JoueurCardForm :joueur="joueur" />

    <button class="btn" @click="validerCreation">Créer le joueur</button>
  </main>
</template>

<style scoped>
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

.btn:hover {
  background: #1565c0;
}

h2{
  text-align: center;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}
#sous-titre{
  font-size: 0.9rem;
  color: gray;
  text-align: center;
  margin-bottom: 1.5rem;
}
</style>y