<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup>
// 根组件：仅作为路由容器
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>