import { useUserStore } from '@/store/modules/user'

// 角色常量 - 与后端数据库中的role_code一致
export const ROLES = {
  ADMIN: 'ADMIN',
  TEACHER: 'CLASS_TEACHER',
  COURSE_TEACHER: 'COURSE_TEACHER',
  STUDENT: 'STUDENT'
}

// 权限配置
export const PERMISSIONS = {
  // 班级管理
  class: {
    view: [ROLES.ADMIN, ROLES.TEACHER],
    edit: [ROLES.ADMIN, ROLES.TEACHER]
  },
  // 学生管理
  student: {
    view: [ROLES.ADMIN, ROLES.TEACHER],
    add: [ROLES.ADMIN],
    edit: [ROLES.ADMIN, ROLES.TEACHER]
  },
  // 课程管理
  course: {
    view: [ROLES.ADMIN, ROLES.TEACHER, ROLES.COURSE_TEACHER],
    edit: [ROLES.ADMIN]
  },
  // 考勤管理
  attendance: {
    view: [ROLES.ADMIN, ROLES.TEACHER, ROLES.COURSE_TEACHER, ROLES.STUDENT],
    edit: [ROLES.ADMIN, ROLES.COURSE_TEACHER]
  },
  // 作业管理
  assignment: {
    view: [ROLES.ADMIN, ROLES.TEACHER, ROLES.COURSE_TEACHER, ROLES.STUDENT],
    edit: [ROLES.ADMIN, ROLES.COURSE_TEACHER]
  },
  // 成绩管理
  grade: {
    view: [ROLES.ADMIN, ROLES.TEACHER, ROLES.COURSE_TEACHER, ROLES.STUDENT],
    edit: [ROLES.ADMIN, ROLES.COURSE_TEACHER]
  },
  // 预警管理
  warning: {
    view: [ROLES.ADMIN, ROLES.TEACHER],
    edit: [ROLES.ADMIN, ROLES.TEACHER]
  },
  // 通知管理
  notice: {
    view: [ROLES.ADMIN, ROLES.TEACHER, ROLES.COURSE_TEACHER, ROLES.STUDENT],
    edit: [ROLES.ADMIN, ROLES.TEACHER]
  },
  // 系统管理
  system: {
    view: [ROLES.ADMIN],
    edit: [ROLES.ADMIN]
  }
}

// 检查是否有权限
export function hasPermission(module, action = 'view') {
  const userStore = useUserStore()
  const roles = userStore.roles || []
  const allowedRoles = PERMISSIONS[module]?.[action] || []
  return roles.some(role => allowedRoles.includes(role))
}

// 检查是否有任一角色
export function hasRole(roleList) {
  const userStore = useUserStore()
  const roles = userStore.roles || []
  return roles.some(role => roleList.includes(role))
}

// 是否是管理员
export function isAdmin() {
  return hasRole([ROLES.ADMIN])
}

// 是否是班主任
export function isTeacher() {
  return hasRole([ROLES.TEACHER])
}

// 是否是任课教师
export function isCourseTeacher() {
  return hasRole([ROLES.COURSE_TEACHER])
}

// 是否是学生
export function isStudent() {
  return hasRole([ROLES.STUDENT])
}
