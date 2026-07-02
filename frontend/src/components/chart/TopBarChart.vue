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
  cyanBluePalette,
  axisLabelStyle,
  splitLineStyle,
  formatAxisTooltip
} from '@/utils/chartTheme'

const props = defineProps({
  data: { type: Array, default: () => [] }
})

const option = computed(() => {
  const reversed = [...props.data].reverse()
  return {
    grid: { ...commonGrid, left: 80 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.96)',
      borderWidth: 0,
      padding: 0,
      extraCssText: 'box-shadow: 0 6px 24px rgba(15, 23, 42, 0.12); border-radius: 12px; backdrop-filter: blur(6px);',
      axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(8, 145, 178, 0.08)' } },
      formatter: (params) => {
        const item = props.data[props.data.length - 1 - params[0].dataIndex]
        if (!item) return ''
        return `<div style="padding: 12px 16px; min-width: 200px;">
          <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px; padding-bottom: 8px; border-bottom: 1px solid #E2E8F0;">
            <span style="background: linear-gradient(135deg, #0891B2, #06B6D4); color: white; padding: 2px 10px; border-radius: 10px; font-weight: 700; font-size: 12px;">🚄 ${item.train_number}</span>
          </div>
          <div style="display:flex;justify-content:space-between;gap:16px;font-size:12px;margin-top:4px;">
            <span style="color:#64748B;">售出票数</span>
            <span style="font-weight:700;color:#0E7490;font-family:monospace;">${item.ticket_count || 0} 张</span>
          </div>
          <div style="display:flex;justify-content:space-between;gap:16px;font-size:12px;margin-top:4px;">
            <span style="color:#64748B;">总收入</span>
            <span style="font-weight:700;color:#0891B2;font-family:monospace;">¥${Number(item.total_revenue || 0).toLocaleString()}</span>
          </div>
        </div>`
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
