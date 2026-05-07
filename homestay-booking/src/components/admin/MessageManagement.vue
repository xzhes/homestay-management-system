<template>
  <div class="page">
    <header class="page-header">
      <h2>留言管理</h2>
      <div class="actions">
        <el-button @click="router.push('/admin/dashboard')">返回首页</el-button>
      </div>
    </header>

    <el-card shadow="never">
      <el-table :data="messages" v-loading="loading" empty-text="暂无留言">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户" width="130" />
        <el-table-column prop="content" label="留言内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'replied' ? 'success' : 'warning'">
              {{ scope.row.status === 'replied' ? '已回复' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="留言时间" width="170">
          <template #default="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170">
          <template #default="scope">
            <el-button link type="primary" @click="openReply(scope.row)">回复</el-button>
            <el-button link type="danger" @click="removeMessage(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="回复留言" width="620px">
      <div class="message-preview">
        <strong>{{ currentMessage.username }}：</strong>
        <p>{{ currentMessage.content }}</p>
      </div>
      <el-input v-model="reply" type="textarea" :rows="5" placeholder="请输入回复内容" />

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">保存回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homestayApi } from '../../api/homestay'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const messages = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentMessage = ref({})
const reply = ref('')

const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}

const loadMessages = async () => {
  loading.value = true
  try {
    const data = await homestayApi.getAdminMessages()
    messages.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载留言失败')
  } finally {
    loading.value = false
  }
}

const openReply = (row) => {
  currentMessage.value = row
  reply.value = row.reply || ''
  dialogVisible.value = true
}

const submitReply = async () => {
  if (!reply.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }
  try {
    const result = await homestayApi.replyMessage(currentMessage.value.id, { reply: reply.value.trim() })
    if (result.code === 200) {
      ElMessage.success('回复成功')
      dialogVisible.value = false
      await loadMessages()
      return
    }
    ElMessage.error(result.message || '回复失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('回复失败')
  }
}

const removeMessage = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除 ${row.username} 的留言吗？`, '提示', { type: 'warning' })
    const result = await homestayApi.deleteMessage(row.id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadMessages()
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
  await loadMessages()
})
</script>

<style scoped>
.page { min-height: 100vh; background: #f6f3eb; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.page-header h2 { margin: 0; color: #4d3b2f; }
.actions { display: flex; gap: 8px; }
.message-preview { background: #f8f2e8; border: 1px solid #eadfcf; border-radius: 8px; padding: 12px; margin-bottom: 12px; color: #5c4634; }
.message-preview p { margin: 8px 0 0; line-height: 1.7; white-space: pre-wrap; }
</style>
