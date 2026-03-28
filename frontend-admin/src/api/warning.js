import request from '@/utils/request'

export function getWarningList(params) {
  return request({
    url: '/warnings',
    method: 'get',
    params
  })
}

export function getWarningById(id) {
  return request({
    url: `/warnings/${id}`,
    method: 'get'
  })
}

export function getStudentWarnings(studentId) {
  return request({
    url: `/warnings/student/${studentId}`,
    method: 'get'
  })
}

export function getClassWarnings(classId) {
  return request({
    url: `/warnings/class/${classId}`,
    method: 'get'
  })
}

export function handleWarning(id, data) {
  return request({
    url: `/warnings/${id}/handle`,
    method: 'put',
    data
  })
}

export function generateWarnings() {
  return request({
    url: '/warnings/generate',
    method: 'post'
  })
}

export function getClassStatistics(classId) {
  return request({
    url: `/warnings/statistics/class/${classId}`,
    method: 'get'
  })
}
