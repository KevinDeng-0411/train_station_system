import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 车次管理
export const trainApi = {
  getAll: () => api.get('/trains'),
  getPage: (params) => api.get('/trains/page', { params }),
  getById: (id) => api.get(`/trains/${id}`),
  create: (data) => api.post('/trains', data),
  update: (id, data) => api.put(`/trains/${id}`, data),
  delete: (id) => api.delete(`/trains/${id}`)
}

// 站点管理
export const stationApi = {
  getAll: () => api.get('/stations'),
  getById: (id) => api.get(`/stations/${id}`),
  create: (data) => api.post('/stations', data),
  update: (id, data) => api.put(`/stations/${id}`, data),
  delete: (id) => api.delete(`/stations/${id}`)
}

// 业务员管理
export const salespersonApi = {
  getAll: () => api.get('/salespeople'),
  getPage: (params) => api.get('/salespeople/page', { params }),
  getById: (id) => api.get(`/salespeople/${id}`),
  create: (data) => api.post('/salespeople', data),
  update: (id, data) => api.put(`/salespeople/${id}`, data),
  delete: (id) => api.delete(`/salespeople/${id}`)
}

// 售票管理
export const ticketApi = {
  getPage: (params) => api.get('/tickets', { params }),
  getById: (id) => api.get(`/tickets/${id}`),
  sale: (data) => api.post('/tickets/sale', data),
  refund: (id, params) => api.post(`/tickets/${id}/refund`, null, { params }),
  checkSeat: (params) => api.get('/tickets/check-seat', { params })
}

// 统计报表
export const statisticsApi = {
  getTrainSales: (trainNumber, date) => api.get(`/statistics/trains/${trainNumber}/date/${date}`),
  getSalespersonRevenue: (date) => api.get(`/statistics/salespeople/date/${date}`)
}

// 备份恢复
export const backupApi = {
  create: () => api.post('/backup'),
  getAll: () => api.get('/backup'),
  restore: (id) => api.post(`/backup/${id}/restore`),
  delete: (id) => api.delete(`/backup/${id}`)
}

export default api
