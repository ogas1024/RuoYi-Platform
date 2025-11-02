<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">我上传的资源</span>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="待审" :value="0"/>
          <el-option label="已通过" :value="1"/>
          <el-option label="驳回" :value="2"/>
          <el-option label="已下架" :value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list" @selection-change="onSel">
      <el-table-column type="selection" width="50"/>
      <el-table-column label="资源名称" prop="resourceName" min-width="200"/>
      <el-table-column label="课程/专业" min-width="200">
        <template #default="scope">{{ scope.row.majorName || '-' }} / {{ scope.row.courseName || scope.row.courseId }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status===0" type="warning">待审</el-tag>
          <el-tag v-else-if="scope.row.status===1" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.status===2" type="danger">驳回</el-tag>
          <el-tag v-else type="info">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下载数" prop="downloadCount" width="100"/>
      <el-table-column label="操作" fixed="right" width="240">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="editRow(scope.row)">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)">删除</el-button>
          <el-button link type="warning" icon="Bottom" v-if="scope.row.status===1" @click="offline(scope.row)">下架</el-button>
          <el-button link type="success" icon="Top" v-if="scope.row.status!==1" @click="online(scope.row)">重新上架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <el-dialog v-model="open" title="编辑资源" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="form.resourceName" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="资源简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MyCourseResource">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import useUserStore from '@/store/modules/user'
import { listResource, updateResource, delResource, offlineResource, onlineResource } from '@/api/manage/courseResource'

const { proxy } = getCurrentInstance()
const userStore = useUserStore()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const selIds = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, uploaderId: userStore.id, status: undefined })

const open = ref(false)
const formRef = ref()
const form = reactive({ id: undefined, resourceName: '', description: '' })
const rules = { resourceName: [{ required: true, message: '请填写资源名称', trigger: 'blur' }], description: [{ required: true, message: '请填写简介', trigger: 'blur' }] }

const getList = async () => {
  loading.value = true
  try {
    const resp = await listResource(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally { loading.value = false }
}
const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.status = undefined; handleQuery() }
const onSel = (rows) => { selIds.value = rows.map(r => r.id) }

const editRow = (row) => { Object.assign(form, { id: row.id, resourceName: row.resourceName, description: row.description }); open.value = true }
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    await updateResource(form)
    proxy.$modal.msgSuccess('修改已提交审核')
    open.value = false
    getList()
  })
}
const delRow = async (row) => {
  await proxy.$modal.confirm('确认删除该资源吗？（待审/驳回/下架可删除）')
  await delResource(row.id)
  proxy.$modal.msgSuccess('删除成功')
  getList()
}
const offline = async (row) => { await offlineResource(row.id); proxy.$modal.msgSuccess('已下架'); getList() }
const online = async (row) => { await onlineResource(row.id); proxy.$modal.msgSuccess('已提交上架审核'); getList() }

onMounted(getList)
</script>

<style scoped>
.hint { color: #909399; }
</style>
