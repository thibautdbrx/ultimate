<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()
const requests = ref([])
const loading = ref(true)
const error = ref(null)

// --- STATES POUR LE TOAST (BANDEAU) ---
const showToast = ref(false)
const toastMessage = ref("")

// --- STATES POUR LA MODALE DE CONFIRMATION ---
const showConfirmModal = ref(false)
const modalAction = ref(null) // 'accepter' ou 'refuser'
const selectedRequest = ref(null)

function triggerToast(message) {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3000)
}

// 1. CHARGEMENT
async function loadRequests() {
  try {
    loading.value = true
    const res = await fetch('/api/joueur/requests', {
      headers: { Authorization: `Bearer ${auth.token}` }
    })
    if (!res.ok) throw new Error('Erreur chargement demandes')
    requests.value = await res.json()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!auth.isAdmin) { router.push("/"); return }
  loadRequests()
})

// --- GESTION DE LA MODALE ---
function openConfirm(action, request) {
  modalAction.value = action
  selectedRequest.value = request
  showConfirmModal.value = true
}

function closeConfirm() {
  showConfirmModal.value = false
  selectedRequest.value = null
}

async function executeAction() {
  const r = selectedRequest.value
  const action = modalAction.value

  closeConfirm() // On ferme d'abord la modale

  try {
    const endpoint = action === 'accepter' ? 'accept' : 'refuse'
    const res = await fetch(`/api/joueur/request/${r.joueur.idJoueur}/equipe/${r.equipe.idEquipe}/${endpoint}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${auth.token}` }
    })

    if (res.ok) {
      triggerToast(action === 'accepter' ? "Joueur accepté !" : "Demande refusée.")
      await loadRequests()
    } else {
      throw new Error("Erreur serveur")
    }
  } catch (e) {
    triggerToast("Erreur lors de l'opération")
  }
}
</script>

<template>
  <main class="admin-requests">

    <Transition name="toast">
      <div v-if="showToast" class="toast-notification">
        <span class="toast-icon">✔</span>
        <span class="toast-text">{{ toastMessage }}</span>
      </div>
    </Transition>

    <Transition name="modal">
      <div v-if="showConfirmModal" class="modal-overlay" @click.self="closeConfirm">
        <div class="modal-content">
          <div class="modal-header" :class="modalAction">
            {{ modalAction === 'accepter' ? 'Confirmer l\'acceptation' : 'Confirmer le refus' }}
          </div>
          <div class="modal-body">
            Voulez-vous vraiment {{ modalAction }} <strong>{{ selectedRequest.joueur.prenomJoueur }} {{ selectedRequest.joueur.nomJoueur }}</strong>
            dans l'équipe <strong>{{ selectedRequest.equipe.nomEquipe }}</strong> ?
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="closeConfirm">Annuler</button>
            <button :class="['btn-confirm', modalAction]" @click="executeAction">
              Confirmer
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <div class="header-section" v-if="requests.length > 0">
      <h2>Demandes d’inscription en attente : <span class="count">{{ requests.length }} </span>
      </h2>
    </div>

    <div v-if="loading && !requests.length" class="state-msg">Chargement…</div>

    <div class="table-container" v-if="requests.length">
      <table>
        <thead>
        <tr>
          <th>Joueur</th>
          <th>Genre</th>
          <th>Équipe ciblée</th>
          <th class="text-right">Décision</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="r in requests" :key="`${r.joueur.idJoueur}-${r.equipe.idEquipe}`">
          <td>
            <div class="joueur-cell">
              <span class="avatar-mini">{{ r.joueur.prenomJoueur[0] }}</span>
              <div>
                <div class="name">{{ r.joueur.prenomJoueur }} {{ r.joueur.nomJoueur }}</div>
              </div>
            </div>
          </td>
          <td><span class="badge-genre">{{ r.joueur.genre }}</span></td>
          <td><span class="team-name">{{ r.equipe.nomEquipe }}</span></td>
          <td class="actions">
            <button class="btn-icon accept" @click="openConfirm('accepter', r)" title="Accepter">✔</button>
            <button class="btn-icon refuse" @click="openConfirm('refuser', r)" title="Refuser">✖</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <p>Toutes les demandes ont été traitées !</p>
    </div>
  </main>
</template>

<style scoped>
/* --- MISE EN PAGE & TABLEAU --- */
.admin-requests {
  padding: 2rem;
  max-width: 1200px; /* Plus large */
  margin: 0 auto;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 2rem;
  border-bottom: 2px solid #eee;
  padding-bottom: 1rem;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  background: #fcfcfc;
  padding: 1.2rem;
  text-align: left;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #888;
}

td {
  padding: 1.2rem;
  border-bottom: 1px solid #f5f5f5;
  vertical-align: middle;
}

.joueur-cell { display: flex; align-items: center; gap: 1rem; }
.avatar-mini {
  width: 35px; height: 35px; background: #e0e7ff; color: #4f46e5;
  border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;
}
.name { font-weight: 600; color: #2c3e50; }
.badge-genre { font-size: 0.8rem; background: #eee; padding: 2px 8px; border-radius: 4px; }
.team-name { color: #2563eb; font-weight: 500; }

.actions { display: flex; gap: 1rem; justify-content: flex-end; }

.btn-icon {
  width: 38px; height: 38px; border-radius: 10px; border: none;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s; color: white;
}
.btn-icon.accept { background: #10b981; }
.btn-icon.refuse { background: #ef4444; }
.btn-icon:hover { transform: scale(1.1); filter: brightness(1.1); }

/* --- MODALE DE CONFIRMATION --- */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center; z-index: 11000;
  backdrop-filter: blur(3px);
}
.modal-content {
  background: white; width: 400px; border-radius: 16px; overflow: hidden;
  box-shadow: 0 20px 50px rgba(0,0,0,0.2);
}
.modal-header { padding: 1.2rem; color: white; font-weight: bold; text-align: center; }
.modal-header.accepter { background: #10b981; }
.modal-header.refuser { background: #ef4444; }
.modal-body { padding: 2rem; text-align: center; line-height: 1.5; color: #444; }
.modal-footer { padding: 1.2rem; display: flex; gap: 1rem; background: #f9f9f9; }

button { flex: 1; padding: 0.8rem; border-radius: 8px; border: none; cursor: pointer; font-weight: 600; }
.btn-cancel { background: #ddd; }
.btn-confirm.accepter { background: #10b981; color: white; }
.btn-confirm.refuser { background: #ef4444; color: white; }

/* --- ANIMATIONS MODALE --- */
.modal-enter-active, .modal-leave-active { transition: opacity 0.3s; }
.modal-enter-from, .modal-leave-to { opacity: 0; }

/* --- TOAST --- */
.toast-notification {
  position: fixed; top: 20px; left: 50%; transform: translateX(-50%);
  background-color: #2ecc71; color: white; padding: 12px 24px;
  border-radius: 50px; z-index: 12000; display: flex; align-items: center; gap: 12px;
}
</style>