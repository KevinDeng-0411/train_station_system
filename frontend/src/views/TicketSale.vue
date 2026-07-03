<template>
  <div>
    <h2>车票销售</h2>
    <el-card>
      <el-form :model="saleForm" ref="saleFormRef" :rules="rules" label-width="120px" style="max-width: 600px;">
        <el-form-item label="选择车次" prop="trainId">
          <el-select
            v-model="saleForm.trainId"
            placeholder="请选择车次"
            @change="onTrainChange"
            style="width: 100%;"
            filterable
          >
            <el-option
              v-for="train in trainList"
              :key="train.id"
              :label="`${train.trainNumber}  ${train.departureCity} → ${train.arrivalCity}  (余票 ${train.remainingSeats}/${train.totalSeats})  ${trainStatusLabel(train.status)}`"
              :value="train.id"
              :disabled="train.status === 0"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出发站点" prop="departureStationId">
          <el-select
            v-model="saleForm.departureStationId"
            placeholder="请先选择车次"
            @change="onDepartureChange"
            style="width: 100%;"
            :disabled="!saleForm.trainId || loadingStations"
          >
            <el-option
              v-for="s in trainStations"
              :key="s.stationId"
              :label="`${s.stopOrder}. ${s.stationName}（${s.city}）`"
              :value="s.stationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="到达站点" prop="arrivalStationId">
          <el-select
            v-model="saleForm.arrivalStationId"
            placeholder="请选择到达站"
            @change="calcPrice"
            style="width: 100%;"
            :disabled="!saleForm.departureStationId"
          >
            <el-option
              v-for="s in availableArrivalStations"
              :key="s.stationId"
              :label="`${s.stopOrder}. ${s.stationName}（${s.city}）¥${s.price}`"
              :value="s.stationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number v-model="saleForm.price" :min="0" :precision="2" style="width: 100%;" />
          <span style="margin-left: 10px; color: #64748B; font-size: 12px;" v-if="priceFromDb">
            📌 来源: <code>train_stations.price</code>
          </span>
        </el-form-item>
        <el-form-item label="乘车日期" prop="saleDate">
          <el-date-picker v-model="saleForm.saleDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="乘客姓名" prop="passengerName">
          <el-input v-model="saleForm.passengerName" />
        </el-form-item>
        <el-form-item label="乘客身份证" prop="passengerIdCard">
          <el-input v-model="saleForm.passengerIdCard" placeholder="18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="售票员" prop="salespersonId">
          <el-select v-model="saleForm.salespersonId" placeholder="请选择售票员" style="width: 100%;">
            <el-option v-for="s in salespersonList" :key="s.id" :label="`${s.name}（${s.employeeCode}）`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSale" :loading="saleLoading">售 票</el-button>
          <el-button @click="resetForm">重 置</el-button>
        </el-form-item>
      </el-form>

      <!-- 座位图 -->
      <el-divider v-if="saleForm.trainId && saleForm.saleDate" />
      <SeatMap
        v-if="saleForm.trainId && saleForm.saleDate"
        :train="selectedTrain"
        :sale-date="saleForm.saleDate"
        :selected-seat="saleForm.seatNumber"
        @select-seat="onSeatSelected"
      />
    </el-card>

    <el-card style="margin-top: 20px;">
      <div class="card-header">
        <h3>销售记录</h3>
        <el-button size="small" @click="loadTickets">刷新</el-button>
      </div>
      <el-table :data="ticketList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="车次" width="120">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" round>{{ row.trainNumber || 'N/A' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="出发站→到达站" min-width="200">
          <template #default="{ row }">
            <span style="color: var(--color-text);">{{ row.departureStationName || 'N/A' }}</span>
            <span style="color: var(--color-primary); margin: 0 6px;">→</span>
            <span style="color: var(--color-text);">{{ row.arrivalStationName || 'N/A' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="seatNumber" label="座位" width="80" />
        <el-table-column prop="passengerName" label="乘客" width="100" />
        <el-table-column prop="salespersonName" label="售票员" width="100" />
        <el-table-column prop="price" label="票价" width="100">
          <template #default="{ row }">
            <span style="font-weight: 600; color: #0891B2;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { trainApi, ticketApi } from '@/api'
import axios from 'axios'
import SeatMap from '@/components/chart/SeatMap.vue'

const loading = ref(false)
const loadingStations = ref(false)
const saleLoading = ref(false)
const trainList = ref([])
const trainStations = ref([])  // 该车次经停的站点（含价格）
const salespersonList = ref([])
const ticketList = ref([])
const saleFormRef = ref(null)
const priceFromDb = ref(false)

const pagination = reactive({ page: 1, size: 10, total: 0 })
const saleForm = reactive({
  trainId: null, departureStationId: null, arrivalStationId: null,
  seatNumber: '', price: 0, saleDate: '',
  passengerName: '', passengerIdCard: '', salespersonId: null
})

const rules = {
  trainId: [{ required: true, message: '请选择车次', trigger: 'change' }],
  departureStationId: [{ required: true, message: '请选择出发站', trigger: 'change' }],
  arrivalStationId: [{ required: true, message: '请选择到达站', trigger: 'change' }],
  seatNumber: [{ required: true, message: '请输入座位号', trigger: 'blur' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }],
  saleDate: [{ required: true, message: '请选择乘车日期', trigger: 'change' }],
  passengerName: [{ required: true, message: '请输入乘客姓名', trigger: 'blur' }],
  passengerIdCard: [
    { required: true, message: '请输入身份证', trigger: 'blur' },
    { pattern: /^[1-9]\d{17}$/, message: '身份证号格式错误（18位）', trigger: 'blur' }
  ],
  salespersonId: [{ required: true, message: '请选择售票员', trigger: 'change' }]
}

// 车次状态显示
const trainStatusLabel = (status) => status === 0 ? '🛑 停运' : '✅ 正常'

// 加载车次
const loadTrains = async () => {
  const res = await trainApi.getAll()
  if (res.data.success) trainList.value = res.data.data
}

// 加载该车次的经停站点（含价格）
const loadTrainStations = async (trainId) => {
  if (!trainId) {
    trainStations.value = []
    return
  }
  loadingStations.value = true
  try {
    const res = await axios.get(`/api/trains/${trainId}/stations`)
    if (res.data.success) {
      trainStations.value = res.data.data
    } else {
      trainStations.value = []
    }
  } catch (e) {
    trainStations.value = []
  } finally {
    loadingStations.value = false
  }
}

// 加载售票员
const loadSalespeople = async () => {
  const res = await axios.get('/api/salespeople')
  if (res.data.success) {
    salespersonList.value = (res.data.data || []).filter(s => s.status === 1)
  }
}

// 加载销售记录
const loadTickets = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.page, pageSize: pagination.size }
    const res = await ticketApi.getPage(params)
    if (res.data.success) {
      ticketList.value = res.data.data
      pagination.total = res.data.total
    }
  } catch (e) { ElMessage.error('加载失败') } finally { loading.value = false }
}

// 车次变化：清空站点选择 + 加载该车次经停的站
const onTrainChange = async (trainId) => {
  saleForm.departureStationId = null
  saleForm.arrivalStationId = null
  saleForm.price = 0
  priceFromDb.value = false
  await loadTrainStations(trainId)
}

// 出发站变化：清空到达站
const onDepartureChange = () => {
  saleForm.arrivalStationId = null
  saleForm.price = 0
  priceFromDb.value = false
}

// 根据出发站计算可选的到达站（只有stop_order大于出发站的）
const availableArrivalStations = computed(() => {
  if (!saleForm.departureStationId) return []
  const dep = trainStations.value.find(s => s.stationId === saleForm.departureStationId)
  if (!dep) return []
  return trainStations.value.filter(s => s.stopOrder > dep.stopOrder)
})

// 到达站变化：从价格表计算（到达站价格 - 出发站价格）
const calcPrice = () => {
  if (saleForm.arrivalStationId && saleForm.departureStationId) {
    const dep = trainStations.value.find(s => s.stationId === saleForm.departureStationId)
    const arr = trainStations.value.find(s => s.stationId === saleForm.arrivalStationId)
    if (dep && arr) {
      const segmentPrice = Number(arr.price || 0) - Number(dep.price || 0)
      saleForm.price = segmentPrice > 0 ? segmentPrice : 0
      priceFromDb.value = true
    }
  }
}

// 当前选中的车次对象
const selectedTrain = computed(() => {
  if (!saleForm.trainId) return null
  return trainList.value.find(t => t.id === saleForm.trainId) || null
})

// 座位图选择回调
const onSeatSelected = (seat) => {
  saleForm.seatNumber = seat
}

// 重置
const resetForm = () => {
  saleFormRef.value?.resetFields()
  saleForm.departureStationId = null
  saleForm.arrivalStationId = null
  saleForm.trainId = null
  saleForm.price = 0
  priceFromDb.value = false
  trainStations.value = []
}

const handleSale = async () => {
  if (!saleFormRef.value) return
  await saleFormRef.value.validate(async (valid) => {
    if (!valid) return
    saleLoading.value = true
    try {
      const res = await ticketApi.sale(saleForm)
      if (res.data.success) {
        await ElMessageBox.alert(`售票成功！车次 ${res.data.data.trainNumber || ''} 座位 ${res.data.data.seatNumber}`, '成功', { type: 'success' })
        resetForm()
        loadTrains()  // 刷新余票
        loadTickets()
      } else {
        ElMessage.error(res.data.message || '售票失败')
      }
    } catch (e) { ElMessage.error('售票失败') } finally { saleLoading.value = false }
  })
}

onMounted(async () => {
  await Promise.all([loadTrains(), loadSalespeople(), loadTickets()])
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.card-header h3 {
  margin: 0;
}
code {
  background: #F1F5F9;
  color: #0891B2;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}
</style>
