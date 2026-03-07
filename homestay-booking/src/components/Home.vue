<template>
  <div class="home-page">
    <header class="top-nav">
      <div class="brand">民宿管理系统</div>
      <nav class="nav-links">
        <a href="javascript:void(0)">首页</a>
        <a href="javascript:void(0)">系统公告</a>
        <a href="javascript:void(0)">用户留言</a>
        <a href="javascript:void(0)">预约入住</a>
      </nav>
      <div class="user-box">
        <span>{{ userName }}</span>
        <el-button size="small" type="danger" @click="logout">退出</el-button>
      </div>
    </header>

    <main class="container">
      <section class="carousel-wrap">
        <el-carousel height="320px" indicator-position="outside">
          <el-carousel-item v-for="item in carouselImages" :key="item">
            <img :src="toImageUrl(item)" class="carousel-image" alt="轮播图" />
          </el-carousel-item>
        </el-carousel>
      </section>

      <section class="section-title">客房展示</section>
      <section class="room-grid">
        <article v-for="room in homestays" :key="room.id" class="room-card">
          <img :src="toImageUrl(room.imageUrl)" class="room-image" :alt="room.name" />
          <div class="room-body">
            <h3>{{ room.name }}</h3>
            <p class="room-desc">{{ room.description || '暂无描述' }}</p>
            <div class="room-foot">
              <span class="price">￥{{ room.price }}</span>
              <el-date-picker
                v-model="dateMap[room.id]"
                type="date"
                size="small"
                placeholder="入住日期"
                value-format="YYYY-MM-DD"
              />
              <el-button size="small" type="primary" @click="reserve(room.id)">预约</el-button>
            </div>
          </div>
        </article>
      </section>

      <section class="section-title">民宿简介</section>
      <section class="intro-wrap">
        <div class="intro-text">
          欢迎来到我们的民宿管理平台。这里提供安静舒适的居住环境，覆盖商务出行、家庭出游、短住长住等多种需求。
          你可以在页面中直接查看房源图片、选择入住日期并提交预约。后台会统一处理预约记录，帮助你和房东高效沟通。
        </div>
        <img :src="toImageUrl(introImage)" class="intro-image" alt="民宿简介图片" />
      </section>

      <section class="section-title">我的预约</section>
      <section class="my-order">
        <el-table :data="myReservations" v-loading="loadingReservations" empty-text="暂无预约">
          <el-table-column prop="id" label="预约ID" width="90" />
          <el-table-column prop="roomName" label="房间名称" min-width="180" />
          <el-table-column prop="date" label="入住日期" width="140" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" link @click="cancelReservation(scope.row.id)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>
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

const defaultImage = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="700"><rect width="100%" height="100%" fill="%23e8dfcf"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23624d39" font-size="42">民宿图片</text></svg>'

const userName = ref(user.username || '游客')
const userId = ref(user.id)
const homestays = ref([])
const myReservations = ref([])
const dateMap = ref({})
const loadingReservations = ref(false)

const toImageUrl = (path) => {
  const url = homestayApi.getImageUrl(path)
  return url || defaultImage
}

const carouselImages = computed(() => {
  const list = homestays.value.map(item => item.imageUrl).filter(Boolean)
  return list.length ? list.slice(0, 5) : [defaultImage]
})

const introImage = computed(() => {
  if (!homestays.value.length) return defaultImage
  return homestays.value[0].imageUrl || defaultImage
})

const roomNameMap = computed(() => {
  const map = {}
  homestays.value.forEach(room => {
    map[room.id] = room.name
  })
  return map
})

const loadHomestays = async () => {
  try {
    const data = await homestayApi.getHomestays()
    homestays.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('获取房源失败')
  }
}

const loadReservations = async () => {
  if (!userId.value) return

  loadingReservations.value = true
  try {
    const data = await homestayApi.getReservations(userId.value)
    const rows = Array.isArray(data) ? data : []
    myReservations.value = rows.map(item => ({
      ...item,
      roomName: roomNameMap.value[item.roomId] || `房源#${item.roomId}`
    }))
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
    await ElMessageBox.confirm('确认取消该预约吗？', '提示', { type: 'warning' })
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

  await loadHomestays()
  await loadReservations()
})
</script>

<style scoped>
.home-page { min-height: 100vh; background: #ece9d6; }
.top-nav { height: 56px; background: #c9ac82; display: grid; grid-template-columns: 180px 1fr auto; align-items: center; padding: 0 16px; color: #4d3b2d; border-bottom: 1px solid #b99b73; }
.brand { font-weight: 700; }
.nav-links { display: flex; gap: 28px; justify-content: center; }
.nav-links a { color: #5c4630; text-decoration: none; font-weight: 600; }
.user-box { display: flex; gap: 10px; align-items: center; }
.container { max-width: 1220px; margin: 0 auto; padding: 16px; }
.carousel-wrap { background: #f4f1e4; padding: 14px; border-radius: 8px; }
.carousel-image { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; }
.section-title { margin: 16px 0 12px; background: #d1b084; color: #6d4f31; text-align: center; padding: 8px; border-radius: 6px; font-weight: 700; }
.room-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; }
.room-card { background: #ffffff; border-radius: 8px; border: 1px solid #eadcc5; overflow: hidden; }
.room-image { width: 100%; height: 170px; object-fit: cover; }
.room-body { padding: 10px; }
.room-body h3 { margin: 0; color: #5a4330; font-size: 17px; }
.room-desc { margin: 8px 0 10px; color: #7d6653; min-height: 38px; }
.room-foot { display: grid; grid-template-columns: auto 1fr auto; gap: 8px; align-items: center; }
.price { color: #bf7f2f; font-weight: 700; }
.intro-wrap { display: grid; grid-template-columns: 1fr 420px; gap: 12px; background: #f3efde; border-radius: 8px; padding: 12px; }
.intro-text { line-height: 1.8; color: #5c4634; background: #fff; border-radius: 8px; padding: 14px; }
.intro-image { width: 100%; height: 280px; border-radius: 8px; object-fit: cover; }
.my-order { background: #fff; border-radius: 8px; border: 1px solid #eadcc5; padding: 10px; }
@media (max-width: 1100px) { .room-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } .intro-wrap { grid-template-columns: 1fr; } }
@media (max-width: 760px) { .top-nav { grid-template-columns: 1fr; gap: 8px; height: auto; padding: 10px; } .nav-links { justify-content: start; gap: 16px; flex-wrap: wrap; } .room-grid { grid-template-columns: 1fr; } }
</style>
