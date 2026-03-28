<template>
  <div class="grade-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="课程">
          <el-select v-model="queryParams.courseId" placeholder="请选择课程" clearable>
            <el-option v-for="item in courseList" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.gradeType" placeholder="请选择类型" clearable>
            <el-option label="平时成绩" :value="1" />
            <el-option label="期中成绩" :value="2" />
            <el-option label="期末成绩" :value="3" />
          </el-select>
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
          <span>成绩列表</span>
          <el-button v-if="canEdit" type="primary" @click="handleAdd">录入成绩</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ getGradeTypeText(row.gradeType) }}</template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" />
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
        <el-form-item label="类型" prop="gradeType">
          <el-select v-model="form.gradeType" placeholder="请选择类型">
            <el-option label="平时成绩" :value="1" />
            <el-option label="期中成绩" :value="2" />
            <el-option label="期末成绩" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-input-number v-model="form.score" :min="0" :max="100" :precision="1" />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="form.semester" placeholder="如：2024-2025-1" />
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
import { getGradeList, createGrade, updateGrade, deleteGrade } from '@/api/grade'
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
const canEdit = computed(() => hasPermission('grade', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, courseId: null, gradeType: null })
const form = reactive({ id: null, studentId: null, courseId: null, gradeType: 1, score: 0, semester: '' })
const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  gradeType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  score: [{ required: true, message: '请输入分数', trigger: 'blur' }]
}

const getGradeTypeText = (type) => {
  const map = { 1: '平时成绩', 2: '期中成绩', 3: '期末成绩' }
  return map[type] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getGradeList(queryParams)
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
  const { data } = await getAllStudents()
  studentList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, courseId: null, gradeType: null }); loadData() }

const handleDialogOpen = () => {
  nextTick(() => formRef.value?.clearValidate())
}

const handleAdd = () => {
  dialogTitle.value = '录入成绩'
  Object.assign(form, { id: null, studentId: null, courseId: null, gradeType: 1, score: 0, semester: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑成绩'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateGrade(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createGrade(form)
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
    await deleteGrade(row.id)
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
