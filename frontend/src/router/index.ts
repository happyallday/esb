import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'
import LogList from '@/views/LogList.vue'
import LogDetail from '@/views/LogDetail.vue'
import InterfaceTest from '@/views/InterfaceTest.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/test',
    name: 'InterfaceTest',
    component: InterfaceTest
  },
  {
    path: '/logs',
    name: 'LogList',
    component: LogList
  },
  {
    path: '/logs/:id',
    name: 'LogDetail',
    component: LogDetail
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router