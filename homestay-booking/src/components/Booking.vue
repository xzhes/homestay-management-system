<template>
  <div class="booking-page">
    <header class="top-nav">
      <div class="brand">民宿管理系统</div>
      <nav class="nav-links">
        <a href="javascript:void(0)" @click="router.push('/home')">首页</a>
        <a href="javascript:void(0)">系统公告</a>
        <a href="javascript:void(0)">用户留言</a>
        <a href="javascript:void(0)" class="active">预约入住</a>
      </nav>
      <div class="user-box">
        <span>{{ userName }}</span>
        <el-button size="small" type="danger" @click="logout">退出</el-button>
      </div>
    </header>

    <main class="container">
      <section class="section-title">预约入住</section>

      <el-card shadow="never">
        <el-form :inline="true" class="reserve-form">
          <el-form-item label="选择房源">
            <el-select v-model="form.roomId" placeholder="请选择房源" style="width: 240px">
              <el-option v-for="room in homestays" :key="room.id" :label="room.name" :value="room.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="入住日期">
            <el-date-picker
              v-model="form.date"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择日期"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitReservation">提交预约</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <section class="section-title">我的预约</section>
      <el-card shadow="never">
        <el-table :data="reservations" v-loading="loading" empty-text="暂无预约记录">
          <el-table-column prop="id" label="预约ID" width="100" />
          <el-table-column prop="roomName" label="房间名称" min-width="160" />
          <el-table-column prop="date" label="入住日期" width="140" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" link @click="deleteReservation(scope.row.id)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homestayApi } from '../api/homestay'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const userName = ref(user.username || '游客')
const userId = ref(user.id)
const homestays = ref([])
const reservationRaw = ref([])
const loading = ref(false)

const form = ref({
  roomId: '',
  date: ''
})

const roomNameMap = computed(() => {
  const map = {}
  homestays.value.forEach((item) => {
    map[item.id] = item.name
  })
  return map
})

const reservations = computed(() => {
  return reservationRaw.value.map((item) => ({
    ...item,
    roomName: roomNameMap.value[item.roomId] || `房源#${item.roomId}`
  }))
})

const loadHomestays = async () => {
  const data = await homestayApi.getHomestays()
  homestays.value = Array.isArray(data) ? data : []
}

const loadReservations = async () => {
  const data = await homestayApi.getReservations(userId.value)
  reservationRaw.value = Array.isArray(data) ? data : []
}

const submitReservation = async () => {
  if (!form.value.roomId || !form.value.date) {
    ElMessage.warning('请选择房源和日期')
    return
  }
  try {
    const result = await homestayApi.submitReservation(userId.value, form.value.roomId, form.value.date)
    if (result.code === 200) {
      ElMessage.success('预约成功')
      form.value = { roomId: '', date: '' }
      await loadReservations()
      return
    }
    ElMessage.error(result.message || '预约失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('预约失败')
  }
}

const deleteReservation = async (id) => {
  try {
    await ElMessageBox.confirm('确认取消该预约吗？', '提示', { type: 'warning' })
    const result = await homestayApi.deleteReservation(id)
    if (result.code === 200) {
      ElMessage.success('取消成功')
      await loadReservations()
      return
    }
    ElMessage.error(result.message || '取消失败')
  } catch (err) {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('取消失败')
    }
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
  loading.value = true
  try {
    await Promise.all([loadHomestays(), loadReservations()])
  } catch (err) {
    console.error(err)
    ElMessage.error('页面数据加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.booking-page { min-height: 100vh; background: #ece9d6; }
.top-nav { height: 56px; background: #c9ac82; display: grid; grid-template-columns: 180px 1fr auto; align-items: center; padding: 0 16px; color: #4d3b2d; border-bottom: 1px solid #b99b73; }
.brand { font-weight: 700; }
.nav-links { display: flex; gap: 28px; justify-content: center; }
.nav-links a { color: #5c4630; text-decoration: none; font-weight: 600; }
.nav-links a.active { color: #2b1c10; }
.user-box { display: flex; gap: 10px; align-items: center; }
.container { max-width: 1220px; margin: 0 auto; padding: 16px; }
.section-title { margin: 16px 0 12px; background: #d1b084; color: #6d4f31; text-align: center; padding: 8px; border-radius: 6px; font-weight: 700; }
.reserve-form { display: flex; flex-wrap: wrap; }
@media (max-width: 760px) {
  .top-nav { grid-template-columns: 1fr; gap: 8px; height: auto; padding: 10px; }
  .nav-links { justify-content: start; gap: 16px; flex-wrap: wrap; }
}
</style>
