<template>
  <div class="dashboard">
    <!-- 学生视图 -->
    <template v-if="isStudent">
      <el-row :gutter="16" class="stat-cards">
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <div class="icon"><el-icon><Calendar /></el-icon></div>
            <div class="info">
              <div class="value">{{ myStats.attendanceTotal || 0 }}</div>
              <div class="label">考勤记录</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <div class="icon"><el-icon><Check /></el-icon></div>
            <div class="info">
              <div class="value">{{ (myStats.attendanceRate || 0).toFixed(1) }}%</div>
              <div class="label">出勤率</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <div class="icon"><el-icon><Document /></el-icon></div>
            <div class="info">
              <div class="value">{{ myStats.totalCourses || 0 }}</div>
              <div class="label">课程数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <div class="icon"><el-icon><TrendCharts /></el-icon></div>
            <div class="info">
              <div class="value">{{ (myStats.averageScore || 0).toFixed(1) }}</div>
              <div class="label">平均成绩</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-card>
            <template #header>我的学业概览</template>
            <div class="my-study-overview">
              <div class="item">
                <span class="label">出勤率</span>
                <el-progress :percentage="myStats.attendanceRate || 0" :stroke-width="16" />
              </div>
              <div class="item">
                <span class="label">及格率</span>
                <el-progress :percentage="myStats.passRate || 0" :stroke-width="16" :status="myStats.passRate >= 60 ? 'success' : 'exception'" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>最新通知</template>
            <el-empty v-if="!latestNotices.length" description="暂无通知" />
            <div v-else class="notice-list">
              <div v-for="notice in latestNotices" :key="notice.id" class="notice-item">
                <el-tag v-if="notice.isTop" type="danger" size="small">置顶</el-tag>
                <span class="title">{{ notice.title }}</span>
                <span class="time">{{ notice.publishTime }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 管理员/教师视图 -->
    <template v-else>
      <el-row :gutter="16" class="stat-cards">
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <div class="icon"><el-icon><User /></el-icon></div>
            <div class="info">
              <div class="value">{{ stats.studentCount }}</div>
              <div class="label">学生总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <div class="icon"><el-icon><School /></el-icon></div>
            <div class="info">
              <div class="value">{{ stats.classCount }}</div>
              <div class="label">班级数量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <div class="icon"><el-icon><Warning /></el-icon></div>
            <div class="info">
              <div class="value">{{ stats.warningCount }}</div>
              <div class="label">待处理预警</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <div class="icon"><el-icon><Bell /></el-icon></div>
            <div class="info">
              <div class="value">{{ stats.noticeCount }}</div>
              <div class="label">通知公告</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>考勤趋势</template>
            <div ref="attendanceChartRef" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>成绩分布</template>
            <div ref="gradeChartRef" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>作业完成情况</template>
            <div ref="assignmentChartRef" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>学风指标</template>
            <div class="study-style">
              <div class="item">
                <span class="label">出勤率</span>
                <el-progress :percentage="studyStyle.attendanceRate" :stroke-width="12" />
              </div>
              <div class="item">
                <span class="label">作业完成率</span>
                <el-progress :percentage="studyStyle.assignmentRate" :stroke-width="12" status="success" />
              </div>
              <div class="item">
                <span class="label">平均成绩</span>
                <el-progress :percentage="studyStyle.averageScore" :stroke-width="12" status="warning" />
              </div>
              <div class="item">
                <span class="label">学风综合评分</span>
                <el-progress :percentage="studyStyle.studyStyleScore" :stroke-width="12" status="exception" />
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { User, School, Warning, Bell, Calendar, Check, Document, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard, getAttendanceTrend, getGradeDistribution, getAssignmentCompletion, getClassStudyStyle } from '@/api/statistics'
import { getStudentStatistics as getAttendanceStats } from '@/api/attendance'
import { getStudentStatistics as getGradeStats } from '@/api/grade'
import { getNoticeList } from '@/api/notice'
import { getCurrentStudent } from '@/api/student'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const isStudent = computed(() => userStore.roles?.includes('STUDENT'))

const stats = reactive({ studentCount: 0, classCount: 0, warningCount: 0, noticeCount: 0 })
const studyStyle = reactive({ attendanceRate: 0, assignmentRate: 0, averageScore: 0, studyStyleScore: 0 })
const myStats = reactive({ attendanceTotal: 0, attendanceRate: 0, totalCourses: 0, averageScore: 0, passRate: 0 })
const latestNotices = ref([])

const attendanceChartRef = ref()
const gradeChartRef = ref()
const assignmentChartRef = ref()

let attendanceChart = null
let gradeChart = null
let assignmentChart = null

const loadStudentData = async () => {
  try {
    const { data: student } = await getCurrentStudent()
    if (student) {
      const [attendanceRes, gradeRes] = await Promise.all([
        getAttendanceStats(student.id),
        getGradeStats(student.id)
      ])
      Object.assign(myStats, {
        attendanceTotal: attendanceRes.data?.total || 0,
        attendanceRate: attendanceRes.data?.attendanceRate || 0,
        totalCourses: gradeRes.data?.totalCourses || 0,
        averageScore: gradeRes.data?.averageScore || 0,
        passRate: gradeRes.data?.passRate || 0
      })
    }
  } catch (e) {
    console.error('加载学生数据失败', e)
  }
  
  try {
    const { data } = await getNoticeList({ pageNum: 1, pageSize: 5 })
    latestNotices.value = (data.records || data.list || []).slice(0, 5)
  } catch (e) {}
}

const loadAdminData = async () => {
  try {
    const { data } = await getDashboard()
    Object.assign(stats, data)
  } catch (e) {}
  
  try {
    const { data } = await getAttendanceTrend({})
    initAttendanceChart(data)
  } catch (e) {}
  
  try {
    const { data } = await getGradeDistribution()
    initGradeChart(data)
  } catch (e) {}
  
  try {
    const { data } = await getAssignmentCompletion()
    initAssignmentChart(data)
  } catch (e) {}
  
  try {
    const { data } = await getClassStudyStyle(1)
    Object.assign(studyStyle, data)
  } catch (e) {}
}

const initAttendanceChart = (data) => {
  if (!attendanceChartRef.value) return
  attendanceChart = echarts.init(attendanceChartRef.value)
  attendanceChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.dates || [] },
    yAxis: { type: 'value', max: 100 },
    series: [{
      name: '出勤率',
      type: 'line',
      smooth: true,
      data: data.rates || [],
      areaStyle: { opacity: 0.3 }
    }]
  })
}

const initGradeChart = (data) => {
  if (!gradeChartRef.value) return
  gradeChart = echarts.init(gradeChartRef.value)
  gradeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data.distribution || [],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
    }]
  })
}

const initAssignmentChart = (data) => {
  if (!assignmentChartRef.value) return
  assignmentChart = echarts.init(assignmentChartRef.value)
  assignmentChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: data.completed || 0, name: '已完成' },
        { value: data.notCompleted || 0, name: '未完成' }
      ]
    }]
  })
}

onMounted(() => {
  if (isStudent.value) {
    loadStudentData()
  } else {
    loadAdminData()
  }
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  attendanceChart?.dispose()
  gradeChart?.dispose()
  assignmentChart?.dispose()
})

const handleResize = () => {
  attendanceChart?.resize()
  gradeChart?.resize()
  assignmentChart?.resize()
}
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-cards {
    margin-bottom: 16px;
  }
  
  .stat-card {
    padding: 20px;
    border-radius: 8px;
    color: #fff;
    display: flex;
    align-items: center;
    gap: 16px;
    
    .icon {
      font-size: 40px;
      opacity: 0.8;
    }
    
    .info {
      .value {
        font-size: 28px;
        font-weight: 600;
      }
      .label {
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }
  
  .chart-row {
    margin-bottom: 16px;
  }
  
  .chart-card {
    .chart {
      height: 300px;
    }
  }
  
  .study-style, .my-study-overview {
    padding: 20px;
    
    .item {
      margin-bottom: 20px;
      
      .label {
        display: block;
        margin-bottom: 8px;
        color: #606266;
      }
    }
  }
  
  .notice-list {
    .notice-item {
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      align-items: center;
      gap: 8px;
      
      &:last-child {
        border-bottom: none;
      }
      
      .title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .time {
        color: #909399;
        font-size: 12px;
      }
    }
  }
}
</style>
