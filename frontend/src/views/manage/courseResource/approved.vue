<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">已上架资源</span>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程名">
        <el-input v-model="queryParams.courseName" placeholder="按课程名模糊" style="width: 200px" />
      </el-form-item>
      <el-form-item label="关键字">
        <el-input v-model="queryParams.keyword" placeholder="资源名/简介/上传者" style="width: 220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="资源名称" prop="resourceName" min-width="220"/>
      <el-table-column label="专业/课程" min-width="220">
        <template #default="scope">{{ scope.row.majorName || scope.row.majorId }} / {{ scope.row.courseName || scope.row.courseId }}</template>
      </el-table-column>
      <el-table-column label="上传者" prop="uploaderName" width="120"/>
      <el-table-column label="下载数" prop="downloadCount" width="100"/>
      <el-table-column label="上架时间" prop="publishTime" width="180"/>
      <el-table-column label="操作" fixed="right" width="240">
        <template #default="scope">
          <el-button link type="warning" icon="Bottom" @click="offlineRow(scope.row)" v-hasPermi="['manage:courseResource:offline']">下架</el-button>
          <el-button link type="primary" icon="Edit" @click="editRow(scope.row)" v-hasPermi="['manage:courseResource:edit']">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)" v-hasPermi="['manage:courseResource:remove']">删除</el-button>
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
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ResourceApproved">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listResource, updateResource, delResource, offlineResource } from '@/api/manage/courseResource'
import { listMajor } from '@/api/manage/major'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, status: 1, majorId: undefined, courseName: '', keyword: '' })
const majors = ref([])

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
const resetQuery = () => { queryParams.majorId = undefined; queryParams.courseName = ''; queryParams.keyword=''; handleQuery() }
const editRow = (row) => { Object.assign(form, { id: row.id, resourceName: row.resourceName, description: row.description }); open.value = true }
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    await updateResource(form)
    proxy.$modal.msgSuccess('已保存并提交审核')
    open.value = false
    getList()
  })
}
const delRow = async (row) => {
  await proxy.$modal.confirm('确认删除该资源吗？（请谨慎操作）')
  await delResource(row.id)
  proxy.$modal.msgSuccess('删除成功')
  getList()
}
const offlineRow = async (row) => {
  await offlineResource(row.id)
  proxy.$modal.msgSuccess('已下架')
  getList()
}

const getMajors = async () => { const resp = await listMajor({ pageNum: 1, pageSize: 100 }); majors.value = resp.rows || [] }
onMounted(async () => { await getMajors(); await getList() })
</script>

<style scoped>
.hint { color: #909399; }
</style>
