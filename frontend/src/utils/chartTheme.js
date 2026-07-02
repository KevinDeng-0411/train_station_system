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

export const cyanGradient = ['#0891B2', '#67E8F9']

export const commonGrid = { left: 50, right: 30, top: 50, bottom: 40, containLabel: true }

// 富文本 tooltip 基础样式
export const richTooltipStyle = {
  backgroundColor: 'rgba(255, 255, 255, 0.96)',
  borderWidth: 0,
  padding: 0,
  extraCssText: 'box-shadow: 0 6px 24px rgba(15, 23, 42, 0.12); border-radius: 12px; backdrop-filter: blur(6px);'
}

// 折线/柱状图：带色块、标题、分隔线
export const axisTooltip = {
  ...richTooltipStyle,
  trigger: 'axis',
  axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(8, 145, 178, 0.08)' } },
  formatter: null  // 在组件中自定义
}

// 饼图/玫瑰图：单条数据展示
export const itemTooltip = {
  ...richTooltipStyle,
  trigger: 'item',
  formatter: null  // 在组件中自定义
}

// === 通用格式化函数 ===

// 处理轴数据类型的图表（折线、柱状）
export function formatAxisTooltip(params, valueType = 'money') {
  const axisLabel = params[0]?.axisValueLabel || ''
  let html = `<div style="padding: 12px 16px; min-width: 200px;">
    <div style="font-size: 13px; color: #475569; font-weight: 600; margin-bottom: 8px; padding-bottom: 8px; border-bottom: 1px solid #E2E8F0;">
      📅 ${axisLabel}
    </div>`
  params.forEach((item, i) => {
    const colorStr = colorOf(item.color)
    html += `<div style="display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-top: ${i === 0 ? 0 : 6}px;">
      <div style="display: flex; align-items: center; gap: 8px; font-size: 12px;">
        <span style="display:inline-block;width:10px;height:10px;border-radius:2px;background:${colorStr};"></span>
        <span style="color:#64748B;">${item.seriesName}</span>
      </div>
      <span style="font-weight:700;color:#0E7490;font-family:'JetBrains Mono',monospace;font-size:13px;">${formatVal(item.value, valueType)}</span>
    </div>`
  })
  return html + '</div>'
}

// 处理饼图/玫瑰图数据
export function formatItemTooltip(params, valueType = 'money') {
  const colorStr = colorOf(params.color)
  return `<div style="padding: 10px 14px; min-width: 180px;">
    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px; padding-bottom: 6px; border-bottom: 1px dashed #E2E8F0;">
      <span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:${colorStr};"></span>
      <span style="font-size:13px;font-weight:700;color:#0F172A;">${params.name}</span>
    </div>
    <div style="display:flex;justify-content:space-between;gap:16px;font-size:13px;">
      <span style="color:#64748B;">${valueType === 'money' ? '收入' : '票数'}</span>
      <span style="font-weight:700;color:#0891B2;font-family:'JetBrains Mono',monospace;">${formatVal(params.value, valueType)}</span>
    </div>
    <div style="text-align:right;font-size:11px;color:#94A3B8;margin-top:2px;">占比 <span style="color:#0E7490;font-weight:600;">${params.percent}%</span></div>
  </div>`
}

function colorOf(c) {
  if (!c) return '#0891B2'
  if (c.colorStops && c.colorStops[0]) return c.colorStops[0].color
  return c
}

function formatVal(value, type) {
  const v = Number(value) || 0
  if (type === 'money') return `¥${v.toLocaleString()}`
  if (type === 'count') return `${v} 张`
  return v.toLocaleString()
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
