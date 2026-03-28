import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: true
    },
    device: 'desktop'
  }),
  
  actions: {
    toggleSidebar() {
      this.sidebar.opened = !this.sidebar.opened
    },
    
    closeSidebar() {
      this.sidebar.opened = false
    },
    
    setDevice(device) {
      this.device = device
    }
  }
})
