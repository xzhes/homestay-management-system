<template>
  <div class="home-page">
    <div class="header">
      <h1>🏠 民宿预订平台</h1>
      <div class="user-info">
        <span>欢迎，{{ userName }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </div>
    </div>

    <div class="content">
      <el-card>
        <h2>客人首页</h2>
        <p>这里将显示可预订的民宿列表</p>
        <p>您的角色：客人 (guest)</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userName = ref('')

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  userName.value = user.username || '游客'
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.header h1 {
  margin: 0;
  color: #409EFF;
}

.user-info {
  display: flex;
  gap: 15px;
  align-items: center;
}

.content {
  padding: 40px;
}
</style>