<script setup>
import { ref } from "vue"
import JoueurCardForm from "@/components/JoueurCardForm.vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth";

const router = useRouter()

const auth = useAuthStore();

// Le joueur à créer, utile pour creer la vue joueurcardform de facon clicable
const joueur = ref({
  id: null,
  nom: "",
  prenom: "",
  genre: "MALE",
  photo: null,
  clickable: true
})

const validerCreation = async () => {
  if (!joueur.value.nom.trim() || !joueur.value.prenom.trim()) {
    alert("Le nom et prénom sont obligatoires.")
    return
  }

  const joueurPayload = {
    nomJoueur: joueur.value.nom,
    prenomJoueur: joueur.value.prenom,
    genre: joueur.value.genre,
  };

  //if (joueur.value.photo){
  //   joueurPayload.photo = joueur.value.photo;
  //}
  /**
   * Faire la requête POST sur http://localhost:8080/api/files/upload
   * avec en paramètre file=@/chemin/vers/imeage/pc.jpg
   *
   * requete fetch = http://localhost:8080/api/files/upload?file=@/chemin/vers/imeage/pc.jpg
   *
   * Récupérer cette réponse dans une variable
   * Créer un nouveau joueur avec dans photoJoueur = data.url
   *
   * Puis lors de l'affichage d'un joueur, dans la balise img, mettre http://localhost:8080/api/files/+joueur.photoJoueur
   *
   * Possibilité de mettre une photo de base et si joueur/photoJoueur est null, afficher l'image de base
   */

  const res = await fetch("/api/joueur", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(joueurPayload)
  })

  if (!res.ok) {
    alert("Erreur : impossible de créer le joueur.")
    return
  }

  alert("Joueur créé avec succès !")
  router.push("/AjouterEquipe")
}
</script>

<template>
  <main class="page">
    <h2>Nouveau joueur</h2>
    <p id="sous-titre">Ajouter un nouveau joueur afin de l'inscripte dans une équipe puis dans une compétitions</p>

    <!-- On réutilise ta carte dynamique -->
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
}

h2{
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
}
#sous-titre{
  font-size: 0.8rem;
  color: gray;
  text-align: center;
}
</style>
