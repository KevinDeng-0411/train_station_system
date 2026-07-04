<template>
  <div>
    <h2 class="page-title">数据备份与恢复</h2>
    <el-card>
      <div class="card-header">
        <div class="header-info">
          <span class="header-emoji">💾</span>
          <div>
            <h3 class="mb-sm">备份中心</h3>
            <p class="hint-text">使用 <code>mysqldump</code> 工具创建数据库快照，支持灾难恢复</p>
          </div>
        </div>
        <el-button type="success" @click="handleCreateBackup" :loading="creating">
          <el-icon><Plus /></el-icon> 创建备份
        </el-button>
      </div>

      <el-divider />

      <EmptyState
        v-if="!loading && backupList.length === 0"
        type="document"
        title="暂无备份记录"
        description="点击右上角'创建备份'按钮生成第一个数据库快照"
        action-text="立即创建"
        @action="handleCreateBackup"
      />
      <el-table v-else :data="backupList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="backupFile" label="文件名" min-width="200">
          <template #default="{ row }">
            <span class="filename">📄 {{ row.backupFile }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="backupSize" label="大小" width="120">
          <template #default="{ row }">{{ formatSize(row.backupSize) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'" effect="dark" round>
              {{ row.status === 'SUCCESS' ? '✅ 成功' : '❌ 失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170">
          <template #default="{ row }">
            <span class="text-mono">{{ row.createdAt }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="160">
          <template #default="{ row }">
            <span v-if="row.errorMessage" class="text-danger">{{ row.errorMessage }}</span>
            <span v-else class="text-muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" :disabled="row.status !== 'SUCCESS'" @click="handleRestore(row)">
              <el-icon><RefreshRight /></el-icon> 恢复
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, RefreshRight, Delete } from '@element-plus/icons-vue'
import { backupApi } from '@/api'
import EmptyState from '@/components/EmptyState.vue'

const loading = ref(false)
const creating = ref(false)
const backupList = ref([])

const loadBackups = async () => {
  loading.value = true
  try {
    const res = await backupApi.getAll()
    if (res.data.success) backupList.value = res.data.data
  } catch (e) { ElMessage.error('加载失败') } finally { loading.value = false }
}

const formatSize = (bytes) => {
  if (!bytes) return '—'
  const kb = bytes / 1024
  if (kb < 1024) return `${kb.toFixed(1)} KB`
  return `${(kb / 1024).toFixed(2)} MB`
}

const handleCreateBackup = async () => {
  creating.value = true
  try {
    const res = await backupApi.create()
    if (res.data.success) ElMessage.success('✅ 备份创建成功')
    else ElMessage.error(res.data.message || '备份创建失败')
    loadBackups()
  } catch (e) { ElMessage.error('备份创建失败') } finally { creating.value = false }
}

const handleRestore = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认恢复备份 ${row.backupFile}?\n\n⚠️ 当前所有数据将被覆盖!`,
      '⚠️ 警告',
      { type: 'warning', confirmButtonText: '确认恢复', cancelButtonText: '取消' }
    )
    const res = await backupApi.restore(row.id)
    if (res.data.success) ElMessage.success('✅ 恢复成功')
    else ElMessage.error(res.data.message || '恢复失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('恢复失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除备份 ${row.backupFile}?`, '提示', { type: 'warning' })
    const res = await backupApi.delete(row.id)
    if (res.data.success) { ElMessage.success('删除成功'); loadBackups() }
    else ElMessage.error(res.data.message || '删除失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => loadBackups())
</script>

<style scoped>
.header-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}
.header-emoji {
  font-size: 36px;
  filter: drop-shadow(0 2px 4px rgba(8, 145, 178, 0.2));
}
.mb-sm { margin: 0 0 var(--space-1) 0; }
.hint-text {
  margin: 0;
  font-size: var(--text-sm);
  color: var(--color-text-muted);
}
.hint-text code {
  background: var(--primitive-cyan-50);
  color: var(--color-primary);
  padding: 1px 6px;
  border-radius: 3px;
  font-family: var(--font-mono);
}
.filename { font-family: var(--font-mono); color: var(--color-text-primary); }
.text-mono { font-family: var(--font-mono); color: var(--color-text-secondary); }
.text-danger { color: var(--color-danger); }
.text-muted { color: var(--color-text-muted); }
</style>
