<template>
  <div class="statistics-dashboard">
    <h2 class="page-title">
      <span class="title-bar"></span>
      数据分析中心
      <span class="date-picker-wrap">
        <el-date-picker
          v-model="form.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :shortcuts="dateShortcuts"
          @change="onDateRangeChange"
        />
        <el-button type="primary" @click="loadAllData" size="default">应用筛选</el-button>
        <el-button @click="resetDateRange" size="default">全部</el-button>
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
          <el-input v-model="form.trainNumber" placeholder="车次号" clearable class="w-140" />
          <el-button type="primary" @click="loadTrainSales">查询</el-button>
        </span>
      </div>
      <el-table :data="trainStats" border stripe v-loading="trainLoading" empty-text="暂无售票数据" class="w-full" style="width: 100%;">
        <el-table-column prop="train_number" label="车次" min-width="90" />
        <el-table-column label="出发站" min-width="120">
          <template #default="{ row }">{{ row.departure_station || '-' }}</template>
        </el-table-column>
        <el-table-column label="到达站" min-width="120">
          <template #default="{ row }">{{ row.arrival_station || '-' }}</template>
        </el-table-column>
        <el-table-column prop="ticket_count" label="售票数" min-width="90" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.ticket_count || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收入" min-width="120" align="right">
          <template #default="{ row }">
            <span class="money">¥{{ Number(row.total_amount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remaining_seats" label="余票" min-width="90" align="center">
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
      <el-table :data="salespersonStats" border stripe v-loading="salespersonLoading" empty-text="暂无销售数据" class="w-full" style="width: 100%;">
        <el-table-column type="index" label="#排名" width="90" align="center">
          <template #default="{ row, $index }">
            <span :class="['rank-badge', `rank-${Math.min($index + 1, 3)}`]">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="employee_code" label="工号" min-width="120" />
        <el-table-column prop="salesperson_name" label="姓名" min-width="100" />
        <el-table-column prop="ticket_count" label="售票数" min-width="100" align="center" />
        <el-table-column label="销售收入" min-width="140" align="right">
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
const form = reactive({
  dateRange: [],  // 默认空 → onMounted 时设为最近7天
  date: new Date().toISOString().slice(0, 10),  // 默认今天（跟随 dateRange 末位）
  trainNumber: 'G101'  // 单车次（用于车次售票明细）
})

// 日期快捷选项
const dateShortcuts = [
  {
    text: '今天',
    value: () => {
      const today = new Date()
      return [today, today]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 6)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 29)
      return [start, end]
    }
  },
  {
    text: '本月',
    value: () => {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth(), 1)
      return [start, now]
    }
  }
]

// 日期范围变化时重新加载
const onDateRangeChange = (val) => {
  if (val && val.length === 2) {
    form.date = val[1] // 默认单日为结束日
    loadAllData()
  }
}

// 重置日期
const resetDateRange = () => {
  form.dateRange = null
  form.date = new Date().toISOString().slice(0, 10)
  loadAllData()
}

// KPI卡片定义
const kpiCards = computed(() => [
  {
    key: 'todayTickets',
    label: '今日售票',
    value: kpiData.value.todayTickets,
    unit: '张',
    icon: Tickets,
    color: 'var(--color-primary)',
    extra: '实时数据'
  },
  {
    key: 'todayRevenue',
    label: '今日收入',
    value: Number(kpiData.value.todayRevenue || 0).toLocaleString(),
    unit: '元',
    icon: Wallet,
    color: 'var(--color-primary-light)',
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
    // 提取日期范围参数
    const startDate = form.dateRange && form.dateRange[0] ? form.dateRange[0] : null
    const endDate = form.dateRange && form.dateRange[1] ? form.dateRange[1] : null

    // KPI（始终是"今日"，不跟日期）
    const kpiRes = await statisticsApi.getKpi()
    if (kpiRes.data.success) kpiData.value = kpiRes.data.data

    // 7天趋势（始终是最近7天）
    const trendRes = await statisticsApi.getTrend()
    if (trendRes.data.success) {
      Object.assign(trendData, trendRes.data.data)
    }

    // 车次TOP（按日期范围筛选）
    const trainTopRes = await statisticsApi.getTrainTop(10, startDate, endDate)
    if (trainTopRes.data.success) {
      trainTopData.value = trainTopRes.data.data
    }

    // 站点热门（按日期范围筛选）
    const stationRes = await statisticsApi.getStationPopular('departure', 8, startDate, endDate)
    if (stationRes.data.success) {
      stationPopularData.value = stationRes.data.data
    }

    // 业务员销售（按日期范围）
    const salespersonRes = await statisticsApi.getSalespersonRevenueByRange(startDate, endDate)
    if (salespersonRes.data.success) {
      salespersonStats.value = salespersonRes.data.data
    }

    // 车次售票明细（用 form.trainNumber + 日期范围）
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
    // 使用日期范围（如果用户已选）或默认最近 7 天
    let startDate, endDate
    if (form.dateRange && form.dateRange.length === 2) {
      startDate = form.dateRange[0]
      endDate = form.dateRange[1]
    } else {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 6)
      startDate = start
      endDate = end
    }
    const fmt = (d) => {
      const date = typeof d === 'string' ? new Date(d) : d
      const pad = (n) => String(n).padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
    }
    const res = await statisticsApi.getTrainSalesByRange(
      form.trainNumber, fmt(startDate), fmt(endDate)
    )
    if (res.data.success) trainStats.value = res.data.data
    else trainStats.value = []
  } catch (e) {
    trainStats.value = []
  } finally {
    trainLoading.value = false
  }
}

onMounted(() => {
  // 初始化默认日期范围为"最近 7 天"（含今天）
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 6)
  form.dateRange = [start, end]
  form.date = end.toISOString().slice(0, 10)
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
  color: var(--color-text-primary);
  margin: 0 0 20px;
}
.title-bar {
  display: inline-block;
  width: 4px;
  height: 22px;
  background: linear-gradient(180deg, var(--color-primary), var(--color-primary-light));
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
  background: var(--color-glass-bg-deep);
  backdrop-filter: blur(10px);
  border: 1px solid var(--color-glass-border);
  border-radius: 16px;
  padding: 18px 20px;
  margin-bottom: 12px;
  box-shadow: 0 8px 32px var(--color-border);
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
  color: var(--color-text-secondary);
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
  color: var(--color-text-muted);
}
.kpi-extra {
  font-size: 11px;
  color: var(--color-text-muted);
  border-top: 1px dashed var(--primitive-slate-200);
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
  color: var(--color-text-primary);
  margin-bottom: 12px;
}
.form-inline {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.money {
  font-weight: 700;
  color: var(--color-primary);
  font-family: var(--font-mono);
}

/* 排名徽章 */
.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--primitive-slate-200);
  color: var(--color-text-secondary);
  font-weight: 700;
  font-size: 13px;
}
.rank-badge.rank-1 { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: white; box-shadow: 0 2px 6px rgba(245, 158, 11, 0.35); }
.rank-badge.rank-2 { background: linear-gradient(135deg, #e5e7eb, #94a3b8); color: white; box-shadow: 0 2px 6px rgba(148, 163, 184, 0.35); }
.rank-badge.rank-3 { background: linear-gradient(135deg, #ea580c, #c2410c); color: white; box-shadow: 0 2px 6px rgba(194, 65, 12, 0.35); }

/* 卡片样式覆盖 */
.detail-card :deep(.el-card__body) {
  padding: 18px 20px;
}
</style>
