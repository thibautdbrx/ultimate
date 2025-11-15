<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, defineProps } from 'vue'

const props = defineProps<{
  autoScroll?: boolean,       // activer/désactiver le scroll automatique
  autoScrollDelay?: number    // délai avant reprise après drag (ms)
}>()

const slider = ref<HTMLElement | null>(null)

let isDown = false
let startY = 0
let scrollTopStart = 0
let autoScrollSpeed = 0.3
let animationFrameId: number
let autoScrollTimeout: number | undefined = undefined
const paused = ref(false)

// --- Drag ---
const handleMouseDown = (e: MouseEvent) => {
  if (!slider.value) return
  isDown = true
  slider.value.classList.add('active-drag')

  startY = e.pageY
  scrollTopStart = slider.value.scrollTop

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

  const dy = e.pageY - startY
  slider.value.scrollTop = scrollTopStart - dy * 1.5
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
let scrollAccumulator = 0

const autoScrollLoop = () => {
  if (!paused.value && !isDown && slider.value) {
    scrollAccumulator += autoScrollSpeed
    const delta = Math.floor(scrollAccumulator)
    if (delta > 0) {
      slider.value.scrollTop += delta
      scrollAccumulator -= delta
    }
    checkInfiniteScroll()
  }
  animationFrameId = requestAnimationFrame(autoScrollLoop)
}

onMounted(() => {
  if (slider.value && props.autoScroll) {
    const children = Array.from(slider.value.children) // snapshot initial
    const len = children.length
    for (let i = 0; i < len; i++) {
      slider.value.appendChild(children[i].cloneNode(true))
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
  scrollbar-width: none;
  -ms-overflow-style: none;
  cursor: grab;
  height: 400px;
  scroll-behavior: auto;
}

.slider::-webkit-scrollbar {
  display: none;
}

.slider.active-drag {
  cursor: grabbing;
  user-select: none;
}
</style>
