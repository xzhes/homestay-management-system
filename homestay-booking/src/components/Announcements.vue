<template>
  <div class="announcement-page">
    <header class="top-nav">
      <div class="brand">民宿管理系统</div>
      <nav class="nav-links">
        <a href="javascript:void(0)" @click="router.push('/home')">首页</a>
        <a href="javascript:void(0)" class="active">系统公告</a>
        <a href="javascript:void(0)">用户留言</a>
        <a href="javascript:void(0)" @click="router.push('/booking')">预约入住</a>
      </nav>
      <div class="user-box">
        <span>{{ userName }}</span>
        <el-button size="small" type="danger" @click="logout">退出</el-button>
      </div>
    </header>

    <main class="container">
      <section class="section-title">系统公告</section>

      <el-card shadow="never" v-loading="loading">
        <div v-if="announcements.length" class="announcement-list">
          <article v-for="item in announcements" :key="item.id" class="announcement-item">
            <div class="item-head">
              <h3>{{ item.title }}</h3>
              <span>{{ formatTime(item.createdAt) }}</span>
            </div>
            <p>{{ item.content }}</p>
          </article>
        </div>
        <el-empty v-else description="暂无系统公告" />
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { homestayApi } from '../api/homestay'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const userName = ref(user.username || '游客')
const userId = ref(user.id)
const announcements = ref([])
const loading = ref(false)

const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const data = await homestayApi.getAnnouncements()
    announcements.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('获取公告失败')
  } finally {
    loading.value = false
  }
}

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

onMounted(async () => {
  if (!userId.value) {
    router.push('/login')
    return
  }
  await loadAnnouncements()
})
</script>

<style scoped>
.announcement-page { min-height: 100vh; background: #ece9d6; }
.top-nav { height: 56px; background: #c9ac82; display: grid; grid-template-columns: 180px 1fr auto; align-items: center; padding: 0 16px; color: #4d3b2d; border-bottom: 1px solid #b99b73; }
.brand { font-weight: 700; }
.nav-links { display: flex; gap: 28px; justify-content: center; }
.nav-links a { color: #5c4630; text-decoration: none; font-weight: 600; }
.nav-links a.active { color: #2b1c10; }
.user-box { display: flex; gap: 10px; align-items: center; }
.container { max-width: 980px; margin: 0 auto; padding: 16px; }
.section-title { margin: 16px 0 12px; background: #d1b084; color: #6d4f31; text-align: center; padding: 8px; border-radius: 6px; font-weight: 700; }
.announcement-list { display: flex; flex-direction: column; gap: 12px; }
.announcement-item { border: 1px solid #eadfcf; border-radius: 8px; padding: 14px; background: #fffaf2; }
.item-head { display: flex; justify-content: space-between; gap: 12px; align-items: center; border-bottom: 1px solid #efe5d6; padding-bottom: 8px; margin-bottom: 10px; }
.item-head h3 { margin: 0; color: #513c2b; font-size: 18px; }
.item-head span { color: #9a8067; font-size: 13px; white-space: nowrap; }
.announcement-item p { margin: 0; color: #67513e; line-height: 1.8; white-space: pre-wrap; }
@media (max-width: 760px) {
  .top-nav { grid-template-columns: 1fr; gap: 8px; height: auto; padding: 10px; }
  .nav-links { justify-content: start; gap: 16px; flex-wrap: wrap; }
  .item-head { align-items: flex-start; flex-direction: column; }
}
</style>
