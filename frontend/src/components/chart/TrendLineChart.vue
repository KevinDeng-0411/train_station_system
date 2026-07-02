<template>
  <div class="chart-card">
    <div class="chart-header">
      <span class="chart-title">📈 最近7天售票趋势</span>
      <span class="chart-subtitle">售票数 / 收入 双轴</span>
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
  commonLegend,
  cyanBluePalette,
  axisLabelStyle,
  splitLineStyle
} from '@/utils/chartTheme'

const props = defineProps({
  dates: { type: Array, default: () => [] },
  ticketCounts: { type: Array, default: () => [] },
  revenues: { type: Array, default: () => [] }
})

const option = computed(() => ({
  grid: { ...commonGrid, top: 60 },
  tooltip: { ...commonTooltip, axisPointer: { type: 'cross', lineStyle: { color: cyanBluePalette[0] } } },
  legend: { ...commonLegend, data: ['售票数', '收入(元)'] },
  xAxis: {
    type: 'category',
    data: props.dates,
    boundaryGap: false,
    axisLabel: axisLabelStyle,
    axisLine: { lineStyle: { color: '#CBD5E1' } }
  },
  yAxis: [
    {
      type: 'value',
      name: '售票数(张)',
      position: 'left',
      axisLabel: axisLabelStyle,
      splitLine: splitLineStyle
    },
    {
      type: 'value',
      name: '收入(元)',
      position: 'right',
      axisLabel: { ...axisLabelStyle, color: cyanBluePalette[1] },
      splitLine: { show: false }
    }
  ],
  series: [
    {
      name: '售票数',
      type: 'line',
      yAxisIndex: 0,
      data: props.ticketCounts,
      smooth: true,
      symbolSize: 8,
      itemStyle: { color: cyanBluePalette[0] },
      lineStyle: { width: 3, color: cyanBluePalette[0] },
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(8, 145, 178, 0.35)' },
            { offset: 1, color: 'rgba(8, 145, 178, 0.02)' }
          ]
        }
      }
    },
    {
      name: '收入(元)',
      type: 'line',
      yAxisIndex: 1,
      data: props.revenues,
      smooth: true,
      symbolSize: 8,
      itemStyle: { color: cyanBluePalette[1] },
      lineStyle: { width: 3, color: cyanBluePalette[1], type: 'dashed' }
    }
  ]
}))
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
