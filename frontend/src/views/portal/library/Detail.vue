<template>
  <div class="app-container" v-loading="loading">
    <el-button link type="primary" icon="Back" @click="$router.back()">返回</el-button>
    <div class="detail">
      <div class="cover" :style="{ backgroundImage: book.coverUrl ? `url(${book.coverUrl})` : 'none' }"></div>
      <div class="info">
        <div class="title">{{ book.title }}</div>
        <div class="meta">ISBN：{{ book.isbn13 }} · 作者：{{ book.author }} · 出版社：{{ book.publisher || '-' }} · 年：{{ book.publishYear || '-' }}</div>
        <div class="meta">上传者：{{ book.uploaderName || '-' }} · 上传时间：{{ book.createTime || '-' }}</div>
        <div class="actions">
          <el-button type="primary" @click="download()">直接下载</el-button>
          <el-button :type="favorite?'warning':'default'" @click="toggleFav">{{ favorite ? '取消收藏' : '收藏' }}</el-button>
        </div>
        <div class="assets" v-if="assets.length">
          <div class="label">可下载格式：</div>
          <el-space>
            <el-button v-for="a in fileAssets" :key="a.id" size="small" @click="download(a.id)">{{ a.format?.toUpperCase() || '文件' }}</el-button>
            <el-button v-for="a in linkAssets" :key="'l'+a.id" size="small" type="success" @click="openLink(a.linkUrl)">外链</el-button>
          </el-space>
        </div>
      </div>
    </div>
    <div class="summary">
      <div class="label">简介</div>
      <div class="content">{{ book.summary || '（暂无简介）' }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { openExternal } from '@/utils/url'
import { useRoute } from 'vue-router'
import { getLibraryPortal, favoriteLibraryPortal } from '@/api/portal/library'
import { getToken } from '@/utils/auth'

const route = useRoute()
const loading = ref(false)
const book = ref({})
const assets = ref([])
const favorite = ref(false)

const fetch = async () => {
  loading.value = true
  try {
    const { data } = await getLibraryPortal(route.query.id)
    book.value = data.book || {}
    assets.value = data.assets || []
    favorite.value = !!data.favorite
  } finally { loading.value = false }
}

const download = (assetId) => {
  const base = import.meta.env.VITE_APP_BASE_API || ''
  const sep = assetId ? `?assetId=${assetId}` : ''
  const url = `${base}/portal/library/${book.value.id}/download${sep}${sep ? '&' : '?'}token=${encodeURIComponent(getToken() || '')}`
  window.open(url, '_blank')
}
const openLink = (link) => { openExternal(link) }
const fileAssets = computed(() => assets.value.filter(a => a.assetType === '0'))
const linkAssets = computed(() => assets.value.filter(a => a.assetType === '1'))
const toggleFav = async () => {
  await favoriteLibraryPortal(book.value.id, !favorite.value)
  favorite.value = !favorite.value
}

onMounted(fetch)
</script>

<style scoped>
.detail { display: flex; gap: 16px; margin-top: 8px; }
.cover { width: 120px; height: 160px; background: #f5f7fa center/cover no-repeat; border: 1px solid #eee; }
.info { flex: 1; }
.title { font-size: 18px; font-weight: 600; margin-bottom: 6px; }
.meta { color: #909399; font-size: 12px; margin-bottom: 10px; }
.actions { display: flex; gap: 8px; margin-bottom: 10px; }
.assets { display: flex; align-items: center; gap: 8px; }
.assets .label { color: #909399; font-size: 12px; }
.summary { margin-top: 16px; }
.summary .label { font-weight: 600; margin-bottom: 6px; }
.content { white-space: pre-wrap; }
</style>
