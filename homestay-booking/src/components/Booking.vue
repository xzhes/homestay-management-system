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
        <el-form class="reserve-form" label-width="100px">
          <el-row :gutter="16">
            <el-col :xs="24" :md="12">
              <el-form-item label="房间类型">
                <el-select v-model="form.roomType" placeholder="请选择房间类型" style="width:100%" @change="handleRoomTypeChange">
                  <el-option v-for="room in homestays" :key="room.id" :label="room.name" :value="room.name" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="12">
              <el-form-item label="入住-离店">
                <el-date-picker
                  v-model="form.dateRange"
                  type="daterange"
                  value-format="YYYY-MM-DD"
                  start-placeholder="入住日期"
                  end-placeholder="离店日期"
                  style="width:100%"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="12"><el-form-item label="住户姓名"><el-input v-model="form.guestName" placeholder="请输入住户姓名" /></el-form-item></el-col>
            <el-col :xs="24" :md="12"><el-form-item label="电话"><el-input v-model="form.phone" placeholder="请输入电话" /></el-form-item></el-col>
            <el-col :xs="24" :md="12"><el-form-item label="身份证"><el-input v-model="form.idCard" placeholder="请输入身份证号" /></el-form-item></el-col>
            <el-col :xs="24" :md="12"><el-form-item label="性别"><el-select v-model="form.gender" placeholder="请选择" style="width:100%"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
            <el-col :xs="24" :md="12"><el-form-item label="入住天数"><el-input :model-value="computedStayDaysText" disabled /></el-form-item></el-col>
            <el-col :xs="24" :md="12"><el-form-item label="实付金额"><el-input :model-value="computedPaidAmountText" disabled /></el-form-item></el-col>
            <el-col :xs="24"><el-form-item label="备注"><el-input v-model="form.note" type="textarea" :rows="2" placeholder="请输入备注" /></el-form-item></el-col>
          </el-row>
          <el-form-item><el-button type="primary" @click="submitReservation">提交预约</el-button></el-form-item>
        </el-form>
      </el-card>

      <section class="section-title">我的预约</section>
      <el-card shadow="never">
        <el-table :data="reservations" v-loading="loading" empty-text="暂无预约记录">
          <el-table-column prop="id" label="预约ID" width="90" />
          <el-table-column prop="roomType" label="房间类型" min-width="140" />
          <el-table-column prop="guestName" label="住户姓名" width="110" />
          <el-table-column prop="date" label="入住时间" width="120" />
          <el-table-column prop="checkOutDate" label="离店时间" width="120" />
          <el-table-column prop="stayDays" label="天数" width="80" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="paidAmountText" label="实付金额" width="110" />
          <el-table-column prop="note" label="备注" min-width="120" />
          <el-table-column label="操作" width="190">
            <template #default="scope">
              <el-button v-if="scope.row.status === '待入住'" type="primary" link @click="checkIn(scope.row.id)">入住</el-button>
              <el-button v-if="scope.row.status === '已入住'" type="warning" link @click="checkOut(scope.row.id)">退房</el-button>
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

// 路由与当前登录用户
const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const userName = ref(user.username || '游客')
const userId = ref(user.id)
const homestays = ref([])
const reservationRaw = ref([])
const loading = ref(false)

// 表单默认值
const createDefaultForm = () => ({
  roomId: '',
  roomType: '',
  dateRange: [],
  guestName: '',
  phone: '',
  idCard: '',
  gender: '',
  note: '',
})
const form = ref(createDefaultForm())

// 房型名称 -> 房源对象映射
const roomByName = computed(() => {
  const map = {}
  homestays.value.forEach((item) => { map[item.name] = item })
  return map
})

// 根据日期区间计算入住天数
const computedStayDays = computed(() => {
  if (!Array.isArray(form.value.dateRange) || form.value.dateRange.length !== 2) return 0
  const [start, end] = form.value.dateRange
  if (!start || !end) return 0
  const startTime = new Date(start).getTime()
  const endTime = new Date(end).getTime()
  const days = Math.floor((endTime - startTime) / (24 * 3600 * 1000))
  return days > 0 ? days : 0
})

// 入住天数显示文案
const computedStayDaysText = computed(() => (computedStayDays.value > 0 ? `${computedStayDays.value} 晚` : '请选择入住和离店日期'))
// 根据房价 * 天数计算实付金额（前端显示）
const computedPaidAmount = computed(() => {
  const room = roomByName.value[form.value.roomType]
  const price = Number(room?.price || 0)
  if (!computedStayDays.value || !price) return 0
  return Number((computedStayDays.value * price).toFixed(2))
})
const computedPaidAmountText = computed(() => (computedPaidAmount.value > 0 ? `¥${computedPaidAmount.value}` : '请选择房型和日期'))

// 组装列表展示字段
const reservations = computed(() => reservationRaw.value.map((item) => ({
  ...item,
  paidAmountText: item.paidAmount == null ? '' : `¥${item.paidAmount}`
})))

// 加载房源列表
const loadHomestays = async () => {
  const data = await homestayApi.getHomestays()
  homestays.value = Array.isArray(data) ? data : []
}

// 加载当前用户的预约
const loadReservations = async () => {
  const data = await homestayApi.getReservations(userId.value)
  reservationRaw.value = Array.isArray(data) ? data : []
}

// 选中房型后写入房源 ID
const handleRoomTypeChange = (roomType) => {
  const room = roomByName.value[roomType]
  form.value.roomId = room ? room.id : ''
}

// 提交预约
const submitReservation = async () => {
  const range = Array.isArray(form.value.dateRange) ? form.value.dateRange : []
  const payload = {
    userId: userId.value,
    roomId: form.value.roomId,
    roomType: form.value.roomType,
    date: range[0],
    checkOutDate: range[1],
    stayDays: computedStayDays.value,
    guestName: form.value.guestName?.trim(),
    phone: form.value.phone?.trim(),
    idCard: form.value.idCard?.trim(),
    gender: form.value.gender,
    note: form.value.note?.trim()
  }

  // 基础校验
  if (!payload.roomType || !payload.roomId || !payload.date || !payload.checkOutDate || !payload.guestName || !payload.phone || !payload.idCard || !payload.gender) {
    ElMessage.warning('请完整填写必填信息')
    return
  }
  if (!payload.stayDays || payload.stayDays <= 0) {
    ElMessage.warning('离店日期必须晚于入住日期')
    return
  }
  try {
    const result = await homestayApi.submitReservation(payload)
    if (result.code === 200) {
      ElMessage.success('预约提交成功')
      form.value = createDefaultForm()
      await loadReservations()
      return
    }
    ElMessage.error(result.message || '预约失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('预约失败')
  }
}

// 用户入住
const checkIn = async (id) => {
  try {
    const result = await homestayApi.checkInReservation(id, userId.value)
    if (result.code === 200) {
      ElMessage.success('入住成功')
      await loadReservations()
      return
    }
    ElMessage.error(result.message || '入住失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('入住失败')
  }
}

// 用户退房
const checkOut = async (id) => {
  try {
    const result = await homestayApi.checkOutReservation(id, userId.value)
    if (result.code === 200) {
      ElMessage.success('退房成功')
      await loadReservations()
      return
    }
    ElMessage.error(result.message || '退房失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('退房失败')
  }
}

// 取消预约
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

// 退出登录
const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

// 页面加载：校验登录并拉取数据
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
.reserve-form { display: block; }
@media (max-width: 760px) {
  .top-nav { grid-template-columns: 1fr; gap: 8px; height: auto; padding: 10px; }
  .nav-links { justify-content: start; gap: 16px; flex-wrap: wrap; }
}
</style>
