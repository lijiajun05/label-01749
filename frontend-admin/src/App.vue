<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getToken } from '@/utils/auth'

const userStore = useUserStore()

onMounted(async () => {
  // 如果有token但没有用户信息，则获取用户信息
  if (getToken() && !userStore.userInfo) {
    try {
      await userStore.getUserInfo()
    } catch (e) {
      // 获取失败则清除token
      userStore.resetToken()
    }
  }
})
</script>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
