<template>
  <div class="assignment-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="课程">
          <el-select v-model="queryParams.courseId" placeholder="请选择课程" clearable>
            <el-option v-for="item in courseList" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题" clearable />
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
          <span>作业列表</span>
          <el-button v-if="canEdit" type="primary" @click="handleAdd">发布作业</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="courseName" label="课程名称">
          <template #default="{ row }">
            {{ row.courseName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canEdit" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleViewSubmits(row)">提交情况</el-button>
            <el-button v-if="canEdit" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @opened="handleDialogOpen">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程">
            <el-option v-for="item in courseList" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择截止时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="submitDialogVisible" title="提交情况" width="700px">
      <el-table :data="submitList" stripe border>
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '已提交' : '未提交' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAssignmentList, createAssignment, updateAssignment, deleteAssignment, getSubmits } from '@/api/assignment'
import { getAllCourses } from '@/api/course'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const courseList = ref([])
const submitDialogVisible = ref(false)
const submitList = ref([])

// 权限控制
const canEdit = computed(() => hasPermission('assignment', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, courseId: null, title: '' })
const form = reactive({ id: null, title: '', courseId: null, deadline: '', description: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getAssignmentList(queryParams)
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

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, courseId: null, title: '' }); loadData() }

const handleDialogOpen = () => {
  nextTick(() => formRef.value?.clearValidate())
}

const handleAdd = () => {
  dialogTitle.value = '发布作业'
  Object.assign(form, { id: null, title: '', courseId: null, deadline: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑作业'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleViewSubmits = async (row) => {
  const { data } = await getSubmits(row.id)
  submitList.value = data
  submitDialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateAssignment(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createAssignment(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该作业吗？', '提示', { type: 'warning' })
  try {
    await deleteAssignment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => { loadData(); loadCourses() })
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
