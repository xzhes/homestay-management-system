import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../components/Login.vue')
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('../components/Home.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('../components/admin/Dashboard.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}')

    if (to.meta.requiresAuth) {
        if (!user.username) {
            next('/login')
        } else if (to.meta.role && user.role !== to.meta.role) {
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