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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="guestName" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="idCard" label="身份证" min-width="180" />
        <el-table-column prop="gender" label="性别" width="90" />
        <el-table-column label="图片" width="120">
          <template #default="scope"><img :src="scope.row.roomImage" class="thumb" alt="房间图片" /></template>
        </el-table-column>
        <el-table-column prop="roomType" label="房间类型" min-width="130" />
        <el-table-column prop="roomNumber" label="房号" width="90" />
        <el-table-column prop="date" label="入住时间" width="120" />
        <el-table-column prop="stayDays" label="居住天数" width="100" />
        <el-table-column prop="statusText" label="状态" width="100" />
        <el-table-column prop="note" label="备注" min-width="120" />
        <el-table-column prop="paidAmountText" label="实付金额" width="110" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === '待确认' || scope.row.status === '已预订' || scope.row.status === 'BOOKED'" type="primary" link @click="confirmReservation(scope.row.id)">确认</el-button>
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

// 路由与登录信息
const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
// 没有图片时的兜底占位图
const defaultImage = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="240" height="160"><rect width="100%" height="100%" fill="%23efe3cf"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23745a3f" font-size="20">No Image</text></svg>'

const loading = ref(false)
const homestays = ref([])
const reservations = ref([])
const users = ref([])

// 状态字段统一显示为中文
const statusMap = {
  BOOKED: '待确认',
  已预订: '待确认',
  CHECKED_IN: '已入住',
  CHECKED_OUT: '已退房',
  待确认: '待确认',
  待入住: '待入住',
  已入住: '已入住',
  已退房: '已退房'
}

// 房源 ID -> 房源对象
const roomMap = computed(() => {
  const map = {}
  homestays.value.forEach((room) => { map[room.id] = room })
  return map
})

// 用户 ID -> 用户名
const userMap = computed(() => {
  const map = {}
  users.value.forEach((item) => { map[item.id] = item.username })
  return map
})

// 组合展示字段（图片/用户名/状态文案/金额）
const tableData = computed(() => reservations.value.map((item) => {
  const room = roomMap.value[item.roomId] || {}
  return {
    ...item,
    userName: userMap.value[item.userId] || `用户#${item.userId}`,
    roomType: item.roomType || room.name || `房源#${item.roomId}`,
    statusText: statusMap[item.status] || item.status || '',
    roomImage: homestayApi.getImageUrl(room.imageUrl) || defaultImage,
    paidAmountText: item.paidAmount == null ? '' : `¥${item.paidAmount}`
  }
}))

// 加载房源、预约、用户数据
const loadData = async () => {
  loading.value = true
  try {
    const [rooms, orders, userList] = await Promise.all([
      homestayApi.getHomestays(),
      homestayApi.getReservations(),
      homestayApi.getUsers()
    ])
    homestays.value = Array.isArray(rooms) ? rooms : []
    reservations.value = Array.isArray(orders) ? orders : []
    users.value = Array.isArray(userList) ? userList : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载预约数据失败')
  } finally {
    loading.value = false
  }
}

// 删除预约
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

// 后台确认预约（待确认 -> 待入住）
const confirmReservation = async (id) => {
  try {
    const result = await homestayApi.confirmReservation(id)
    if (result.code === 200) {
      ElMessage.success('确认成功，状态已变更为待入住')
      await loadData()
      return
    }
    ElMessage.error(result.message || '确认失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('确认失败')
  }
}

// 进入页面时检查管理员权限
onMounted(async () => {
  if (user.role !== 'admin') {
    router.push('/home')
    return
  }
  await loadData()
})
</script>

<style scoped>
.page { min-height: 100vh; background: #f6f3eb; padding: 20px; }
.page-header { margin-bottom: 14px; display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; color: #4d3b2f; }
.actions { display: flex; gap: 8px; }
.thumb { width: 70px; height: 50px; object-fit: cover; border-radius: 6px; border: 1px solid #ece1cf; background: #f8f4ec; }
</style>
