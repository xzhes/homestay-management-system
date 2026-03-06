<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <h2>🏠 民宿管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-icon><Avatar /></el-icon>
            {{ userName }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <el-menu
            default-active="dashboard"
            class="menu"
            @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataLine /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="properties">
            <el-icon><House /></el-icon>
            <span>房源管理</span>
          </el-menu-item>
          <el-menu-item index="bookings">
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="calendar">
            <el-icon><Calendar /></el-icon>
            <span>日历管理</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区 -->
      <el-main class="content">
        <!-- 欢迎信息 -->
        <div class="welcome-card">
          <h3>👋 欢迎回来，{{ userName }}！</h3>
          <p>今天是 {{ currentDate }}，祝你工作愉快</p>
        </div>

        <!-- 数据统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: #409EFF;">
                <el-icon :size="30"><House /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalProperties }}</div>
                <div class="stat-label">房源总数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: #67C23A;">
                <el-icon :size="30"><Tickets /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalBookings }}</div>
                <div class="stat-label">总订单数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: #E6A23C;">
                <el-icon :size="30"><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingBookings }}</div>
                <div class="stat-label">待确认订单</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: #F56C6C;">
                <el-icon :size="30"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalUsers }}</div>
                <div class="stat-label">注册用户</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 快捷操作 -->
        <el-card class="section-card" shadow="never">
          <template #header>
            <h4>⚡ 快捷操作</h4>
          </template>
          <el-space wrap :size="15">
            <el-button type="primary" @click="goToProperties">
              <el-icon><Plus /></el-icon>
              添加房源
            </el-button>
            <el-button type="success" @click="goToBookings">
              <el-icon><View /></el-icon>
              查看订单
            </el-button>
            <el-button type="warning" @click="goToCalendar">
              <el-icon><Calendar /></el-icon>
              日历管理
            </el-button>
          </el-space>
        </el-card>

        <!-- 最新订单 -->
        <el-card class="section-card" shadow="never">
          <template #header>
            <h4>📋 最新订单</h4>
          </template>
          <el-table :data="recentBookings" style="width: 100%">
            <el-table-column prop="id" label="订单号" width="100" />
            <el-table-column prop="propertyName" label="房源名称" />
            <el-table-column prop="guestName" label="客人姓名" width="120" />
            <el-table-column prop="checkIn" label="入住日期" width="120" />
            <el-table-column prop="checkOut" label="退房日期" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === '待确认'" type="warning">待确认</el-tag>
                <el-tag v-else-if="scope.row.status === '已确认'" type="success">已确认</el-tag>
                <el-tag v-else-if="scope.row.status === '已完成'" type="info">已完成</el-tag>
                <el-tag v-else type="danger">已取消</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button size="small" type="primary" link @click="viewBooking(scope.row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 阻止未定义组件警告
const Avatar = ref(null)
const ArrowDown = ref(null)
const DataLine = ref(null)
const House = ref(null)
const Tickets = ref(null)
const Calendar = ref(null)
const User = ref(null)
const Clock = ref(null)
const Plus = ref(null)
const View = ref(null)
const userName = ref('管理员')
const currentDate = ref('')

// 统计数据
const stats = ref({
  totalProperties: 12,
  totalBookings: 48,
  pendingBookings: 5,
  totalUsers: 126
})

// 最新订单（模拟数据）
const recentBookings = ref([
  {
    id: '20240306001',
    propertyName: '温馨小屋',
    guestName: '张三',
    checkIn: '2024-03-10',
    checkOut: '2024-03-12',
    status: '待确认'
  },
  {
    id: '20240306002',
    propertyName: '海景房',
    guestName: '李四',
    checkIn: '2024-03-15',
    checkOut: '2024-03-18',
    status: '已确认'
  },
  {
    id: '20240305001',
    propertyName: '山景别墅',
    guestName: '王五',
    checkIn: '2024-03-08',
    checkOut: '2024-03-11',
    status: '已完成'
  }
])

// 获取当前日期
const getCurrentDate = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[today.getDay()]
  currentDate.value = `${year}年${month}月${day}日 ${weekDay}`
}

// 获取用户信息
const getUserInfo = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (user.name) {
    userName.value = user.name
  } else if (user.username) {
    userName.value = user.username
  }
}

// 菜单选择
const handleMenuSelect = (index) => {
  if (index === 'dashboard') {
    // 已经在dashboard
  } else if (index === 'properties') {
    goToProperties()
  } else if (index === 'bookings') {
    goToBookings()
  } else if (index === 'calendar') {
    goToCalendar()
  } else if (index === 'users') {
    ElMessage.info('用户管理功能开发中...')
  }
}

// 跳转到房源管理
const goToProperties = () => {
  ElMessage.info('房源管理功能即将开发...')
  // router.push('/admin/properties')
}

// 跳转到订单管理
const goToBookings = () => {
  ElMessage.info('订单管理功能即将开发...')
  // router.push('/admin/bookings')
}

// 跳转到日历管理
const goToCalendar = () => {
  ElMessage.info('日历管理功能即将开发...')
  // router.push('/admin/calendar')
}

// 查看订单详情
const viewBooking = (booking) => {
  ElMessage.info(`查看订单 ${booking.id} 详情`)
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  getCurrentDate()
  getUserInfo()
})
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  height: 60px;
}

.header-left h2 {
  margin: 0;
  color: #409EFF;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f0f2f5;
}

.main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: #fff;
  box-shadow: 2px 0 8px rgba(0,0,0,0.08);
}

.menu {
  border-right: none;
  height: 100%;
}

.content {
  overflow-y: auto;
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.welcome-card h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-card p {
  margin: 0;
  opacity: 0.9;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.section-card {
  margin-bottom: 20px;
}

.section-card h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}
</style>