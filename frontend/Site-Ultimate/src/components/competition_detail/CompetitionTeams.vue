<script setup>
import CarteEquipe from "@/components/card_equipe.vue"
import ImageFond from "@/assets/img/img_equipe.jpg"

defineProps({
  teams: { type: Array, default: () => [] },
  editMode: { type: Boolean, default: false },
  isStarted: { type: Boolean, default: false }
})

const emit = defineEmits(['delete-team', 'open-add', 'go-to-team'])
</script>

<template>
  <section class="equipes-section">
    <h3>Équipes engagées</h3>

    <div v-if="editMode" class="edit-actions">
      <button v-if="!isStarted" class="btn-primary" @click="emit('open-add')">
        Ajouter une équipe
      </button>
      <p v-else class="competition-deja-commencee">
        La compétition a déjà commencé, ajout impossible.
      </p>
    </div>

    <p v-if="teams.length === 0" class="info-msg">Aucune équipe n'a été sélectionnée pour le moment</p>

    <div class="teams-grid">
      <div v-for="(t, index) in teams" :key="t.idEquipe" class="team-card-wrapper">
        <button
            v-if="editMode && !isStarted"
            class="btn-delete"
            @click="emit('delete-team', { index, id: t.idEquipe })"
        >
          Supprimer
        </button>
        <CarteEquipe
            :equipe="t"
            :image="ImageFond"
            :licencie="false"
            @click="emit('go-to-team', t)"
        />
      </div>
    </div>
  </section>
</template>

<style scoped>
.equipes-section { margin-top: 3rem; }
.edit-actions { display: flex; gap: 1rem; margin: 1rem 0; }
.info-msg { text-align: center; color: #666; font-style: italic; }
.teams-grid { display: flex; flex-wrap: wrap; gap: 1.5rem; justify-content: center; margin: 2rem 0; }
.team-card-wrapper { flex: 0 1 220px; display: flex; flex-direction: column; gap: 1rem; }
.btn-primary { background-color: #333; color: white; padding: 0.5rem 1rem; border: none; border-radius: 8px; cursor: pointer; }
.btn-delete { background: #e74c3c; color: white; border: none; padding: 6px 10px; border-radius: 8px; cursor: pointer; }
.competition-deja-commencee { color: #d32f2f; font-size: 0.9rem; }
</style>