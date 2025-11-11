<!-- components/Slider.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'

const slider = ref<HTMLElement | null>(null)

let isDown = false
let startX = 0
let scrollLeft = 0

const handleMouseDown = (e: MouseEvent) => {
  isDown = true
  if (slider.value) {
    slider.value.classList.add('active-drag')
    startX = e.pageX - slider.value.offsetLeft
    scrollLeft = slider.value.scrollLeft
  }
}

const handleMouseUp = () => {
  isDown = false
  if (slider.value) slider.value.classList.remove('active-drag')
}

const handleMouseMove = (e: MouseEvent) => {
  if (!isDown || !slider.value) return
  e.preventDefault()
  const x = e.pageX - slider.value.offsetLeft
  const walk = (x - startX) * 1.5
  slider.value.scrollLeft = scrollLeft - walk
}

onMounted(() => {
  document.addEventListener('mouseup', handleMouseUp)
})
</script>

<template>
  <section
      ref="slider"
      class="slider"
      @mousedown="handleMouseDown"
      @mouseup="handleMouseUp"
      @mousemove="handleMouseMove"
      @mouseleave="handleMouseUp"
  >
    <slot />
  </section>
</template>

<style scoped>
.slider {
  display: flex;
  flex-direction: row;
  gap: 1.5rem;
  padding: 2rem;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  cursor: grab;
  flex-wrap: nowrap;
}

.slider::-webkit-scrollbar {
  display: none;
}

.slider.active-drag {
  cursor: grabbing;
  user-select: none;
}
</style>
