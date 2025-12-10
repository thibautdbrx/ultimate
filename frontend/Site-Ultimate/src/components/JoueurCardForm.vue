<script setup>
import { ref } from 'vue'
import champs_input from "@/components/champs_input.vue"
import UserIcon from "@/assets/icons/avatar.svg"
import defaultImage from '../assets/img_joueur/pnj.jpg'

const props = defineProps({
  joueur: Object,
})
const fileInput = ref(null)

const previewSrc = ref(defaultImage)

const handleFileChange = (event) => {
  const file = event.target.files[0];
  
  if (file) {
    props.joueur.photoJoueur = file;
    
    const reader = new FileReader();
    reader.onload = (e) => {
      previewSrc.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}

const supprimerPhoto = () => {
  previewSrc.value = defaultImage;
  
  props.joueur.photoJoueur = null;

  if (fileInput.value) {
    fileInput.value.value = '';
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
        <option value="HOMME">HOMME</option>
        <option value="FEMME">FEMME</option>
      </select>

      <select v-else class="genre-select" v-model="joueur.genre" disabled>
        <option value="HOMME">HOMME</option>
        <option value="FEMME">FEMME</option>
      </select>
    </label>

    <label v-if="joueur.clickable" class="photo-label">
      Photo du joueur :
      <input 
        ref="fileInput"
        id="file-upload" 
        class="photo_input" 
        type="file"
        accept=".jpg, .jpeg, .png, .avif"
        @change="handleFileChange" 
      />
    </label>

    <div class="image-preview-container">
      <img :src="previewSrc" alt="preview" id="previewImage">
      
      <button 
        v-if="previewSrc !== defaultImage && joueur.clickable" 
        @click="supprimerPhoto" 
        class="delete-btn"
        title="Supprimer la photo"
      >
        ✕
      </button>
    </div>

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

/* Nouveau container pour positionner le bouton relative à l'image */
.image-preview-container {
  position: relative;
  width: 100px;
  margin: 0 auto;
}

#previewImage {
  border-radius: 50%;
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
}

/* Style du bouton supprimer */
.delete-btn {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #ff4d4d;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.delete-btn:hover {
  background-color: #d93636;
}
</style>