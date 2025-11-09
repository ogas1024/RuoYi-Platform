<template>
  <!-- 工具栏（发布/我的） + 筛选（类型/包含已解决/关键字） + 列表 + 详情弹窗 + 分页 -->
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="goEdit">发布事物</el-button>
      <el-button text icon="User" @click="goMy">我的发布</el-button>
    </div>
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="类型">
        <el-select v-model="q.type" clearable placeholder="全部">
          <el-option label="丢失" value="lost"/>
          <el-option label="捡到" value="found"/>
        </el-select>
      </el-form-item>
      <el-form-item label="包含已解决">
        <el-switch v-model="includeSolved" @change="handleQuery"/>
      </el-form-item>
      <el-form-item label="关键字">
        <el-input v-model="q.keyword" placeholder="标题/正文/联系方式" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <!-- <el-table-column prop="id" label="ID" width="80"/> -->
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type==='lost'?'danger':'success'">{{ row.type === 'lost' ? '丢失' : '捡到' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="200"/>
      <el-table-column prop="location" label="地点" min-width="120"/>
      <el-table-column prop="publishTime" label="发布时间" width="170"/>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.solvedFlag==1" type="info">已解决</el-tag>
          <el-tag v-else type="warning">未解决</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="q.pageNum" v-model:limit="q.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" title="详情" width="720px">
      <div v-if="detail">
        <h3>{{ detail.title }}</h3>
        <div class="meta">类型：{{ detail.type === 'lost' ? '丢失' : '捡到' }} · 地点：{{ detail.location || '-' }} ·
          时间：{{ detail.lostTime || '-' }}
        </div>
        <div class="contact" v-if="detail.contactInfo">联系方式：{{ detail.contactInfo }}</div>
        <el-divider/>
        <div class="content" style="white-space:pre-wrap">{{ detail.content }}</div>
        <div class="images" v-if="images.length">
          <el-image v-for="img in images" :key="img.id" :src="img.url" :preview-src-list="images.map(i=>i.url)"
                    style="width:120px;height:120px;margin:4px;object-fit:cover"/>
        </div>
      </div>
      <template #footer>
        <el-button @click="open=false">关闭</el-button>
      </template>
    </el-dialog>
  </div>

</template>

<script setup>
// listLostFoundPortal/getLostFoundPortal -> /portal/lostfound/**

// - getList：分页查询；openDetail：详情含图片；goEdit/goMy：跳转到发布页/我的发布
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {listLostFoundPortal, getLostFoundPortal} from '@/api/portal/lostfound'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const includeSolved = ref(false)
const q = ref({pageNum: 1, pageSize: 10, type: '', keyword: '', solvedFlag: 0})

const getList = async () => {
  loading.value = true
  try {
    q.value.solvedFlag = includeSolved.value ? undefined : 0
    const {rows, total: t} = await listLostFoundPortal(q.value)
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}
const handleQuery = () => {
  q.value.pageNum = 1;
  getList()
}
const resetQuery = () => {
  q.value = {pageNum: 1, pageSize: 10, type: '', keyword: '', solvedFlag: includeSolved.value ? undefined : 0};
  getList()
}

const open = ref(false)
const detail = ref(null)
const images = ref([])
const openDetail = async (row) => {
  const res = await getLostFoundPortal(row.id)
  const data = res.data || res
  detail.value = data.item
  images.value = data.images || []
  open.value = true
}

const goEdit = () => router.push({path: '/portal/lostfound/edit'})
const goMy = () => router.push({path: '/portal/lostfound/my'})

onMounted(getList)
</script>
