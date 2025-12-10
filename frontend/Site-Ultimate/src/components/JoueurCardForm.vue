<script setup>
import { ref } from 'vue' // 1. On importe ref
import champs_input from "@/components/champs_input.vue"
import UserIcon from "@/assets/icons/avatar.svg"
// 2. On importe l'image par défaut pour qu'elle soit gérée correctement
import defaultImage from '../assets/img_joueur/pnj.jpg'

const props = defineProps({
  joueur: Object,
})

// 3. Variable réactive pour la source de l'image (au début, c'est l'image par défaut)
const previewSrc = ref(defaultImage)

// 4. Fonction appelée par le @change du template
const handleFileChange = (event) => {
  const file = event.target.files[0];
  
  if (file) {
    // Mise à jour de ton objet joueur
    props.joueur.photoJoueur = file;

    // Création de la prévisualisation (méthode FileReader)
    const reader = new FileReader();
    reader.onload = (e) => {
      previewSrc.value = e.target.result; // On met à jour la variable réactive
    };
    reader.readAsDataURL(file);
  }
}
</script>

<template>
  <div class="joueur-card">

    <champs_input
        label="Nom"
        placeholder="Nom"
        v-model="joueur.nomJoueur"
        :icon="UserIcon"
        :clickable="joueur.clickable"
    />

    <champs_input
        label="Prénom"
        placeholder="Prénom"
        v-model="joueur.prenomJoueur"
        :icon="UserIcon"
        :clickable="joueur.clickable"
    />

    <label class="photo-label">
      Genre :
      <select v-if="joueur.clickable" class="genre-select" v-model="joueur.genre">
        <option value="MALE">MALE</option>
        <option value="FEMALE">FEMALE</option>
      </select>

      <select v-else class="genre-select" v-model="joueur.genre" disabled>
        <option value="MALE">MALE</option>
        <option value="FEMALE">FEMALE</option>
      </select>
    </label>

      <label v-if="joueur.clickable" class="photo-label">
        Photo du joueur :
        <input 
          id="file-upload" 
          class="photo_input" 
          type="file" 
          @change="handleFileChange" 
        />
      </label>

      <img :src="previewSrc" alt="preview" id="previewImage">

  </div>
</template>

<style scoped>
.joueur-card {
  padding: 1rem;
  border-radius: 10px;
  border: 1px solid #ddd;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  width: 260px;
}

.genre-select {
  border-radius: 8px;
  padding: 0.6rem;
  border: 1px solid #ccc;
  margin-right: 50%;
}
.photo-label {
  font-size: 0.85rem;
  color: #444;
}

#previewImage {
  border-radius: 50%;
  width: 100px;
  height: 100px;
  margin: auto;
}
</style>