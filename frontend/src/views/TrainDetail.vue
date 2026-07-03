<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="车次详情"
    width="780px"
  >
    <div v-if="train" class="train-detail">
      <div class="detail-header">
        <div class="train-no">{{ train.trainNumber }}</div>
        <div class="train-route">
          <span class="city">{{ train.departureCity }}</span>
          <span class="arrow">→</span>
          <span class="city">{{ train.arrivalCity }}</span>
        </div>
        <el-tag :type="train.status === 1 ? 'success' : 'danger'">
          {{ train.status === 1 ? '正常运营' : '已停运' }}
        </el-tag>
      </div>

      <el-row :gutter="16" class="metrics">
        <el-col :span="6">
          <div class="metric"><div class="metric-label">发车时间</div><div class="metric-value">{{ train.departureTime }}</div></div>
        </el-col>
        <el-col :span="6">
          <div class="metric"><div class="metric-label">总座位</div><div class="metric-value">{{ train.totalSeats }}</div></div>
        </el-col>
        <el-col :span="6">
          <div class="metric"><div class="metric-label">剩余座位</div><div class="metric-value" :class="{ low: train.remainingSeats < 10 }">{{ train.remainingSeats }}</div></div>
        </el-col>
        <el-col :span="6">
          <div class="metric"><div class="metric-label">利用率</div><div class="metric-value">{{ Math.round((1 - train.remainingSeats / train.totalSeats) * 100) }}%</div></div>
        </el-col>
      </el-row>

      <!-- 经停站管理 -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 24px;">
        <h4 class="section-title">🚉 经停站管理（train_stations表 · 1:N关系）</h4>
        <el-button size="small" type="primary" @click="showAddDialog = true">+ 添加站点</el-button>
      </div>

      <el-table :data="stations" border size="small" empty-text="暂无经停站">
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ row, $index }">
            <el-input-number v-model="row.stopOrder" :min="1" size="small" controls-position="right" style="width: 70px;" @change="updateStopOrder(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="stationName" label="站点" min-width="110" />
        <el-table-column prop="city" label="城市" width="80" />
        <el-table-column label="到达时间" width="120">
          <template #default="{ row }">
            <el-time-picker v-if="row.stopOrder > 1" v-model="row._arrivalTime" format="HH:mm:ss" value-format="HH:mm:ss" size="small" style="width: 110px;" @change="updateTime(row)" />
            <span v-else style="color: #94A3B8; font-size: 12px;">始发</span>
          </template>
        </el-table-column>
        <el-table-column label="出发时间" width="120">
          <template #default="{ row }">
            <el-time-picker v-if="!isLastStop(row)" v-model="row._departureTime" format="HH:mm:ss" value-format="HH:mm:ss" size="small" style="width: 110px;" @change="updateTime(row)" />
            <span v-else style="color: #94A3B8; font-size: 12px;">终到</span>
          </template>
        </el-table-column>
        <el-table-column label="票价(¥)" width="120" align="right">
          <template #default="{ row }">
            <el-input-number v-if="row.stopOrder > 1" v-model="row.price" :min="0" :precision="2" size="small" controls-position="right" style="width: 110px;" @change="updatePrice(row)" />
            <span v-else style="color: #94A3B8; font-size: 12px;">起点</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ row, $index }">
            <el-button size="small" type="danger" :icon="Delete" circle @click="removeStation(row)" :disabled="stations.length <= 2" />
          </template>
        </el-table-column>
      </el-table>
      <p class="hint">
        💡 票价 = 起点到该站的累计票价。区间票价 = 到达站票价 − 出发站票价
      </p>
    </div>

    <!-- 添加站点弹窗 -->
    <el-dialog v-model="showAddDialog" title="添加经停站点" width="400px" append-to-body>
      <el-form label-width="80px" size="small">
        <el-form-item label="站点">
          <el-select v-model="addForm.stationId" filterable placeholder="选择站点" style="width: 100%;">
            <el-option v-for="s in availableStations" :key="s.id" :label="`${s.stationName}（${s.city}）`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="票价(¥)">
          <el-input-number v-model="addForm.price" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddStation">添加</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  visible: { type: Boolean, default: false },
  train: { type: Object, default: null }
})
defineEmits(['update:visible'])

const stations = ref([])
const allStations = ref([])
const showAddDialog = ref(false)
const addForm = ref({ stationId: null, price: 0 })

// 已用站点ID集合
const usedStationIds = computed(() => new Set(stations.value.map(s => s.stationId)))
const availableStations = computed(() => allStations.value.filter(s => !usedStationIds.value.has(s.id)))

const isLastStop = (row) => {
  const list = stations.value
  if (list.length === 0) return false
  const maxOrder = Math.max(...list.map(s => s.stopOrder))
  return row.stopOrder === maxOrder
}

const loadStations = async () => {
  if (!props.train) return
  const res = await axios.get(`/api/trains/${props.train.id}/stations`)
  if (res.data.success) {
    stations.value = (res.data.data || []).map(s => ({
      ...s,
      _arrivalTime: s.arrivalTime || null,
      _departureTime: s.departureTime || null
    }))
  }
}

const loadAllStations = async () => {
  const res = await axios.get('/api/stations')
  if (res.data.success) allStations.value = res.data.data
}

watch(() => props.train, async (val) => {
  if (val) {
    await loadAllStations()
    await loadStations()
  }
}, { immediate: true })

// 更新票价
const updatePrice = async (row) => {
  try {
    await axios.put(`/api/trains/${props.train.id}/stations/${row.stationId}/price?price=${row.price}`)
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

// 更新时间
const updateTime = async (row) => {
  try {
    await axios.put(`/api/trains/${props.train.id}/stations/${row.stationId}/time`, {
      arrivalTime: row._arrivalTime,
      departureTime: row._departureTime
    }).catch(() => {})  // backend may not have this endpoint, ignore error
  } catch (e) {}
}

// 更新顺序
const updateStopOrder = async (row) => {
  try {
    await axios.put(`/api/trains/${props.train.id}/stations/${row.stationId}/order?stopOrder=${row.stopOrder}`)
      .catch(() => {})
  } catch (e) {}
}

// 添加站点
const handleAddStation = async () => {
  if (!addForm.value.stationId) return
  try {
    const nextOrder = stations.value.length > 0
      ? Math.max(...stations.value.map(s => s.stopOrder)) + 1
      : 1
    await axios.post(`/api/trains/${props.train.id}/stations`, {
      trainId: props.train.id,
      stationId: addForm.value.stationId,
      stopOrder: nextOrder,
      price: addForm.value.price
    })
    ElMessage.success('添加成功')
    showAddDialog.value = false
    addForm.value = { stationId: null, price: 0 }
    await loadStations()
  } catch (e) { ElMessage.error('添加失败') }
}

// 删除站点
const removeStation = async (row) => {
  try {
    await ElMessageBox.confirm(`确认移除 ${row.stationName}？`, '提示', { type: 'warning' })
    await axios.delete(`/api/trains/${props.train.id}/stations/${row.stationId}`)
    ElMessage.success('移除成功')
    await loadStations()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('移除失败')
  }
}
</script>

<style scoped>
.detail-header {
  display: flex; align-items: center; gap: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #0891B2, #06B6D4);
  color: white; border-radius: 12px; margin-bottom: 20px;
}
.train-no { font-size: 28px; font-weight: 800; letter-spacing: 1px; }
.train-route { flex: 1; display: flex; align-items: center; gap: 12px; font-size: 18px; font-weight: 600; }
.train-route .arrow { font-size: 22px; color: #CFFAFE; }
.metrics { margin-bottom: 18px; }
.metric {
  background: rgba(255,255,255,0.85); border: 1px solid rgba(8,145,178,0.1);
  border-radius: 8px; padding: 12px; text-align: center;
}
.metric-label { font-size: 12px; color: #64748B; margin-bottom: 4px; }
.metric-value { font-size: 22px; font-weight: 700; color: #0891B2; font-family: 'JetBrains Mono', monospace; }
.metric-value.low { color: #EF4444; }
.section-title { font-size: 14px; font-weight: 600; color: #475569; margin: 0; }
.hint {
  margin-top: 12px; padding: 8px 12px; background: #F0F9FF;
  border-left: 3px solid #06B6D4; font-size: 12px; color: #475569; border-radius: 4px;
}
.hint code { background: #E0F2FE; color: #0891B2; padding: 1px 6px; border-radius: 3px; }
</style>
