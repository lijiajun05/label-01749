<template>
  <div class="my-grade">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>成绩统计</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-item">
            <div class="value">{{ statistics.totalCourses || 0 }}</div>
            <div class="label">课程数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item success">
            <div class="value">{{ (statistics.averageScore || 0).toFixed(1) }}</div>
            <div class="label">平均分</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item" :class="statistics.passRate >= 60 ? 'success' : 'danger'">
            <div class="value">{{ (statistics.passRate || 0).toFixed(1) }}%</div>
            <div class="label">及格率</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <template #header>我的成绩</template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column label="成绩类型" width="100">
          <template #default="{ row }">{{ getGradeTypeText(row.gradeType) }}</template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getStudentGrades, getStudentStatistics } from '@/api/grade'
import { getCurrentStudent } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const statistics = reactive({})
const studentId = ref(null)

const getGradeTypeText = (type) => {
  const map = { 1: '平时成绩', 2: '期中成绩', 3: '期末成绩' }
  return map[type] || '未知'
}

const loadData = async () => {
  if (!studentId.value) return
  loading.value = true
  try {
    const [gradesRes, statsRes] = await Promise.all([
      getStudentGrades(studentId.value),
      getStudentStatistics(studentId.value)
    ])
    tableData.value = gradesRes.data || []
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
  &.danger .value { color: #f56c6c; }
}
</style>
