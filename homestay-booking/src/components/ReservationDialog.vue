<template>
  <div v-if="show" class="dialog-overlay" @click="$emit('close')">
    <div class="dialog" @click.stop>
      <h2>预约 {{ homestay?.name }}</h2>
      <div class="form-group">
        <label>入住日期:</label>
        <input type="date" v-model="selectedDate" :min="today" />
      </div>
      <div class="dialog-buttons">
        <button @click="handleConfirm" class="confirm-btn">确认预约</button>
        <button @click="$emit('close')" class="cancel-btn">取消</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReservationDialog',
  props: {
    show: Boolean,
    homestay: Object
  },
  data() {
    return {
      selectedDate: '',
      today: new Date().toISOString().split('T')[0]
    }
  },
  watch: {
    show(newVal) {
      if (newVal) {
        this.selectedDate = this.today
      }
    }
  },
  methods: {
    handleConfirm() {
      if (!this.selectedDate) {
        alert('请选择入住日期')
        return
      }
      this.$emit('confirm', this.selectedDate)
    }
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 12px;
  min-width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.dialog h2 {
  color: #333;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

input[type="date"] {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.dialog-buttons {
  display: flex;
  gap: 10px;
  margin-top: 25px;
}

.dialog-buttons button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.confirm-btn {
  background: #27ae60;
  color: white;
}

.confirm-btn:hover {
  background: #229954;
}

.cancel-btn {
  background: #95a5a6;
  color: white;
}

.cancel-btn:hover {
  background: #7f8c8d;
}
</style>
