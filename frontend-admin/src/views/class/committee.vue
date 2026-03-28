<template>
  <div class="committee-manage">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="全部班级" clearable @change="handleSearch">
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
          <span>班委列表</span>
          <el-button type="primary" @click="openDialog()">新增班委</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="className" label="班级" />
        <el-table-column prop="position" label="职务" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑班委' : '新增班委'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级" required>
          <el-select v-model="form.classId" placeholder="请选择班级" @change="loadStudentList" style="width: 100%">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生" required>
          <el-select v-model="form.studentId" placeholder="请选择学生" filterable style="width: 100%">
            <el-option v-for="item in studentList" :key="item.id" :label="`${item.realName || ''} (${item.studentNo})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务" required>
          <el-input v-model="form.position" placeholder="请输入职务，如：班长、学习委员" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllClasses, getClassStudents, getAllCommittees, getCommittees, createCommittee, updateCommittee, deleteCommittee } from '@/api/class'

const loading = ref(false)
const tableData = ref([])
const classList = ref([])
const studentList = ref([])
const dialogVisible = ref(false)

const queryParams = reactive({ classId: null })
const form = reactive({ id: null, classId: null, studentId: null, position: '' })

const loadClassList = async () => {
  const { data } = await getAllClasses()
  classList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const loadData = async () => {
  loading.value = true
  try {
    if (queryParams.classId) {
      const { data } = await getCommittees(queryParams.classId)
      const list = Array.isArray(data) ? data : (data.records || data.list || [])
      const classInfo = classList.value.find(c => c.id === queryParams.classId)
      tableData.value = list.map(item => ({ ...item, className: classInfo?.className || item.className }))
    } else {
      const { data } = await getAllCommittees()
      tableData.value = Array.isArray(data) ? data : (data.records || data.list || [])
    }
  } finally {
    loading.value = false
  }
}

const loadStudentList = async () => {
  if (!form.classId) {
    studentList.value = []
    return
  }
  const { data } = await getClassStudents(form.classId)
  studentList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const handleSearch = () => loadData()
const handleReset = () => { queryParams.classId = null; loadData() }

const openDialog = async (row = null) => {
  if (row) {
    form.id = row.id
    form.classId = row.classId
    form.studentId = row.studentId
    form.position = row.position
    await loadStudentList()
  } else {
    form.id = null
    form.classId = null
    form.studentId = null
    form.position = ''
    studentList.value = []
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.classId || !form.studentId || !form.position) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (form.id) {
      await updateCommittee(form.id, { studentId: form.studentId, position: form.position })
      ElMessage.success('修改成功')
    } else {
      await createCommittee(form.classId, { studentId: form.studentId, position: form.position })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该班委吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteCommittee(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadClassList()
  loadData()
})
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
