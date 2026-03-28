<template>
  <div class="log-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="queryParams.module" placeholder="请输入模块" clearable />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryParams.operationType" placeholder="请选择类型" clearable>
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
            <el-option label="登录" value="登录" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>操作日志</template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeColor(row.operationType)">{{ row.operationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="操作内容" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @change="loadData"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLogList } from '@/api/log'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  module: '',
  operationType: '',
  startDate: '',
  endDate: ''
})

const getOperationTypeColor = (type) => {
  const map = { '新增': 'success', '修改': 'warning', '删除': 'danger', '登录': 'info' }
  return map[type] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getLogList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleDateChange = (val) => {
  if (val) {
    queryParams.startDate = val[0]
    queryParams.endDate = val[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => {
  Object.assign(queryParams, { pageNum: 1, username: '', module: '', operationType: '', startDate: '', endDate: '' })
  dateRange.value = []
  loadData()
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
