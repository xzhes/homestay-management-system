<template>
  <div class="page">
    <header class="page-header">
      <h2>预约管理</h2>
      <div class="actions">
        <el-button @click="router.push('/admin/dashboard')">返回首页</el-button>
      </div>
    </header>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" empty-text="暂无预约数据">
        <el-table-column prop="id" label="预约ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column label="房间图片" width="120">
          <template #default="scope">
            <img :src="scope.row.roomImage" class="thumb" alt="房间图片" />
          </template>
        </el-table-column>
        <el-table-column prop="roomName" label="房间名称" min-width="180" />
        <el-table-column prop="roomId" label="房源ID" width="100" />
        <el-table-column prop="date" label="入住日期" width="140" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" link @click="removeReservation(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homestayApi } from '../../api/homestay'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const defaultImage = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="240" height="160"><rect width="100%" height="100%" fill="%23efe3cf"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23745a3f" font-size="20">No Image</text></svg>'
const loading = ref(false)
const homestays = ref([])
const reservations = ref([])

const roomMap = computed(() => {
  const map = {}
  homestays.value.forEach((room) => {
    map[room.id] = room
  })
  return map
})

const tableData = computed(() => {
  return reservations.value.map((item) => {
    const room = roomMap.value[item.roomId] || {}
    return {
      ...item,
      roomName: room.name || `房源#${item.roomId}`,
      roomImage: homestayApi.getImageUrl(room.imageUrl) || defaultImage
    }
  })
})

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
    ElMessage.error('加载预约数据失败')
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

onMounted(async () => {
  if (user.role !== 'admin') {
    router.push('/home')
    return
  }
  await loadData()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f6f3eb;
  padding: 20px;
}

.page-header {
  margin-bottom: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  margin: 0;
  color: #4d3b2f;
}

.actions {
  display: flex;
  gap: 8px;
}

.thumb {
  width: 70px;
  height: 50px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #ece1cf;
  background: #f8f4ec;
}
</style>
