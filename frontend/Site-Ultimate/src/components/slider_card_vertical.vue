<!-- components/Slider.vue -->
<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, defineProps } from 'vue'

const props = defineProps<{
  autoScroll?: boolean,       // activer/désactiver le scroll automatique
  autoScrollDelay?: number    // délai avant reprise après drag (ms)
}>()

const slider = ref<HTMLElement | null>(null)

let isDown = false
let startY = 0
let scrollTop = 0
let autoScrollSpeed = 0.5
let animationFrameId: number
let autoScrollTimeout: number | undefined = undefined
const paused = ref(false) // true = scroll en pause

// --- Drag ---
const handleMouseDown = (e: MouseEvent) => {
  isDown = true
  if (!slider.value) return
  slider.value.classList.add('active-drag')
  startY = e.pageY - slider.value.offsetTop
  scrollTop = slider.value.scrollTop

  paused.value = true
  if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
}

const handleMouseUp = () => {
  isDown = false
  if (slider.value) slider.value.classList.remove('active-drag')

  if (props.autoScroll) {
    const delay = props.autoScrollDelay ?? 1000
    if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
    autoScrollTimeout = window.setTimeout(() => {
      paused.value = false
    }, delay)
  }
}

const handleMouseMove = (e: MouseEvent) => {
  if (!isDown || !slider.value) return
  e.preventDefault()
  const y = e.pageY - slider.value.offsetTop
  const walk = (y - startY) * 1.5
  slider.value.scrollTop = scrollTop - walk
  checkInfiniteScroll()
}

// --- Boucle infinie ---
const checkInfiniteScroll = () => {
  if (!slider.value) return
  const totalHeight = slider.value.scrollHeight
  if (slider.value.scrollTop >= totalHeight / 2) {
    slider.value.scrollTop -= totalHeight / 2
  } else if (slider.value.scrollTop <= 0) {
    slider.value.scrollTop += totalHeight / 2
  }
}

// --- Auto-scroll continu ---
const autoScrollLoop = () => {
  if (!props.autoScroll) {
    animationFrameId = requestAnimationFrame(autoScrollLoop)
    return
  }

  if (!paused.value && !isDown && slider.value) {
    slider.value.scrollTop += autoScrollSpeed
    checkInfiniteScroll()
  }

  animationFrameId = requestAnimationFrame(autoScrollLoop)
}

onMounted(() => {
  if (slider.value && props.autoScroll) {
    const len = slider.value.children.length
    for (let i = 0; i < len; i++) {
      slider.value.appendChild(slider.value.children[i].cloneNode(true))
    }
  }

  document.addEventListener('mouseup', handleMouseUp)

  if (props.autoScroll) {
    paused.value = false
    requestAnimationFrame(autoScrollLoop)
  }
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationFrameId)
  document.removeEventListener('mouseup', handleMouseUp)
  if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
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
  flex-direction: column;
  gap: 1.5rem;
  padding: 2rem;
  overflow-y: scroll;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  cursor: grab;
  flex-wrap: nowrap;
  scroll-behavior: auto;
  height: 400px; /* hauteur du slider vertical */
}

.slider::-webkit-scrollbar {
  display: none;
}

.slider.active-drag {
  cursor: grabbing;
  user-select: none;
}
</style>
