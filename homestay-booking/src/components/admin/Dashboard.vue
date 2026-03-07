<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-logo">H</div>
        <div class="brand-text">
          <h2>民宿管理系统</h2>
          <p>后台首页</p>
        </div>
      </div>

      <ul class="menu">
        <li v-for="item in menuItems" :key="item" class="menu-item" :class="{ active: item === '首页' }">
          {{ item }}
        </li>
      </ul>
    </aside>

    <div class="main">
      <header class="topbar">
        <div class="topbar-left">☰</div>
        <div class="topbar-right">
          <span>{{ userName }}</span>
          <el-button size="small" type="danger" @click="handleLogout">退出</el-button>
        </div>
      </header>

      <main class="content">
        <section class="hero">
          <h1>Hi ~ 欢迎使用民宿管理系统后台</h1>
          <p>管理房源、预约与用户信息，保持运营清晰高效。</p>
        </section>

        <section class="stats-section">
          <h3 class="section-title">数据统计</h3>
          <div class="stats-grid">
            <article class="stat-card">
              <div class="stat-icon icon-home">🏠</div>
              <div>
                <div class="stat-value">{{ homestays.length }}</div>
                <div class="stat-label">房源总数</div>
              </div>
            </article>
            <article class="stat-card">
              <div class="stat-icon icon-booking">📋</div>
              <div>
                <div class="stat-value">{{ reservations.length }}</div>
                <div class="stat-label">预约总数</div>
              </div>
            </article>
            <article class="stat-card">
              <div class="stat-icon icon-today">🗓</div>
              <div>
                <div class="stat-value">{{ todayCount }}</div>
                <div class="stat-label">今日预约</div>
              </div>
            </article>
            <article class="stat-card">
              <div class="stat-icon icon-user">👤</div>
              <div>
                <div class="stat-value">{{ uniqueUserCount }}</div>
                <div class="stat-label">预约用户数</div>
              </div>
            </article>
          </div>
        </section>

        <section class="banner">
          <div class="banner-overlay">
            <h3>温馨舒适的住宿体验</h3>
            <p>一个更简洁的管理后台首页示例</p>
          </div>
        </section>

        <section class="feature-section">
          <h3 class="section-title">系统功能</h3>
          <div class="feature-grid">
            <article class="feature-card">
              <h4>房源管理</h4>
              <p>维护房源基础信息</p>
            </article>
            <article class="feature-card">
              <h4>预约管理</h4>
              <p>查看并处理用户预约</p>
            </article>
            <article class="feature-card">
              <h4>用户管理</h4>
              <p>统一管理管理员和普通用户</p>
            </article>
          </div>
        </section>

        <section class="table-wrap">
          <el-card shadow="never">
            <template #header>
              <div class="table-title">最新预约</div>
            </template>
            <el-table :data="recentReservations" v-loading="loading" empty-text="暂无预约">
              <el-table-column prop="id" label="预约ID" width="100" />
              <el-table-column prop="userId" label="用户ID" width="100" />
              <el-table-column prop="roomId" label="房源ID" width="100" />
              <el-table-column prop="date" label="入住日期" width="140" />
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button type="danger" link @click="removeReservation(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homestayApi } from '../../api/homestay'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const userName = ref(user.username || '管理员')
const homestays = ref([])
const reservations = ref([])
const loading = ref(false)
const menuItems = ['首页', '用户管理', '房源管理', '预约管理']

const todayCount = computed(() => {
  const today = new Date().toISOString().slice(0, 10)
  return reservations.value.filter(item => item.date === today).length
})

const uniqueUserCount = computed(() => new Set(reservations.value.map(item => item.userId)).size)

const recentReservations = computed(() => reservations.value.slice(0, 6))

const loadData = async () => {
  loading.value = true
  try {
    const [rooms, orders] = await Promise.all([
      homestayApi.getHomestays(),
      homestayApi.getReservations()
    ])
    homestays.value = Array.isArray(rooms) ? rooms : []
    reservations.value = Array.isArray(orders) ? orders : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const removeReservation = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该预约吗？', '提示', { type: 'warning' })
    const result = await homestayApi.deleteReservation(id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadData()
      return
    }
    ElMessage.error(result.message || '删除失败')
  } catch (err) {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('删除失败')
    }
  }
}

const handleLogout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

onMounted(async () => {
  if (user.role !== 'admin') {
    router.push('/home')
    return
  }
  await loadData()
})
</script>

<style scoped>
:root {
  --bg: #f6f3eb;
  --panel: #efe8db;
  --ink: #49382c;
  --muted: #8e7b69;
  --card: #ffffff;
}

.dashboard {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 230px 1fr;
  background: var(--bg);
  color: var(--ink);
}

.sidebar {
  background: linear-gradient(180deg, #efe7da 0%, #e7dece 100%);
  border-right: 1px solid #decfb8;
  padding: 18px 12px;
}

.brand {
  display: flex;
  gap: 10px;
  align-items: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
}

.brand-logo {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #d7c3a7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.brand-text h2 {
  margin: 0;
  font-size: 16px;
}

.brand-text p {
  margin: 2px 0 0;
  color: var(--muted);
  font-size: 12px;
}

.menu {
  margin: 16px 0 0;
  padding: 0;
  list-style: none;
}

.menu-item {
  padding: 10px 12px;
  border-radius: 10px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.35);
  color: #5a4737;
}

.menu-item.active {
  background: #ddcbb1;
  font-weight: 600;
}

.main {
  min-width: 0;
}

.topbar {
  height: 52px;
  background: #faf7f1;
  border-bottom: 1px solid #eadfce;
  padding: 0 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.content {
  padding: 18px;
}

.hero {
  background: radial-gradient(circle at 20% 30%, #efe4d2 0, #efe4d2 12%, #ede5d9 13%, #efe6dc 100%);
  border-radius: 16px;
  padding: 30px 24px;
  text-align: center;
  margin-bottom: 18px;
}

.hero h1 {
  margin: 0 0 8px;
  font-size: 40px;
}

.hero p {
  margin: 0;
  color: var(--muted);
}

.section-title {
  text-align: center;
  font-size: 32px;
  margin: 0 0 12px;
}

.stats-section,
.feature-section {
  margin-bottom: 18px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.stat-card {
  background: var(--card);
  border-radius: 14px;
  padding: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 8px 20px rgba(88, 67, 44, 0.07);
}

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
}

.icon-home {
  background: linear-gradient(135deg, #5e75e5, #7457c8);
}

.icon-booking {
  background: linear-gradient(135deg, #ef6d8f, #d54676);
}

.icon-today {
  background: linear-gradient(135deg, #45b8eb, #3d92cf);
}

.icon-user {
  background: linear-gradient(135deg, #41c08c, #2b9f71);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  margin-top: 4px;
  color: var(--muted);
  font-size: 13px;
}

.banner {
  height: 220px;
  border-radius: 18px;
  margin-bottom: 18px;
  background:
    linear-gradient(120deg, rgba(37, 29, 21, 0.75), rgba(84, 61, 45, 0.35)),
    linear-gradient(180deg, #ab8a70 0%, #6f5b4d 100%);
  display: flex;
  align-items: end;
}

.banner-overlay {
  color: #fff;
  padding: 20px;
}

.banner-overlay h3 {
  margin: 0 0 6px;
  font-size: 26px;
}

.banner-overlay p {
  margin: 0;
  opacity: 0.9;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.feature-card {
  background: var(--card);
  border-radius: 14px;
  padding: 18px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(88, 67, 44, 0.07);
}

.feature-card h4 {
  margin: 0 0 8px;
}

.feature-card p {
  margin: 0;
  color: var(--muted);
}

.table-title {
  font-weight: 600;
}

@media (max-width: 1000px) {
  .dashboard {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }

  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .hero h1 {
    font-size: 24px;
  }

  .section-title {
    font-size: 22px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
