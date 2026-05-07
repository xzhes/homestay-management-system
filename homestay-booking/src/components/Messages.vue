<template>
  <div class="message-page">
    <header class="top-nav">
      <div class="brand">民宿管理系统</div>
      <nav class="nav-links">
        <a href="javascript:void(0)" @click="router.push('/home')">首页</a>
        <a href="javascript:void(0)" @click="router.push('/announcements')">系统公告</a>
        <a href="javascript:void(0)" class="active">用户留言</a>
        <a href="javascript:void(0)" @click="router.push('/booking')">预约入住</a>
      </nav>
      <div class="user-box">
        <span>{{ userName }}</span>
        <el-button size="small" type="danger" @click="logout">退出</el-button>
      </div>
    </header>

    <main class="container">
      <section class="section-title">用户留言</section>

      <el-card shadow="never" class="form-card">
        <el-input
          v-model="content"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="请输入留言内容"
        />
        <div class="submit-row">
          <el-button type="primary" :loading="submitting" @click="submitMessage">提交留言</el-button>
        </div>
      </el-card>

      <section class="section-title">我的留言</section>
      <el-card shadow="never" v-loading="loading">
        <div v-if="messages.length" class="message-list">
          <article v-for="item in messages" :key="item.id" class="message-item">
            <div class="message-head">
              <span>{{ formatTime(item.createdAt) }}</span>
              <el-tag :type="item.status === 'replied' ? 'success' : 'warning'" size="small">
                {{ item.status === 'replied' ? '已回复' : '待处理' }}
              </el-tag>
            </div>
            <p class="message-content">{{ item.content }}</p>
            <div v-if="item.reply" class="reply-box">
              <strong>管理员回复：</strong>
              <span>{{ item.reply }}</span>
            </div>
          </article>
        </div>
        <el-empty v-else description="暂无留言记录" />
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
const content = ref('')
const messages = ref([])
const loading = ref(false)
const submitting = ref(false)

const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}

const loadMessages = async () => {
  loading.value = true
  try {
    const data = await homestayApi.getMessages(userId.value)
    messages.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载留言失败')
  } finally {
    loading.value = false
  }
}

const submitMessage = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('留言内容不能为空')
    return
  }

  submitting.value = true
  try {
    const result = await homestayApi.createMessage({
      userId: userId.value,
      username: userName.value,
      content: content.value.trim()
    })
    if (result.code === 200) {
      ElMessage.success('留言提交成功')
      content.value = ''
      await loadMessages()
      return
    }
    ElMessage.error(result.message || '提交失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
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
  await loadMessages()
})
</script>

<style scoped>
.message-page { min-height: 100vh; background: #ece9d6; }
.top-nav { height: 56px; background: #c9ac82; display: grid; grid-template-columns: 180px 1fr auto; align-items: center; padding: 0 16px; color: #4d3b2d; border-bottom: 1px solid #b99b73; }
.brand { font-weight: 700; }
.nav-links { display: flex; gap: 28px; justify-content: center; }
.nav-links a { color: #5c4630; text-decoration: none; font-weight: 600; }
.nav-links a.active { color: #2b1c10; }
.user-box { display: flex; gap: 10px; align-items: center; }
.container { max-width: 980px; margin: 0 auto; padding: 16px; }
.section-title { margin: 16px 0 12px; background: #d1b084; color: #6d4f31; text-align: center; padding: 8px; border-radius: 6px; font-weight: 700; }
.form-card { margin-bottom: 12px; }
.submit-row { margin-top: 12px; display: flex; justify-content: flex-end; }
.message-list { display: flex; flex-direction: column; gap: 12px; }
.message-item { border: 1px solid #eadfcf; border-radius: 8px; padding: 14px; background: #fffaf2; }
.message-head { display: flex; justify-content: space-between; gap: 12px; align-items: center; color: #9a8067; font-size: 13px; }
.message-content { color: #5d4735; line-height: 1.8; white-space: pre-wrap; }
.reply-box { background: #f0eadc; border-radius: 8px; padding: 10px; color: #5b4634; line-height: 1.7; }
@media (max-width: 760px) {
  .top-nav { grid-template-columns: 1fr; gap: 8px; height: auto; padding: 10px; }
  .nav-links { justify-content: start; gap: 16px; flex-wrap: wrap; }
}
</style>
