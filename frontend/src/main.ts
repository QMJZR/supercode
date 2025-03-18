import "./assets/style.css"

import { createApp } from 'vue'
import App from './App.vue'
import { router } from "./route"

createApp(App).use(router).mount('#app')
