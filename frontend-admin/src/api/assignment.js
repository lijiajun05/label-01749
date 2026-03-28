import request from '@/utils/request'

export function getAssignmentList(params) {
  return request({
    url: '/assignments',
    method: 'get',
    params
  })
}

export function getAssignmentById(id) {
  return request({
    url: `/assignments/${id}`,
    method: 'get'
  })
}

export function createAssignment(data) {
  return request({
    url: '/assignments',
    method: 'post',
    data
  })
}

export function updateAssignment(id, data) {
  return request({
    url: `/assignments/${id}`,
    method: 'put',
    data
  })
}

export function deleteAssignment(id) {
  return request({
    url: `/assignments/${id}`,
    method: 'delete'
  })
}

export function getSubmits(assignmentId) {
  return request({
    url: `/assignments/${assignmentId}/submits`,
    method: 'get'
  })
}

export function recordSubmit(assignmentId, data) {
  return request({
    url: `/assignments/${assignmentId}/submits`,
    method: 'post',
    data
  })
}

export function updateSubmit(submitId, data) {
  return request({
    url: `/assignments/submits/${submitId}`,
    method: 'put',
    data
  })
}

export function getStudentAssignments(studentId) {
  return request({
    url: `/assignments/student/${studentId}`,
    method: 'get'
  })
}

export function getClassStatistics(classId) {
  return request({
    url: `/assignments/statistics/class/${classId}`,
    method: 'get'
  })
}
