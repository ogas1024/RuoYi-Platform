<template>
  <div class="app-container">
    <el-alert title="待审核（status=0）" type="info" show-icon class="mb8"/>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="isbn13" label="ISBN-13" width="140"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="author" label="作者" width="160"/>
      <el-table-column prop="uploaderName" label="上传者" width="120"/>
      <el-table-column prop="createTime" label="上传时间" width="180"/>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" type="primary" text @click="openDetail(row)">查看</el-button>
          <el-button size="small" type="success" text @click="approve(row)">通过</el-button>
          <el-button size="small" type="warning" text @click="openReject(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="detailOpen" title="图书详情" width="820px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ISBN-13">{{ detail.isbn13 }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ detail.author }}</el-descriptions-item>
        <el-descriptions-item label="出版社">{{ detail.publisher || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传者">{{ detail.uploaderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2"><div class="content">{{ detail.summary || '（暂无简介）' }}</div></el-descriptions-item>
      </el-descriptions>
      <el-table :data="assets" class="mt12" size="small" border>
        <el-table-column prop="assetType" label="类型" width="80">
          <template #default="{ row }">{{ row.assetType==='0'?'文件':'外链' }}</template>
        </el-table-column>
        <el-table-column prop="format" label="格式" width="100"/>
        <el-table-column prop="fileUrl" label="文件URL"/>
        <el-table-column prop="linkUrl" label="外链URL"/>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button v-if="row.assetType==='0'" size="small" type="primary" text @click="downloadAsset(detail.id, row.id)">下载</el-button>
            <el-button v-else size="small" type="success" text @click="openLink(row.linkUrl)">打开外链</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
  
    <el-dialog v-model="rejectOpen" title="驳回原因" width="460px" append-to-body>
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="请输入驳回原因（必填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelReject">取消</el-button>
        <el-button type="primary" @click="submitReject">确 定</el-button>
      </template>
    </el-dialog>
  </template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { listLibrary, approveLibrary, rejectLibrary, getLibrary, listLibraryAssets } from '@/api/manage/library'
import { openExternal } from '@/utils/url'

const loading = ref(false)
const list = ref([])
const detailOpen = ref(false)
const detail = ref({})
const assets = ref([])
const getList = async () => {
  loading.value = true
  try {
    const { rows } = await listLibrary({ pageNum: 1, pageSize: 50, status: 0 })
    list.value = rows || []
  } finally { loading.value = false }
}
const approve = async (row) => { await approveLibrary(row.id); getList() }
// 驳回理由弹窗
const rejectOpen = ref(false)
const rejectFormRef = ref()
const rejectForm = ref({ reason: '' })
const rejectRules = reactive({ reason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }] })
const rejectRow = ref(null)
const openReject = (row) => { rejectRow.value = row; rejectForm.value.reason = ''; rejectOpen.value = true }
const cancelReject = () => { rejectOpen.value = false }
const submitReject = () => {
  rejectFormRef.value.validate(async valid => {
    if (!valid) return
    await rejectLibrary(rejectRow.value.id, rejectForm.value.reason)
    rejectOpen.value = false
    getList()
  })
}
const openDetail = async (row) => {
  const { data } = await getLibrary(row.id)
  detail.value = data || {}
  const res = await listLibraryAssets(row.id)
  assets.value = res.data || []
  detailOpen.value = true
}
import { getToken } from '@/utils/auth'
const downloadAsset = (bookId, assetId) => {
  const base = import.meta.env.VITE_APP_BASE_API || ''
  const url = `${base}/manage/library/${bookId}/download?assetId=${assetId}&token=${encodeURIComponent(getToken() || '')}`
  window.open(url, '_blank')
}
const openLink = (link) => { openExternal(link) }

onMounted(getList)
</script>
