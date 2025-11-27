<script setup>
import { ref, watch, onMounted, computed} from "vue"
import CardJoueur from "@/components/Card_joueur.vue"

const props = defineProps({
  show: Boolean,
})

const emit = defineEmits(["close", "select", "nvj"])

const search = ref("")
const joueurs = ref([])

async function loadJoueurs() {
  const res = await fetch("/api/joueur/solo")
  joueurs.value = await res.json()
}

onMounted(async () => {
  loadJoueurs()
})
//pour rafraichir les jouerus quand on ouvre l'overlay
watch(() => props.show, async (v) => {
  if (v) {
    await loadJoueurs()
  }
})

const filtered = computed(() =>
    joueurs.value.filter(j =>
        (j.nomJoueur + " " + j.prenomJoueur)
            .toLowerCase()
            .includes(search.value.toLowerCase())
    )
)
</script>

<template>
  <div v-if="show" class="overlay-bg">

    <div class="overlay">
      <div class="titre">
        <h3>Choisir un joueur</h3>
        <button id="close-btt-haut" @click="emit('close')">Fermer</button> </div>
        <button id="close-btn-haut" @click="emit('nvj')">Nouveau joueur</button>


      <input
          type="text"
          class="search-input"
          placeholder="Rechercher un joueur (Nom Prenom)"
          v-model="search"
      />

      <div class="joueurs-grid">
        <div
            v-for="j in filtered"
            :key="j.id"
            @click="emit('select', j)"
            class="selectable"
        >
          <CardJoueur
              :nom="j.nomJoueur"
              :prenom="j.prenomJoueur"
              :genre="j.genre"
          />
        </div>
      </div>

      <button class="close-btn" @click="emit('close')">Fermer</button>

    </div>
  </div>
</template>

<style scoped>
.overlay-bg {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.overlay {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  width: 700px;
  max-height: 90vh;
  overflow-y: auto;
}



.search-input {
  width: 100%;
  padding: 0.8rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  margin-bottom: 1rem;
}
.joueurs-grid {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.8rem;
}
.selectable {
  cursor: pointer;
  transition: 0.2s;
}
.selectable:hover {
  transform: scale(1.03);
}
.create-btn, .close-btn {
  margin-top: 1rem;
  padding: 0.7rem 1rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  width: 100%;
}
.create-btn {
  background: #1e88e5;
  color: white;
}
.close-btn {
  background: #ddd;
}

.titre{
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  align-items: center;
}

#close-btt-haut{
  background: #ddd;
  width: 7rem;
  height: 3rem;
  border:none;
  border-radius: 0.5rem;
}

</style>
