import request from '@/utils/request'

export function getAttendanceList(params) {
  return request({
    url: '/attendances',
    method: 'get',
    params
  })
}

export function getStudentAttendance(studentId) {
  return request({
    url: `/attendances/student/${studentId}`,
    method: 'get'
  })
}

export function getCourseAttendance(courseId) {
  return request({
    url: `/attendances/course/${courseId}`,
    method: 'get'
  })
}

export function createAttendance(data) {
  return request({
    url: '/attendances',
    method: 'post',
    data
  })
}

export function batchCreateAttendance(data) {
  return request({
    url: '/attendances/batch',
    method: 'post',
    data
  })
}

export function updateAttendance(id, data) {
  return request({
    url: `/attendances/${id}`,
    method: 'put',
    data
  })
}

export function deleteAttendance(id) {
  return request({
    url: `/attendances/${id}`,
    method: 'delete'
  })
}

export function getStudentStatistics(studentId) {
  return request({
    url: `/attendances/statistics/student/${studentId}`,
    method: 'get'
  })
}

export function getClassStatistics(classId) {
  return request({
    url: `/attendances/statistics/class/${classId}`,
    method: 'get'
  })
}
