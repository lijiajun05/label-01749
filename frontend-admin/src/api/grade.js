import request from '@/utils/request'

export function getGradeList(params) {
  return request({
    url: '/grades',
    method: 'get',
    params
  })
}

export function getStudentGrades(studentId) {
  return request({
    url: `/grades/student/${studentId}`,
    method: 'get'
  })
}

export function getCourseGrades(courseId) {
  return request({
    url: `/grades/course/${courseId}`,
    method: 'get'
  })
}

export function createGrade(data) {
  return request({
    url: '/grades',
    method: 'post',
    data
  })
}

export function batchCreateGrade(data) {
  return request({
    url: '/grades/batch',
    method: 'post',
    data
  })
}

export function updateGrade(id, data) {
  return request({
    url: `/grades/${id}`,
    method: 'put',
    data
  })
}

export function deleteGrade(id) {
  return request({
    url: `/grades/${id}`,
    method: 'delete'
  })
}

export function getStudentStatistics(studentId) {
  return request({
    url: `/grades/statistics/student/${studentId}`,
    method: 'get'
  })
}

export function getClassStatistics(classId) {
  return request({
    url: `/grades/statistics/class/${classId}`,
    method: 'get'
  })
}

export function getCourseDistribution(courseId) {
  return request({
    url: `/grades/distribution/course/${courseId}`,
    method: 'get'
  })
}
