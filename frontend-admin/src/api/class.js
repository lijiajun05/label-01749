import request from '@/utils/request'

export function getClassList(params) {
  return request({
    url: '/classes',
    method: 'get',
    params
  })
}

export function getAllClasses() {
  return request({
    url: '/classes/all',
    method: 'get'
  })
}

export function getClassById(id) {
  return request({
    url: `/classes/${id}`,
    method: 'get'
  })
}

export function createClass(data) {
  return request({
    url: '/classes',
    method: 'post',
    data
  })
}

export function updateClass(id, data) {
  return request({
    url: `/classes/${id}`,
    method: 'put',
    data
  })
}

export function deleteClass(id) {
  return request({
    url: `/classes/${id}`,
    method: 'delete'
  })
}

export function getClassStudents(id) {
  return request({
    url: `/classes/${id}/students`,
    method: 'get'
  })
}

export function getClassContacts(id) {
  return request({
    url: `/classes/${id}/contacts`,
    method: 'get'
  })
}

// 班委管理
export function getAllCommittees() {
  return request({
    url: '/committees',
    method: 'get'
  })
}

export function getCommittees(classId) {
  return request({
    url: `/classes/${classId}/committees`,
    method: 'get'
  })
}

export function createCommittee(classId, data) {
  return request({
    url: `/classes/${classId}/committees`,
    method: 'post',
    data
  })
}

export function updateCommittee(id, data) {
  return request({
    url: `/committees/${id}`,
    method: 'put',
    data
  })
}

export function deleteCommittee(id) {
  return request({
    url: `/committees/${id}`,
    method: 'delete'
  })
}

// 班会记录
export function getAllMeetings(params) {
  return request({
    url: '/meetings',
    method: 'get',
    params
  })
}

export function getMeetings(classId, params) {
  return request({
    url: `/classes/${classId}/meetings`,
    method: 'get',
    params
  })
}

export function getMeetingById(id) {
  return request({
    url: `/meetings/${id}`,
    method: 'get'
  })
}

export function createMeeting(classId, data) {
  return request({
    url: `/classes/${classId}/meetings`,
    method: 'post',
    data
  })
}

export function updateMeeting(id, data) {
  return request({
    url: `/meetings/${id}`,
    method: 'put',
    data
  })
}

export function deleteMeeting(id) {
  return request({
    url: `/meetings/${id}`,
    method: 'delete'
  })
}
