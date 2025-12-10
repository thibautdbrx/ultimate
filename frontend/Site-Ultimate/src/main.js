import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './main.css'

import { createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)

const auth = useAuthStore()
auth.loadToken()

createApp(App).use(router).mount('#app')
