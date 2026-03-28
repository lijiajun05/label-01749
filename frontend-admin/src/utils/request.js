import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 200) {
      // 401: 未授权，非登录页面需要跳转
      if (res.code === 401) {
        const isLoginPage = router.currentRoute.value.path === '/login'
        if (!isLoginPage) {
          removeToken()
          router.push('/login')
        }
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '请求失败'
    
    if (error.response) {
      const { status, data } = error.response
      message = data?.message || '请求失败'
      
      if (status === 401) {
        const isLoginPage = router.currentRoute.value.path === '/login'
        if (!isLoginPage) {
          removeToken()
          router.push('/login')
        }
      } else if (status === 403) {
        message = data?.message || '无权限访问'
      }
    } else {
      message = '网络错误，请检查网络连接'
    }
    
    return Promise.reject(new Error(message))
  }
)

export default service
