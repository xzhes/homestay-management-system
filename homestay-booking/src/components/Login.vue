<template>
  <div class="login-page">
    <div class="login-shell">
      <aside class="login-aside">
        <div class="brand">H</div>
        <h1>民宿预约管理系统</h1>
        <p>一站式管理房源、预约和入住状态</p>
        <div class="aside-note">温暖与秩序并存的住宿体验</div>
      </aside>

      <div class="login-card">
        <div class="tabs">
          <button :class="{ active: mode === 'login' }" @click="switchMode('login')">登录</button>
          <button :class="{ active: mode === 'register' }" @click="switchMode('register')">注册</button>
        </div>

        <h2>{{ mode === 'login' ? '欢迎回来' : '创建新账号' }}</h2>
        <p class="subtitle">{{ mode === 'login' ? '请输入账号密码登录系统' : '注册后即可预约房间' }}</p>

        <div class="form-group">
          <label>用户名</label>
          <input v-model="username" placeholder="请输入用户名" @keyup.enter="mode === 'login' ? handleLogin() : handleRegister()" />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" @keyup.enter="mode === 'login' ? handleLogin() : handleRegister()" />
        </div>

        <div class="form-group" v-if="mode === 'register'">
          <label>确认密码</label>
          <input v-model="confirmPassword" type="password" placeholder="再次输入密码" @keyup.enter="handleRegister" />
        </div>

        <button class="primary" @click="mode === 'login' ? handleLogin() : handleRegister()" :disabled="loading">
          {{ loading ? (mode === 'login' ? '登录中...' : '注册中...') : (mode === 'login' ? '登录' : '注册') }}
        </button>

        <p class="error-msg" v-if="errorMsg">{{ errorMsg }}</p>
        <p class="success-msg" v-if="successMsg">{{ successMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { homestayApi } from '../api/homestay'

// 路由
const router = useRouter()

// 登录/注册模式
const mode = ref('login')
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

// 清空提示信息
const resetMsg = () => {
  errorMsg.value = ''
  successMsg.value = ''
}

// 切换登录/注册
const switchMode = (next) => {
  mode.value = next
  resetMsg()
}

// 登录处理
const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  resetMsg()

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

// 注册处理
const handleRegister = async () => {
  if (!username.value || !password.value || !confirmPassword.value) {
    errorMsg.value = '请完整填写注册信息'
    return
  }
  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  resetMsg()

  try {
    const result = await homestayApi.register(username.value, password.value)
    if (result.code === 200) {
      successMsg.value = '注册成功，请登录'
      mode.value = 'login'
      password.value = ''
      confirmPassword.value = ''
      return
    }
    errorMsg.value = result.message || '注册失败'
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
  background: radial-gradient(circle at top, #efe6d8 0%, #e7dcc9 40%, #d9cbb5 100%);
  padding: 24px;
}

.login-shell {
  width: min(920px, 92vw);
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  background: #fff;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 16px 40px rgba(70, 52, 35, 0.2);
}

.login-aside {
  background: linear-gradient(140deg, #a68261, #6b4b33);
  color: #f7f2ea;
  padding: 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
}

.login-aside .brand {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 700;
}

.login-aside h1 {
  font-size: 26px;
  margin: 0;
}

.login-aside p {
  margin: 0;
  opacity: 0.92;
}

.aside-note {
  margin-top: 18px;
  font-size: 14px;
  opacity: 0.75;
}

.login-card {
  padding: 36px 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.tabs button {
  flex: 1;
  border: 1px solid #e2d7c6;
  background: #f8f4ee;
  padding: 10px 0;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  color: #6a5240;
}

.tabs button.active {
  background: #6b4b33;
  color: #fff;
  border-color: #6b4b33;
}

h2 {
  margin: 0;
  color: #4b3b2f;
}

.subtitle {
  margin: 0 0 8px;
  color: #8a745f;
  font-size: 14px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-size: 13px;
  color: #6c5747;
}

input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #d9cbb5;
  border-radius: 10px;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #8f6a4e;
  box-shadow: 0 0 0 3px rgba(143, 106, 78, 0.15);
}

.primary {
  width: 100%;
  padding: 12px;
  background: #6b4b33;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 6px;
}

.primary:disabled {
  background: #c7b8a6;
  cursor: not-allowed;
}

.error-msg {
  color: #c0392b;
  text-align: center;
  margin-top: 8px;
  font-size: 14px;
}

.success-msg {
  color: #2f8f5b;
  text-align: center;
  margin-top: 8px;
  font-size: 14px;
}

@media (max-width: 860px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-aside {
    padding: 28px;
  }
}
</style>
