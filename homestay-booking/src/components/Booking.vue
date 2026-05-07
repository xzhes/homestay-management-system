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

      <section class="search-panel">
        <div class="search-head">预约筛选</div>
        <div class="search-body">
          <el-row :gutter="12">
            <el-col :xs="24" :sm="24" :md="10" :lg="9">
              <el-form-item label="入住日期" label-width="72px" class="inline-form-item">
                <el-date-picker
                  v-model="filterBar.dateRange"
                  type="daterange"
                  value-format="YYYY-MM-DD"
                  start-placeholder="入住日期"
                  end-placeholder="离店日期"
                  :disabled-date="disablePastDate"
                  style="width:100%"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="12" :md="5" :lg="5">
              <el-form-item label="房型" label-width="52px" class="inline-form-item">
                <el-select v-model="filterBar.roomType" clearable placeholder="全部房型" style="width:100%">
                  <el-option v-for="room in homestays" :key="room.id" :label="room.name" :value="room.name" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="12" :md="5" :lg="5">
              <el-form-item label="状态" label-width="52px" class="inline-form-item">
                <el-select v-model="filterBar.status" placeholder="全部状态" style="width:100%">
                  <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="4" :lg="5" class="search-actions">
              <el-button type="primary" @click="applySearch">查询</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-col>
          </el-row>
        </div>
      </section>

      <section class="room-grid-wrap">
        <div class="room-grid" v-if="filteredRoomCards.length">
          <article class="room-card" v-for="room in filteredRoomCards" :key="room.id">
            <img :src="toImageUrl(room.imageUrl)" :alt="room.name" class="room-image" />
            <div class="room-content">
              <h3>{{ room.name }}</h3>
              <p class="room-desc">{{ room.description || '舒适房型，适合入住休息' }}</p>
              <div class="room-meta">
                <span class="room-tag" :class="room.statusClass">{{ room.statusText }}</span>
                <span class="room-price">¥ {{ room.price || 0 }}</span>
              </div>
              <el-button
                size="small"
                type="primary"
                class="book-btn"
                :disabled="room.checked && !room.available"
                @click="pickRoom(room)"
              >
                {{ room.checked && !room.available ? '该日期不可订' : '预订此房型' }}
              </el-button>
            </div>
          </article>
        </div>
        <el-card shadow="never" v-else>
          <el-empty description="当前筛选条件下无房源" />
        </el-card>
      </section>

      <section class="section-title">填写预约信息</section>
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
                  :disabled-date="disablePastDate"
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

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const userName = ref(user.username || '游客')
const userId = ref(user.id)
const homestays = ref([])
const reservationRaw = ref([])
const allReservationRaw = ref([])
const loading = ref(false)

const defaultImage = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="900" height="540"><rect width="100%" height="100%" fill="%23efe4cf"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23705842" font-size="36">民宿图片</text></svg>'
const activeStatuses = new Set(['待确认', '待入住', '已入住', '已预订', 'BOOKED', 'CHECKED_IN'])
const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '可预订', value: 'available' },
  { label: '已占用', value: 'occupied' }
]

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
const filterBar = ref({
  dateRange: [],
  roomType: '',
  status: 'all'
})

const roomByName = computed(() => {
  const map = {}
  homestays.value.forEach((item) => { map[item.name] = item })
  return map
})

const parseLocalDate = (value) => {
  if (!value) return null
  const parts = String(value).split('-').map(Number)
  if (parts.length !== 3 || parts.some(Number.isNaN)) return null
  const dt = new Date(parts[0], parts[1] - 1, parts[2])
  if (Number.isNaN(dt.getTime())) return null
  return dt
}

const getTodayStart = () => {
  const now = new Date()
  return new Date(now.getFullYear(), now.getMonth(), now.getDate())
}

const disablePastDate = (date) => {
  return date.getTime() < getTodayStart().getTime()
}

const toImageUrl = (path) => {
  const url = homestayApi.getImageUrl(path)
  return url || defaultImage
}

const selectedSearchRange = computed(() => {
  const range = Array.isArray(filterBar.value.dateRange) ? filterBar.value.dateRange : []
  if (range.length !== 2 || !range[0] || !range[1]) return null
  const checkIn = parseLocalDate(range[0])
  const checkOut = parseLocalDate(range[1])
  if (!checkIn || !checkOut || checkIn >= checkOut) return null
  return { checkIn, checkOut }
})

const isRoomOccupiedInRange = (roomId, range) => {
  if (!range) return false
  return allReservationRaw.value.some((reservation) => {
    if (!reservation || Number(reservation.roomId) !== Number(roomId)) return false
    if (!activeStatuses.has(reservation.status)) return false
    const resCheckIn = parseLocalDate(reservation.date)
    const resCheckOut = parseLocalDate(reservation.checkOutDate)
    if (!resCheckIn || !resCheckOut || resCheckIn >= resCheckOut) return false
    return resCheckIn < range.checkOut && range.checkIn < resCheckOut
  })
}

const roomCards = computed(() => homestays.value.map((room) => {
  const range = selectedSearchRange.value
  const checked = !!range
  const occupied = checked ? isRoomOccupiedInRange(room.id, range) : false
  const available = !occupied
  let statusText = '待选择日期'
  let statusClass = 'tag-pending'
  if (checked && available) {
    statusText = '可预订'
    statusClass = 'tag-available'
  } else if (checked && !available) {
    statusText = '已占用'
    statusClass = 'tag-occupied'
  }
  return {
    ...room,
    checked,
    available,
    statusText,
    statusClass
  }
}))

const filteredRoomCards = computed(() => {
  let list = roomCards.value
  if (filterBar.value.roomType) {
    list = list.filter((room) => room.name === filterBar.value.roomType)
  }
  if (filterBar.value.status === 'available') {
    list = list.filter((room) => !room.checked || room.available)
  } else if (filterBar.value.status === 'occupied') {
    list = list.filter((room) => room.checked && !room.available)
  }
  return list
})

const computedStayDays = computed(() => {
  if (!Array.isArray(form.value.dateRange) || form.value.dateRange.length !== 2) return 0
  const [start, end] = form.value.dateRange
  if (!start || !end) return 0
  const startTime = new Date(start).getTime()
  const endTime = new Date(end).getTime()
  const days = Math.floor((endTime - startTime) / (24 * 3600 * 1000))
  return days > 0 ? days : 0
})

const computedStayDaysText = computed(() => (computedStayDays.value > 0 ? `${computedStayDays.value} 晚` : '请选择入住和离店日期'))
const computedPaidAmount = computed(() => {
  const room = roomByName.value[form.value.roomType]
  const price = Number(room?.price || 0)
  if (!computedStayDays.value || !price) return 0
  return Number((computedStayDays.value * price).toFixed(2))
})
const computedPaidAmountText = computed(() => (computedPaidAmount.value > 0 ? `¥${computedPaidAmount.value}` : '请选择房型和日期'))

const reservations = computed(() => reservationRaw.value.map((item) => ({
  ...item,
  paidAmountText: item.paidAmount == null ? '' : `¥${item.paidAmount}`
})))

const applySearch = () => {
  if (filterBar.value.dateRange?.length === 2) {
    ElMessage.success('已按条件筛选房源')
    return
  }
  ElMessage.info('已筛选房源，建议选择日期查看可预订状态')
}

const resetSearch = () => {
  filterBar.value = {
    dateRange: [],
    roomType: '',
    status: 'all'
  }
}

const pickRoom = (room) => {
  if (room.checked && !room.available) {
    ElMessage.warning('该房型在所选日期已占用')
    return
  }
  form.value.roomType = room.name
  form.value.roomId = room.id
  if (filterBar.value.dateRange?.length === 2) {
    form.value.dateRange = [...filterBar.value.dateRange]
  }
  ElMessage.success(`已选择 ${room.name}`)
}

const loadHomestays = async () => {
  const data = await homestayApi.getHomestays()
  homestays.value = Array.isArray(data) ? data : []
}

const loadReservations = async () => {
  const [mine, all] = await Promise.all([
    homestayApi.getReservations(userId.value),
    homestayApi.getReservations()
  ])
  reservationRaw.value = Array.isArray(mine) ? mine : []
  allReservationRaw.value = Array.isArray(all) ? all : []
}

const handleRoomTypeChange = (roomType) => {
  const room = roomByName.value[roomType]
  form.value.roomId = room ? room.id : ''
}

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

  if (!payload.roomType || !payload.roomId || !payload.date || !payload.checkOutDate || !payload.guestName || !payload.phone || !payload.idCard || !payload.gender) {
    ElMessage.warning('请完整填写必填信息')
    return
  }
  if (!payload.stayDays || payload.stayDays <= 0) {
    ElMessage.warning('离店日期必须晚于入住日期')
    return
  }
  const checkInDate = parseLocalDate(payload.date)
  if (!checkInDate || checkInDate < getTodayStart()) {
    ElMessage.warning('入住日期不能早于当前日期')
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
.search-panel {
  background: #f4ead5;
  border-radius: 10px;
  border: 1px solid #e2d4bc;
  padding: 10px 12px 12px;
}
.search-head {
  font-weight: 700;
  color: #6f4f2f;
  margin-bottom: 10px;
}
.search-body {
  background: #efe5d3;
  border-radius: 8px;
  padding: 10px;
}
.inline-form-item {
  margin-bottom: 0;
}
.search-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-start;
  margin-top: 2px;
}
.room-grid-wrap {
  margin-top: 14px;
}
.room-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}
.room-card {
  background: #fff;
  border: 1px solid #eadfcf;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 6px 16px rgba(73, 53, 36, 0.08);
}
.room-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
}
.room-content {
  padding: 10px;
}
.room-content h3 {
  margin: 0;
  font-size: 18px;
  color: #5d4129;
}
.room-desc {
  margin: 8px 0 10px;
  color: #81644f;
  min-height: 38px;
}
.room-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.room-tag {
  font-size: 12px;
  border-radius: 6px;
  padding: 3px 8px;
  border: 1px solid transparent;
}
.tag-available {
  color: #4e9c50;
  background: #e8f6e8;
  border-color: #cbe9cc;
}
.tag-occupied {
  color: #c05a58;
  background: #fdeeee;
  border-color: #f1c8c8;
}
.tag-pending {
  color: #8a7a66;
  background: #f3eee7;
  border-color: #e2d7c9;
}
.room-price {
  color: #c29359;
  font-size: 28px;
  font-weight: 700;
}
.book-btn {
  width: 100%;
}
.reserve-form { display: block; }
@media (max-width: 760px) {
  .top-nav { grid-template-columns: 1fr; gap: 8px; height: auto; padding: 10px; }
  .nav-links { justify-content: start; gap: 16px; flex-wrap: wrap; }
  .room-grid { grid-template-columns: 1fr; }
}
@media (min-width: 761px) and (max-width: 1100px) {
  .room-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
