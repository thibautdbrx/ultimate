<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: String,
  type: String,
  placeholder: String,
  modelValue: String,
  icon: [String,Object],
  clickable: {type: Boolean, default:true},
})

const emit = defineEmits(['update:modelValue']) // sert dire au parent que la variable à changer dans son élément
const model = computed({ //dit de envoyer l'info au parents quand model est modifier
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
</script>


<template>
  <div class="input-group">
    <label>{{ label }}</label>
    <div class="input-wrapper">
      <span class="icon">

        <component :is="icon" alt="icon" class="card-svg-icon"/>

      </span>
      <input v-if="clickable"
          :type="type"
          :placeholder="placeholder"
          v-model="model"
      />

      <input v-else
             disabled
             :type="type"
             :placeholder="placeholder"
             v-model="model"
      />
    </div>
  </div>
</template>



<style scoped>
.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

label {
  font-size: 0.85rem;
  color: #333;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 0.4rem 0.6rem;
  background: #fafafa;
}

.icon {
  color: #888;
  margin-right: 0.4rem;
  display: flex;
  align-items: center;
}

input {
  border: none;
  outline: none;
  flex: 1;
  background: transparent;
  font-size: 0.95rem;
  padding: 0.4rem 0;
}
</style>
