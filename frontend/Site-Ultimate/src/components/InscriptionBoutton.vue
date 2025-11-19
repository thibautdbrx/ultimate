<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  texte: String,
  direction: String, // "form" ou "page"
})

const router = useRouter()

const Click = () => {
  if (props.direction === "page") {
    router.push("/Inscription")
  }
}
</script>

<template>
  <button
      :type="props.direction === 'form' ? 'submit' : 'button'"
      :style="direction === 'form' ? { backgroundColor: '#0066ff' } : {backgroundColor: '#aebacc'}"
      class="inscription-btn"
      @click="Click"
  >
    <p id="texte_btt">{{ texte }}</p>
  </button>
</template>

<style scoped>
.inscription-btn {
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 400;
  transition: background 0.2s;
}


#texte_btt {
  margin: 0;
}
</style>
