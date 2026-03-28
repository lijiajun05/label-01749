import request from '@/utils/request'

export function getDashboard(classId) {
  return request({
    url: '/statistics/dashboard',
    method: 'get',
    params: { classId }
  })
}

export function getAttendanceTrend(params) {
  return request({
    url: '/statistics/attendance/trend',
    method: 'get',
    params
  })
}

export function getGradeDistribution(classId) {
  return request({
    url: '/statistics/grade/distribution',
    method: 'get',
    params: { classId }
  })
}

export function getAssignmentCompletion(classId) {
  return request({
    url: '/statistics/assignment/completion',
    method: 'get',
    params: { classId }
  })
}

export function getClassStudyStyle(classId) {
  return request({
    url: `/statistics/study-style/class/${classId}`,
    method: 'get'
  })
}

export function getStudentStudyStyle(studentId) {
  return request({
    url: `/statistics/study-style/student/${studentId}`,
    method: 'get'
  })
}
