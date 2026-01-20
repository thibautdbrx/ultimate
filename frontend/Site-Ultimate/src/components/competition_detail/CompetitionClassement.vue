<script setup>
import { computed } from 'vue'

const props = defineProps({
  classement: { type: Array, default: () => [] },
  hasMatches: { type: Boolean, default: false }
})

const emit = defineEmits(['go-to-team'])

const classementTrie = computed(() => {
  return [...props.classement].sort((a, b) => a.rang - b.rang)
})
</script>

<template>
  <section class="classement-section">
    <h3>Classement du tournoi</h3>

    <p v-if="!hasMatches" class="info-msg">
      Le classement sera mis à jour automatiquement dès que les matchs auront été générés et joués.
    </p>

    <ul v-else-if="classementTrie.length" class="classement-list">
      <li v-for="c in classementTrie" :key="c.idClassement?.idEquipe || c.equipe.idEquipe"
          :class="['classement-item', `rang-${c.rang}`]">
        <span class="rang">{{ c.rang }}</span>
        <span class="equipe" @click="emit('go-to-team', c.equipe)">
            {{ c.equipe.nomEquipe }}
        </span>
        <span class="score">{{ c.score }} pts</span>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.classement-section {
  margin-top: 3rem;
  display: flex;
  flex-direction: column; }

.classement-list {
  list-style: none;
  padding: 0; margin: 0;
}

.classement-item {
  display: grid;
  grid-template-columns: 40px 1fr 80px;
  align-items: center;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
  border-radius: 10px;
  background: #f4f4f4;
  font-weight: 500;
}

.classement-item .rang {
  font-weight: bold;
  text-align: center;
}

.classement-item .equipe {
  cursor: pointer;
}

.classement-item .score {
  text-align: right;
  font-weight: bold;
}

.rang-1 { background: linear-gradient(90deg, #ffd700, #fff4b0); }
.rang-2 { background: linear-gradient(90deg, #c0c0c0, #eeeeee); }
.rang-3 { background: linear-gradient(90deg, #cd7f32, #f1d1b3); }

.info-msg { text-align: center; font-size: 0.95rem; color: #666; margin-top: 1rem; font-style: italic; }
</style>