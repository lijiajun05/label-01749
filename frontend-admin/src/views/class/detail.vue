<template>
  <div class="class-detail">
    <el-card class="info-card">
      <template #header>班级信息</template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="班级名称">{{ classInfo.className }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ classInfo.grade }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ classInfo.major }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ classInfo.department }}</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ classInfo.studentCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="classInfo.status === 1 ? 'success' : 'danger'">
            {{ classInfo.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="tab-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="班委信息" name="committee">
          <div class="tab-header" v-if="canEdit">
            <el-button type="primary" @click="openCommitteeDialog()">新增班委</el-button>
          </div>
          <el-table :data="committees" stripe border>
            <el-table-column prop="position" label="职务" />
            <el-table-column prop="studentName" label="姓名" />
            <el-table-column prop="studentNo" label="学号" />
            <el-table-column prop="phone" label="联系电话" />
            <el-table-column label="操作" width="150" v-if="canEdit">
              <template #default="{ row }">
                <el-button type="primary" link @click="openCommitteeDialog(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteCommittee(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="班会记录" name="meeting">
          <div class="tab-header" v-if="canEdit">
            <el-button type="primary" @click="openMeetingDialog()">新增班会</el-button>
          </div>
          <el-table :data="meetings" stripe border>
            <el-table-column prop="title" label="主题" />
            <el-table-column prop="meetingTime" label="时间" width="180" />
            <el-table-column prop="location" label="地点" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column label="操作" width="150" v-if="canEdit">
              <template #default="{ row }">
                <el-button type="primary" link @click="openMeetingDialog(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteMeeting(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="班级通讯录" name="contacts">
          <el-table :data="contacts" stripe border>
            <el-table-column prop="studentNo" label="学号" />
            <el-table-column prop="realName" label="姓名" />
            <el-table-column prop="gender" label="性别">
              <template #default="{ row }">{{ row.gender === 'M' ? '男' : '女' }}</template>
            </el-table-column>
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="email" label="邮箱" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 班委编辑对话框 -->
    <el-dialog v-model="committeeDialogVisible" :title="committeeForm.id ? '编辑班委' : '新增班委'" width="500px">
      <el-form :model="committeeForm" label-width="80px">
        <el-form-item label="学生" required>
          <el-select v-model="committeeForm.studentId" placeholder="请选择学生" filterable style="width: 100%">
            <el-option v-for="item in studentList" :key="item.id" :label="`${item.realName || ''} (${item.studentNo})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务" required>
          <el-input v-model="committeeForm.position" placeholder="请输入职务" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="committeeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCommittee">确定</el-button>
      </template>
    </el-dialog>

    <!-- 班会编辑对话框 -->
    <el-dialog v-model="meetingDialogVisible" :title="meetingForm.id ? '编辑班会' : '新增班会'" width="600px">
      <el-form :model="meetingForm" label-width="80px">
        <el-form-item label="主题" required>
          <el-input v-model="meetingForm.title" placeholder="请输入班会主题" />
        </el-form-item>
        <el-form-item label="时间" required>
          <el-date-picker v-model="meetingForm.meetingTime" type="datetime" placeholder="请选择时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="地点" required>
          <el-input v-model="meetingForm.location" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="meetingForm.content" type="textarea" :rows="4" placeholder="请输入班会内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="meetingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMeeting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassById, getCommittees, getMeetings, getClassContacts, getClassStudents, createCommittee, updateCommittee, deleteCommittee, createMeeting, updateMeeting, deleteMeeting } from '@/api/class'
import { hasRole, ROLES } from '@/utils/permission'

const route = useRoute()
const classId = route.params.id

const classInfo = reactive({})
const activeTab = ref('committee')
const committees = ref([])
const meetings = ref([])
const contacts = ref([])
const studentList = ref([])

// 权限控制 - 只有管理员和班主任可以编辑
const canEdit = computed(() => hasRole([ROLES.ADMIN, ROLES.TEACHER]))

// 班委表单
const committeeDialogVisible = ref(false)
const committeeForm = reactive({ id: null, studentId: null, position: '' })

// 班会表单
const meetingDialogVisible = ref(false)
const meetingForm = reactive({ id: null, title: '', meetingTime: '', location: '', content: '' })

// 加载数据
const loadData = async () => {
  const { data } = await getClassById(classId)
  Object.assign(classInfo, data)
  
  const { data: committeeData } = await getCommittees(classId)
  committees.value = Array.isArray(committeeData) ? committeeData : (committeeData.records || committeeData.list || [])
  
  const { data: meetingData } = await getMeetings(classId, { pageNum: 1, pageSize: 100 })
  meetings.value = meetingData.records || meetingData.list || []
  
  const { data: contactData } = await getClassContacts(classId)
  contacts.value = Array.isArray(contactData) ? contactData : (contactData.records || contactData.list || [])
  
  // 加载学生列表用于班委选择
  const { data: studentData } = await getClassStudents(classId)
  studentList.value = Array.isArray(studentData) ? studentData : (studentData.records || studentData.list || [])
}

// 打开班委对话框
const openCommitteeDialog = (row = null) => {
  if (row) {
    committeeForm.id = row.id
    committeeForm.studentId = row.studentId
    committeeForm.position = row.position
  } else {
    committeeForm.id = null
    committeeForm.studentId = null
    committeeForm.position = ''
  }
  committeeDialogVisible.value = true
}

// 提交班委
const submitCommittee = async () => {
  if (!committeeForm.studentId || !committeeForm.position) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (committeeForm.id) {
      await updateCommittee(committeeForm.id, { studentId: committeeForm.studentId, position: committeeForm.position })
      ElMessage.success('修改成功')
    } else {
      await createCommittee(classId, { studentId: committeeForm.studentId, position: committeeForm.position })
      ElMessage.success('新增成功')
    }
    committeeDialogVisible.value = false
    const { data: committeeData } = await getCommittees(classId)
    committees.value = Array.isArray(committeeData) ? committeeData : (committeeData.records || committeeData.list || [])
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 删除班委
const handleDeleteCommittee = (row) => {
  ElMessageBox.confirm('确定删除该班委吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteCommittee(row.id)
    ElMessage.success('删除成功')
    const { data: committeeData } = await getCommittees(classId)
    committees.value = Array.isArray(committeeData) ? committeeData : (committeeData.records || committeeData.list || [])
  }).catch(() => {})
}

// 打开班会对话框
const openMeetingDialog = (row = null) => {
  if (row) {
    meetingForm.id = row.id
    meetingForm.title = row.title
    meetingForm.meetingTime = row.meetingTime
    meetingForm.location = row.location
    meetingForm.content = row.content
  } else {
    meetingForm.id = null
    meetingForm.title = ''
    meetingForm.meetingTime = ''
    meetingForm.location = ''
    meetingForm.content = ''
  }
  meetingDialogVisible.value = true
}

// 提交班会
const submitMeeting = async () => {
  if (!meetingForm.title || !meetingForm.meetingTime || !meetingForm.location) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (meetingForm.id) {
      await updateMeeting(meetingForm.id, { title: meetingForm.title, meetingTime: meetingForm.meetingTime, location: meetingForm.location, content: meetingForm.content })
      ElMessage.success('修改成功')
    } else {
      await createMeeting(classId, { title: meetingForm.title, meetingTime: meetingForm.meetingTime, location: meetingForm.location, content: meetingForm.content })
      ElMessage.success('新增成功')
    }
    meetingDialogVisible.value = false
    const { data: meetingData } = await getMeetings(classId, { pageNum: 1, pageSize: 100 })
    meetings.value = meetingData.records || meetingData.list || []
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 删除班会
const handleDeleteMeeting = (row) => {
  ElMessageBox.confirm('确定删除该班会记录吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteMeeting(row.id)
    ElMessage.success('删除成功')
    const { data: meetingData } = await getMeetings(classId, { pageNum: 1, pageSize: 100 })
    meetings.value = meetingData.records || meetingData.list || []
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.info-card { margin-bottom: 16px; }
.tab-header { margin-bottom: 16px; }
</style>
