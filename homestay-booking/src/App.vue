<template>
  <div class="container">
    <!-- 未登录 -->
    <LoginForm v-if="!loggedIn" @login-success="handleLoginSuccess" />

    <!-- 已登录 -->
    <template v-else>
      <HomestayList
          :username="currentUser"
          :homestays="homestays"
          :loading="loadingHomestays"
          @logout="handleLogout"
          @reserve="openReservationDialog"
      />

      <ReservationDialog
          :show="showDialog"
          :homestay="selectedHomestay"
          @close="closeDialog"
          @confirm="handleReservation"
      />

      <!-- 提示消息 -->
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>
    </template>
  </div>
</template>

<script>
import LoginForm from './components/LoginForm.vue'
import HomestayList from './components/HomestayList.vue'
import ReservationDialog from './components/ReservationDialog.vue'
import { homestayApi } from './api/homestay'

export default {
  name: 'App',
  components: {
    LoginForm,
    HomestayList,
    ReservationDialog
  },
  data() {
    return {
      loggedIn: false,
      currentUser: '',
      userId: 1,
      homestays: [],
      loadingHomestays: false,
      showDialog: false,
      selectedHomestay: null,
      message: '',
      messageType: ''
    }
  },
  methods: {
    async handleLoginSuccess(username) {
      this.loggedIn = true
      this.currentUser = username
      await this.loadHomestays()
    },

    handleLogout() {
      this.loggedIn = false
      this.currentUser = ''
      this.homestays = []
    },

    async loadHomestays() {
      this.loadingHomestays = true
      try {
        this.homestays = await homestayApi.getHomestays()
      } catch (err) {
        this.showMessage('加载失败', 'error')
        console.error(err)
      } finally {
        this.loadingHomestays = false
      }
    },

    openReservationDialog(homestay) {
      this.selectedHomestay = homestay
      this.showDialog = true
    },

    closeDialog() {
      this.showDialog = false
      // 不要立即清空 selectedHomestay，等预约完成后再清空
      // this.selectedHomestay = null  ← 删除这行
    },

    async handleReservation(date) {
      // 在关闭对话框之前先保存 homestay 的 id
      const homestayId = this.selectedHomestay?.id

      if (!homestayId) {
        this.showMessage('预约信息错误', 'error')
        return
      }

      this.closeDialog()

      try {
        const result = await homestayApi.submitReservation(
            this.userId,
            homestayId,  // 使用保存的 id
            date
        )

        if (result === 'success') {
          this.showMessage('预约成功！', 'success')
        } else {
          this.showMessage('预约失败', 'error')
        }
      } catch (err) {
        this.showMessage('网络错误', 'error')
        console.error(err)
      } finally {
        // 预约完成后再清空
        this.selectedHomestay = null
      }
    },

    showMessage(text, type) {
      this.message = text
      this.messageType = type
      setTimeout(() => {
        this.message = ''
      }, 3000)
    }
  }
}
</script>

<style>
/* CSS 部分保持不变 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 15px 25px;
  border-radius: 8px;
  font-weight: bold;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  animation: slideIn 0.3s ease-out;
  z-index: 2000;
}

.message.success {
  background: #27ae60;
  color: white;
}

.message.error {
  background: #e74c3c;
  color: white;
}

@keyframes slideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>