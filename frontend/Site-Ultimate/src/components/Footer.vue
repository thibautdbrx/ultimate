<script setup lang="ts">

import Button from "@/components/Bouton.vue";
</script>

<template>
  <footer class="footer">
  <div class="footer_top">
  <h3>Ultimate Match Manager</h3>
  <Button label="Connexion" />
  </div>
  <p id="signature_footer">Novembre 2025</p>
  </footer>
</template>

<style scoped>
.footer_top{
  margin-top: 1rem;
  display: flex;
  text-align: center;
  justify-content: space-between;
}

.footer{
  background-color: white;
  padding: 2rem;

}


h3{
  margin: 0rem;
}

#signature_footer{
text-align: center;
}
</style>