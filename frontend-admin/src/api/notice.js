import request from '@/utils/request'

export function getNoticeList(params) {
  return request({
    url: '/notices',
    method: 'get',
    params
  })
}

export function getNoticeById(id) {
  return request({
    url: `/notices/${id}`,
    method: 'get'
  })
}

export function createNotice(data) {
  return request({
    url: '/notices',
    method: 'post',
    data
  })
}

export function updateNotice(id, data) {
  return request({
    url: `/notices/${id}`,
    method: 'put',
    data
  })
}

export function deleteNotice(id) {
  return request({
    url: `/notices/${id}`,
    method: 'delete'
  })
}

export function setTop(id, isTop) {
  return request({
    url: `/notices/${id}/top`,
    method: 'put',
    params: { isTop }
  })
}

export function withdrawNotice(id) {
  return request({
    url: `/notices/${id}/withdraw`,
    method: 'put'
  })
}

export function markAsRead(id) {
  return request({
    url: `/notices/${id}/read`,
    method: 'post'
  })
}

export function getReadStatus(id) {
  return request({
    url: `/notices/${id}/read-status`,
    method: 'get'
  })
}

export function getUnreadCount() {
  return request({
    url: '/notices/unread-count',
    method: 'get'
  })
}
