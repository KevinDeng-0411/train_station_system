import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/trains'
  },
  {
    path: '/trains',
    name: 'TrainManage',
    component: () => import('../views/TrainManage.vue')
  },
  {
    path: '/stations',
    name: 'StationManage',
    component: () => import('../views/StationManage.vue')
  },
  {
    path: '/salespeople',
    name: 'SalespersonManage',
    component: () => import('../views/SalespersonManage.vue')
  },
  {
    path: '/tickets',
    name: 'TicketSale',
    component: () => import('../views/TicketSale.vue')
  },
  {
    path: '/tickets/refund',
    name: 'TicketRefund',
    component: () => import('../views/TicketRefund.vue')
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('../views/Statistics.vue')
  },
  {
    path: '/backup',
    name: 'Backup',
    component: () => import('../views/Backup.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
