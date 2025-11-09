<template>
  <!-- 门户公告列表 + 搜索 + 分页 + 跳转详情/外链 -->
  <div class="app-container">
    <el-form :inline="true" :model="query" class="mb10">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="搜索标题" clearable @keyup.enter="handleQuery"
                  style="width: 260px"/>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="query.includeExpired">包含过期</el-checkbox>
      </el-form-item>
      <el-form-item label="已读状态">
        <el-select v-model="query.read" placeholder="全部" clearable style="width: 160px"
                   @clear="() => { query.read = undefined }">
          <el-option :value="true" label="已读"/>
          <el-option :value="false" label="未读"/>
        </el-select>
      </el-form-item>
      <el-form-item label="排序">
        <el-select v-model="query.orderBy" placeholder="发布时间" style="width: 160px">
          <el-option label="发布时间" value="publishTime"/>
          <el-option label="更新时间" value="updateTime"/>
          <el-option label="过期时间" value="expireTime"/>
        </el-select>
        <el-select v-model="query.orderDir" placeholder="降序" style="width: 120px" class="ml8">
          <el-option label="降序" value="desc"/>
          <el-option label="升序" value="asc"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading">
      <el-table-column label="标题" min-width="320">
        <template #default="{ row }">
          <el-tag v-if="row.pinned" type="warning" class="mr5">置顶</el-tag>
          <span class="link" @click="openDetail(row)">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="expired" label="过期" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.expired" type="info">是</el-tag>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column prop="expireTime" label="过期时间" width="170"/>
      <el-table-column prop="read" label="已读" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.read" type="success">是</el-tag>
          <el-tag v-else type="danger" plain>否</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="170"/>
      <el-table-column prop="updateTime" label="更新时间" width="170"/>
      <el-table-column prop="editCount" label="编辑次数" width="100"/>
      <el-table-column prop="readCount" label="阅读次数" width="100"/>
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
        <h2>
          <el-tag v-if="detail.notice?.pinned" type="warning" class="mr5">置顶</el-tag>
          {{ detail.notice?.title }}
        </h2>
        <div class="meta">
          <span>发布时间：{{ detail.notice?.publishTime }}</span>
          <el-tag v-if="detail.notice?.expired" type="info" class="ml8">已过期</el-tag>
        </div>
        <div class="meta mt8">
          <span>更新时间：{{ detail.notice?.updateTime }}</span>
          <span class="ml16">编辑次数：{{ detail.notice?.editCount }}</span>
          <span class="ml16">阅读次数：{{ detail.notice?.readCount }}</span>
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
// 见 @/api/portal/notice -> /portal/notice/**
getList：分页查询；openDetail/openLink：查看详情或外链
import {ref, reactive, onMounted} from 'vue'
import {listNotice, getNotice} from '@/api/portal/notice'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  includeExpired: false,
  read: undefined,
  orderBy: 'publishTime',
  orderDir: 'desc'
})

const show = ref(false)
const detail = reactive({})

function getList() {
  loading.value = true
  listNotice(query).then(res => {
    list.value = res.rows || [];
    total.value = res.total || 0
  }).finally(() => loading.value = false)
}

function handleQuery() {
  query.pageNum = 1;
  getList()
}

function resetQuery() {
  query.keyword = '';
  query.includeExpired = false;
  query.read = undefined;
  query.orderBy = 'publishTime';
  query.orderDir = 'desc';
  handleQuery()
}

function openDetail(row) {
  getNotice(row.id).then(res => {
    Object.assign(detail, res.data || {});
    show.value = true;
    getList()
  })
}

onMounted(getList)
</script>

<style scoped>
.mb10 {
  margin-bottom: 10px
}

.ml8 {
  margin-left: 8px
}

.ml16 {
  margin-left: 16px
}

.mt8 {
  margin-top: 8px
}

.mr5 {
  margin-right: 5px
}

.link {
  cursor: pointer;
  color: var(--el-color-primary)
}

.content :deep(img) {
  max-width: 100%
}

.mt16 {
  margin-top: 16px
}
</style>
