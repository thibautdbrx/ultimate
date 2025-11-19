<script setup>
import { ref, onMounted, onBeforeUnmount, defineProps } from 'vue'

const props = defineProps({
  autoScroll: { type: Boolean, default: false },
  autoScrollDelay: { type: Number, default: 1000 }
})

const slider = ref(null)

let isDown = false
let startX = 0
let scrollLeft = 0
let autoScrollSpeed = 0.5
let animationFrameId
let autoScrollTimeout
const paused = ref(false)

// --- Drag ---
const handleMouseDown = (e) => {
  isDown = true
  if (!slider.value) return
  slider.value.classList.add('active-drag')
  startX = e.pageX - slider.value.offsetLeft
  scrollLeft = slider.value.scrollLeft

  paused.value = true
  if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
}

const handleMouseUp = () => {
  isDown = false
  if (slider.value) slider.value.classList.remove('active-drag')

  if (props.autoScroll) {
    if (autoScrollTimeout) clearTimeout(autoScrollTimeout)
    autoScrollTimeout = setTimeout(() => {
      paused.value = false
    }, props.autoScrollDelay)
  }
}

const handleMouseMove = (e) => {
  if (!isDown || !slider.value) return
  e.preventDefault()
  const x = e.pageX - slider.value.offsetLeft
  const walk = (x - startX) * 1.5
  slider.value.scrollLeft = scrollLeft - walk
  checkInfiniteScroll()
}

// --- Boucle infinie ---
const checkInfiniteScroll = () => {
  if (!slider.value) return
  const totalWidth = slider.value.scrollWidth
  if (slider.value.scrollLeft >= totalWidth / 2) {
    slider.value.scrollLeft -= totalWidth / 2
  } else if (slider.value.scrollLeft <= 0) {
    slider.value.scrollLeft += totalWidth / 2
  }
}

// --- Auto-scroll continu ---
const autoScrollLoop = () => {
  if (!props.autoScroll) {
    animationFrameId = requestAnimationFrame(autoScrollLoop)
    return
  }

  if (!paused.value && !isDown && slider.value) {
    slider.value.scrollLeft += autoScrollSpeed
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
  flex-direction: row;
  gap: 1.5rem;
  padding: 2rem;
  overflow-x: scroll;
  scrollbar-width: none;
  -ms-overflow-style: none;
  cursor: grab;
  flex-wrap: nowrap;
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
