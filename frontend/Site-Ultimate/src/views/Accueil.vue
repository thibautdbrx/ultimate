<script setup lang="ts">
import {ref} from "vue";

const count = ref(0);
const increment = () => {
  count.value++
}
const decrement = () => {
  count.value--
}
</script>

<template>
  <main class="Acceuil">
  <h1 :id="`p-${count}`">bouton qui augmente</h1>
  <p :style="{color: count > 5 ? 'red' : 'black'}" >{{count}}
  </p>
  <button v-on:click="increment">incrementer</button>
  <button @click="decrement">décrementer</button>
  <p v-show="count>=5">ARRETE DE CLIQUER</p>
  <p v-if="count>=6">VRAIMENT ARRET</p>
  </main>
</template>

<style scoped>

</style>