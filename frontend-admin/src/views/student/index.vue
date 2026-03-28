<template>
  <div class="student-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable>
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
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
          <span>学生列表</span>
          <el-button v-if="canAdd" type="primary" @click="handleAdd">新增学生</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 'M' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="className" label="班级" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" />
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
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="M">男</el-radio>
            <el-radio value="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
import { getStudentList, createStudent, updateStudent, deleteStudent } from '@/api/student'
import { getAllClasses } from '@/api/class'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const classList = ref([])

// 权限控制
const canEdit = computed(() => hasPermission('student', 'edit'))
const canAdd = computed(() => hasPermission('student', 'add'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, studentNo: '', realName: '', classId: null })
const form = reactive({ id: null, studentNo: '', realName: '', gender: 'M', classId: null, phone: '', email: '' })
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getStudentList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const loadClasses = async () => {
  const { data } = await getAllClasses()
  classList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, studentNo: '', realName: '', classId: null }); loadData() }

const handleDialogOpen = () => {
  nextTick(() => formRef.value?.clearValidate())
}

const handleAdd = () => {
  dialogTitle.value = '新增学生'
  Object.assign(form, { id: null, studentNo: '', realName: '', gender: 'M', classId: null, phone: '', email: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑学生'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateStudent(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createStudent(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该学生吗？', '提示', { type: 'warning' })
  try {
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => { loadData(); loadClasses() })
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
