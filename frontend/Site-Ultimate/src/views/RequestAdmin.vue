<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()
const requests = ref([])
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    const res = await fetch('/api/joueur/requests', {
      headers: {
        Authorization: `Bearer ${auth.token}`
      }
    })

    if (!res.ok) throw new Error('Erreur chargement demandes')

    requests.value = await res.json()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
})

if (!auth.isAdmin) {
  router.push("/");
}

async function accepter(idJoueur, idEquipe) {
  await fetch(`/api/joueur/request/${idJoueur}/equipe/${idEquipe}/accept`, {
    method: 'PATCH',
    headers: {
      Authorization: `Bearer ${auth.token}`
    }
  })

  // retirer la ligne après validation
  requests.value = requests.value.filter(
      r => r.joueur.idJoueur !== idJoueur || r.equipe.idEquipe !== idEquipe
  )
}

async function refuser(idJoueur, idEquipe) {
  await fetch(`/api/joueur/request/${idJoueur}/equipe/${idEquipe}/refuse`, {
    method: 'PATCH',
    headers: {
      Authorization: `Bearer ${auth.token}`
    }
  })

  requests.value = requests.value.filter(
      r => r.joueur.idJoueur !== idJoueur || r.equipe.idEquipe !== idEquipe
  )
}
</script>

<template>
  <main class="admin-requests">
    <h2>Demandes d’inscription</h2>

    <p v-if="loading">Chargement…</p>
    <p v-if="error" class="error">{{ error }}</p>

    <table v-if="!loading && requests.length">
      <thead>
      <tr>
        <th>Prénom</th>
        <th>Nom</th>
        <th>Équipe</th>
        <th>Actions</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="r in requests" :key="`${r.joueur.idJoueur}-${r.equipe.idEquipe}`">
        <td>{{ r.joueur.prenomJoueur }}</td>
        <td>{{ r.joueur.nomJoueur }}</td>
        <td>{{ r.equipe.nomEquipe }}</td>
        <td class="actions">
          <button class="accept"
                  @click="accepter(r.joueur.idJoueur, r.equipe.idEquipe)">
            ✔
          </button>
          <button class="refuse"
                  @click="refuser(r.joueur.idJoueur, r.equipe.idEquipe)">
            ✖
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <p v-if="!loading && !requests.length">
      Aucune demande en attente
    </p>
  </main>
</template>

<style scoped>
.admin-requests {
  padding: 2rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  text-align: center;
  padding: 0.75rem;
  border-bottom: 1px solid #ddd;

}

.actions {
  display: flex;
  gap: 0.5rem;
}

button {
  border: none;
  padding: 0.4rem 0.7rem;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 4px;
}

.accept {
  background: #2ecc71;
  color: white;
}

.refuse {
  background: #e74c3c;
  color: white;
}

.error {
  color: red;
}
</style>