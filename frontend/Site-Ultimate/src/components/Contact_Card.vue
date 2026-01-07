<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: String,
  role: String,
  email: String,
  icon: Object,
  color1: String,
  color2: String
})

function sendMail() {
  window.location.href = `mailto:${props.email}`
}
const ROLE_COLORS = {
  backend: {
    color: '#dc2626',
    backgroundColor: '#fee2e2'
  },
  frontend: {
    color: '#7c3aed',
    backgroundColor: '#ede9fe'
  }
}

const roleStyle = computed(() => {
  return ROLE_COLORS[props.role?.toLowerCase()] ?? {
    color: '#155dfc',
    backgroundColor: '#dbeafe'
  }
})

</script>



<template>
  <div class="card_info" @click="sendMail">
    <div class="card-content">
      <div class="header">
        <h3>{{ name }}</h3>
        <span class="role" :style="roleStyle">{{ role }}</span>
      </div>

      <div class="info">
        <p class="value">{{ email }}</p>

        <div class="icon">
          <div class="icon-inner">
            <component
                :is="icon"
                class="card-svg-icon"
                :style="{ color: color2 }"
            />
          </div>
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
  width: 260px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card_info:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

/* ===== HEADER ===== */
.header {
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

h3 {
  margin: 0;
  font-size: 1.1rem;
}

.role {
   font-size: 0.8rem;
   font-weight: 600;
   padding: 0.2rem 0.6rem;
   border-radius: 999px;
   align-self: flex-start;
 }


/* ===== INFO ===== */
.info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.value {
  font-size: 0.9rem;
  font-weight: 500;
}

.icon {
  min-width: 56px;
  min-height: 56px;
  border-radius: 12px;
  display: flex;
  place-items: center;
  background-color: #dbeafe;
  justify-content: center;

}


.card-svg-icon {
  width: 100%;
  height: 100%;
  display: block;
}


</style>
