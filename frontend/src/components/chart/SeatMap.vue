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
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(8, 145, 178, 0.1);
  border-radius: 12px;
  padding: 16px;
}
.seat-legend {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  justify-content: center;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #475569;
}
.dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 3px;
}
.dot.free { background: #E0F2FE; border: 1px solid #BAE6FD; }
.dot.sold { background: #F1F5F9; border: 1px solid #CBD5E1; cursor: not-allowed; opacity: 0.5; }
.dot.selected { background: #06B6D4; border: 1px solid #0891B2; }

.seat-grid {
  display: grid;
  grid-template-columns: 32px repeat(5, 42px);
  gap: 4px;
  justify-content: center;
  margin-bottom: 16px;
}
.row-header, .col-header {
  text-align: center;
  font-size: 11px;
  color: #94A3B8;
  font-weight: 600;
  align-self: center;
}
.row-num {
  text-align: center;
  font-size: 12px;
  color: #64748B;
  font-weight: 600;
  align-self: center;
}
.seat {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.15s;
  background: #E0F2FE;
  color: #0891B2;
  border: 2px solid transparent;
}
.seat:hover { background: #BAE6FD; }
/* 窗边座位 */
.seat.window-left { border-left: 3px solid #06B6D4; }
.seat.window-right { border-right: 3px solid #06B6D4; }
/* 过道座位 */
.seat.aisle-left { margin-right: 12px; }
.seat.aisle-right { margin-left: 12px; }
/* 已售：灰色 */
.seat:has(.sold) { display: none; }
/* 需用JS处理 - 使用内联style */
.seat.sold-override {
  background: #F1F5F9;
  color: #CBD5E1;
  cursor: not-allowed;
  border-color: #E2E8F0;
}
/* 已选 */
.seat.selected-override {
  background: #06B6D4;
  color: white;
  border-color: #0891B2;
  box-shadow: 0 2px 8px rgba(8, 145, 178, 0.3);
}
.empty {
  text-align: center;
  padding: 40px;
  color: #94A3B8;
  font-size: 14px;
}
.hint {
  margin: 12px 0 0;
  font-size: 12px;
  color: #64748B;
  text-align: center;
}
.hint code {
  background: #E0F2FE;
  color: #0891B2;
  padding: 1px 6px;
  border-radius: 3px;
}
</style>
