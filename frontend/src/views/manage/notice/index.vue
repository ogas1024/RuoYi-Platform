<template>
  <div class="app-container">
    <el-form :inline="true" :model="query" class="mb10">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="标题关键词" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="默认已发布" clearable style="width: 160px">
          <el-option :value="1" label="已发布" />
          <el-option :value="0" label="草稿" />
          <el-option :value="2" label="撤回" />
          <el-option :value="3" label="已过期" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="query.includeExpired">包含过期</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery" v-hasPermi="['manage:notice:list']">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" icon="Plus" @click="handleAdd" v-hasPermi="['manage:notice:add']">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="240">
        <template #default="scope">
          <el-tag v-if="scope.row.pinned" type="warning" class="mr5">置顶</el-tag>
          <span class="link" @click="viewDetail(scope.row)">{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.status===1" type="success">已发布</el-tag>
          <el-tag v-else-if="row.status===0">草稿</el-tag>
          <el-tag v-else-if="row.status===2" type="warning">撤回</el-tag>
          <el-tag v-else>其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expired" label="过期" width="70">
        <template #default="{ row }">
          <el-tag v-if="row.expired" type="info">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="read" label="已读" width="70">
        <template #default="{ row }">
          <el-tag v-if="row.read" type="success">已读</el-tag>
          <el-tag v-else type="danger" plain>未读</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="170" />
      <el-table-column prop="updateTime" label="更新时间" width="170" />
      <el-table-column prop="editCount" label="编辑次数" width="90" />
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="View" @click="viewDetail(row)" v-hasPermi="['manage:notice:get']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="editRow(row)" v-hasPermi="['manage:notice:edit']">编辑</el-button>
          <el-button link type="primary" icon="Upload" @click="publishRow(row)" v-if="row.status!==1" v-hasPermi="['manage:notice:publish']">发布</el-button>
          <el-button link type="warning" icon="Download" @click="retractRow(row)" v-if="row.status===1" v-hasPermi="['manage:notice:publish']">撤回</el-button>
          <el-button link type="warning" icon="Top" @click="pinRow(row, !row.pinned)" v-if="row.status===1" v-hasPermi="['manage:notice:pin']">{{ row.pinned ? '取消置顶' : '置顶' }}</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(row)" v-hasPermi="['manage:notice:remove']">删除</el-button>
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
  </div>
  
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listNotice, publishNotice, retractNotice, pinNotice, delNotice } from '@/api/manage/notice'
import { ElMessageBox, ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', status: undefined, includeExpired: false })

function getList() {
  loading.value = true
  listNotice(query).then(res => {
    list.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => loading.value = false)
}

function handleQuery() { query.pageNum = 1; getList() }
function resetQuery() { query.keyword = ''; query.status = undefined; query.includeExpired = false; handleQuery() }

function publishRow(row) {
  publishNotice(row.id).then(() => { ElMessage.success('发布成功'); getList() })
}

function retractRow(row) {
  retractNotice(row.id).then(() => { ElMessage.success('撤回成功'); getList() })
}

function pinRow(row, pinned) {
  pinNotice(row.id, pinned).then(() => { ElMessage.success(pinned ? '已置顶' : '已取消置顶'); getList() })
}

function delRow(row) {
  ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' }).then(() => {
    delNotice(row.id).then(() => { ElMessage.success('删除成功'); getList() })
  }).catch(() => {})
}

function viewDetail(row) {
  // 最小实现：调用详情接口记录已读，并弹出内容摘要
  import('@/api/manage/notice').then(({ getNotice }) => {
    getNotice(row.id).then(res => {
      ElMessage.success('已记录已读')
      getList()
      // 可根据需要展示弹窗，这里简化为提示
    })
  })
}

function handleAdd() { window.location.hash = '#/manage/notice/edit' }
function editRow(row) { window.location.hash = `#/manage/notice/edit?id=${row.id}` }

onMounted(getList)
</script>

<style scoped>
.mb10 { margin-bottom: 10px; }
.mr5 { margin-right: 5px; }
.link { cursor: pointer; color: var(--el-color-primary); }
</style>
