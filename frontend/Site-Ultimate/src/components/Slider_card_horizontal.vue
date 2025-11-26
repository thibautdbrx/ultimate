<script setup>
import { ref, onMounted, onBeforeUnmount, defineProps } from "vue";

const props = defineProps({
  speed: { type: Number, default: 0.5 },
  pauseDelay: { type: Number, default: 500 },
  bounceOvershoot: { type: Number, default: 20 }
});

const slider = ref(null);

let direction = 1;
let virtualPos = 0;
let paused = false;
let frame;
let resumeTimeout;

// -------- PAUSE --------
function pause() {
  paused = true;
  if (resumeTimeout) clearTimeout(resumeTimeout);
}

// -------- REPRISE --------
function resumeAfterDelay() {
  if (resumeTimeout) clearTimeout(resumeTimeout);

  resumeTimeout = setTimeout(() => {
    //  resynchroniser avant de reprendre
    if (slider.value) {
      virtualPos = slider.value.scrollLeft;
    }
    paused = false;
  }, props.pauseDelay);
}

// -------- AUTO-SCROLL --------
function loop() {
  const el = slider.value;
  if (!el) return frame = requestAnimationFrame(loop);

  const maxScroll = el.scrollWidth - el.clientWidth;

  if (!paused) {
    virtualPos += direction * props.speed;

    // rebond droit
    if (virtualPos > maxScroll) {
      virtualPos = Math.min(virtualPos, maxScroll + props.bounceOvershoot);
      direction = -1;
    }

    // rebond gauche
    if (virtualPos < 0) {
      virtualPos = Math.max(virtualPos, -props.bounceOvershoot);
      direction = 1;
    }

    el.scrollLeft = virtualPos;
  }

  frame = requestAnimationFrame(loop);
}

// -------- INTERACTIONS UTILISATEUR --------
function syncAndPause() {
  if (!slider.value) return;

  pause();

  // synchroniser virtualPos dès que l’utilisateur touche au scroll
  virtualPos = slider.value.scrollLeft;

  resumeAfterDelay();
}

function onPointerEnter() {
  pause();
  // sync pour éviter les décalages
  if (slider.value) virtualPos = slider.value.scrollLeft;
}

function onPointerLeave() {
  resumeAfterDelay();
}

onMounted(() => {
  if (slider.value) virtualPos = slider.value.scrollLeft;

  frame = requestAnimationFrame(loop);

  const el = slider.value;
  if (!el) return;

  el.addEventListener("pointerenter", onPointerEnter, { passive: true });
  el.addEventListener("pointerleave", onPointerLeave, { passive: true });

  // utilisateur déplace : sync + pause
  el.addEventListener("wheel", syncAndPause, { passive: true });
  el.addEventListener("mousedown", syncAndPause, { passive: true });
  el.addEventListener("touchstart", syncAndPause, { passive: true });
  el.addEventListener("touchmove", syncAndPause, { passive: true });
});

onBeforeUnmount(() => {
  cancelAnimationFrame(frame);
  clearTimeout(resumeTimeout);

  if (!slider.value) return;
  const el = slider.value;

  el.removeEventListener("pointerenter", onPointerEnter);
  el.removeEventListener("pointerleave", onPointerLeave);
  el.removeEventListener("wheel", syncAndPause);
  el.removeEventListener("mousedown", syncAndPause);
  el.removeEventListener("touchstart", syncAndPause);
  el.removeEventListener("touchmove", syncAndPause);
});
</script>

<template>
  <section class="slider" ref="slider">
    <slot />
  </section>
</template>

<style scoped>
.slider {
  width: 100%;
  overflow-x: auto;
  display: flex;
  gap: 1.5rem;
  padding: 1rem;
  flex-direction: row;

  scrollbar-width: none;
  -webkit-overflow-scrolling: touch;
}

.slider::-webkit-scrollbar {
  display: none;
}
</style>
