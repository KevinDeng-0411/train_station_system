<template>
  <div>
    <h2>车次管理</h2>
    <el-card>
      <el-form :inline="true" :model="searchForm" style="margin-bottom: 15px;">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="车次号/城市" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="handleAdd">新增车次</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="trainNumber" label="车次号" width="120" />
        <el-table-column prop="departureCity" label="出发城市" width="120" />
        <el-table-column prop="arrivalCity" label="到达城市" width="120" />
        <el-table-column prop="totalSeats" label="总座位" width="100" />
        <el-table-column prop="remainingSeats" label="余票" width="100">
          <template #default="{ row }">
            <el-tag :type="row.remainingSeats > 10 ? 'success' : row.remainingSeats > 0 ? 'warning' : 'danger'">
              {{ row.remainingSeats }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="departureTime" label="发车时间" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '停运' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="info" @click="handleViewDetail(row)">详情</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 15px;"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="车次号" prop="trainNumber">
          <el-input v-model="form.trainNumber" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="出发城市" prop="departureCity">
          <el-input v-model="form.departureCity" />
        </el-form-item>
        <el-form-item label="到达城市" prop="arrivalCity">
          <el-input v-model="form.arrivalCity" />
        </el-form-item>
        <el-form-item label="总座位数" prop="totalSeats">
          <el-input-number v-model="form.totalSeats" :min="1" />
        </el-form-item>
        <el-form-item label="发车时间" prop="departureTime">
          <el-time-picker v-model="form.departureTime" format="HH:mm:ss" value-format="HH:mm:ss" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停运</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 车次详情弹窗 -->
    <TrainDetail v-model:visible="detailVisible" :train="detailTrain" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { trainApi } from '@/api'
import TrainDetail from './TrainDetail.vue'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增车次')
const formRef = ref(null)
const detailVisible = ref(false)
const detailTrain = ref(null)

const searchForm = reactive({ keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  trainNumber: '',
  departureCity: '',
  arrivalCity: '',
  totalSeats: 100,
  remainingSeats: 100,
  departureTime: '08:00:00',
  status: 1
})

const rules = {
  trainNumber: [{ required: true, message: '请输入车次号', trigger: 'blur' }],
  departureCity: [{ required: true, message: '请输入出发城市', trigger: 'blur' }],
  arrivalCity: [{ required: true, message: '请输入到达城市', trigger: 'blur' }],
  totalSeats: [{ required: true, message: '请输入总座位数', trigger: 'blur' }],
  departureTime: [{ required: true, message: '请选择发车时间', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.page, pageSize: pagination.size, keyword: searchForm.keyword }
    const res = await trainApi.getPage(params)
    if (res.data.success) {
      tableData.value = res.data.data
      pagination.total = res.data.total
    }
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { id: null, trainNumber: '', departureCity: '', arrivalCity: '', totalSeats: 100, remainingSeats: 100, departureTime: '08:00:00', status: 1 })
  dialogTitle.value = '新增车次'
  dialogVisible.value = true
}

const handleViewDetail = (row) => {
  detailTrain.value = row
  detailVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑车次'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该车次?', '提示', { type: 'warning' })
    const res = await trainApi.delete(row.id)
    if (res.data.success) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const api = form.id ? trainApi.update(form.id, form) : trainApi.create(form)
      const res = await api
      if (res.data.success) {
        ElMessage.success(form.id ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadData()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (e) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => loadData())
</script>
