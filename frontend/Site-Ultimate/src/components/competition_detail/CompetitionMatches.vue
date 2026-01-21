<script setup>
import { computed } from 'vue'
import SliderCardHorizontal from "@/components/Slider_card_horizontal.vue"
import CardMatch from "@/components/card/card_match.vue"

const props = defineProps({
  matches: { type: Array, default: () => [] }
})

const formatDate = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}

const upcomingMatches = computed(() => props.matches.filter(m => m.status !== "FINISHED"))
const finishedMatches = computed(() => props.matches.filter(m => m.status === "FINISHED"))
</script>

<template>
  <div v-if="matches.length > 0" class="prochain_matches">

    <h3>Matchs prochains</h3>
    <SliderCardHorizontal v-if="upcomingMatches.length > 0">
      <div v-for="match in upcomingMatches" :key="match.idMatch">
        <CardMatch :title="formatDate(match.dateMatch)" :match="match" />
      </div>
    </SliderCardHorizontal>
    <p v-else class="info-msg">Aucun match à venir.</p>

    <h3>Matchs finis</h3>
    <SliderCardHorizontal v-if="finishedMatches.length > 0">
      <div v-for="match in finishedMatches" :key="match.idMatch">
        <CardMatch :title="formatDate(match.dateMatch)" :match="match" />
      </div>
    </SliderCardHorizontal>
    <p v-else class="info-msg">Aucun match terminé.</p>

  </div>
</template>

<style scoped>
.prochain_matches {
  display: flex;
  flex-direction:column;
  gap: 2rem;
  width: 100%;
  max-width: 1200px;
  margin: 1rem auto;
}
.info-msg { text-align: center; color: #999; font-style: italic; font-size: 0.9rem; }
</style>