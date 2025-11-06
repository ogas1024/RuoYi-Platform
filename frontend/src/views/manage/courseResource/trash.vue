<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">回收站（已驳回/已下架）</span>
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
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="资源名称" prop="resourceName" min-width="220"/>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag type="danger" v-if="scope.row.status===2">已驳回</el-tag>
          <el-tag type="info" v-else>已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="原因" min-width="240">
        <template #default="scope">
          <span class="reason" v-if="scope.row.status===2 || scope.row.status===3">{{ scope.row.auditReason || '无' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="180"/>
      <el-table-column label="操作" fixed="right" width="360">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="openDetail(scope.row)">查看</el-button>
          <el-divider direction="vertical" />
          <el-button link type="primary" icon="Download" @click="downloadRow(scope.row)" v-hasPermi="['manage:courseResource:download']">下载</el-button>
          <el-button link type="success" icon="Top" @click="onlineRow(scope.row)" v-hasPermi="['manage:courseResource:online']">重新上架</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)" v-hasPermi="['manage:courseResource:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
    
    <!-- 详情对话框（高亮显示原因） -->
    <el-dialog v-model="detailOpen" title="资源详情" width="720px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="资源名称" :span="2">{{ detail.resourceName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="danger" v-if="detail.status===2">已驳回</el-tag>
          <el-tag type="info" v-else>已下架</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.resourceType===1?'外链':'文件' }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ detail.majorName || detail.majorId }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ detail.courseName || detail.courseId }}</el-descriptions-item>
        <el-descriptions-item label="上传者">{{ detail.uploaderName }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上架时间">{{ detail.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="下载次数">{{ detail.downloadCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="原因" :span="2">
          <div class="reason-box">
            <el-tag v-if="detail.status===2" type="danger" effect="dark" class="mr8">驳回</el-tag>
            <el-tag v-else type="info" effect="dark" class="mr8">下架</el-tag>
            <span class="reason-strong">{{ detail.auditReason || '无' }}</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ detail.description || '（无）' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailOpen=false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ResourceTrash">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listResource, delResource, onlineResource, getResource } from '@/api/manage/courseResource'
import { getToken } from '@/utils/auth'
import { listMajor } from '@/api/manage/major'
import { listMyMajors } from '@/api/manage/majorLead'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const detailOpen = ref(false)
const detail = ref({})
const queryParams = reactive({ pageNum: 1, pageSize: 10, status: undefined, majorId: undefined, courseId: undefined })
const allowedMajors = ref([])
const userStore = useUserStore()

const getList = async () => {
  loading.value = true
  try {
    // 查询已驳回(2)+已下架(3)，这里简单分两次查询合并也可；为简化用两次调用拼接
    const [rej, off] = await Promise.all([
      listResource({ ...queryParams, status: 2 }),
      listResource({ ...queryParams, status: 3 })
    ])
    const rows = (rej.rows || []).concat(off.rows || [])
    list.value = rows
    total.value = rows.length
  } finally { loading.value = false }
}
const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.majorId = undefined; queryParams.courseId = undefined; handleQuery() }
const downloadRow = (row) => {
  const token = getToken()
  const url = `${import.meta.env.VITE_APP_BASE_API}/manage/courseResource/${row.id}/download?token=${encodeURIComponent(token)}`
  window.open(url, '_blank')
}
const delRow = async (row) => { await delResource(row.id); proxy.$modal.msgSuccess('删除成功'); getList() }
const onlineRow = async (row) => { await onlineResource(row.id); proxy.$modal.msgSuccess('已提交上架审核'); getList() }
const openDetail = async (row) => {
  try {
    const { data } = await getResource(row.id)
    detail.value = data || row
  } catch (e) {
    detail.value = row || {}
  }
  detailOpen.value = true
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
.reason { color: #d43f3a; font-weight: 600; }
.reason-box { display: flex; align-items: center; gap: 8px; }
.mr8 { margin-right: 8px; }
</style>
