<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">已上架资源</span>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in allowedMajors" :key="m.id" :label="m.majorName" :value="m.id" />
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
      <el-table-column label="操作" fixed="right" width="460">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="openDetail(scope.row)" v-hasPermi="['manage:courseResource:query']">查看</el-button>
          <el-button link type="primary" icon="Download" @click="downloadRow(scope.row)" v-hasPermi="['manage:courseResource:download']">下载</el-button>
          <el-button link :type="scope.row.isBest===1?'info':'success'" icon="Star" @click="toggleBest(scope.row)" v-hasPermi="['manage:courseResource:best']">{{ scope.row.isBest===1?'取消最佳':'设为最佳' }}</el-button>
          <el-button link type="warning" icon="Bottom" @click="openOffline(scope.row)" v-hasPermi="['manage:courseResource:offline']">下架</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailOpen" title="资源详情" width="720px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="资源名称" :span="2">{{ detail.resourceName }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag type="success">已上架</el-tag></el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.resourceType===1?'外链':'文件' }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ detail.majorName || detail.majorId }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ detail.courseName || detail.courseId }}</el-descriptions-item>
        <el-descriptions-item label="上传者">{{ detail.uploaderName }}</el-descriptions-item>
        <el-descriptions-item label="上架时间">{{ detail.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="下载次数">{{ detail.downloadCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ detail.description || '（无）' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailOpen=false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>

    <el-dialog v-model="offlineOpen" title="下架原因" width="460px" append-to-body>
      <el-form ref="offlineFormRef" :model="offlineForm" :rules="offlineRules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input v-model="offlineForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="请输入下架原因（必填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelOffline">取消</el-button>
        <el-button type="primary" @click="submitOffline">确 定</el-button>
      </template>
    </el-dialog>
  </template>

<script setup name="ResourceApproved">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listResource, updateResource, delResource, offlineResource, setBestResource, unsetBestResource, getResource } from '@/api/manage/courseResource'
import { getToken } from '@/utils/auth'
import { listMajor } from '@/api/manage/major'
import { listMyMajors } from '@/api/manage/majorLead'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, status: 1, majorId: undefined, courseName: '', keyword: '' })
const allowedMajors = ref([])
const userStore = useUserStore()

const open = ref(false)
const formRef = ref()
const form = reactive({ id: undefined, resourceName: '', description: '' })
const rules = { resourceName: [{ required: true, message: '请填写资源名称', trigger: 'blur' }], description: [{ required: true, message: '请填写简介', trigger: 'blur' }] }
const detailOpen = ref(false)
const detail = ref({})

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
const downloadRow = (row) => {
  const token = getToken()
  const url = `${import.meta.env.VITE_APP_BASE_API}/manage/courseResource/${row.id}/download?token=${encodeURIComponent(token)}`
  window.open(url, '_blank')
}
const openDetail = async (row) => {
  try {
    const { data } = await getResource(row.id)
    detail.value = data || row
  } catch (e) {
    detail.value = row || {}
  }
  detailOpen.value = true
}
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
// 下架弹窗
const offlineOpen = ref(false)
const offlineFormRef = ref()
const offlineForm = ref({ reason: '' })
const offlineRules = reactive({ reason: [{ required: true, message: '请输入下架原因', trigger: 'blur' }] })
const offlineRowRef = ref(null)
const openOffline = (row) => { offlineRowRef.value = row; offlineForm.value.reason=''; offlineOpen.value = true }
const cancelOffline = () => { offlineOpen.value = false }
const submitOffline = () => {
  offlineFormRef.value.validate(async valid => {
    if (!valid) return
    await offlineResource(offlineRowRef.value.id, offlineForm.value.reason)
    offlineOpen.value = false
    proxy.$modal.msgSuccess('已下架')
    getList()
  })
}
const toggleBest = async (row) => {
  if (row.isBest === 1) {
    await unsetBestResource(row.id)
    proxy.$modal.msgSuccess('已取消最佳')
  } else {
    await setBestResource(row.id)
    proxy.$modal.msgSuccess('已设为最佳')
  }
  getList()
}

const getAllowedMajors = async () => {
  const roles = userStore.roles || []
  const isAdmin = roles.includes('admin') || roles.includes('super_admin')
  if (isAdmin) {
    const resp = await listMajor({ pageNum: 1, pageSize: 100 });
    allowedMajors.value = resp.rows || []
  } else if (roles.includes('major_lead')) {
    const resp = await listMyMajors();
    allowedMajors.value = resp.data || resp.rows || []
  } else {
    allowedMajors.value = []
  }
}
onMounted(async () => { await getAllowedMajors(); await getList() })
</script>

<style scoped>
.hint { color: #909399; }
</style>
