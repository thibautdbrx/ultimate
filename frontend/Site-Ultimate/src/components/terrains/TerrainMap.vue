<script setup>
import "leaflet/dist/leaflet.css"
import { LMap, LTileLayer, LMarker, LTooltip } from "@vue-leaflet/vue-leaflet"

defineProps({
  terrains: { type: Array, default: () => [] },
  zoom: { type: Number, default: 6 },
  center: { type: Array, default: () => [46.603354, 1.888334] }
})

const emit = defineEmits(['select-marker', 'update:zoom', 'update:center'])
</script>

<template>
  <div class="map-wrapper">
    <l-map
        :zoom="zoom"
        :center="center"
        :use-global-leaflet="false"
        @update:zoom="emit('update:zoom', $event)"
        @update:center="emit('update:center', $event)"
    >
      <l-tile-layer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"></l-tile-layer>
      <l-marker
          v-for="terrain in terrains"
          :key="terrain.id_terrain"
          :lat-lng="[terrain.latitude, terrain.longitude]"
          @click="emit('select-marker', terrain)"
      >
        <l-tooltip>{{ terrain.nom }}</l-tooltip>
      </l-marker>
    </l-map>
  </div>
</template>

<style scoped>
.map-wrapper { height: 100%; width: 100%; }
</style>