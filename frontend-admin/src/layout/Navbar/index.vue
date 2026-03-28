<template>
  <div class="navbar">
    <div class="left">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
          {{ item.meta?.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="right">
      <el-popover placement="bottom" :width="320" trigger="click" @show="loadNotices">
        <template #reference>
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notice-badge">
            <el-icon class="notice-icon">
              <Bell />
            </el-icon>
          </el-badge>
        </template>
        <div class="notice-popover">
          <div class="notice-header">
            <span>通知消息</span>
            <el-button type="primary" link @click="$router.push('/notice/list')">查看全部</el-button>
          </div>
          <el-scrollbar max-height="300px">
            <div v-if="noticeList.length === 0" class="notice-empty">暂无新通知</div>
            <div v-else class="notice-list">
              <div v-for="item in noticeList" :key="item.id" class="notice-item" @click="handleReadNotice(item)">
                <div class="notice-title">{{ item.title }}</div>
                <div class="notice-time">{{ item.publishTime }}</div>
              </div>
            </div>
          </el-scrollbar>
        </div>
      </el-popover>
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" :src="userInfo?.avatar">
            {{ userInfo?.realName?.charAt(0) }}
          </el-avatar>
          <span class="username">{{ userInfo?.realName }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-dialog v-model="noticeDetailVisible" title="通知详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentNotice.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentNotice.content }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ currentNotice.publishTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getUnreadCount, getNoticeList, markAsRead } from '@/api/notice'
import { Bell } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const unreadCount = ref(0)
const noticeList = ref([])
const noticeDetailVisible = ref(false)
const currentNotice = ref({})

const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta?.title)
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    await userStore.logout()
    router.push('/login')
  }
}

const loadNotices = async () => {
  try {
    const { data } = await getNoticeList({ pageNum: 1, pageSize: 5 })
    noticeList.value = data.records || data.list || []
  } catch (e) {}
}

const handleReadNotice = async (notice) => {
  currentNotice.value = notice
  noticeDetailVisible.value = true
  try {
    await markAsRead(notice.id)
    await loadUnreadCount()
  } catch (e) {}
}

const loadUnreadCount = async () => {
  try {
    const { data } = await getUnreadCount()
    unreadCount.value = data
  } catch (e) {}
}

onMounted(async () => {
  try {
    await userStore.getUserInfo()
    await loadUnreadCount()
  } catch (e) {
    // ignore
  }
})
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  
  .left {
    display: flex;
    align-items: center;
  }
  
  .right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .notice-badge {
      cursor: pointer;
      
      .notice-icon {
        font-size: 20px;
        color: #606266;
      }
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      
      .username {
        color: #303133;
        font-size: 14px;
      }
    }
  }
}

.notice-popover {
  .notice-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    margin-bottom: 10px;
  }
  
  .notice-empty {
    text-align: center;
    color: #999;
    padding: 20px 0;
  }
  
  .notice-list {
    .notice-item {
      padding: 10px 0;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      
      &:hover {
        background: #f5f7fa;
      }
      
      &:last-child {
        border-bottom: none;
      }
      
      .notice-title {
        font-size: 14px;
        color: #303133;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .notice-time {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}
</style>
