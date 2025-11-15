<!-- components/SliderVertical.vue -->
<script setup>
import { ref, onMounted, onBeforeUnmount, defineProps } from 'vue'

const props = defineProps({
  autoScroll: { type: Boolean, default: false },
  autoScrollDelay: { type: Number, default: 1000 }
})

const slider = ref(null)

let isDown = false
let startY = 0
let scrollTopStart = 0
let autoScrollSpeed = 0.3
let animationFrameId
let autoScrollTimeout
const paused = ref(false)
let scrollAccumulator = 0

// --- Drag ---
function handleMouseDown(e) {
  if (!slider.value) return
  isDown = true
  slider.value.classList.add('active-drag')

  startY = e.pageY
  scrollTopStart = slider.value.scrollTop

  paused.value = true
  if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
}

function handleMouseUp() {
  isDown = false
  if (slider.value) slider.value.classList.remove('active-drag')

  if (props.autoScroll) {
    if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
    autoScrollTimeout = setTimeout(() => {
      paused.value = false
    }, props.autoScrollDelay)
  }
}

function handleMouseMove(e) {
  if (!isDown || !slider.value) return
  e.preventDefault()

  const dy = e.pageY - startY
  slider.value.scrollTop = scrollTopStart - dy * 1.5
  checkInfiniteScroll()
}

// --- Boucle infinie ---
function checkInfiniteScroll() {
  if (!slider.value) return
  const totalHeight = slider.value.scrollHeight

  if (slider.value.scrollTop >= totalHeight / 2) {
    slider.value.scrollTop -= totalHeight / 2
  } else if (slider.value.scrollTop <= 0) {
    slider.value.scrollTop += totalHeight / 2
  }
}

function autoScrollLoop() {
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

// --- Lifecycle ---
onMounted(() => {
  if (slider.value && props.autoScroll) {
    // clone les enfants pour boucle infinie
    const children = Array.from(slider.value.children)
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
