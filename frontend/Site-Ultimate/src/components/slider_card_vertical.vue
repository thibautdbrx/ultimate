<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";

const props = defineProps({
  speed: { type: Number, default: 0.5 }, // vitesse auto scroll
  pauseDelay: { type: Number, default: 500 } // pause après scroll manuel
});

const slider = ref(null);
let direction = 1; // 1 = descend, -1 = monte
let frame;
let pauseTimeout;
let paused = false;

// ----- Auto-scroll -----

let virtualPos = 0;

function autoScroll() {
  if (!slider.value) return;

  if (!paused) {
    virtualPos += direction * props.speed; // valeur décimale
    slider.value.scrollTop = virtualPos;   // arrondi automatiquement
  }


  // haut → change direction
    if (slider.value.scrollTop + slider.value.clientHeight >= slider.value.scrollHeight) {
      direction = -1;
    }
    if (slider.value.scrollTop <= 0) {
      direction = 1;

    }

  frame = requestAnimationFrame(autoScroll);
}

// ----- Scroll manuel -----
function handleUserScroll() {
  paused = true; // pause auto-scroll

  clearTimeout(pauseTimeout);

  pauseTimeout = setTimeout(() => {
    paused = false; // reprend après X ms
  }, props.pauseDelay);
}

onMounted(() => {
  frame = requestAnimationFrame(autoScroll);

  if (slider.value) {
    slider.value.addEventListener("wheel", handleUserScroll, { passive: true });
    slider.value.addEventListener("mousedown", handleUserScroll);
  }
});

onBeforeUnmount(() => {
  cancelAnimationFrame(frame);
  clearTimeout(pauseTimeout);

  if (slider.value) {
    slider.value.removeEventListener("wheel", handleUserScroll);
    slider.value.removeEventListener("mousedown", handleUserScroll);
  }
});
</script>

<template>
  <section class="slider" ref="slider">
    <slot />
  </section>
</template>

<style scoped>
.slider {
  height: 400px;
  overflow-y: auto;   /* <-- IMPORTANT pour le scroll manuel */
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1rem;
  align-items: center;

  scrollbar-width: none; /* Firefox : cache scrollbar */
}

.slider::-webkit-scrollbar {
  display: none; /* Chrome : cache scrollbar */
}
</style>
