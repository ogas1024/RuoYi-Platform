<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="openAdd" v-hasPermi="['manage:majorLead:add']">新增负责人</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID"><el-input v-model.number="queryParams.userId" style="width:160px"/></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="ID" prop="id" width="80"/>
      <el-table-column label="专业ID" prop="majorId" width="120"/>
      <el-table-column label="用户ID" prop="userId" width="120"/>
      <el-table-column label="备注" prop="remark" min-width="200"/>
      <el-table-column label="操作" fixed="right" width="160">
        <template #default="scope">
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)" v-hasPermi="['manage:majorLead:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <el-dialog v-model="open" title="新增专业负责人" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="专业" prop="majorId">
          <el-select v-model="form.majorId" clearable placeholder="选择专业">
            <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID" prop="userId"><el-input v-model.number="form.userId"/></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MajorLeadManage">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listMajorLead, addMajorLead, delMajorLead } from '@/api/manage/majorLead'
import { listMajor } from '@/api/manage/major'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, majorId: undefined, userId: undefined })
const majors = ref([])

const open = ref(false)
const formRef = ref()
const form = reactive({ majorId: undefined, userId: undefined, remark: '' })
const rules = { majorId: [{ required: true, message: '请填写专业ID', trigger: 'blur' }], userId: [{ required: true, message: '请填写用户ID', trigger: 'blur' }] }

const getList = async () => {
  loading.value = true
  try {
    const resp = await listMajorLead(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally { loading.value = false }
}
const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.majorId = undefined; queryParams.userId = undefined; handleQuery() }
const openAdd = () => { Object.assign(form, { majorId: undefined, userId: undefined, remark: '' }); open.value = true }
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    await addMajorLead(form)
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
const delRow = async (row) => { await delMajorLead(row.id); proxy.$modal.msgSuccess('删除成功'); getList() }

const getMajors = async () => { const resp = await listMajor({ pageNum: 1, pageSize: 100 }); majors.value = resp.rows || [] }
onMounted(async () => { await getMajors(); await getList() })
</script>

<style scoped>
.hint { color: #909399; }
</style>
