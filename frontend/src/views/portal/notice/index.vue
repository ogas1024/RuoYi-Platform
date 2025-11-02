<template>
  <div class="app-container">
    <el-form :inline="true" :model="query" class="mb10">
      <el-form-item>
        <el-input v-model="query.keyword" placeholder="搜索标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading">
      <el-table-column label="标题" min-width="320">
        <template #default="{ row }">
          <el-tag v-if="row.pinned" type="warning" class="mr5">置顶</el-tag>
          <span class="link" @click="openDetail(row)">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="170" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.expired" type="info">已过期</el-tag>
          <el-tag v-else-if="row.read" type="success">已读</el-tag>
          <el-tag v-else type="danger" plain>未读</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="getList"
    />

    <el-drawer v-model="show" title="公告详情" size="60%">
      <template #default>
        <h2>{{ detail.notice?.title }}</h2>
        <div class="meta">
          <span>发布时间：{{ detail.notice?.publishTime }}</span>
          <el-tag v-if="detail.notice?.expired" type="info" class="ml8">已过期</el-tag>
        </div>
        <div class="content" v-html="detail.notice?.contentHtml"></div>
        <div v-if="(detail.attachments||[]).length" class="mt16">
          <h4>附件</h4>
          <ul>
            <li v-for="a in detail.attachments" :key="a.id">
              <a :href="a.fileUrl" target="_blank">{{ a.fileName }}</a>
            </li>
          </ul>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listNotice, getNotice } from '@/api/manage/notice'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const show = ref(false)
const detail = reactive({})

function getList() {
  loading.value = true
  listNotice(query).then(res => { list.value = res.rows||[]; total.value = res.total||0 }).finally(()=>loading.value=false)
}
function handleQuery(){ query.pageNum=1; getList() }

function openDetail(row){
  getNotice(row.id).then(res => { Object.assign(detail, res.data||{}); show.value = true; getList() })
}

onMounted(getList)
</script>

<style scoped>
.mb10{ margin-bottom:10px }
.ml8{ margin-left:8px }
.link{ cursor:pointer; color: var(--el-color-primary) }
.content :deep(img){ max-width:100% }
.mt16{ margin-top:16px }
</style>

