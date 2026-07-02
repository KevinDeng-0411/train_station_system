<template>
  <div>
    <h2>统计报表</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <h3>车次售票统计</h3>
          <el-form :inline="true" style="margin: 15px 0;">
            <el-form-item label="车次号">
              <el-input v-model="trainForm.trainNumber" placeholder="如: G101" clearable />
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="trainForm.date" type="date" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="queryTrainStats">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="trainStats" border stripe v-loading="trainLoading">
            <el-table-column prop="trainNumber" label="车次" width="100" />
            <el-table-column prop="departureStation" label="出发站" width="120" />
            <el-table-column prop="arrivalStation" label="到达站" width="120" />
            <el-table-column prop="ticketCount" label="售票数" width="100" />
            <el-table-column prop="totalAmount" label="收入" width="120" />
            <el-table-column prop="remainingSeats" label="余票" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h3>业务员销售统计</h3>
          <el-form :inline="true" style="margin: 15px 0;">
            <el-form-item label="日期">
              <el-date-picker v-model="salespersonForm.date" type="date" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="querySalespersonStats">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="salespersonStats" border stripe v-loading="salespersonLoading">
            <el-table-column prop="employeeCode" label="工号" width="120" />
            <el-table-column prop="salespersonName" label="姓名" width="120" />
            <el-table-column prop="ticketCount" label="售票数" width="100" />
            <el-table-column prop="totalRevenue" label="销售收入" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { statisticsApi } from '@/api'

const trainLoading = ref(false)
const salespersonLoading = ref(false)
const trainStats = ref([])
const salespersonStats = ref([])

const trainForm = reactive({ trainNumber: 'G101', date: '2026-07-02' })
const salespersonForm = reactive({ date: '2026-07-02' })

const queryTrainStats = async () => {
  if (!trainForm.trainNumber || !trainForm.date) { ElMessage.warning('请填写完整信息'); return }
  trainLoading.value = true
  try {
    const res = await statisticsApi.getTrainSales(trainForm.trainNumber, trainForm.date)
    if (res.data.success) trainStats.value = res.data.data
    else ElMessage.error(res.data.message || '查询失败')
  } catch (e) { ElMessage.error('查询失败') } finally { trainLoading.value = false }
}

const querySalespersonStats = async () => {
  if (!salespersonForm.date) { ElMessage.warning('请选择日期'); return }
  salespersonLoading.value = true
  try {
    const res = await statisticsApi.getSalespersonRevenue(salespersonForm.date)
    if (res.data.success) salespersonStats.value = res.data.data
    else ElMessage.error(res.data.message || '查询失败')
  } catch (e) { ElMessage.error('查询失败') } finally { salespersonLoading.value = false }
}

onMounted(() => { queryTrainStats(); querySalespersonStats() })
</script>
