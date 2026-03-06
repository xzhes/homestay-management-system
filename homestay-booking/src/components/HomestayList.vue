<template>
  <div class="main-page">
    <div class="header">
      <h1>民宿系统主页</h1>
      <div class="user-info">
        欢迎, {{ username }}
        <button @click="$emit('logout')" class="logout-btn">退出</button>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <ul v-else-if="homestays.length > 0" class="homestay-list">
      <li v-for="item in homestays" :key="item.id" class="homestay-item">
        <div class="homestay-info">
          <h3>{{ item.name }}</h3>
          <p class="price">￥{{ item.price }}/晚</p>
          <p v-if="item.description" class="description">
            {{ item.description }}
          </p>
        </div>
        <button
            @click="$emit('reserve', item)"
            class="reserve-btn"
        >
          预约
        </button>
      </li>
    </ul>

    <p v-else class="no-data">暂无民宿信息</p>
  </div>
</template>

<script>
export default {
  name: 'HomestayList',
  props: {
    username: String,
    homestays: Array,
    loading: Boolean
  }
}
</script>

<style scoped>
.main-page {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header h1 {
  color: #333;
  font-size: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
}

.logout-btn {
  width: auto;
  padding: 8px 20px;
  background: #e74c3c;
  font-size: 14px;
}

.logout-btn:hover {
  background: #c0392b;
}

.loading {
  text-align: center;
  color: white;
  font-size: 18px;
  padding: 40px;
}

.homestay-list {
  list-style: none;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.homestay-item {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.homestay-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
}

.homestay-info h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 20px;
}

.price {
  color: #e74c3c;
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.description {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
  line-height: 1.5;
}

.reserve-btn {
  width: 100%;
  background: #27ae60;
  margin-top: 10px;
  padding: 12px;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.reserve-btn:hover {
  background: #229954;
}

.no-data {
  text-align: center;
  color: white;
  font-size: 18px;
  padding: 40px;
}

button {
  cursor: pointer;
}
</style>