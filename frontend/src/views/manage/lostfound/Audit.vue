<template>
  <div class="app-container">
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="类型">
        <el-select v-model="q.type" clearable placeholder="全部">
          <el-option label="丢失" value="lost"/>
          <el-option label="捡到" value="found"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type==='lost'?'danger':'success'">{{ row.type === 'lost' ? '丢失' : '捡到' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="220"/>
      <el-table-column prop="createBy" label="提交人" width="120"/>
      <el-table-column prop="createTime" label="提交时间" width="170"/>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openDetail(row)" v-hasPermi="['manage:lostfound:get']">
            详情
          </el-button>
          <el-button size="small" text type="success" @click="approve(row)"
                     v-hasPermi="['manage:lostfound:audit:approve']">通过
          </el-button>
          <el-button size="small" text type="danger" @click="openReject(row)"
                     v-hasPermi="['manage:lostfound:audit:reject']">驳回
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="page.pageNum" v-model:limit="page.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="detailOpen" title="详情" width="720px">
      <div v-if="detail">
        <h3>{{ detail.title }}</h3>
        <div class="meta">类型：{{ detail.type === 'lost' ? '丢失' : '捡到' }} · 地点：{{ detail.location || '-' }}</div>
        <div class="contact" v-if="detail.contactInfo">联系方式：{{ detail.contactInfo }}</div>
        <el-divider/>
        <div style="white-space:pre-wrap">{{ detail.content }}</div>
        <div class="images" v-if="images.length">
          <el-image v-for="img in images" :key="img.id" :src="img.url" :preview-src-list="images.map(i=>i.url)"
                    style="width:120px;height:120px;margin:4px;object-fit:cover"/>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="rejectOpen" title="驳回理由" width="480px">
      <el-input v-model="reason" type="textarea" :rows="4" placeholder="必填"/>
      <template #footer>
        <el-button @click="rejectOpen=false">取消</el-button>
        <el-button type="primary" @click="doReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {listAuditLostFound, getLostFound, approveLostFound, rejectLostFound} from '@/api/manage/lostfound'
import {ElMessage} from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = reactive({pageNum: 1, pageSize: 10})
const q = ref({type: ''})

const getList = async () => {
  loading.value = true
  try {
    const {rows, total: t} = await listAuditLostFound({...q.value, ...page})
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}
const resetQuery = () => {
  q.value = {type: ''};
  page.pageNum = 1;
  getList()
}

const detailOpen = ref(false)
const detail = ref(null)
const images = ref([])
const openDetail = async (row) => {
  const res = await getLostFound(row.id)
  const data = res.data || res
  detail.value = data.item
  images.value = data.images || []
  detailOpen.value = true
}

const approve = async (row) => {
  await approveLostFound(row.id)
  ElMessage.success('已通过')
  getList()
}

const rejectOpen = ref(false)
const currentId = ref(null)
const reason = ref('')
const openReject = (row) => {
  currentId.value = row.id;
  reason.value = '';
  rejectOpen.value = true
}
const doReject = async () => {
  await rejectLostFound(currentId.value, reason.value)
  ElMessage.success('已驳回')
  rejectOpen.value = false
  getList()
}

onMounted(getList)
</script>
