<template>
  <div class="login-box">
    <h1>民宿系统登录</h1>

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
</template>

<script>
import { homestayApi } from '../api/homestay'

export default {
  name: 'LoginForm',
  data() {
    return {
      username: '',
      password: '',
      errorMsg: '',
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!this.username || !this.password) {
        this.errorMsg = '请输入用户名和密码'
        return
      }

      this.loading = true
      this.errorMsg = ''

      try {
        const result = await homestayApi.login(this.username, this.password)

        if (result === 'success') {
          this.$emit('login-success', this.username)
        } else {
          this.errorMsg = '用户名或密码错误'
        }
      } catch (err) {
        this.errorMsg = '网络错误，请稍后重试'
        console.error(err)
      } finally {
        this.loading = false
      }
    }
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
  color: #333;
  margin-bottom: 30px;
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
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: #667eea;
}

button {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover:not(:disabled) {
  background: #5568d3;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error-msg {
  color: #e74c3c;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
</style>