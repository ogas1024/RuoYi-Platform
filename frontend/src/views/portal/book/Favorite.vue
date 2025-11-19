<template>
  <!-- 我的收藏列表 + 刷新按钮 + 卡片展示 + 分页 + 跳转图书详情 -->
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item>
        <el-button type="primary" icon="Refresh" @click="getList">刷新</el-button>
      </el-form-item>
    </el-form>
    <div class="card-grid" v-loading="loading">
      <div v-for="item in list" :key="item.id" class="book-card" @click="openDetail(item)">
        <div class="cover" :style="{ backgroundImage: item.coverUrl ? `url(${item.coverUrl})` : 'none' }"></div>
        <div class="info">
          <div class="title">{{ item.title }}</div>
          <div class="meta">{{ item.author }} · {{ item.publisher || '-' }}</div>
        </div>
      </div>
    </div>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>
  </div>
</template>

<script setup>
// listFavoriteLibraryPortal -> /portal/library/favorite/list
// getList：分页查询；openDetail：路由跳转到详情页
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {listFavoriteLibraryPortal} from '@/api/portal/library'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({pageNum: 1, pageSize: 10})

const getList = async () => {
  loading.value = true
  try {
    const {rows, total: t} = await listFavoriteLibraryPortal(queryParams.value)
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}
const openDetail = (item) => router.push({path: '/portal/library/detail', query: {id: item.id}})
onMounted(getList)
</script>

<style scoped>
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
</style>
