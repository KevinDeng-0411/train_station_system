<template>
  <div class="chart-card">
    <div class="chart-header">
      <span class="chart-title">🥧 业务员收入占比</span>
      <span class="chart-subtitle">环形饼图</span>
    </div>
    <v-chart :option="option" autoresize class="chart-canvas" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { cyanBluePalette, formatItemTooltip } from '@/utils/chartTheme'

const props = defineProps({
  data: { type: Array, default: () => [] }
})

const option = computed(() => ({
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(255, 255, 255, 0.96)',
    borderWidth: 0,
    padding: 0,
    extraCssText: 'box-shadow: 0 6px 24px rgba(15, 23, 42, 0.12); border-radius: 12px; backdrop-filter: blur(6px);',
    formatter: (params) => formatItemTooltip(params, 'money')
  },
  legend: {
    type: 'scroll',
    bottom: 10,
    itemWidth: 10,
    itemHeight: 10,
    textStyle: { color: '#475569', fontSize: 11 }
  },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 700,
          color: '#0F172A'
        },
        scaleSize: 8
      },
      labelLine: { show: false },
      data: props.data.map((item, i) => ({
        name: item.salesperson_name || '未知',
        value: Number(item.total_revenue) || 0,
        itemStyle: { color: cyanBluePalette[i % cyanBluePalette.length] }
      }))
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
