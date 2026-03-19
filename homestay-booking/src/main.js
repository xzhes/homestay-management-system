import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 引入 Element Plus 组件库与样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 使用路由
app.use(router)

// 使用 Element Plus
app.use(ElementPlus)

// 注册所有图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 挂载应用
app.mount('#app')