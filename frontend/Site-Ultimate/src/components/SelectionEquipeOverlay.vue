<script setup>
import { ref, watch, onMounted, computed} from "vue"
import CardEquipe from "@/components/card_equipe.vue"

const props = defineProps({
  show: Boolean,
  all:{
    type: Boolean,
    required: false,
    default: true,
  }
})

const emit = defineEmits(["close", "select", "nvj"])

const search = ref("")
const equipes = ref([])

async function loadEquipes() {
  const res = await fetch("/api/equipe")
  equipes.value = await res.json()
}



onMounted(async () => {
  loadEquipes()
})
//pour rafraichir les jouerus quand on ouvre l'overlay
watch(() => props.show, async (v) => {
  if (v) {
    await loadEquipes()
  }
})

const filtered = computed(() =>
    equipes.value.filter(e =>
        (e.nomEquipe)
            .toLowerCase()
            .includes(search.value.toLowerCase())
    )
)
</script>

<template>
  <div v-if="show" class="overlay-bg">

    <div class="overlay">
      <div class="titre">
        <h3>Choisir une équipe</h3>
        <button id="close-btt-haut" @click="emit('close')">Fermer</button>
        <button v-if="all" id="close-btt-haut" @click="emit('nvj')">Nouvelle équipe</button>
      </div>

      <input
          type="text"
          class="search-input"
          placeholder="Rechercher une équipe"
          v-model="search"
      />

      <div class="joueurs-grid">
        <div
            v-for="e in filtered"
            :key="e.idEquipe"
            @click="emit('select', e)"
            class="selectable"
        >
          <CardEquipe
              :equipe="e"
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
