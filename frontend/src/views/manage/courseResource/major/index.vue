<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="openAdd" v-hasPermi="['manage:major:add']">新增专业</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="名称">
        <el-input v-model="queryParams.majorName" placeholder="专业名称" style="width: 200px"
                  @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="专业名称" prop="majorName" min-width="200"/>
      <el-table-column label="状态" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status==='0'?'success':'info'">{{
              scope.row.status === '0' ? '正常' : '停用'
            }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="200"/>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="openEdit(scope.row)" v-hasPermi="['manage:major:edit']">
            编辑
          </el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)" v-hasPermi="['manage:major:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" :title="form.id?'编辑专业':'新增专业'" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" maxlength="128"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MajorManage">
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import {listMajor, addMajor, updateMajor, delMajor} from '@/api/manage/major'

const {proxy} = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({pageNum: 1, pageSize: 10, majorName: ''})

const open = ref(false)
const formRef = ref()
const form = reactive({id: undefined, majorName: '', status: '0', remark: ''})
const rules = {majorName: [{required: true, message: '请填写专业名称', trigger: 'blur'}]}

const getList = async () => {
  loading.value = true
  try {
    const resp = await listMajor(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally {
    loading.value = false
  }
}
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList()
}
const resetQuery = () => {
  queryParams.majorName = '';
  handleQuery()
}
const openAdd = () => {
  Object.assign(form, {id: undefined, majorName: '', status: '0', remark: ''});
  open.value = true
}
const openEdit = (row) => {
  Object.assign(form, row);
  open.value = true
}
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.id) await updateMajor(form); else await addMajor(form)
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
const delRow = async (row) => {
  await delMajor(row.id);
  proxy.$modal.msgSuccess('删除成功');
  getList()
}

onMounted(getList)
</script>

<style scoped>
.hint {
  color: #909399;
}
</style>

