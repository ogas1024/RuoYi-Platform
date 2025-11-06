<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="goUpload">上传图书</el-button>
      <el-alert type="info" class="ml12" :closable="false" show-icon>
        <template #title>显示我上传的所有图书；驳回/下架将展示原因。</template>
      </el-alert>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="关键字">
        <el-input v-model="queryParams.keyword" placeholder="标题/作者/ISBN/关键字" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="格式">
        <el-select v-model="queryParams.format" clearable placeholder="全部">
          <el-option label="PDF" value="pdf"/>
          <el-option label="EPUB" value="epub"/>
          <el-option label="MOBI" value="mobi"/>
          <el-option label="ZIP" value="zip"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="card-grid" v-loading="loading">
      <div v-for="item in list" :key="item.id" class="book-card">
        <div class="cover" :style="coverStyle(item)"></div>
        <div class="info">
          <div class="title" :title="item.title">{{ item.title }}</div>
          <div class="meta">{{ item.author }} · {{ item.publisher || '未知出版社' }} · {{ item.publishYear || '-' }}</div>
          <div class="foot">
            <el-tag type="success" v-if="item.status===1">已通过</el-tag>
            <el-tag type="info" v-else-if="item.status===0">待审</el-tag>
            <el-tag type="danger" v-else-if="item.status===2">已驳回</el-tag>
            <el-tag type="warning" v-else-if="item.status===3">已下架</el-tag>
            <el-button size="small" type="primary" text @click="openDetail(item)">查看</el-button>
            <el-button size="small" type="warning" text @click="openEdit(item)">编辑</el-button>
          </div>
          <div class="reason" v-if="item.status===2 || item.status===3">
            <span class="label">原因：</span>
            <span>{{ item.auditReason || '无' }}</span>
          </div>
        </div>
      </div>
    </div>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { listLibraryPortal } from '@/api/portal/library'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, keyword: '', format: '' })

const ensureUserId = async () => {
  if (!userStore.id) {
    try { await userStore.getInfo() } catch (_) {}
  }
}

const getList = async () => {
  loading.value = true
  try {
    await ensureUserId()
    const params = { ...queryParams.value, uploaderId: userStore.id }
    const { rows, total: t } = await listLibraryPortal(params)
    list.value = rows || []
    total.value = t || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.value.pageNum = 1; getList() }
const resetQuery = () => { queryParams.value = { pageNum: 1, pageSize: 10, keyword: '', format: '' }; getList() }

const openDetail = (item) => {
  router.push({ path: '/portal/library/detail', query: { id: item.id } })
}
const openEdit = (item) => {
  router.push({ path: '/portal/library/edit', query: { id: item.id } })
}

const coverStyle = (item) => ({ backgroundImage: item.coverUrl ? `url(${item.coverUrl})` : 'none' })
const goUpload = () => router.push({ path: '/portal/library/upload' })

onMounted(getList)
</script>

<style scoped>
.toolbar { margin-bottom: 12px; display: flex; gap: 8px; align-items: center; }
.ml12 { margin-left: 12px; }
.card-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 12px; }
.book-card { display: flex; gap: 12px; padding: 12px; border: 1px solid #ebeef5; border-radius: 6px; background: #fff; }
.book-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.cover { width: 72px; height: 96px; background: #f5f7fa center/cover no-repeat; border: 1px solid #eee; }
.info { flex: 1; min-width: 0; }
.title { font-weight: 600; font-size: 15px; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.meta { color: #909399; font-size: 12px; margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.foot { display: flex; align-items: center; gap: 8px; }
.reason { margin-top: 6px; color: #909399; font-size: 12px; }
.reason .label { color: #909399; }
</style>
