import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null,
    roles: []
  }),
  
  actions: {
    async login(loginForm) {
      const { data } = await login(loginForm)
      this.token = data.token
      setToken(data.token)
      return data
    },
    
    async getUserInfo() {
      const { data } = await getUserInfo()
      this.userInfo = data
      this.roles = [data.roleCode]
      return data
    },
    
    async logout() {
      try {
        await logout()
      } catch (e) {
        // ignore
      }
      this.token = ''
      this.userInfo = null
      this.roles = []
      removeToken()
    },
    
    resetToken() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      removeToken()
    }
  }
})
