// 后端接口基础地址
const API_BASE = 'http://localhost:8081/api'
const SERVER_BASE = 'http://localhost:8081'

// 统一解析接口返回
async function parseResponse(res) {
  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return await res.json()
  }
  return { code: res.ok ? 200 : res.status, message: await res.text() }
}

export const homestayApi = {
  // 登录
  async login(username, password) {
    const res = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    return await parseResponse(res)
  },

  // 注册
  async register(username, password) {
    const res = await fetch(`${API_BASE}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    return await parseResponse(res)
  },

  // 获取房源列表
  async getHomestays() {
    const res = await fetch(`${API_BASE}/homestays`)
    return await parseResponse(res)
  },

  async getAnnouncements() {
    const res = await fetch(`${API_BASE}/announcements`)
    return await parseResponse(res)
  },

  // 提交预约
  async submitReservation(payload) {
    const res = await fetch(`${API_BASE}/reserve/submit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  // 查询预约（可按 userId 过滤）
  async getReservations(userId) {
    const query = userId ? `?userId=${encodeURIComponent(userId)}` : ''
    const res = await fetch(`${API_BASE}/reservations${query}`)
    return await parseResponse(res)
  },

  // 删除预约
  async deleteReservation(id) {
    const res = await fetch(`${API_BASE}/reservations/${id}`, {
      method: 'DELETE'
    })
    return await parseResponse(res)
  },

  // 后台确认预约
  async confirmReservation(id) {
    const res = await fetch(`${API_BASE}/reservations/${id}/confirm`, {
      method: 'PUT'
    })
    return await parseResponse(res)
  },

  // 用户办理入住
  async checkInReservation(id, userId) {
    const res = await fetch(`${API_BASE}/reservations/${id}/check-in?userId=${encodeURIComponent(userId)}`, {
      method: 'PUT'
    })
    return await parseResponse(res)
  },

  // 用户办理退房
  async checkOutReservation(id, userId) {
    const res = await fetch(`${API_BASE}/reservations/${id}/check-out?userId=${encodeURIComponent(userId)}`, {
      method: 'PUT'
    })
    return await parseResponse(res)
  },

  // 后台房源管理
  async getAdminHomestays() {
    const res = await fetch(`${API_BASE}/admin/homestays`)
    return await parseResponse(res)
  },

  async createHomestay(payload) {
    const res = await fetch(`${API_BASE}/admin/homestays`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  async updateHomestay(id, payload) {
    const res = await fetch(`${API_BASE}/admin/homestays/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  async deleteHomestay(id) {
    const res = await fetch(`${API_BASE}/admin/homestays/${id}`, {
      method: 'DELETE'
    })
    return await parseResponse(res)
  },

  async getAdminAnnouncements() {
    const res = await fetch(`${API_BASE}/admin/announcements`)
    return await parseResponse(res)
  },

  async createAnnouncement(payload) {
    const res = await fetch(`${API_BASE}/admin/announcements`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  async updateAnnouncement(id, payload) {
    const res = await fetch(`${API_BASE}/admin/announcements/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  async deleteAnnouncement(id) {
    const res = await fetch(`${API_BASE}/admin/announcements/${id}`, {
      method: 'DELETE'
    })
    return await parseResponse(res)
  },

  // 上传房源图片
  async uploadHomestayImage(file) {
    const formData = new FormData()
    formData.append('file', file)

    const res = await fetch(`${API_BASE}/admin/homestays/upload`, {
      method: 'POST',
      body: formData
    })
    return await parseResponse(res)
  },

  // 后台用户管理
  async getUsers() {
    const res = await fetch(`${API_BASE}/admin/users`)
    return await parseResponse(res)
  },

  async createUser(payload) {
    const res = await fetch(`${API_BASE}/admin/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  async updateUser(id, payload) {
    const res = await fetch(`${API_BASE}/admin/users/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return await parseResponse(res)
  },

  async deleteUser(id) {
    const res = await fetch(`${API_BASE}/admin/users/${id}`, {
      method: 'DELETE'
    })
    return await parseResponse(res)
  },

  // 拼接图片访问地址
  getImageUrl(path) {
    if (!path) return ''
    if (path.startsWith('http://') || path.startsWith('https://')) return path
    return `${SERVER_BASE}${path}`
  }
}
