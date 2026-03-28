<template>
  <div class="meeting-manage">
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
          <span>班会记录</span>
          <el-button type="primary" @click="openDialog()">新增班会</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="title" label="主题" />
        <el-table-column prop="meetingTime" label="时间" width="180" />
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑班会' : '新增班会'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级" required>
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%" :disabled="!!form.id">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="主题" required>
          <el-input v-model="form.title" placeholder="请输入班会主题" />
        </el-form-item>
        <el-form-item label="时间" required>
          <el-date-picker v-model="form.meetingTime" type="datetime" placeholder="请选择时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="地点" required>
          <el-input v-model="form.location" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入班会内容" />
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
import { getAllClasses, getAllMeetings, getMeetings, createMeeting, updateMeeting, deleteMeeting } from '@/api/class'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const classList = ref([])
const dialogVisible = ref(false)

const queryParams = reactive({ classId: null, pageNum: 1, pageSize: 10 })
const form = reactive({ id: null, classId: null, title: '', meetingTime: '', location: '', content: '' })

const loadClassList = async () => {
  const { data } = await getAllClasses()
  classList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const loadData = async () => {
  loading.value = true
  try {
    let data
    if (queryParams.classId) {
      const res = await getMeetings(queryParams.classId, { pageNum: queryParams.pageNum, pageSize: queryParams.pageSize })
      data = res.data
    } else {
      const res = await getAllMeetings({ pageNum: queryParams.pageNum, pageSize: queryParams.pageSize })
      data = res.data
    }
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { queryParams.classId = null; queryParams.pageNum = 1; loadData() }

const openDialog = (row = null) => {
  if (row) {
    form.id = row.id
    form.classId = row.classId
    form.title = row.title
    form.meetingTime = row.meetingTime
    form.location = row.location
    form.content = row.content
  } else {
    form.id = null
    form.classId = null
    form.title = ''
    form.meetingTime = ''
    form.location = ''
    form.content = ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.classId || !form.title || !form.meetingTime || !form.location) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (form.id) {
      await updateMeeting(form.id, { title: form.title, meetingTime: form.meetingTime, location: form.location, content: form.content })
      ElMessage.success('修改成功')
    } else {
      await createMeeting(form.classId, { title: form.title, meetingTime: form.meetingTime, location: form.location, content: form.content })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该班会记录吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteMeeting(row.id)
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
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
