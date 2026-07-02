// ECharts 统一主题配置 - 与青蓝主题统一

export const cyanBluePalette = [
  '#0891B2',  // 主色
  '#06B6D4',  // 亮青
  '#0E7490',  // 深青
  '#67E8F9',  // 浅青
  '#22D3EE',  // 天青
  '#155E75',  // 墨青
  '#A5F3FC',  // 冰青
  '#164E63'   // 藏青
]

export const gradientColor = null
export const cyanGradient = ['#0891B2', '#67E8F9']

export const commonGrid = { left: 50, right: 30, top: 50, bottom: 40, containLabel: true }

export const commonTooltip = {
  trigger: 'axis',
  backgroundColor: 'rgba(255, 255, 255, 0.95)',
  borderColor: '#CFFAFE',
  borderWidth: 1,
  textStyle: { color: '#334155', fontSize: 12 },
  axisPointer: { lineStyle: { color: '#67E8F9', type: 'dashed' } }
}

export const commonLegend = {
  textStyle: { color: '#475569', fontSize: 12 },
  top: 10,
  itemWidth: 12,
  itemHeight: 12
}

// 文字样式
export const axisLabelStyle = {
  color: '#64748B',
  fontSize: 11
}

export const splitLineStyle = {
  lineStyle: { color: '#E2E8F0', type: 'dashed' }
}

export const seriesCommonStyle = {
  symbol: 'circle',
  symbolSize: 6,
  smooth: true,
  lineStyle: { width: 3 }
}
