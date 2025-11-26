<script setup >
defineProps({
  title: String,
  value: Number,
  icon: Object,
  color1: String,
  color2:String
})
</script>

<template>
  <div class="card_info">
    <div class="card-content">
      <h3>{{ title }}</h3>
      <div class="info">
        <p class="value">{{ value }}</p>
        <div class="icon" :style="{ backgroundColor: color1 }">
          <component :is="icon" alt="icon" class="card-svg-icon" :style="{color:color2}"/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card_info {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 1rem 1.5rem;
  width: 220px;
  transition: transform 0.2s ease;
}



.card-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.value {
  font-size: 1.5rem;
  font-weight: 600;
}

.icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}



h3{
  margin: 0;
}
</style>