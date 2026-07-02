<template>
  <div>
    <h2>车票销售</h2>
    <el-card>
      <el-form :model="saleForm" ref="saleFormRef" :rules="rules" label-width="120px" style="max-width: 600px;">
        <el-form-item label="选择车次" prop="trainId">
          <el-select v-model="saleForm.trainId" placeholder="请选择车次" @change="onTrainChange" style="width: 100%;">
            <el-option v-for="train in trainList" :key="train.id" :label="`${train.trainNumber} ${train.departureCity}->${train.arrivalCity} ${train.departureTime}`" :value="train.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="出发站点" prop="departureStationId">
          <el-select v-model="saleForm.departureStationId" placeholder="请选择出发站" @change="calcPrice" style="width: 100%;">
            <el-option v-for="s in stationList" :key="s.id" :label="s.stationName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="到达站点" prop="arrivalStationId">
          <el-select v-model="saleForm.arrivalStationId" placeholder="请选择到达站" @change="calcPrice" style="width: 100%;">
            <el-option v-for="s in stationList" :key="s.id" :label="s.stationName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="座位号" prop="seatNumber">
          <el-input v-model="saleForm.seatNumber" placeholder="如: 1A" />
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number v-model="saleForm.price" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="乘车日期" prop="saleDate">
          <el-date-picker v-model="saleForm.saleDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="乘客姓名" prop="passengerName">
          <el-input v-model="saleForm.passengerName" />
        </el-form-item>
        <el-form-item label="乘客身份证" prop="passengerIdCard">
          <el-input v-model="saleForm.passengerIdCard" />
        </el-form-item>
        <el-form-item label="售票员" prop="salespersonId">
          <el-select v-model="saleForm.salespersonId" placeholder="请选择售票员" style="width: 100%;">
            <el-option v-for="s in salespersonList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSale" :loading="saleLoading">售 票</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <h3>销售记录</h3>
      <el-table :data="ticketList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="trainNumber" label="车次" width="100" />
        <el-table-column prop="seatNumber" label="座位" width="80" />
        <el-table-column prop="passengerName" label="乘客" width="100" />
        <el-table-column prop="price" label="票价" width="100" />
        <el-table-column prop="saleDate" label="乘车日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '有效' : '已退票' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size" :total="pagination.total"
        layout="total, prev, pager, next" @current-change="loadTickets" style="margin-top: 15px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { trainApi, stationApi, salespersonApi, ticketApi } from '@/api'

const loading = ref(false)
const saleLoading = ref(false)
const trainList = ref([])
const stationList = ref([])
const salespersonList = ref([])
const ticketList = ref([])
const saleFormRef = ref(null)

const pagination = reactive({ page: 1, size: 10, total: 0 })
const saleForm = reactive({
  trainId: null, departureStationId: null, arrivalStationId: null,
  seatNumber: '', price: 0, saleDate: '', passengerName: '', passengerIdCard: '', salespersonId: null
})

const rules = {
  trainId: [{ required: true, message: '请选择车次', trigger: 'change' }],
  departureStationId: [{ required: true, message: '请选择出发站', trigger: 'change' }],
  arrivalStationId: [{ required: true, message: '请选择到达站', trigger: 'change' }],
  seatNumber: [{ required: true, message: '请输入座位号', trigger: 'blur' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }],
  saleDate: [{ required: true, message: '请选择乘车日期', trigger: 'change' }],
  passengerName: [{ required: true, message: '请输入乘客姓名', trigger: 'blur' }],
  passengerIdCard: [{ required: true, message: '请输入身份证', trigger: 'blur' }],
  salespersonId: [{ required: true, message: '请选择售票员', trigger: 'change' }]
}

const loadTrains = async () => {
  const res = await trainApi.getAll()
  if (res.data.success) trainList.value = res.data.data
}
const loadStations = async () => {
  const res = await stationApi.getAll()
  if (res.data.success) stationList.value = res.data.data
}
const loadSalespeople = async () => {
  const res = await salespersonApi.getAll()
  if (res.data.success) salespersonList.value = res.data.data.filter(s => s.status === 1)
}
const loadTickets = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.page, pageSize: pagination.size }
    const res = await ticketApi.getPage(params)
    if (res.data.success) { ticketList.value = res.data.data; pagination.total = res.data.total }
  } catch (e) { ElMessage.error('加载失败') } finally { loading.value = false }
}

const onTrainChange = async (trainId) => {
  saleForm.departureStationId = null
  saleForm.arrivalStationId = null
  saleForm.price = 0
}

const calcPrice = () => {
  if (saleForm.departureStationId && saleForm.arrivalStationId) {
    const dep = stationList.value.find(s => s.id === saleForm.departureStationId)
    const arr = stationList.value.find(s => s.id === saleForm.arrivalStationId)
    if (dep && arr) saleForm.price = Math.abs(arr.id - dep.id) * 50
  }
}

const handleSale = async () => {
  if (!saleFormRef.value) return
  await saleFormRef.value.validate(async (valid) => {
    if (!valid) return
    saleLoading.value = true
    try {
      const res = await ticketApi.sale(saleForm)
      if (res.data.success) {
        ElMessage.success('售票成功')
        saleFormRef.value.resetFields()
        loadTickets()
      } else {
        ElMessage.error(res.data.message || '售票失败')
      }
    } catch (e) { ElMessage.error('售票失败') } finally { saleLoading.value = false }
  })
}

onMounted(() => { loadTrains(); loadStations(); loadSalespeople(); loadTickets() })
</script>
