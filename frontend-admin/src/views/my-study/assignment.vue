<template>
  <div class="my-assignment">
    <el-card class="table-card">
      <template #header>我的作业</template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="作业标题" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column label="提交状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getSubmitStatusType(row.submitStatus)">
              {{ getSubmitStatusText(row.submitStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ row.submitTime || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentAssignments } from '@/api/assignment'
import { getCurrentStudent } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const studentId = ref(null)

const getSubmitStatusType = (status) => {
  const map = { 0: 'danger', 1: 'success', 2: 'warning' }
  return map[status] || 'info'
}

const getSubmitStatusText = (status) => {
  const map = { 0: '未提交', 1: '已提交', 2: '逾期提交' }
  return map[status] || '未知'
}

const loadData = async () => {
  if (!studentId.value) return
  loading.value = true
  try {
    const { data } = await getStudentAssignments(studentId.value)
    tableData.value = data || []
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
</style>
