<template>
  <div class="app-wrapper">
    <Sidebar class="sidebar-container" />
    <div class="main-container">
      <Navbar />
      <AppMain />
    </div>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar/index.vue'
import Navbar from './Navbar/index.vue'
import AppMain from './AppMain/index.vue'
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;
}

.sidebar-container {
  width: 200px;
  height: 100%;
  background: #304156;
  transition: width 0.3s;
  flex-shrink: 0;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f5f7fa;
}
</style>
