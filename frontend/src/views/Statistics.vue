<template>
  <div class="statistics-dashboard">
    <h2 class="page-title">
      <span class="title-bar"></span>
      数据分析中心
      <span class="date-picker-wrap">
        <el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" />
      </span>
    </h2>

    <!-- KPI 卡片行 -->
    <el-row :gutter="16" class="kpi-row" v-loading="loading">
      <el-col :xs="12" :sm="12" :md="6" v-for="kpi in kpiCards" :key="kpi.key">
        <div class="kpi-card" :style="{ '--accent': kpi.color }">
          <el-icon class="kpi-bg-icon"><component :is="kpi.icon" /></el-icon>
          <div class="kpi-label">{{ kpi.label }}</div>
          <div class="kpi-value">
            <span class="number">{{ kpi.value }}</span>
            <span class="unit">{{ kpi.unit }}</span>
          </div>
          <div class="kpi-extra">{{ kpi.extra }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 4个图表 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <TrendLineChart
          :dates="trendData.dates"
          :ticket-counts="trendData.ticketCounts"
          :revenues="trendData.revenues"
        />
      </el-col>
      <el-col :xs="24" :lg="12">
        <DonutPieChart :data="salespersonStats" />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <TopBarChart :data="trainTopData" />
      </el-col>
      <el-col :xs="24" :lg="12">
        <RosePieChart :data="stationPopularData" />
      </el-col>
    </el-row>

    <!-- 现有表格：车次售票明细 + 收入迷你柱状 -->
    <el-card class="detail-card">
      <div class="card-title">
        <span class="title-bar"></span>
        车次售票明细
        <span class="form-inline">
          <el-input v-model="form.trainNumber" placeholder="车次号" clearable style="width: 140px;" />
          <el-button type="primary" @click="loadTrainSales">查询</el-button>
        </span>
      </div>
      <el-table :data="trainStats" border stripe v-loading="trainLoading" empty-text="暂无售票数据">
        <el-table-column prop="train_number" label="车次" width="100" />
        <el-table-column label="出发站" width="110">
          <template #default="{ row }">{{ row.departure_station || '-' }}</template>
        </el-table-column>
        <el-table-column label="到达站" width="110">
          <template #default="{ row }">{{ row.arrival_station || '-' }}</template>
        </el-table-column>
        <el-table-column prop="ticket_count" label="售票数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.ticket_count || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收入" width="140" align="right">
          <template #default="{ row }">
            <span class="money">¥{{ Number(row.total_amount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remaining_seats" label="余票" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.remaining_seats > 10 ? 'success' : 'danger'">
              {{ row.remaining_seats }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 现有表格：业务员收入排行 -->
    <el-card class="detail-card">
      <div class="card-title">
        <span class="title-bar"></span>
        业务员收入排行
      </div>
      <el-table :data="salespersonStats" border stripe v-loading="salespersonLoading" empty-text="暂无销售数据">
        <el-table-column type="index" label="#排名" width="80" align="center">
          <template #default="{ row, $index }">
            <span :class="['rank-badge', `rank-${Math.min($index + 1, 3)}`]">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="employee_code" label="工号" width="120" />
        <el-table-column prop="salesperson_name" label="姓名" width="120" />
        <el-table-column prop="ticket_count" label="售票数" width="100" align="center" />
        <el-table-column label="销售收入" width="160" align="right">
          <template #default="{ row }">
            <span class="money">¥{{ Number(row.total_revenue || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Tickets, Wallet, Van, UserFilled,
  TrendCharts, DataLine, Histogram, Promotion
} from '@element-plus/icons-vue'
import { statisticsApi } from '@/api'
import TrendLineChart from '@/components/chart/TrendLineChart.vue'
import TopBarChart from '@/components/chart/TopBarChart.vue'
import DonutPieChart from '@/components/chart/DonutPieChart.vue'
import RosePieChart from '@/components/chart/RosePieChart.vue'

const loading = ref(false)
const trainLoading = ref(false)
const salespersonLoading = ref(false)

// KPI数据
const kpiData = ref({
  todayTickets: 0,
  todayRevenue: 0,
  totalRevenue: 0,
  activeTrains: 0,
  activeSalespeople: 0
})

// 图表数据
const trendData = reactive({ dates: [], ticketCounts: [], revenues: [] })
const trainTopData = ref([])
const stationPopularData = ref([])

// 表格数据
const trainStats = ref([])
const salespersonStats = ref([])

// 查询条件
const form = reactive({ date: '2026-07-02', trainNumber: 'G101' })

// KPI卡片定义
const kpiCards = computed(() => [
  {
    key: 'todayTickets',
    label: '今日售票',
    value: kpiData.value.todayTickets,
    unit: '张',
    icon: Tickets,
    color: '#0891B2',
    extra: '实时数据'
  },
  {
    key: 'todayRevenue',
    label: '今日收入',
    value: Number(kpiData.value.todayRevenue || 0).toLocaleString(),
    unit: '元',
    icon: Wallet,
    color: '#06B6D4',
    extra: '今日总销售额'
  },
  {
    key: 'totalRevenue',
    label: '累计总收入',
    value: Number(kpiData.value.totalRevenue || 0).toLocaleString(),
    unit: '元',
    icon: DataLine,
    color: '#0E7490',
    extra: '所有已售车票'
  },
  {
    key: 'activeTrains',
    label: '在售车次',
    value: kpiData.value.activeTrains,
    unit: '条',
    icon: Van,
    color: '#155E75',
    extra: `${kpiData.value.activeSalespeople} 位业务员在岗`
  }
])

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    // KPI
    const kpiRes = await statisticsApi.getKpi()
    if (kpiRes.data.success) kpiData.value = kpiRes.data.data

    // 7天趋势
    const trendRes = await statisticsApi.getTrend()
    if (trendRes.data.success) {
      Object.assign(trendData, trendRes.data.data)
    }

    // 车次TOP
    const trainTopRes = await statisticsApi.getTrainTop(10)
    if (trainTopRes.data.success) {
      trainTopData.value = trainTopRes.data.data
    }

    // 站点热门
    const stationRes = await statisticsApi.getStationPopular('departure', 8)
    if (stationRes.data.success) {
      stationPopularData.value = stationRes.data.data
    }

    // 业务员销售
    const salespersonRes = await statisticsApi.getSalespersonRevenue(form.date)
    if (salespersonRes.data.success) {
      salespersonStats.value = salespersonRes.data.data
    }

    // 车次售票明细（用 form.trainNumber 和 form.date）
    await loadTrainSales()
  } catch (e) {
    console.error('加载仪表盘数据失败:', e)
  } finally {
    loading.value = false
  }
}

const loadTrainSales = async () => {
  trainLoading.value = true
  try {
    const res = await statisticsApi.getTrainSales(form.trainNumber, form.date)
    if (res.data.success) trainStats.value = res.data.data
    else trainStats.value = []
  } catch (e) {
    trainStats.value = []
  } finally {
    trainLoading.value = false
  }
}

onMounted(() => {
  loadAllData()
})
</script>

<style scoped>
.statistics-dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #0F172A;
  margin: 0 0 20px;
}
.title-bar {
  display: inline-block;
  width: 4px;
  height: 22px;
  background: linear-gradient(180deg, #0891B2, #06B6D4);
  border-radius: 2px;
}
.date-picker-wrap {
  margin-left: auto;
}

/* KPI卡片 */
.kpi-row {
  margin-bottom: 16px;
}
.kpi-card {
  position: relative;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  padding: 18px 20px;
  margin-bottom: 12px;
  box-shadow: 0 8px 32px rgba(8, 145, 178, 0.08);
  border-left: 4px solid var(--accent);
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
}
.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(8, 145, 178, 0.15);
}
.kpi-bg-icon {
  position: absolute;
  right: -10px;
  bottom: -10px;
  font-size: 88px;
  color: var(--accent);
  opacity: 0.08;
}
.kpi-label {
  font-size: 13px;
  color: #64748B;
  font-weight: 500;
}
.kpi-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin: 8px 0 6px;
}
.kpi-value .number {
  font-size: 28px;
  font-weight: 800;
  color: var(--accent);
  letter-spacing: -0.5px;
}
.kpi-value .unit {
  font-size: 12px;
  color: #94A3B8;
}
.kpi-extra {
  font-size: 11px;
  color: #94A3B8;
  border-top: 1px dashed #E2E8F0;
  padding-top: 6px;
  margin-top: 4px;
}

/* 图表区域 */
.chart-row {
  margin-bottom: 16px;
}

/* 详情卡片 */
.detail-card {
  margin-bottom: 16px;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 12px;
}
.form-inline {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.money {
  font-weight: 700;
  color: #0891B2;
  font-family: 'JetBrains Mono', monospace;
}

/* 排名徽章 */
.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #E2E8F0;
  color: #64748B;
  font-weight: 700;
  font-size: 13px;
}
.rank-badge.rank-1 { background: linear-gradient(135deg, #FBBF24, #F59E0B); color: white; }
.rank-badge.rank-2 { background: linear-gradient(135deg, #D1D5DB, #94A3B8); color: white; }
.rank-badge.rank-3 { background: linear-gradient(135deg, #D97706, #B45309); color: white; }

/* 卡片样式覆盖 */
.detail-card :deep(.el-card__body) {
  padding: 18px 20px;
}
</style>
