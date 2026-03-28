<template>
  <div class="analysis">
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="班级">
          <el-select v-model="classId" placeholder="请选择班级" @change="loadData">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header>学风综合指标</template>
          <div class="study-style">
            <div class="item">
              <span class="label">出勤率</span>
              <el-progress :percentage="studyStyle.attendanceRate" :stroke-width="16" />
            </div>
            <div class="item">
              <span class="label">作业完成率</span>
              <el-progress :percentage="studyStyle.assignmentRate" :stroke-width="16" status="success" />
            </div>
            <div class="item">
              <span class="label">平均成绩</span>
              <el-progress :percentage="studyStyle.averageScore" :stroke-width="16" status="warning" />
            </div>
            <div class="item highlight">
              <span class="label">学风综合评分</span>
              <div class="score">{{ studyStyle.studyStyleScore.toFixed(1) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>成绩分布</template>
          <div ref="gradeChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card>
          <template #header>考勤趋势</template>
          <div ref="attendanceChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>作业完成情况</template>
          <div ref="assignmentChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getAllClasses } from '@/api/class'
import { getClassStudyStyle, getAttendanceTrend, getGradeDistribution, getAssignmentCompletion } from '@/api/statistics'

const classList = ref([])
const classId = ref(null)
const studyStyle = reactive({ attendanceRate: 0, assignmentRate: 0, averageScore: 0, studyStyleScore: 0 })

const gradeChartRef = ref()
const attendanceChartRef = ref()
const assignmentChartRef = ref()
let gradeChart = null
let attendanceChart = null
let assignmentChart = null

const loadClasses = async () => {
  const { data } = await getAllClasses()
  classList.value = Array.isArray(data) ? data : (data.records || data.list || [])
  if (classList.value.length > 0) {
    classId.value = classList.value[0].id
    loadData()
  }
}

const loadData = async () => {
  if (!classId.value) return
  
  try {
    const { data } = await getClassStudyStyle(classId.value)
    Object.assign(studyStyle, data)
  } catch (e) {}
  
  try {
    const { data } = await getAttendanceTrend({ classId: classId.value })
    initAttendanceChart(data)
  } catch (e) {}
  
  try {
    const { data } = await getGradeDistribution(classId.value)
    initGradeChart(data)
  } catch (e) {}
  
  try {
    const { data } = await getAssignmentCompletion(classId.value)
    initAssignmentChart(data)
  } catch (e) {}
}

const initAttendanceChart = (data) => {
  if (!attendanceChartRef.value) return
  if (!attendanceChart) attendanceChart = echarts.init(attendanceChartRef.value)
  attendanceChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.dates || [] },
    yAxis: { type: 'value', max: 100 },
    series: [{ name: '出勤率', type: 'line', smooth: true, data: data.rates || [], areaStyle: { opacity: 0.3 } }]
  })
}

const initGradeChart = (data) => {
  if (!gradeChartRef.value) return
  if (!gradeChart) gradeChart = echarts.init(gradeChartRef.value)
  gradeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{ type: 'pie', radius: ['40%', '70%'], data: data.distribution || [] }]
  })
}

const initAssignmentChart = (data) => {
  if (!assignmentChartRef.value) return
  if (!assignmentChart) assignmentChart = echarts.init(assignmentChartRef.value)
  assignmentChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: '60%',
      data: [{ value: data.completed || 0, name: '已完成' }, { value: data.notCompleted || 0, name: '未完成' }]
    }]
  })
}

onMounted(() => {
  loadClasses()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  gradeChart?.dispose()
  attendanceChart?.dispose()
  assignmentChart?.dispose()
})

const handleResize = () => {
  gradeChart?.resize()
  attendanceChart?.resize()
  assignmentChart?.resize()
}
</script>

<style lang="scss" scoped>
.filter-card { margin-bottom: 16px; }
.chart { height: 300px; }
.study-style {
  .item {
    margin-bottom: 20px;
    .label { display: block; margin-bottom: 8px; color: #606266; }
    &.highlight {
      text-align: center;
      padding-top: 20px;
      .score { font-size: 48px; font-weight: 600; color: #409EFF; }
    }
  }
}
</style>
