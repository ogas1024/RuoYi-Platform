<template>
  <!-- 工具栏（上传/排行） + 搜索筛选 + 卡片列表 + 分页 + 详情页由路由进入 -->
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="goUpload">上传图书</el-button>
      <el-button text @click="goTop" icon="Trophy">下载/贡献榜</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="关键字">
        <el-input v-model="queryParams.keyword" placeholder="标题/作者/ISBN/关键字" clearable
                  @keyup.enter="handleQuery"/>
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
      <div v-for="item in list" :key="item.id" class="book-card" @click="openDetail(item)">
        <div class="cover" :style="coverStyle(item)"></div>
        <div class="info">
          <div class="title" :title="item.title">{{ item.title }}</div>
          <div class="meta">{{ item.author }} · {{ item.publisher || '未知出版社' }} · {{
              item.publishYear || '-'
            }}
          </div>
          <div class="foot">
            <span class="label">下载</span>
            <span class="count">{{ item.downloadCount || 0 }}</span>
            <el-button size="small" type="primary" text @click.stop="downloadDefault(item)">下载</el-button>
          </div>
        </div>
      </div>
    </div>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>
  </div>
</template>

<script setup>
// listLibraryPortal -> /portal/library/list；下载：/portal/library/{id}/download（需 token）

// - getList：分页查询；openDetail：跳转详情；downloadDefault：下载默认资源
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {listLibraryPortal} from '@/api/portal/library'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({pageNum: 1, pageSize: 10, keyword: '', format: ''})

const getList = async () => {
  loading.value = true
  try {
    const {rows, total: t} = await listLibraryPortal(queryParams.value)
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList()
}
const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 10, keyword: '', format: ''};
  getList()
}

const openDetail = (item) => {
  router.push({path: '/portal/library/detail', query: {id: item.id}})
}
import {getToken} from '@/utils/auth'

const downloadDefault = (item) => {
  const base = import.meta.env.VITE_APP_BASE_API || ''
  const url = `${base}/portal/library/${item.id}/download?token=${encodeURIComponent(getToken() || '')}`
  window.open(url, '_blank')
}
const coverStyle = (item) => ({backgroundImage: item.coverUrl ? `url(${item.coverUrl})` : 'none'})
const goUpload = () => router.push({path: '/portal/library/upload'})
const goTop = () => router.push({path: '/portal/library/top'})

onMounted(getList)
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
}

.book-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  cursor: pointer;
  background: #fff;
}

.book-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, .06);
}

.cover {
  width: 72px;
  height: 96px;
  background: #f5f7fa center/cover no-repeat;
  border: 1px solid #eee;
}

.info {
  flex: 1;
  min-width: 0;
}

.title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.foot {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  color: #909399;
  font-size: 12px;
}

.count {
  font-weight: 600;
}
</style>
