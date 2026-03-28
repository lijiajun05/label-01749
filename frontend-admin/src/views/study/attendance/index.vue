<template>
  <div class="attendance-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="课程">
          <el-select v-model="queryParams.courseId" placeholder="请选择课程" clearable>
            <el-option v-for="item in courseList" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="queryParams.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>考勤记录</span>
          <el-button v-if="canEdit" type="primary" @click="handleAdd">录入考勤</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="学生姓名">
          <template #default="{ row }">
            {{ row.studentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="studentNo" label="学号">
          <template #default="{ row }">
            {{ row.studentNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称">
          <template #default="{ row }">
            {{ row.courseName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="attendanceDate" label="日期" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column v-if="canEdit" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @opened="handleDialogOpen">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择学生" filterable>
            <el-option v-for="item in studentList" :key="item.id" :label="`${item.realName || ''} (${item.studentNo})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程">
            <el-option v-for="item in courseList" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="attendanceDate">
          <el-date-picker v-model="form.attendanceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="出勤" :value="1" />
            <el-option label="迟到" :value="2" />
            <el-option label="早退" :value="3" />
            <el-option label="缺勤" :value="0" />
            <el-option label="请假" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAttendanceList, createAttendance, updateAttendance, deleteAttendance } from '@/api/attendance'
import { getAllCourses } from '@/api/course'
import { getAllStudents } from '@/api/student'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const courseList = ref([])
const studentList = ref([])

// 权限控制
const canEdit = computed(() => hasPermission('attendance', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, courseId: null, date: '' })
const form = reactive({ id: null, studentId: null, courseId: null, attendanceDate: '', status: 1, remark: '' })
const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  attendanceDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const getStatusType = (status) => {
  const map = { 0: 'danger', 1: 'success', 2: 'warning', 3: 'warning', 4: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '缺勤', 1: '出勤', 2: '迟到', 3: '早退', 4: '请假' }
  return map[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getAttendanceList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const loadCourses = async () => {
  const { data } = await getAllCourses()
  courseList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const loadStudents = async () => {
  try {
    const { data } = await getAllStudents()
    studentList.value = Array.isArray(data) ? data : (data.records || data.list || [])
  } catch (e) {
    studentList.value = []
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, courseId: null, date: '' }); loadData() }

const handleDialogOpen = () => {
  nextTick(() => formRef.value?.clearValidate())
}

const handleAdd = () => {
  dialogTitle.value = '录入考勤'
  Object.assign(form, { id: null, studentId: null, courseId: null, attendanceDate: '', status: 1, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑考勤'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateAttendance(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createAttendance(form)
      ElMessage.success('录入成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  try {
    await deleteAttendance(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => { loadData(); loadCourses(); loadStudents() })
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
