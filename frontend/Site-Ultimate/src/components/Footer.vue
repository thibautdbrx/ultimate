<script setup >

import Button from "@/components/Bouton.vue";
import {RouterLink} from "vue-router";
</script>

<template>
  <footer class="footer">
    <div class="footer_top">
      <RouterLink to="/Contact" class="nav-link">Contact</RouterLink>
      <h3 class="footer_title">Ultimate Match Manager</h3>
      <p id="signature_footer">©VAFLOURENTIBEON</p>
    </div>
  </footer>
</template>

<style scoped>
.footer_top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative; /* pour que l'élément central soit positionné par rapport à ce conteneur */
  width: 100%;
}

/* Le titre au centre exact */
.footer_title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  margin: 0;
  text-align: center;
}

.footer {
  background-color: white;
  padding: 2rem;
}

.nav-link {
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: #7a0ca7;
}

#signature_footer {
  margin: 0;
}
</style>
