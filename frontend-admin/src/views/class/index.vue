<template>
  <div class="class-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="班级名称">
          <el-input v-model="queryParams.className" placeholder="请输入班级名称" clearable />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="queryParams.grade" placeholder="请输入年级" clearable />
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
          <span>班级列表</span>
          <el-button v-if="canEdit" type="primary" @click="handleAdd">新增班级</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="className" label="班级名称" min-width="120" />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="major" label="专业" min-width="120" />
        <el-table-column prop="department" label="院系" min-width="100" />
        <el-table-column prop="studentCount" label="人数" width="70" />
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-if="canEdit" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="$router.push(`/class/detail/${row.id}`)">详情</el-button>
            <el-button v-if="canEdit" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-input v-model="form.department" placeholder="请输入院系" />
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
import { getClassList, createClass, updateClass, deleteClass } from '@/api/class'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

// 权限控制
const canEdit = computed(() => hasPermission('class', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, className: '', grade: '' })
const form = reactive({ id: null, className: '', grade: '', major: '', department: '' })
const rules = { className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getClassList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, className: '', grade: '' }); loadData() }

const handleAdd = () => {
  dialogTitle.value = '新增班级'
  Object.assign(form, { id: null, className: '', grade: '', major: '', department: '' })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑班级'
  Object.assign(form, row)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateClass(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createClass(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该班级吗？', '提示', { type: 'warning' })
  try {
    await deleteClass(row.id)
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
