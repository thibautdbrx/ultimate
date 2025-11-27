<script setup>
import { ref } from "vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"

const emit = defineEmits(["close", "created"])

const nom = ref("")
const prenom = ref("")
const genre = ref("MALE")
const photo = ref(null)

const joueur_ajout = ref({
  id: null,
  nom: "",
  prenom: "",
  genre: "MALE",
  photo: null,
  clickable: true
})

function onFileChange(e) {
  photo.value = e.target.files[0] || null
}

async function validerCreation() {
  const joueurPayload = {
    nomJoueur: joueur_ajout.value.nom,
    prenomJoueur: joueur_ajout.value.prenom,
    genre: joueur_ajout.value.genre,
  };
  //if (photo.value) form.append("photo", photo.value)

  const res = await fetch("/api/joueur", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(joueurPayload),
  })

  if (!res.ok) {
    alert("Erreur : impossible de créer le joueur.")
    return
  }

  alert("Joueur créé avec succès !")
  emit("created") // renvoie le joueur créé
}
</script>

<template>
  <div class="overlay-bg">
    <div class="overlay">

      <div class="header">
        <h3>Ajouter un joueur</h3>
        <button class="close" @click="emit('close')">✕</button>
      </div>

      <JoueurCardForm :joueur="joueur_ajout" />

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

.close {
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
</style>
