<template>
  <div>
    <h2>站点管理</h2>
    <el-card>
      <el-form :inline="true" class="mb-sm">
        <el-form-item>
          <el-button type="success" @click="handleAdd">新增站点</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="stationName" label="站点名称" width="200" />
        <el-table-column prop="city" label="所在城市" width="150" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="站点名称" prop="stationName">
          <el-input v-model="form.stationName" />
        </el-form-item>
        <el-form-item label="所在城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { stationApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增站点')
const formRef = ref(null)

const form = reactive({ id: null, stationName: '', city: '' })
const rules = {
  stationName: [{ required: true, message: '请输入站点名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入所在城市', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await stationApi.getAll()
    if (res.data.success) tableData.value = res.data.data
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  Object.assign(form, { id: null, stationName: '', city: '' })
  dialogTitle.value = '新增站点'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑站点'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该站点?', '提示', { type: 'warning' })
    const res = await stationApi.delete(row.id)
    if (res.data.success) { ElMessage.success('删除成功'); loadData() }
    else ElMessage.error(res.data.message || '删除失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const api = form.id ? stationApi.update(form.id, form) : stationApi.create(form)
      const res = await api
      if (res.data.success) { ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
      else ElMessage.error(res.data.message || '操作失败')
    } catch (e) { ElMessage.error('操作失败') }
  })
}

onMounted(() => loadData())
</script>
