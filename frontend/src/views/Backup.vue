<template>
  <div>
    <h2>数据备份与恢复</h2>
    <el-card>
      <el-button type="success" @click="handleCreateBackup" :loading="creating">创建备份</el-button>
      <el-divider />
      <h3>备份记录</h3>
      <el-table :data="backupList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="backupFile" label="文件名" width="200" />
        <el-table-column prop="backupSize" label="大小(字节)" width="120">
          <template #default="{ row }">{{ row.backupSize || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="errorMessage" label="错误信息" width="200" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleRestore(row)" :disabled="row.status !== 'SUCCESS'">恢复</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { backupApi } from '@/api'

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

const handleCreateBackup = async () => {
  creating.value = true
  try {
    const res = await backupApi.create()
    if (res.data.success) ElMessage.success('备份创建成功')
    else ElMessage.error(res.data.message || '备份创建失败')
    loadBackups()
  } catch (e) { ElMessage.error('备份创建失败') } finally { creating.value = false }
}

const handleRestore = async (row) => {
  try {
    await ElMessageBox.confirm('确认恢复该备份? 当前数据将被覆盖!', '警告', { type: 'warning' })
    const res = await backupApi.restore(row.id)
    if (res.data.success) ElMessage.success('恢复成功')
    else ElMessage.error(res.data.message || '恢复失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('恢复失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该备份?', '提示', { type: 'warning' })
    const res = await backupApi.delete(row.id)
    if (res.data.success) { ElMessage.success('删除成功'); loadBackups() }
    else ElMessage.error(res.data.message || '删除失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => loadBackups())
</script>
