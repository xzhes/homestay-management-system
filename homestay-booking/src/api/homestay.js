const API_BASE = 'http://localhost:8081/api'
const SERVER_BASE = 'http://localhost:8081'

async function parseResponse(res) {
  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return await res.json()
  }
  return { code: res.ok ? 200 : res.status, message: await res.text() }
}

export const homestayApi = {
  async login(username, password) {
    const res = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    return await parseResponse(res)
  },

  async getHomestays() {
    const res = await fetch(`${API_BASE}/homestays`)
    return await parseResponse(res)
  },

  async submitReservation(userId, roomId, date) {
    const res = await fetch(`${API_BASE}/reserve/submit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId, roomId, date })
    })
    return await parseResponse(res)
  },

  async getReservations(userId) {
    const query = userId ? `?userId=${encodeURIComponent(userId)}` : ''
    const res = await fetch(`${API_BASE}/reservations${query}`)
    return await parseResponse(res)
  },

  async deleteReservation(id) {
    const res = await fetch(`${API_BASE}/reservations/${id}`, {
      method: 'DELETE'
    })
    return await parseResponse(res)
  },

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

  async uploadHomestayImage(file) {
    const formData = new FormData()
    formData.append('file', file)

    const res = await fetch(`${API_BASE}/admin/homestays/upload`, {
      method: 'POST',
      body: formData
    })
    return await parseResponse(res)
  },

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

  getImageUrl(path) {
    if (!path) return ''
    if (path.startsWith('http://') || path.startsWith('https://')) return path
    return `${SERVER_BASE}${path}`
  }
}
