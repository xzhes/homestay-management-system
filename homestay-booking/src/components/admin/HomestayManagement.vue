<template>
  <div class="page">
    <header class="page-header">
      <h2>房源管理</h2>
      <div class="actions">
        <el-button @click="router.push('/admin/dashboard')">返回首页</el-button>
        <el-button type="primary" @click="openCreate">新增房源</el-button>
      </div>
    </header>

    <el-card shadow="never">
      <el-table :data="homestays" v-loading="loading" empty-text="暂无房源">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column label="图片" width="110">
          <template #default="scope">
            <img :src="toImageUrl(scope.row.imageUrl)" class="thumb" alt="房源图片" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="房间名称" min-width="180" />
        <el-table-column prop="price" label="价格(元/晚)" width="140" />
        <el-table-column prop="description" label="描述" min-width="220" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="removeHomestay(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房源' : '新增房源'" width="560px">
      <el-form label-width="90px">
        <el-form-item label="房间名称">
          <el-input v-model="form.name" placeholder="请输入房间名称" />
        </el-form-item>

        <el-form-item label="价格">
          <el-input v-model.number="form.price" type="number" min="0" placeholder="请输入价格，例如 268" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>

        <el-form-item label="房源图片">
          <div class="upload-area">
            <img :src="toImageUrl(form.imageUrl)" class="preview" alt="图片预览" />
            <div class="upload-tools">
              <el-upload :show-file-list="false" :http-request="handleUploadRequest" accept="image/*">
                <el-button :loading="uploading">上传图片</el-button>
              </el-upload>
              <el-input v-model="form.imageUrl" placeholder="或手动粘贴图片链接" />
            </div>
          </div>
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
const defaultImage = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="480" height="320"><rect width="100%" height="100%" fill="%23efe3cf"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23745a3f" font-size="28">民宿图片</text></svg>'

const homestays = ref([])
const loading = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const form = ref({ name: '', price: 0, description: '', imageUrl: '' })

const toImageUrl = (path) => {
  const url = homestayApi.getImageUrl(path)
  return url || defaultImage
}

const loadHomestays = async () => {
  loading.value = true
  try {
    const data = await homestayApi.getAdminHomestays()
    homestays.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    ElMessage.error('加载房源失败')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  isEdit.value = false
  currentId.value = null
  form.value = { name: '', price: 0, description: '', imageUrl: '' }
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  form.value = {
    name: row.name,
    price: Number(row.price || 0),
    description: row.description || '',
    imageUrl: row.imageUrl || ''
  }
  dialogVisible.value = true
}

// 上传图片后，把返回路径写入 form.imageUrl
const handleUploadRequest = async (option) => {
  uploading.value = true
  try {
    const result = await homestayApi.uploadHomestayImage(option.file)
    if (result.code === 200) {
      form.value.imageUrl = result.data
      ElMessage.success('图片上传成功')
      return
    }
    ElMessage.error(result.message || '图片上传失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('图片上传失败')
  } finally {
    uploading.value = false
  }
}

const submitForm = async () => {
  if (!form.value.name) {
    ElMessage.warning('房间名称不能为空')
    return
  }
  if (
    form.value.price === null ||
    form.value.price === '' ||
    Number.isNaN(Number(form.value.price)) ||
    Number(form.value.price) < 0
  ) {
    ElMessage.warning('价格不合法')
    return
  }

  try {
    let result
    if (isEdit.value) result = await homestayApi.updateHomestay(currentId.value, form.value)
    else result = await homestayApi.createHomestay(form.value)

    if (result.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      await loadHomestays()
      return
    }
    ElMessage.error(result.message || '保存失败')
  } catch (err) {
    console.error(err)
    ElMessage.error('保存失败')
  }
}

const removeHomestay = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除房源 ${row.name} 吗？`, '提示', { type: 'warning' })
    const result = await homestayApi.deleteHomestay(row.id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadHomestays()
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
  await loadHomestays()
})
</script>

<style scoped>
.page { min-height: 100vh; background: #f6f3eb; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.page-header h2 { margin: 0; color: #4d3b2f; }
.actions { display: flex; gap: 8px; }
.thumb { width: 70px; height: 50px; object-fit: cover; border-radius: 6px; border: 1px solid #ece1cf; }
.upload-area { display: grid; grid-template-columns: 180px 1fr; gap: 12px; width: 100%; }
.preview { width: 100%; height: 120px; border-radius: 8px; object-fit: cover; border: 1px solid #eadfcf; background: #f8f4ec; }
.upload-tools { display: flex; flex-direction: column; gap: 10px; }
@media (max-width: 700px) { .upload-area { grid-template-columns: 1fr; } }
</style>
