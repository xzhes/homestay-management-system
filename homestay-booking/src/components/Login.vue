<template>
  <div class="login-page">
    <div class="login-box">
      <h1>民宿预约管理系统</h1>

      <div class="form-group">
        <input
          v-model="username"
          placeholder="用户名"
          @keyup.enter="handleLogin"
        />
      </div>

      <div class="form-group">
        <input
          v-model="password"
          type="password"
          placeholder="密码"
          @keyup.enter="handleLogin"
        />
      </div>

      <button @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <p class="error-msg" v-if="errorMsg">{{ errorMsg }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { homestayApi } from '../api/homestay'

const router = useRouter()

const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await homestayApi.login(username.value, password.value)

    if (result.code === 200) {
      localStorage.setItem('user', JSON.stringify(result.data))
      if (result.data.role === 'admin') {
        router.push('/admin/dashboard')
      } else {
        router.push('/home')
      }
      return
    }

    errorMsg.value = result.message || '用户名或密码错误'
  } catch (err) {
    errorMsg.value = '网络错误，请稍后重试'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #355c7d 0%, #6c5b7b 55%, #c06c84 100%);
}

.login-box {
  width: min(420px, 92vw);
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-box h1 {
  text-align: center;
  color: #273043;
  margin-bottom: 30px;
  font-size: 24px;
}

.form-group {
  margin-bottom: 20px;
}

input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #355c7d;
}

button {
  width: 100%;
  padding: 12px;
  background: #355c7d;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
}

button:hover:not(:disabled) {
  background: #2a4a65;
}

button:disabled {
  background: #b7b7b7;
  cursor: not-allowed;
}

.error-msg {
  color: #d7263d;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
</style>
