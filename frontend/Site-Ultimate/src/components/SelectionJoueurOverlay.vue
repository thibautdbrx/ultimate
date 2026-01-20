<script setup>
import { ref, watch, onMounted, computed} from "vue"
import CardJoueur from "@/components/card_joueur.vue";
import api from '@/services/api' // Ajout de l'import api

const props = defineProps({
  show: Boolean,
  genre: String,
  id_equipe: {type: String,  required: false, default: "None"},
})

const emit = defineEmits(["close", "select", "nvj"])

const search = ref("")
const joueurs = ref([])

// --- AJOUT : LOGIQUE TOAST ---
const showToast = ref(false)
const toastMessage = ref("")
const toastType = ref("error")

const notify = (msg, type = "error") => {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3500)
}

async function loadJoueurs() {
  try {
    let response
    if (props.id_equipe === "None") {
      response = await api.get("/joueur/solo/")
    } else {
      response = await api.get("/joueur/solo/", {
        params: { idEquipe: props.id_equipe }
      })
    }

    // Axios met les données directement dans response.data
    const data = response.data
    joueurs.value = Array.isArray(data) ? data : []

  } catch (e) {
    console.error(e)
    joueurs.value = []

    // Gestion spécifique de l'erreur 401 (Equipe complète) via Axios
    if (e.response && e.response.status === 401) {
      notify("L'équipe est déjà complète !", "error")
    } else {
      notify("Impossible de contacter le serveur.")
    }
  }
}

onMounted(async () => {
  loadJoueurs()
})

// pour rafraichir les joueurs quand on ouvre l'overlay
watch(() => props.show, async (v) => {
  if (v) {
    await loadJoueurs()
  }
})

const filtered = computed(() =>
    (joueurs.value || []).filter(j =>
        (j.nomJoueur + " " + j.prenomJoueur)
            .toLowerCase()
            .includes(search.value.toLowerCase())
    )
)
</script>

<template>
  <div v-if="show" class="overlay-bg">

    <Transition name="toast">
      <div v-if="showToast" :class="['toast-notification', toastType]">
        <span class="toast-icon">{{ toastType === 'success' ? '✔' : '✖' }}</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

    <div class="overlay">
      <div class="titre">
        <h3>Choisir un joueur</h3>
        <button id="close-btt-haut" @click="emit('close')">Fermer</button>
        <button id="close-btt-haut" @click="emit('nvj')">Nouveau joueur</button>
      </div>

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
              :photo="j.photoJoueur"
          />
        </div>
      </div>

      <button class="close-btn" @click="emit('close')">Fermer</button>

    </div>
  </div>
</template>

<style scoped>
/* --- AJOUT : STYLE TOAST --- */
.toast-notification {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
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
  width: 20px; height: 20px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: bold;
}
.success .toast-icon { color: #2ecc71; }
.error .toast-icon { color: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, -40px); }

/* --- TON STYLE EXISTANT (INCHANGÉ) --- */
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