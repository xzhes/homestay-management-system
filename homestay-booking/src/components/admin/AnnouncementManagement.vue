<template>
  <div class="page">
    <header class="page-header">
      <h2>公告管理</h2>
      <div class="actions">
        <el-button @click="router.push('/admin/dashboard')">返回首页</el-button>
        <el-button type="primary" @click="openCreate">新增公告</el-button>
      </div>
    </header>

    <el-card shadow="never">
      <el-table :data="announcements" v-loading="loading" empty-text="暂无公告">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'published' ? 'success' : 'info'">
              {{ scope.row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="170">
          <template #default="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="removeAnnouncement(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="620px">
      <el-form label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
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
const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const form = ref({ title: '', content: '', status: 'published' })

const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const data = await homestayApi.getAdminAnnouncements()
    announcements.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  isEdit.value = false
  currentId.value = null
  form.value = { title: '', content: '', status: 'published' }
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  form.value = {
    title: row.title || '',
    content: row.content || '',
    status: row.status || 'published'
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    ElMessage.warning('标题和内容不能为空')
    return
  }

  const payload = {
    title: form.value.title.trim(),
    content: form.value.content.trim(),
    status: form.value.status
  }

  try {
    const result = isEdit.value
      ? await homestayApi.updateAnnouncement(currentId.value, payload)
      : await homestayApi.createAnnouncement(payload)

    if (result.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      await loadAnnouncements()
      return
    }
    ElMessage.error(result.message || '保存失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('保存失败')
  }
}

const removeAnnouncement = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除公告“${row.title}”吗？`, '提示', { type: 'warning' })
    const result = await homestayApi.deleteAnnouncement(row.id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadAnnouncements()
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
  await loadAnnouncements()
})
</script>

<style scoped>
.page { min-height: 100vh; background: #f6f3eb; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.page-header h2 { margin: 0; color: #4d3b2f; }
.actions { display: flex; gap: 8px; }
@media (max-width: 700px) {
  .page-header { align-items: flex-start; flex-direction: column; gap: 10px; }
}
</style>
