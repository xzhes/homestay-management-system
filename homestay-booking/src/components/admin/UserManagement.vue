<template>
  <div class="page">
    <header class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="openCreateDialog">新增用户</el-button>
        <el-button @click="router.push('/admin/dashboard')">返回首页</el-button>
      </div>
    </header>

    <el-card shadow="never">
      <el-table :data="users" v-loading="loading" empty-text="暂无用户数据">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="160" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="removeUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
      <el-form label-width="90px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="guest" />
          </el-select>
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
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homestayApi } from '../../api/homestay'

const router = useRouter()
const loading = ref(false)
const users = ref([])
const dialogVisible = ref(false)
const mode = ref('create')
const editingId = ref(null)

const form = ref({
  username: '',
  password: '',
  role: 'guest'
})

const dialogTitle = computed(() => (mode.value === 'create' ? '新增用户' : '编辑用户'))

const resetForm = () => {
  form.value = {
    username: '',
    password: '',
    role: 'guest'
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await homestayApi.getUsers()
    users.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载用户失败')
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  mode.value = 'create'
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  mode.value = 'edit'
  editingId.value = row.id
  form.value = {
    username: row.username,
    password: '',
    role: row.role || 'guest'
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }

  try {
    let result
    if (mode.value === 'create') {
      result = await homestayApi.createUser(form.value)
    } else {
      result = await homestayApi.updateUser(editingId.value, form.value)
    }

    if (result.code === 200) {
      ElMessage.success(mode.value === 'create' ? '新增成功' : '更新成功')
      dialogVisible.value = false
      await loadUsers()
      return
    }
    ElMessage.error(result.message || '保存失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('保存失败')
  }
}

const removeUser = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？', '提示', { type: 'warning' })
    const result = await homestayApi.deleteUser(id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadUsers()
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
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (user.role !== 'admin') {
    router.push('/home')
    return
  }
  await loadUsers()
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

.header-actions {
  display: flex;
  gap: 10px;
}

.page-header h2 {
  margin: 0;
  color: #4d3b2f;
}
</style>
