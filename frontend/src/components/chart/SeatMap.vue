<template>
  <div class="seat-map">
    <div class="seat-legend">
      <span class="legend-item"><span class="dot free"></span>可选</span>
      <span class="legend-item"><span class="dot sold"></span>已售</span>
      <span class="legend-item"><span class="dot selected"></span>当前选择</span>
    </div>
    <div class="seat-grid" v-if="rows > 0">
      <div class="row-header">窗</div>
      <div v-for="col in seatsPerRow" :key="'h'+col" class="col-header">
        {{ colLabels[col - 1] }}
      </div>

      <template v-for="row in rows" :key="'r'+row">
        <div class="row-num">{{ row }}</div>
        <div
          v-for="col in seatsPerRow"
          :key="`${row}-${col}`"
          class="seat"
          :class="[seatPositionClass(row, col), seatStatusClass(row, col)]"
          @click="selectSeat(row, col)"
        >
          {{ colLabels[col - 1] }}
        </div>
      </template>
    </div>
    <div v-else class="empty">请先选择车次和乘车日期</div>
    <p class="hint">
      📌 点击座位选择 · 此图来自 <code>tickets</code> 表
    </p>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  train: { type: Object, default: null },
  saleDate: { type: String, default: '' },
  selectedSeat: { type: String, default: '' }
})

const emit = defineEmits(['select-seat'])

const soldSeats = ref([])

// 高铁座位布局：每行5座 (A,B,C 过道 D,F)
const seatsPerRow = 5
const colLabels = ['A', 'B', 'C', 'D', 'F']
const currentSeat = ref(null)  // {row, col}

const rows = computed(() => {
  if (!props.train) return 0
  return Math.ceil(props.train.totalSeats / seatsPerRow)
})

// 座位号生成
function seatLabel(row, col) {
  return `${row}${colLabels[col - 1]}`
}

// 座位位置样式
function seatPositionClass(row, col) {
  const c = col - 1
  if (c === 0) return 'window-left'
  if (c === seatsPerRow - 1) return 'window-right'
  if (c === 2) return 'aisle-left'
  if (c === 3) return 'aisle-right'
  return ''
}

// 座位状态（可用/已售/已选）
function seatStatusClass(row, col) {
  const label = seatLabel(row, col)
  if (props.selectedSeat === label) return 'selected-override'
  if (soldSeats.value.includes(label)) return 'sold-override'
  return ''
}

// 加载已售座位
const loadSoldSeats = async () => {
  if (!props.train || !props.train.id || !props.saleDate) {
    soldSeats.value = []
    return
  }
  try {
    const res = await axios.get('/api/tickets/sold-seats', {
      params: { trainId: props.train.id, saleDate: props.saleDate }
    })
    if (res.data.success) {
      soldSeats.value = res.data.data || []
    }
  } catch (e) {
    soldSeats.value = []
  }
  currentSeat.value = null
}

watch(() => [props.train, props.saleDate], loadSoldSeats, { deep: true })

function selectSeat(row, col) {
  const label = seatLabel(row, col)
  if (soldSeats.value.includes(label)) return  // 已售不能选
  currentSeat.value = { row, col }
  emit('select-seat', label)
}
</script>

<style scoped>
.seat-map {
  background: var(--color-glass-bg-deep);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  backdrop-filter: blur(10px);
}
.seat-legend {
  display: flex;
  gap: var(--space-5);
  margin-bottom: var(--space-4);
  justify-content: center;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}
.dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 3px;
  transition: transform var(--duration-fast) var(--ease-default);
}
.legend-item:hover .dot { transform: scale(1.15); }
.dot.free { background: var(--primitive-cyan-100); border: 1px solid var(--primitive-cyan-200); }
.dot.sold { background: var(--primitive-slate-100); border: 1px solid var(--primitive-slate-300); opacity: 0.5; }
.dot.selected { background: var(--color-primary); border: 1px solid var(--color-primary-dark); }

.seat-grid {
  display: grid;
  grid-template-columns: 32px repeat(5, 42px);
  gap: 4px;
  justify-content: center;
  margin-bottom: var(--space-4);
}
.row-header, .col-header {
  text-align: center;
  font-size: var(--text-xs);
  color: var(--color-text-muted);
  font-weight: 600;
  align-self: center;
  font-family: var(--font-mono);
}
.row-num {
  text-align: center;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  font-weight: 600;
  align-self: center;
  font-family: var(--font-mono);
}
.seat {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 700;
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-bounce);
  background: var(--primitive-cyan-100);
  color: var(--color-primary);
  border: 2px solid transparent;
  position: relative;
  user-select: none;
}
.seat:hover {
  background: var(--primitive-cyan-200);
  transform: scale(1.08) translateY(-2px);
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.25);
  z-index: 1;
}
.seat:active { transform: scale(0.96); }
/* 窗边座位 */
.seat.window-left { border-left: 3px solid var(--color-primary-light); }
.seat.window-right { border-right: 3px solid var(--color-primary-light); }
/* 过道座位 */
.seat.aisle-left { margin-right: 12px; }
.seat.aisle-right { margin-left: 12px; }
.seat.sold-override {
  background: var(--primitive-slate-100);
  color: var(--primitive-slate-300);
  cursor: not-allowed;
  border-color: var(--primitive-slate-200);
  opacity: 0.6;
  transform: none;
}
.seat.sold-override:hover {
  background: var(--primitive-slate-100);
  transform: none;
  box-shadow: none;
}
.seat.selected-override {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary-dark);
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.4);
  transform: scale(1.05);
  animation: seatPulse 1.5s var(--ease-default) infinite;
}
@keyframes seatPulse {
  0%, 100% { box-shadow: 0 4px 12px rgba(8, 145, 178, 0.4); }
  50% { box-shadow: 0 4px 20px rgba(8, 145, 178, 0.7); }
}
.empty {
  text-align: center;
  padding: var(--space-10);
  color: var(--color-text-muted);
  font-size: var(--text-md);
}
.hint {
  margin: var(--space-3) 0 0;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  text-align: center;
}
.hint code {
  background: var(--primitive-cyan-50);
  color: var(--color-primary);
  padding: 1px 6px;
  border-radius: 3px;
  font-family: var(--font-mono);
}

/* 深色模式 */
[data-theme="dark"] .seat {
  background: rgba(8, 145, 178, 0.2);
  color: var(--primitive-cyan-200);
}
[data-theme="dark"] .seat:hover {
  background: rgba(8, 145, 178, 0.35);
}
[data-theme="dark"] .seat.sold-override {
  background: rgba(71, 85, 105, 0.3);
  color: var(--primitive-slate-500);
}
[data-theme="dark"] .seat-map {
  background: rgba(30, 41, 59, 0.85);
}
</style>
