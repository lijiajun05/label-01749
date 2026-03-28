<template>
  <div class="warning-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="类型">
          <el-select v-model="queryParams.warningType" placeholder="请选择类型" clearable>
            <el-option label="作业未交" :value="1" />
            <el-option label="出勤率低" :value="2" />
            <el-option label="挂科风险" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="canEdit" type="warning" @click="handleGenerate">生成预警</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>预警列表</template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getWarningTypeColor(row.warningType)">
              {{ getWarningTypeText(row.warningType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : 'success'">
              {{ row.status === 0 ? '未处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="canEdit && row.status === 0" type="success" link @click="handleHandle(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="loadData"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="预警详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentWarning.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ getWarningTypeText(currentWarning.warningType) }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentWarning.content }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentWarning.status === 0 ? '未处理' : '已处理' }}</el-descriptions-item>
        <el-descriptions-item v-if="currentWarning.handleResult" label="处理结果">{{ currentWarning.handleResult }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="handleVisible" title="处理预警" width="500px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="处理结果">
          <el-input v-model="handleForm.handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWarningList, handleWarning, generateWarnings } from '@/api/warning'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const handleVisible = ref(false)
const currentWarning = ref({})
const handleForm = reactive({ handleResult: '' })

// 权限控制
const canEdit = computed(() => hasPermission('warning', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, warningType: null, status: null })

const getWarningTypeText = (type) => {
  const map = { 1: '作业未交', 2: '出勤率低', 3: '挂科风险' }
  return map[type] || '未知'
}

const getWarningTypeColor = (type) => {
  const map = { 1: 'warning', 2: 'info', 3: 'danger' }
  return map[type] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getWarningList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, warningType: null, status: null }); loadData() }

const handleGenerate = async () => {
  try {
    await generateWarnings()
    ElMessage.success('预警生成完成')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '生成失败')
  }
}

const handleView = (row) => {
  currentWarning.value = row
  detailVisible.value = true
}

const handleHandle = (row) => {
  currentWarning.value = row
  handleForm.handleResult = ''
  handleVisible.value = true
}

const submitHandle = async () => {
  try {
    await handleWarning(currentWarning.value.id, handleForm)
    ElMessage.success('处理成功')
    handleVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '处理失败')
  }
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
