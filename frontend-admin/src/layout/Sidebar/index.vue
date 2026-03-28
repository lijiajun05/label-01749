<template>
  <div class="sidebar">
    <div class="logo">
      <span class="title">班级管理系统</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <template v-for="route in filteredRoutes" :key="route.path">
        <el-sub-menu v-if="route.children && getVisibleChildren(route).length > 1" :index="route.path">
          <template #title>
            <SvgIcon :name="route.meta?.icon" />
            <span>{{ route.meta?.title }}</span>
          </template>
          <el-menu-item
            v-for="child in getVisibleChildren(route)"
            :key="child.path"
            :index="route.path + '/' + child.path"
          >
            {{ child.meta?.title }}
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item
          v-else-if="route.children && getVisibleChildren(route).length === 1"
          :index="route.redirect || (route.path + '/' + getVisibleChildren(route)[0].path)"
        >
          <SvgIcon :name="route.meta?.icon || getVisibleChildren(route)[0].meta?.icon" />
          <span>{{ getVisibleChildren(route)[0].meta?.title }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { asyncRoutes } from '@/router'
import { useUserStore } from '@/store/modules/user'
import SvgIcon from '@/components/SvgIcon/index.vue'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

// 检查路由是否有权限
const hasPermission = (route) => {
  const roles = userStore.roles || []
  // 如果路由没有设置roles，则所有人都可以访问
  if (!route.meta?.roles || route.meta.roles.length === 0) return true
  // 检查用户角色是否在允许的角色列表中
  return route.meta.roles.some(role => roles.includes(role))
}

// 过滤有权限的路由
const filteredRoutes = computed(() => {
  return asyncRoutes.filter(r => {
    // 隐藏的路由不显示
    if (r.hidden) return false
    // 检查父路由权限
    if (!hasPermission(r)) return false
    // 检查是否有可见的子路由
    if (r.children) {
      const visibleChildren = r.children.filter(c => !c.hidden && hasPermission(c))
      return visibleChildren.length > 0
    }
    return true
  })
})

// 获取可见的子路由
const getVisibleChildren = (route) => {
  if (!route.children) return []
  return route.children.filter(c => !c.hidden && hasPermission(c))
}
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  overflow-y: auto;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #263445;
    
    .title {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
    }
  }
  
  :deep(.el-menu) {
    border-right: none;
  }
  
  :deep(.el-sub-menu__title),
  :deep(.el-menu-item) {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}
</style>
