const API_BASE = 'http://localhost:8081/api'

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
  }
}
