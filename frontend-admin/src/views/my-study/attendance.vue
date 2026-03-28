<template>
  <div class="my-attendance">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>考勤统计</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="value">{{ statistics.total || 0 }}</div>
            <div class="label">总记录</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item success">
            <div class="value">{{ statistics.present || 0 }}</div>
            <div class="label">出勤</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item warning">
            <div class="value">{{ statistics.late || 0 }}</div>
            <div class="label">迟到</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item danger">
            <div class="value">{{ statistics.absent || 0 }}</div>
            <div class="label">缺勤</div>
          </div>
        </el-col>
      </el-row>
      <div class="rate-info">
        <span>出勤率：</span>
        <el-progress :percentage="statistics.attendanceRate || 0" :stroke-width="20" />
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>我的考勤记录</template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="attendanceDate" label="日期" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getStudentAttendance, getStudentStatistics } from '@/api/attendance'
import { getCurrentStudent } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const statistics = reactive({})
const studentId = ref(null)

const getStatusType = (status) => {
  const map = { 0: 'danger', 1: 'success', 2: 'warning', 3: 'warning', 4: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '缺勤', 1: '出勤', 2: '迟到', 3: '早退', 4: '请假' }
  return map[status] || '未知'
}

const loadData = async () => {
  if (!studentId.value) return
  loading.value = true
  try {
    const [attendanceRes, statsRes] = await Promise.all([
      getStudentAttendance(studentId.value),
      getStudentStatistics(studentId.value)
    ])
    tableData.value = attendanceRes.data || []
    Object.assign(statistics, statsRes.data || {})
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const { data } = await getCurrentStudent()
    if (data) {
      studentId.value = data.id
      loadData()
    }
  } catch (e) {
    console.error('获取学生信息失败', e)
  }
})
</script>

<style lang="scss" scoped>
.info-card {
  margin-bottom: 16px;
}
.stat-item {
  text-align: center;
  padding: 20px;
  .value {
    font-size: 32px;
    font-weight: bold;
    color: #303133;
  }
  .label {
    color: #909399;
    margin-top: 8px;
  }
  &.success .value { color: #67c23a; }
  &.warning .value { color: #e6a23c; }
  &.danger .value { color: #f56c6c; }
}
.rate-info {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  span { font-weight: bold; }
  .el-progress { flex: 1; }
}
</style>
