<template>
  <div>
    <h2>业务员管理</h2>
    <el-card>
      <el-form :inline="true" :model="searchForm" style="margin-bottom: 15px;">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/工号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="handleAdd">新增业务员</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="employeeCode" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="idCard" label="身份证" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="工号" prop="employeeCode">
          <el-input v-model="form.employeeCode" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="身份证" prop="idCard">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
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
import { salespersonApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增业务员')
const formRef = ref(null)

const searchForm = reactive({ keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({ id: null, employeeCode: '', name: '', phone: '', idCard: '', status: 1 })

const rules = {
  employeeCode: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.page, pageSize: pagination.size, keyword: searchForm.keyword }
    const res = await salespersonApi.getPage(params)
    if (res.data.success) { tableData.value = res.data.data; pagination.total = res.data.total }
  } catch (e) { ElMessage.error('加载数据失败') } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleAdd = () => { Object.assign(form, { id: null, employeeCode: '', name: '', phone: '', idCard: '', status: 1 }); dialogTitle.value = '新增业务员'; dialogVisible.value = true }
const handleEdit = (row) => { Object.assign(form, row); dialogTitle.value = '编辑业务员'; dialogVisible.value = true }
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该业务员?', '提示', { type: 'warning' })
    const res = await salespersonApi.delete(row.id)
    if (res.data.success) { ElMessage.success('删除成功'); loadData() }
    else ElMessage.error(res.data.message || '删除失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const api = form.id ? salespersonApi.update(form.id, form) : salespersonApi.create(form)
      const res = await api
      if (res.data.success) { ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
      else ElMessage.error(res.data.message || '操作失败')
    } catch (e) { ElMessage.error('操作失败') }
  })
}

onMounted(() => loadData())
</script>
