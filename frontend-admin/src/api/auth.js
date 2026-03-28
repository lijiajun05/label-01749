import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: {
      username: data.username,
      password: data.password
    }
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'put',
    data: {
      oldPassword: data.oldPassword,
      newPassword: data.newPassword
    }
  })
}
