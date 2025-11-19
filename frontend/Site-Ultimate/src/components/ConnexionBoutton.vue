<script setup>
defineProps({
  texte:String
})
</script>

<template>
  <button type="submit" class="login-btn">
    <p id="texte_btt">{{texte}}</p>
  </button>
</template>

<style scoped>
.login-btn {
  background-color: #0066ff;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}

.login-btn:hover {
  background-color: #0052cc;
}

#texte_btt{
  margin: 0rem;
}
</style>
