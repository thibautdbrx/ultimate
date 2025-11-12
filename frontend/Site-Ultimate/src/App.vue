<script setup>
import {ref} from 'vue'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
</script>

<template>
  <div id="app">
    <Header />
    <router-view /> <!-- C’est ici que la page actuelle s’affiche -->
  <Footer />
  </div>
</template>

<style scoped>
#app{
  background-color: #f8fafc;
  height:100%;
}


</style>
