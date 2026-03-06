// 统一管理所有 API 请求
const API_BASE = 'http://localhost:8081/api'  // ✅ 保持8081不变

export const homestayApi = {
    // 登录
    async login(username, password) {
        const res = await fetch(`${API_BASE}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        })
        return await res.json()  // ⭐ 只改这里：text() 改成 json()
    },

    // 获取民宿列表
    async getHomestays() {
        const res = await fetch(`${API_BASE}/homestays`)
        return await res.json()
    },

    // 提交预约
    async submitReservation(userId, roomId, date) {
        const res = await fetch(`${API_BASE}/reserve/submit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, roomId, date })
        })
        return await res.text()
    }
}