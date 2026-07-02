<template>
  <div>
    <h2>退票管理</h2>
    <el-card>
      <el-form :model="refundForm" ref="refundFormRef" :rules="rules" label-width="120px" style="max-width: 500px;">
        <el-form-item label="车票ID" prop="ticketId">
          <el-input-number v-model="refundForm.ticketId" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="操作员ID" prop="operatorId">
          <el-select v-model="refundForm.operatorId" placeholder="请选择操作员" style="width: 100%;">
            <el-option v-for="s in salespersonList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="退票原因" prop="reason">
          <el-input v-model="refundForm.reason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleRefund" :loading="loading">退 票</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <h3>有效车票列表</h3>
      <el-table :data="ticketList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="trainNumber" label="车次" width="100" />
        <el-table-column prop="seatNumber" label="座位" width="80" />
        <el-table-column prop="passengerName" label="乘客" width="100" />
        <el-table-column prop="price" label="票价" width="100" />
        <el-table-column prop="saleDate" label="乘车日期" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { salespersonApi, ticketApi } from '@/api'

const loading = ref(false)
const salespersonList = ref([])
const ticketList = ref([])
const refundFormRef = ref(null)

const refundForm = reactive({ ticketId: null, operatorId: null, reason: '' })
const rules = {
  ticketId: [{ required: true, message: '请输入车票ID', trigger: 'blur' }],
  operatorId: [{ required: true, message: '请选择操作员', trigger: 'change' }]
}

const loadSalespeople = async () => {
  const res = await salespersonApi.getAll()
  if (res.data.success) salespersonList.value = res.data.data.filter(s => s.status === 1)
}

const loadTickets = async () => {
  try {
    const res = await ticketApi.getPage({ pageNum: 1, pageSize: 100 })
    if (res.data.success) ticketList.value = res.data.data.filter(t => t.status === 1)
  } catch (e) { ElMessage.error('加载失败') }
}

const handleRefund = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await ticketApi.refund(refundForm.ticketId, {
        operatorId: refundForm.operatorId,
        reason: refundForm.reason
      })
      if (res.data.success) {
        ElMessage.success('退票成功')
        refundFormRef.value.resetFields()
        loadTickets()
      } else {
        ElMessage.error(res.data.message || '退票失败')
      }
    } catch (e) { ElMessage.error('退票失败') } finally { loading.value = false }
  })
}

onMounted(() => { loadSalespeople(); loadTickets() })
</script>
