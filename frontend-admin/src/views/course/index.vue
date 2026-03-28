<template>
  <div class="course-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="queryParams.semester" placeholder="请输入学期" clearable />
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
          <span>课程列表</span>
          <el-button v-if="canEdit" type="primary" @click="handleAdd">新增课程</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="courseCode" label="课程代码" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
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
import { getCourseList, createCourse, updateCourse, deleteCourse } from '@/api/course'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

// 权限控制
const canEdit = computed(() => hasPermission('course', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, courseName: '', semester: '' })
const form = reactive({ id: null, courseName: '', courseCode: '', credit: 3, semester: '' })
const rules = { courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getCourseList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, courseName: '', semester: '' }); loadData() }

const handleAdd = () => {
  dialogTitle.value = '新增课程'
  Object.assign(form, { id: null, courseName: '', courseCode: '', credit: 3, semester: '' })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑课程'
  Object.assign(form, row)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateCourse(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createCourse(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该课程吗？', '提示', { type: 'warning' })
  try {
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
