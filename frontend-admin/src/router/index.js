import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const Layout = () => import('@/layout/index.vue')

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true
  }
]

export const asyncRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'dashboard' }
      }
    ]
  },
  {
    path: '/class',
    component: Layout,
    redirect: '/class/list',
    meta: { title: '班级管理', icon: 'class', roles: ['ADMIN', 'CLASS_TEACHER'] },
    children: [
      {
        path: 'list',
        name: 'ClassList',
        component: () => import('@/views/class/index.vue'),
        meta: { title: '班级管理', roles: ['ADMIN', 'CLASS_TEACHER'] }
      },
      {
        path: 'detail/:id',
        name: 'ClassDetail',
        component: () => import('@/views/class/detail.vue'),
        meta: { title: '班级详情', roles: ['ADMIN', 'CLASS_TEACHER'] },
        hidden: true
      },
      {
        path: 'committee',
        name: 'Committee',
        component: () => import('@/views/class/committee.vue'),
        meta: { title: '班委管理', roles: ['ADMIN', 'CLASS_TEACHER'] }
      },
      {
        path: 'meeting',
        name: 'Meeting',
        component: () => import('@/views/class/meeting.vue'),
        meta: { title: '班会管理', roles: ['ADMIN', 'CLASS_TEACHER'] }
      }
    ]
  },
  {
    path: '/student',
    component: Layout,
    redirect: '/student/list',
    meta: { title: '学生管理', icon: 'student', roles: ['ADMIN', 'CLASS_TEACHER'] },
    children: [
      {
        path: 'list',
        name: 'StudentList',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学生管理', roles: ['ADMIN', 'CLASS_TEACHER'] }
      }
    ]
  },
  {
    path: '/course',
    component: Layout,
    redirect: '/course/list',
    meta: { title: '课程管理', icon: 'course', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER'] },
    children: [
      {
        path: 'list',
        name: 'CourseList',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '课程管理', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER'] }
      }
    ]
  },
  {
    path: '/study',
    component: Layout,
    redirect: '/study/attendance',
    meta: { title: '学风管理', icon: 'study', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER'] },
    children: [
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/study/attendance/index.vue'),
        meta: { title: '考勤管理', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER'] }
      },
      {
        path: 'assignment',
        name: 'Assignment',
        component: () => import('@/views/study/assignment/index.vue'),
        meta: { title: '作业管理', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER'] }
      },
      {
        path: 'grade',
        name: 'Grade',
        component: () => import('@/views/study/grade/index.vue'),
        meta: { title: '成绩管理', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER'] }
      },
      {
        path: 'analysis',
        name: 'Analysis',
        component: () => import('@/views/study/analysis/index.vue'),
        meta: { title: '学风分析', roles: ['ADMIN', 'CLASS_TEACHER'] }
      }
    ]
  },
  // 学生专属 - 我的学业
  {
    path: '/my-study',
    component: Layout,
    redirect: '/my-study/attendance',
    meta: { title: '我的学业', icon: 'study', roles: ['STUDENT'] },
    children: [
      {
        path: 'attendance',
        name: 'MyAttendance',
        component: () => import('@/views/my-study/attendance.vue'),
        meta: { title: '我的考勤', roles: ['STUDENT'] }
      },
      {
        path: 'assignment',
        name: 'MyAssignment',
        component: () => import('@/views/my-study/assignment.vue'),
        meta: { title: '我的作业', roles: ['STUDENT'] }
      },
      {
        path: 'grade',
        name: 'MyGrade',
        component: () => import('@/views/my-study/grade.vue'),
        meta: { title: '我的成绩', roles: ['STUDENT'] }
      }
    ]
  },
  {
    path: '/warning',
    component: Layout,
    redirect: '/warning/list',
    meta: { title: '预警管理', icon: 'warning', roles: ['ADMIN', 'CLASS_TEACHER'] },
    children: [
      {
        path: 'list',
        name: 'WarningList',
        component: () => import('@/views/warning/index.vue'),
        meta: { title: '预警管理', roles: ['ADMIN', 'CLASS_TEACHER'] }
      }
    ]
  },
  {
    path: '/notice',
    component: Layout,
    redirect: '/notice/list',
    meta: { title: '通知管理', icon: 'notice', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER', 'STUDENT'] },
    children: [
      {
        path: 'list',
        name: 'NoticeList',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '通知管理', roles: ['ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER', 'STUDENT'] }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'system', roles: ['ADMIN'] },
    children: [
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'log',
        name: 'LogManage',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', roles: ['ADMIN'] }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/404', hidden: true }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
