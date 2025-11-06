<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="已驳回" :value="2"/>
          <el-option label="已下架" :value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">筛选</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="isbn13" label="ISBN-13" width="140"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="author" label="作者" width="160"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }"><el-tag :type="row.status===2?'danger':'info'">{{ row.status===2?'已驳回':'已下架' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="320">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openDetail(row)" v-hasPermi="['manage:library:get']">查看</el-button>
          <el-button size="small" type="primary" text @click="online(row)" v-hasPermi="['manage:library:online']">上架(待审)</el-button>
          <el-button size="small" type="danger" text @click="remove(row)" v-hasPermi="['manage:library:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <el-dialog v-model="detailOpen" title="图书详情" width="820px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ISBN-13">{{ detail.isbn13 }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ detail.author }}</el-descriptions-item>
        <el-descriptions-item label="出版社">{{ detail.publisher || '-' }}</el-descriptions-item>
        <el-descriptions-item label="驳回原因">{{ detail.auditReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)">{{ statusText(detail.status) }}</el-tag>
        </el-descriptions-item>
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
            <el-button v-if="row.assetType==='0'" size="small" type="primary" text @click="downloadAsset(detail.id, row.id)" v-hasPermi="['manage:library:download']">下载</el-button>
            <el-button v-else size="small" type="success" text @click="openLink(row.linkUrl)">打开外链</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listLibrary, onlineLibrary, delLibrary, getLibrary, listLibraryAssets } from '@/api/manage/library'
import { openExternal } from '@/utils/url'
import { getToken } from '@/utils/auth'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, status: null })
const detailOpen = ref(false)
const detail = ref({})
const assets = ref([])

const getList = async () => {
  loading.value = true
  try {
    const { rows, total: t } = await listLibrary(queryParams.value)
    list.value = rows || []
    total.value = t || 0
  } finally { loading.value = false }
}
const resetQuery = () => { queryParams.value = { pageNum: 1, pageSize: 10, status: null }; getList() }
const online = async (row) => { await onlineLibrary(row.id); getList() }
const remove = async (row) => { await delLibrary(row.id); getList() }
const openDetail = async (row) => {
  const { data } = await getLibrary(row.id)
  detail.value = data || {}
  const res = await listLibraryAssets(row.id)
  assets.value = res.data || []
  detailOpen.value = true
}
const downloadAsset = (bookId, assetId) => {
  const base = import.meta.env.VITE_APP_BASE_API || ''
  const url = `${base}/manage/library/${bookId}/download?assetId=${assetId}&token=${encodeURIComponent(getToken() || '')}`
  window.open(url, '_blank')
}
const openLink = (link) => { openExternal(link) }
onMounted(getList)

// 统一的状态展示
const statusText = s => ({ 0:'待审', 1:'已通过', 2:'已驳回', 3:'已下架' })[Number(s)] || '-'
const statusType = s => ({ 0:'warning', 1:'success', 2:'danger', 3:'info' })[Number(s)] || 'info'
</script>
