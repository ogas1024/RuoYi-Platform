<template>
  <!-- 类型/关键字筛选 + 列表 + 操作列（详情/编辑/下架/删除/标记解决） + 分页 + 详情/下架/编辑弹窗 -->
  <div class="app-container">
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="类型">
        <el-select v-model="q.type" clearable placeholder="全部">
          <el-option label="丢失" value="lost"/>
          <el-option label="捡到" value="found"/>
        </el-select>
      </el-form-item>
      <el-form-item label="关键字">
        <el-input v-model="q.keyword" placeholder="标题/正文/联系方式" clearable @keyup.enter="getList"/>
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
      <el-table-column prop="publishTime" label="发布时间" width="170"/>
      <el-table-column label="已解决" width="90">
        <template #default="{ row }">
          <el-tag :type="row.solvedFlag==1?'':'warning'">{{ row.solvedFlag == 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="360">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openDetail(row)" v-hasPermi="['manage:lostfound:get']">
            详情
          </el-button>
          <el-button size="small" text @click="openEdit(row)" v-hasPermi="['manage:lostfound:edit']">编辑</el-button>
          <el-button size="small" text type="warning" @click="openOffline(row)"
                     v-hasPermi="['manage:lostfound:offline']">下架
          </el-button>
          <el-button size="small" text type="danger" @click="remove(row)" v-hasPermi="['manage:lostfound:remove']">
            删除
          </el-button>
          <el-button
              size="small"
              text
              :type="row.solvedFlag==1 ? 'warning' : 'success'"
              @click="toggleSolved(row)"
              v-hasPermi="['manage:lostfound:solve']"
          >
            {{ row.solvedFlag == 1 ? '标记未解决' : '标记已解决' }}
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

    <el-dialog v-model="offlineOpen" title="下架理由" width="480px">
      <el-input v-model="reason" type="textarea" :rows="4" placeholder="必填"/>
      <template #footer>
        <el-button @click="offlineOpen=false">取消</el-button>
        <el-button type="primary" @click="doOffline">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editOpen" title="编辑条目" width="760px" :destroy-on-close="true">
      <LostFoundEditor :key="editForm.id || 'new'" v-model="editForm" upload-scope="manage" :max-size-mb="10"/>
      <template #footer>
        <el-button @click="editOpen=false">取消</el-button>
        <el-button type="primary" @click="doEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// listLostFound/getLostFound/offlineLostFound/removeLostFound/setSolvedLostFound/updateLostFound -> /manage/lostfound/**
// - getList：分页查询；openDetail：详情含图片；doOffline：下架；doEdit：保存编辑；remove/toggleSolved：删除/切换解决状态
import {ref, reactive, onMounted} from 'vue'
import {
  listLostFound,
  getLostFound,
  offlineLostFound,
  removeLostFound,
  setSolvedLostFound,
  updateLostFound
} from '@/api/manage/lostfound'
import {ElMessage, ElMessageBox} from 'element-plus'
import LostFoundEditor from '@/components/lostfound/Editor.vue'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = reactive({pageNum: 1, pageSize: 10})
const q = ref({type: '', keyword: ''})

const getList = async () => {
  loading.value = true
  try {
    const {rows, total: t} = await listLostFound({...q.value, ...page})
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}
const resetQuery = () => {
  q.value = {type: '', keyword: ''};
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

const offlineOpen = ref(false)
const currentId = ref(null)
const reason = ref('')
const openOffline = (row) => {
  currentId.value = row.id;
  reason.value = '';
  offlineOpen.value = true
}
const doOffline = async () => {
  await offlineLostFound(currentId.value, reason.value)
  ElMessage.success('已下架')
  offlineOpen.value = false
  getList()
}

// 编辑对话框
const editOpen = ref(false)
const editForm = ref({id: null, title: '', content: '', contactInfo: '', location: '', lostTime: '', images: []})
const openEdit = async (row) => {
  const res = await getLostFound(row.id)
  const data = res.data || res
  editForm.value = {...(data.item || {}), images: data.images || []}
  editOpen.value = true
}
const doEdit = async () => {
  const payload = JSON.parse(JSON.stringify(editForm.value))
  payload.images = (payload.images || []).filter(i => i.url).map((i, idx) => ({...i, sortNo: idx}))
  await updateLostFound(payload)
  ElMessage.success('已保存')
  editOpen.value = false
  getList()
}
const remove = async (row) => {
  await ElMessageBox.confirm('确认删除？', '提示')
  await removeLostFound(row.id)
  ElMessage.success('已删除')
  getList()
}
const toggleSolved = async (row) => {
  const target = row.solvedFlag == 1 ? 0 : 1
  await setSolvedLostFound(row.id, !!target)
  ElMessage.success('已更新解决状态')
  getList()
}

onMounted(getList)
</script>
