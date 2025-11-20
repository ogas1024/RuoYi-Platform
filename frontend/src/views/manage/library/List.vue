<template>
  <!-- 关键词筛选 + 已通过图书列表 + 操作列（查看/下架） + 分页 + 详情/下架原因弹窗 -->
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="关键字">
        <el-input v-model="queryParams.keyword" placeholder="标题/作者/ISBN" clearable @keyup.enter="getList"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="isbn13" label="ISBN-13" width="140"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="author" label="作者" width="160"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openDetail(row)" v-hasPermi="['manage:library:get']">
            查看
          </el-button>
          <el-button size="small" text type="info" @click="openOffline(row)" v-if="row.status===1"
                     v-hasPermi="['manage:library:offline']">下架
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>
  </div>

  <el-dialog v-model="detailOpen" title="图书详情" width="820px" append-to-body>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="ISBN-13">{{ detail.isbn13 }}</el-descriptions-item>
      <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
      <el-descriptions-item label="作者">{{ detail.author }}</el-descriptions-item>
      <el-descriptions-item label="出版社">{{ detail.publisher || '-' }}</el-descriptions-item>
      <el-descriptions-item label="出版年">{{ detail.publishYear || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusType(detail.status)">{{ statusText(detail.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="下载数">{{ detail.downloadCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="简介" :span="2">
        <div class="content">{{ detail.summary || '（暂无简介）' }}</div>
      </el-descriptions-item>
    </el-descriptions>
    <el-table :data="assets" class="mt12" size="small" border>
      <el-table-column prop="assetType" label="类型" width="80">
        <template #default="{ row }">{{ row.assetType === '0' ? '文件' : '外链' }}</template>
      </el-table-column>
      <el-table-column prop="format" label="格式" width="100"/>
      <el-table-column prop="fileUrl" label="文件URL"/>
      <el-table-column prop="linkUrl" label="外链URL"/>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-if="row.assetType==='0'" size="small" type="primary" text
                     @click="downloadAsset(detail.id, row.id)" v-hasPermi="['manage:library:download']">下载
          </el-button>
          <el-button v-else size="small" type="success" text @click="openLink(row.linkUrl)">打开外链</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>

  <el-dialog v-model="offlineOpen" title="下架原因" width="460px" append-to-body>
    <el-form ref="offlineFormRef" :model="offlineForm" :rules="offlineRules" label-width="80px">
      <el-form-item label="原因" prop="reason">
        <el-input v-model="offlineForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit
                  placeholder="请输入下架原因（必填）"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="cancelOffline">取消</el-button>
      <el-button type="primary" @click="submitOffline">确 定</el-button>
    </template>
  </el-dialog>

</template>

<script setup>
// listLibrary/getLibrary/listLibraryAssets/offlineLibrary -> /manage/library/**
// - getList：分页查询；openDetail：查看详情与资产清单；submitOffline：提交下架并刷新
import {ref, onMounted, reactive} from 'vue'
import {listLibrary, offlineLibrary, getLibrary, listLibraryAssets} from '@/api/manage/library'
import {openExternal} from '@/utils/url'
import {getToken} from '@/utils/auth'

const loading = ref(false)
const list = ref([])
const total = ref(0)
// 图书列表仅展示“已通过”
const queryParams = ref({pageNum: 1, pageSize: 10, keyword: '', status: 1})

const getList = async () => {
  loading.value = true
  try {
    const {rows, total: t} = await listLibrary(queryParams.value)
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}
const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 10, keyword: '', status: 1};
  getList()
}
const statusText = s => ({0: '待审', 1: '已通过', 2: '驳回', 3: '已下架'})[s] || '-'
const statusType = s => ({0: 'warning', 1: 'success', 2: 'danger', 3: 'info'})[s] || 'info'
// 下架理由弹窗
const offlineOpen = ref(false)
const offlineFormRef = ref()
const offlineForm = ref({reason: ''})
const offlineRules = reactive({reason: [{required: true, message: '请输入下架原因', trigger: 'blur'}]})
const offlineRow = ref(null)
const openOffline = (row) => {
  offlineRow.value = row;
  offlineForm.value.reason = '';
  offlineOpen.value = true
}
const cancelOffline = () => {
  offlineOpen.value = false
}
const submitOffline = () => {
  offlineFormRef.value.validate(async valid => {
    if (!valid) return
    await offlineLibrary(offlineRow.value.id, offlineForm.value.reason)
    offlineOpen.value = false
    getList()
  })
}

// 查看详情弹窗
const detailOpen = ref(false)
const detail = ref({})
const assets = ref([])
const openDetail = async (row) => {
  const {data} = await getLibrary(row.id)
  detail.value = data || {}
  const res = await listLibraryAssets(row.id)
  assets.value = res.data || []
  detailOpen.value = true
}
const downloadAsset = (bookId, assetId) => {
  const base = import.meta.env.VITE_APP_BASE_API || '/prod-api'
  const url = `${base}/manage/library/${bookId}/download?assetId=${assetId}&token=${encodeURIComponent(getToken() || '')}`
  window.open(url, '_blank')
}
const openLink = (link) => {
  openExternal(link)
}

onMounted(getList)
</script>
