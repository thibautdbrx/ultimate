<script setup>
import { ref } from 'vue'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
</script>

<template>
  <div class="layout-container">
    <Header />
    
    <main>
      <router-view />
    </main>
    
    <Footer />
  </div>
</template>

<style>
body {
  margin: 0;
  height: 100%; 
}

.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh; 
}

main {
  flex: 1;
}
</style>