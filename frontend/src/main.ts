import "./assets/style.css"
import 'element-plus/dist/index.css'
import { createApp } from 'vue'
import App from './App.vue'
import { router } from "./route"
import axios from 'axios';

axios.defaults.baseURL = ("http://localhost:8080")
axios.defaults.timeout = 30000;

createApp(App).use(router).mount('#app')
