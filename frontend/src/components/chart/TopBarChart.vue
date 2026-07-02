<template>
  <div class="chart-card">
    <div class="chart-header">
      <span class="chart-title">🏆 车次收入 TOP10</span>
      <span class="chart-subtitle">总销售收入排名</span>
    </div>
    <v-chart :option="option" autoresize class="chart-canvas" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import {
  commonGrid,
  commonTooltip,
  cyanBluePalette,
  axisLabelStyle,
  splitLineStyle
} from '@/utils/chartTheme'

const props = defineProps({
  data: { type: Array, default: () => [] }  // [{train_number, ticket_count, total_revenue}]
})

const option = computed(() => {
  // 反转使其水平展示时从上到下递增
  const reversed = [...props.data].reverse()
  return {
    grid: { ...commonGrid, left: 80 },
    tooltip: {
      ...commonTooltip,
      formatter: (params) => {
        const item = props.data[props.data.length - 1 - params.dataIndex]
        if (!item) return ''
        return `<b>${item.train_number}</b><br/>票数: ${item.ticket_count} 张<br/>收入: ¥${item.total_revenue}`
      }
    },
    xAxis: {
      type: 'value',
      axisLabel: axisLabelStyle,
      splitLine: splitLineStyle
    },
    yAxis: {
      type: 'category',
      data: reversed.map(d => d.train_number || ''),
      axisLabel: axisLabelStyle,
      axisLine: { lineStyle: { color: '#CBD5E1' } }
    },
    series: [
      {
        name: '车次收入',
        type: 'bar',
        data: reversed.map((d, i) => ({
          value: Number(d.total_revenue) || 0,
          itemStyle: {
            color: {
              type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
              colorStops: [
                { offset: 0, color: cyanBluePalette[3] },
                { offset: 1, color: cyanBluePalette[0] }
              ]
            },
            borderRadius: [0, 6, 6, 0]
          }
        })),
        barMaxWidth: 28,
        label: {
          show: true,
          position: 'right',
          formatter: (params) => `¥${Number(params.value).toLocaleString()}`,
          color: '#0F172A',
          fontWeight: 600
        }
      }
    ]
  }
})
</script>

<style scoped>
.chart-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(8, 145, 178, 0.08);
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 12px;
}
.chart-title {
  font-size: 16px;
  font-weight: 700;
  color: #0F172A;
}
.chart-subtitle {
  font-size: 12px;
  color: #94A3B8;
}
.chart-canvas {
  flex: 1;
  min-height: 280px;
}
</style>
