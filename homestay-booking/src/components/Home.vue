<template>
  <div class="home-page">
    <header class="header">
      <h1>民宿预约</h1>
      <div class="user-tools">
        <span>当前用户：{{ userName }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </div>
    </header>

    <main class="content">
      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="card-title">可预订房源</div>
        </template>

        <el-empty v-if="!homestays.length && !loadingHomestays" description="暂无房源" />
        <div v-else class="homestay-grid">
          <el-card v-for="room in homestays" :key="room.id" class="room-card" shadow="hover">
            <h3>{{ room.name }}</h3>
            <p class="price">￥{{ room.price }} / 晚</p>
            <p class="desc">{{ room.description || '暂无描述' }}</p>
            <div class="reserve-row">
              <el-date-picker
                v-model="dateMap[room.id]"
                type="date"
                placeholder="选择入住日期"
                value-format="YYYY-MM-DD"
              />
              <el-button type="primary" @click="reserve(room.id)">提交预约</el-button>
            </div>
          </el-card>
        </div>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="card-title">我的预约</div>
        </template>

        <el-table :data="myReservations" v-loading="loadingReservations" empty-text="暂无预约">
          <el-table-column prop="id" label="预约ID" width="100" />
          <el-table-column prop="roomId" label="房源ID" width="100" />
          <el-table-column prop="date" label="入住日期" width="160" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="danger" link @click="cancelReservation(scope.row.id)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homestayApi } from '../api/homestay'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const userName = ref(user.username || '游客')
const userId = ref(user.id)
const homestays = ref([])
const myReservations = ref([])
const dateMap = ref({})
const loadingHomestays = ref(false)
const loadingReservations = ref(false)

const loadHomestays = async () => {
  loadingHomestays.value = true
  try {
    const data = await homestayApi.getHomestays()
    homestays.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('获取房源失败')
  } finally {
    loadingHomestays.value = false
  }
}

const loadReservations = async () => {
  if (!userId.value) return

  loadingReservations.value = true
  try {
    const data = await homestayApi.getReservations(userId.value)
    myReservations.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('获取预约失败')
  } finally {
    loadingReservations.value = false
  }
}

const reserve = async (roomId) => {
  if (!userId.value) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }

  const date = dateMap.value[roomId]
  if (!date) {
    ElMessage.warning('请先选择入住日期')
    return
  }

  try {
    const result = await homestayApi.submitReservation(userId.value, roomId, date)
    if (result.code === 200) {
      ElMessage.success('预约成功')
      await loadReservations()
      return
    }
    ElMessage.error(result.message || '预约失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('预约失败')
  }
}

const cancelReservation = async (id) => {
  try {
    await ElMessageBox.confirm('确认取消该预约吗？', '提示', {
      type: 'warning'
    })

    const result = await homestayApi.deleteReservation(id)
    if (result.code === 200) {
      ElMessage.success('已取消')
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

  await Promise.all([loadHomestays(), loadReservations()])
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f2f4f8;
}

.header {
  background: #ffffff;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5eaf3;
}

.header h1 {
  margin: 0;
  color: #273043;
}

.user-tools {
  display: flex;
  gap: 12px;
  align-items: center;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-card {
  margin-bottom: 16px;
}

.card-title {
  font-weight: 600;
}

.homestay-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.room-card h3 {
  margin: 0 0 10px;
  color: #273043;
}

.price {
  margin: 0 0 8px;
  color: #d1495b;
  font-weight: 700;
}

.desc {
  margin: 0 0 14px;
  color: #5c6370;
}

.reserve-row {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
