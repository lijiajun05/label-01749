<template>
  <div class="notice-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="类型">
          <el-select v-model="queryParams.noticeType" placeholder="请选择类型" clearable>
            <el-option label="缴费通知" :value="1" />
            <el-option label="评优通知" :value="2" />
            <el-option label="活动通知" :value="3" />
            <el-option label="其他通知" :value="4" />
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
          <span>通知列表</span>
          <el-button v-if="canEdit" type="primary" @click="handleAdd">发布通知</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small" style="margin-right: 8px">置顶</el-tag>
            {{ row.title }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ getNoticeTypeText(row.noticeType) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '已撤回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="canEdit" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="canEdit" type="warning" link @click="handleTop(row)">{{ row.isTop ? '取消置顶' : '置顶' }}</el-button>
            <el-button v-if="canEdit && row.status === 1" type="danger" link @click="handleWithdraw(row)">撤回</el-button>
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
        <el-form-item label="类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择类型">
            <el-option label="缴费通知" :value="1" />
            <el-option label="评优通知" :value="2" />
            <el-option label="活动通知" :value="3" />
            <el-option label="其他通知" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标班级">
          <el-select v-model="form.classId" placeholder="全部班级" clearable>
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="通知详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentNotice.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ getNoticeTypeText(currentNotice.noticeType) }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentNotice.content }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ currentNotice.publishTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, createNotice, updateNotice, setTop, withdrawNotice } from '@/api/notice'
import { getAllClasses } from '@/api/class'
import { hasPermission } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const detailVisible = ref(false)
const currentNotice = ref({})
const formRef = ref()
const classList = ref([])

// 权限控制
const canEdit = computed(() => hasPermission('notice', 'edit'))

const queryParams = reactive({ pageNum: 1, pageSize: 10, noticeType: null })
const form = reactive({ id: null, title: '', noticeType: 4, classId: null, content: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const getNoticeTypeText = (type) => {
  const map = { 1: '缴费通知', 2: '评优通知', 3: '活动通知', 4: '其他通知' }
  return map[type] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getNoticeList(queryParams)
    tableData.value = data.records || data.list || []
    total.value = data.total || 0
  } catch (e) {
    console.error('Load notice error:', e)
  } finally {
    loading.value = false
  }
}

const loadClasses = async () => {
  const { data } = await getAllClasses()
  classList.value = Array.isArray(data) ? data : (data.records || data.list || [])
}

const handleSearch = () => { queryParams.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(queryParams, { pageNum: 1, noticeType: null }); loadData() }

const handleDialogOpen = () => {
  nextTick(() => formRef.value?.clearValidate())
}

const handleAdd = () => {
  dialogTitle.value = '发布通知'
  Object.assign(form, { id: null, title: '', noticeType: 4, classId: null, content: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑通知'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  currentNotice.value = row
  detailVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateNotice(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createNotice(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleTop = async (row) => {
  try {
    await setTop(row.id, !row.isTop)
    ElMessage.success(row.isTop ? '取消置顶成功' : '置顶成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleWithdraw = async (row) => {
  await ElMessageBox.confirm('确定撤回该通知吗？', '提示', { type: 'warning' })
  try {
    await withdrawNotice(row.id)
    ElMessage.success('撤回成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '撤回失败')
  }
}

onMounted(() => { loadData(); loadClasses() })
</script>

<style lang="scss" scoped>
.search-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
