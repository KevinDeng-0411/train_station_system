<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="车次详情"
    width="720px"
  >
    <div v-if="train" class="train-detail">
      <!-- 头部信息 -->
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

      <!-- 关键指标 -->
      <el-row :gutter="16" class="metrics">
        <el-col :span="6">
          <div class="metric">
            <div class="metric-label">发车时间</div>
            <div class="metric-value">{{ train.departureTime }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="metric">
            <div class="metric-label">总座位</div>
            <div class="metric-value">{{ train.totalSeats }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="metric">
            <div class="metric-label">剩余座位</div>
            <div class="metric-value" :class="{ low: train.remainingSeats < 10 }">
              {{ train.remainingSeats }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="metric">
            <div class="metric-label">利用率</div>
            <div class="metric-value">
              {{ Math.round((1 - train.remainingSeats / train.totalSeats) * 100) }}%
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 经停站和价格表 -->
      <h4 class="section-title">🚉 经停站及价格（演示 train_stations 1:N 关系）</h4>
      <el-table :data="stations" border size="small" empty-text="暂无经停站信息">
        <el-table-column prop="stopOrder" label="序号" width="60" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.stopOrder === 1 ? 'success' : 'primary'">
              {{ row.stopOrder }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stationName" label="站点名称" min-width="120" />
        <el-table-column prop="city" label="城市" min-width="100" />
        <el-table-column label="到达时间" width="120">
          <template #default="{ row }">
            <span v-if="row.arrivalTime">{{ row.arrivalTime }}</span>
            <span v-else style="color: #94A3B8;">— 始发站</span>
          </template>
        </el-table-column>
        <el-table-column label="出发时间" width="120">
          <template #default="{ row }">
            <span v-if="row.departureTime">{{ row.departureTime }}</span>
            <span v-else style="color: #94A3B8;">— 终点站</span>
          </template>
        </el-table-column>
        <el-table-column label="票价" width="100" align="right">
          <template #default="{ row }">
            <span v-if="row.price > 0" style="color: #0891B2; font-weight: 600;">
              ¥{{ row.price }}
            </span>
            <span v-else style="color: #94A3B8;">— 起点</span>
          </template>
        </el-table-column>
      </el-table>
      <p class="hint">
        💡 上表来源于 <code>train_stations</code> 表，演示了车次与站点的<strong>一对多</strong>关系。
      </p>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  visible: { type: Boolean, default: false },
  train: { type: Object, default: null }
})
defineEmits(['update:visible'])

const stations = ref([])

// 加载经停站
watch(() => props.train, async (val) => {
  if (val) {
    const res = await axios.get(`/api/trains/${val.id}/stations`)
    if (res.data.success) stations.value = res.data.data
  }
}, { immediate: true })
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #0891B2, #06B6D4);
  color: white;
  border-radius: 12px;
  margin-bottom: 20px;
}
.train-no {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 1px;
}
.train-route {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
}
.train-route .arrow {
  font-size: 22px;
  color: #CFFAFE;
}
.metrics {
  margin-bottom: 24px;
}
.metric {
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(8, 145, 178, 0.1);
  border-radius: 8px;
  padding: 12px;
  text-align: center;
}
.metric-label {
  font-size: 12px;
  color: #64748B;
  margin-bottom: 4px;
}
.metric-value {
  font-size: 22px;
  font-weight: 700;
  color: #0891B2;
  font-family: 'JetBrains Mono', monospace;
}
.metric-value.low { color: #EF4444; }
.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  margin: 20px 0 12px;
}
.hint {
  margin-top: 12px;
  padding: 8px 12px;
  background: #F0F9FF;
  border-left: 3px solid #06B6D4;
  font-size: 12px;
  color: #475569;
  border-radius: 4px;
}
code {
  background: #E0F2FE;
  color: #0891B2;
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 12px;
}
</style>
