<template>
  <div>
    <h2>退票管理</h2>
    <el-card>
      <el-form :model="refundForm" ref="refundFormRef" :rules="rules" label-width="100px" style="max-width: 500px;">
        <el-form-item label="车票ID" prop="ticketId">
          <el-input-number v-model="refundForm.ticketId" :min="1" style="width: 200px;" :disabled="!!presetTicketId" />
        </el-form-item>
        <el-form-item label="操作员" prop="operatorId">
          <el-select v-model="refundForm.operatorId" placeholder="请选择操作员" class="w-full">
            <el-option v-for="s in salespersonList" :key="s.id" :label="`${s.name}（${s.employeeCode}）`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="退票原因" prop="reason">
          <el-input v-model="refundForm.reason" type="textarea" :rows="2" placeholder="请说明退票原因" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleRefund" :loading="loading">退 票</el-button>
          <el-button @click="resetForm" v-if="presetTicketId">清空选择</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt-md">
      <div class="card-header">
        <h3>有效车票列表（点击行可快速退票）</h3>
        <el-button size="small" @click="loadTickets">刷新</el-button>
      </div>
      <el-table
        :data="ticketList"
        border
        stripe
        v-loading="loadingTickets"
        @row-click="onRowClick"
        row-style="cursor: pointer;"
        empty-text="暂无有效车票"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="车次" width="100">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" round>{{ row.trainNumber || 'N/A' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="出发→到达" min-width="200">
          <template #default="{ row }">
            <span>{{ row.departureStationName }}</span>
            <span class="route-arrow">→</span>
            <span>{{ row.arrivalStationName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="seatNumber" label="座位" width="70" />
        <el-table-column prop="passengerName" label="乘客" width="100" />
        <el-table-column prop="price" label="票价" width="90">
          <template #default="{ row }">
            <span class="text-money">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="salespersonName" label="售票员" width="100" />
        <el-table-column prop="saleDate" label="乘车日期" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              type="danger"
              @click.stop="quickRefund(row)"
            >
              退票
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { ticketApi, salespersonApi } from '@/api'

const loading = ref(false)
const loadingTickets = ref(false)
const salespersonList = ref([])
const ticketList = ref([])
const refundFormRef = ref(null)
const presetTicketId = ref(false)  // 是否从列表预设了ticketId

const refundForm = reactive({
  ticketId: null, operatorId: null, reason: ''
})

const rules = {
  ticketId: [{ required: true, message: '请选择要退的车票', trigger: 'change' }],
  operatorId: [{ required: true, message: '请选择操作员', trigger: 'change' }]
}

const loadSalespeople = async () => {
  const res = await salespersonApi.getAll()
  if (res.data.success) {
    salespersonList.value = (res.data.data || []).filter(s => s.status === 1)
  }
}

// 加载有效车票（status=1）
const loadTickets = async () => {
  loadingTickets.value = true
  try {
    const res = await ticketApi.getPage({ pageNum: 1, pageSize: 100 })
    if (res.data.success) {
      ticketList.value = (res.data.data || []).filter(t => t.status === 1)
    }
  } catch (e) { ElMessage.error('加载失败') } finally { loadingTickets.value = false }
}

// 点击行：填入ticketId
const onRowClick = (row) => {
  refundForm.ticketId = row.id
  presetTicketId.value = true
  ElMessage.info(`已选中车票 #${row.id}，请选择操作员后点击"退票"`)
}

// 快速退票按钮：直接弹确认
const quickRefund = async (row) => {
  refundForm.ticketId = row.id
  presetTicketId.value = true
  await confirmAndRefund(row)
}

// 确认弹窗
const confirmAndRefund = async (row) => {
  if (!refundForm.operatorId) {
    ElMessage.warning('请先在表单中选择操作员')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认退票吗？\n车票 #${row.id} | 乘客 ${row.passengerName} | 票价 ¥${row.price}`,
      '退票确认',
      { type: 'warning', confirmButtonText: '确认退票', cancelButtonText: '取消' }
    )
    await doRefund(row)
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

// 执行退票
const doRefund = async (row) => {
  loading.value = true
  try {
    const res = await ticketApi.refund(row.id, {
      operatorId: refundForm.operatorId,
      reason: refundForm.reason || '无'
    })
    if (res.data.success) {
      ElMessage.success(`退票成功！退款 ¥${row.price}`)
      resetForm()
      loadTickets()
    } else {
      ElMessage.error(res.data.message || '退票失败')
    }
  } catch (e) { ElMessage.error('退票失败') } finally { loading.value = false }
}

const handleRefund = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (!valid) return
    const row = ticketList.value.find(t => t.id === refundForm.ticketId)
    if (!row) {
      ElMessage.error('未找到车票信息')
      return
    }
    await confirmAndRefund(row)
  })
}

const resetForm = () => {
  refundFormRef.value?.resetFields()
  refundForm.ticketId = null
  refundForm.operatorId = null
  refundForm.reason = ''
  presetTicketId.value = false
}

onMounted(async () => {
  await Promise.all([loadSalespeople(), loadTickets()])
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
</style>
