import { createRouter, createWebHistory } from 'vue-router'

// 页面组件（先用占位组件，后面再创建实际页面）
const routes = [
    {
        path: '/',
        redirect: '/login'  // 默认跳转到登录页
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../App.vue')  // 暂时用 App.vue（包含登录）
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('../components/Home.vue'),  // 客人首页
        meta: { requiresAuth: true }
    },
    {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('../components/admin/Dashboard.vue'),  // 管理员后台
        meta: { requiresAuth: true, role: 'admin' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫：检查登录状态和权限
router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}')

    if (to.meta.requiresAuth) {
        // 需要登录的页面
        if (!user.username) {
            // 未登录，跳转到登录页
            next('/login')
        } else if (to.meta.role && user.role !== to.meta.role) {
            // 角色不匹配
            alert('无权访问此页面')
            next('/home')
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router