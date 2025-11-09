<template>
  <!-- 专业/课程筛选 + 待审资源列表 + 操作列（查看/下载/通过/驳回） + 分页 + 详情/驳回弹窗 -->
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">待审核资源</span>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in allowedMajors" :key="m.id" :label="m.majorName" :value="m.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="课程名">
        <el-input v-model="queryParams.courseName" placeholder="按课程名模糊" style="width: 200px"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="资源名称" prop="resourceName" min-width="220"/>
      <el-table-column label="专业/课程" min-width="200">
        <template #default="scope">{{ scope.row.majorName || scope.row.majorId }} /
          {{ scope.row.courseName || scope.row.courseId }}
        </template>
      </el-table-column>
      <el-table-column label="上传者" prop="uploaderName" width="120"/>
      <el-table-column label="提交时间" prop="createTime" width="180"/>
      <el-table-column label="操作" fixed="right" width="300">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="openDetail(scope.row)"
                     v-hasPermi="['manage:courseResource:query']">查看
          </el-button>
          <el-button link type="primary" icon="Download" @click="downloadRow(scope.row)"
                     v-hasPermi="['manage:courseResource:download']">下载
          </el-button>
          <el-button link type="success" icon="CircleCheck" @click="approve(scope.row)">通过</el-button>
          <el-button link type="danger" icon="Close" @click="openReject(scope.row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>
  </div>
  <!-- 详情对话框 -->
  <el-dialog v-model="detailOpen" title="资源详情" width="720px" append-to-body>
    <el-descriptions :column="2" border size="small">
      <el-descriptions-item label="资源名称" :span="2">{{ detail.resourceName }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag type="warning">待审核</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="类型">{{ detail.resourceType === 1 ? '外链' : '文件' }}</el-descriptions-item>
      <el-descriptions-item label="专业">{{ detail.majorName || detail.majorId }}</el-descriptions-item>
      <el-descriptions-item label="课程">{{ detail.courseName || detail.courseId }}</el-descriptions-item>
      <el-descriptions-item label="上传者">{{ detail.uploaderName }}</el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ detail.createTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="简介" :span="2">{{ detail.description || '（无）' }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailOpen=false">关 闭</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="rejectOpen" title="驳回原因" width="460px" append-to-body>
    <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
      <el-form-item label="原因" prop="reason">
        <el-input v-model="rejectForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit
                  placeholder="请输入驳回原因（必填）"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="cancelReject">取消</el-button>
      <el-button type="primary" @click="submitReject">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup name="ResourceAudit">
// listResource/approveResource/rejectResource/getResource -> /manage/courseResource/**
// - getList：分页拉取待审数据；approve/reject：审核通过/驳回；downloadRow：凭 token 下载
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import {listResource, approveResource, rejectResource, getResource} from '@/api/manage/courseResource'
import {getToken} from '@/utils/auth'
import {listMajor} from '@/api/manage/major'
import {listMyMajors} from '@/api/manage/majorLead'
import useUserStore from '@/store/modules/user'

const {proxy} = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({pageNum: 1, pageSize: 10, status: 0, majorId: undefined, courseId: undefined})
const allowedMajors = ref([])
const userStore = useUserStore()
const detailOpen = ref(false)
const detail = ref({})

const getList = async () => {
  loading.value = true
  try {
    const resp = await listResource(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally {
    loading.value = false
  }
}
const getAllowedMajors = async () => {
  const roles = userStore.roles || []
  const isAdmin = roles.includes('admin') || roles.includes('super_admin')
  if (isAdmin) {
    const resp = await listMajor({pageNum: 1, pageSize: 100})
    allowedMajors.value = resp.rows || []
  } else if (roles.includes('major_lead')) {
    const resp = await listMyMajors()
    allowedMajors.value = resp.data || resp.rows || []
  } else {
    allowedMajors.value = []
  }
}
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList()
}
const resetQuery = () => {
  queryParams.majorId = undefined;
  queryParams.courseId = undefined;
  handleQuery()
}
const downloadRow = (row) => {
  const token = getToken()
  const url = `${import.meta.env.VITE_APP_BASE_API}/manage/courseResource/${row.id}/download?token=${encodeURIComponent(token)}`
  window.open(url)
}
const openDetail = async (row) => {
  try {
    const {data} = await getResource(row.id)
    detail.value = data || row
  } catch (e) {
    detail.value = row || {}
  }
  detailOpen.value = true
}
const approve = async (row) => {
  await approveResource(row.id);
  proxy.$modal.msgSuccess('已通过');
  getList()
}
// 驳回弹窗
const rejectOpen = ref(false)
const rejectFormRef = ref()
const rejectForm = ref({reason: ''})
const rejectRules = reactive({reason: [{required: true, message: '请输入驳回原因', trigger: 'blur'}]})
const rejectRow = ref(null)
const openReject = (row) => {
  rejectRow.value = row;
  rejectForm.value.reason = '';
  rejectOpen.value = true
}
const cancelReject = () => {
  rejectOpen.value = false
}
const submitReject = () => {
  rejectFormRef.value.validate(async valid => {
    if (!valid) return
    await rejectResource(rejectRow.value.id, rejectForm.value.reason)
    rejectOpen.value = false
    proxy.$modal.msgSuccess('已驳回')
    getList()
  })
}

onMounted(async () => {
  await getAllowedMajors();
  await getList()
})
</script>

<style scoped>
.hint {
  color: #909399;
}
</style>
