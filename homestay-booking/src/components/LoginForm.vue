<template>
  <div class="login-box">
    <h1>🏠 民宿系统登录</h1>

    <div class="form-group">
      <el-input
          v-model="username"
          placeholder="用户名"
          size="large"
          @keyup.enter="handleLogin"
      />
    </div>

    <div class="form-group">
      <el-input
          v-model="password"
          type="password"
          placeholder="密码"
          size="large"
          show-password
          @keyup.enter="handleLogin"
      />
    </div>

    <el-button
        type="primary"
        size="large"
        :loading="loading"
        @click="handleLogin"
        style="width: 100%"
    >
      {{ loading ? '登录中...' : '登录' }}
    </el-button>

    <p class="error-msg" v-if="errorMsg">{{ errorMsg }}</p>
  </div>
  <el-button type="primary">这是Element Plus按钮</el-button>
</template>

<script setup>
import { ref } from 'vue'
import { homestayApi } from '../api/homestay'
import { ElMessage } from 'element-plus'






// 定义 emit（用于向父组件传递事件）
const emit = defineEmits(['login-success'])

// 响应式数据
const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

// 登录函数
const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await homestayApi.login(username.value, password.value)

    if (result === 'success') {
      ElMessage.success('登录成功！')
      emit('login-success', username.value)
    } else {
      errorMsg.value = '用户名或密码错误'
      ElMessage.error('用户名或密码错误')
    }
  } catch (err) {
    errorMsg.value = '网络错误，请稍后重试'
    ElMessage.error('网络错误，请稍后重试')
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-box {
  max-width: 400px;
  margin: 100px auto;
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-box h1 {
  text-align: center;
  color: #409EFF;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.error-msg {
  color: #f56c6c;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
</style>