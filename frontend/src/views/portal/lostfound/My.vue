<template>
  <div class="app-container">
    <div class="toolbar"><el-button type="primary" icon="Plus" @click="goCreate">发布事物</el-button></div>
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="状态">
        <el-select v-model="q.status" clearable placeholder="全部">
          <el-option label="草稿" :value="0"/>
          <el-option label="待审核" :value="1"/>
          <el-option label="已发布" :value="2"/>
          <el-option label="驳回" :value="3"/>
          <el-option label="下架" :value="4"/>
        </el-select>
      </el-form-item>
      <el-form-item label="是否已解决">
        <el-select v-model="q.solvedFlag" clearable placeholder="全部">
          <el-option label="未解决" :value="0"/>
          <el-option label="已解决" :value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }"><el-tag :type="row.type==='lost'?'danger':'success'">{{ row.type==='lost'?'丢失':'捡到' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="200"/>
      <el-table-column prop="status" label="审核状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status===1" type="warning">待审</el-tag>
          <el-tag v-else-if="row.status===2" type="success">已发布</el-tag>
          <el-tag v-else-if="row.status===3" type="danger">驳回</el-tag>
          <el-tag v-else-if="row.status===4" type="info">下架</el-tag>
          <el-tag v-else>草稿</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="已解决" width="90">
        <template #default="{ row }"><el-tag :type="row.solvedFlag==1?'':'warning'">{{ row.solvedFlag==1?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openEdit(row)" :disabled="row.solvedFlag==1">编辑</el-button>
          <el-button size="small" text type="success" @click="solve(row)" :disabled="row.solvedFlag==1 || row.status!==2">标记已解决</el-button>
          <el-button size="small" text type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="q.pageNum" v-model:limit="q.pageSize" @pagination="getList"/>

    <!-- 编辑对话框（与后台一致） -->
    <el-dialog v-model="editOpen" title="编辑条目" width="760px" :destroy-on-close="true">
      <LostFoundEditor :key="editForm.id || 'new'" v-model="editForm" upload-scope="portal" />
      <template #footer>
        <el-button @click="editOpen=false">取消</el-button>
        <el-button type="primary" @click="doEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { myLostFoundPortal, removeLostFoundPortal, solveLostFoundPortal, getLostFoundPortal, updateLostFoundPortal } from '@/api/portal/lostfound'
import { ElMessage, ElMessageBox } from 'element-plus'
import LostFoundEditor from '@/components/lostfound/Editor.vue'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const q = ref({ pageNum:1, pageSize:10, status: '', solvedFlag: '' })

const getList = async () => {
  loading.value = true
  try {
    const { rows, total: t } = await myLostFoundPortal(q.value)
    list.value = rows || []
    total.value = t || 0
  } finally { loading.value = false }
}
const resetQuery = () => { q.value = { pageNum:1, pageSize:10, status: '', solvedFlag: '' }; getList() }

// 编辑对话框逻辑
const editOpen = ref(false)
const editForm = ref({ id: null, title: '', content: '', contactInfo: '', location: '', lostTime: '', images: [] })
const openEdit = async (row) => {
  const res = await getLostFoundPortal(row.id)
  const data = res.data || res
  editForm.value = { ...(data.item||{}), images: data.images || [] }
  editOpen.value = true
}
const doEdit = async () => {
  const payload = JSON.parse(JSON.stringify(editForm.value))
  payload.images = (payload.images||[]).filter(i=>i.url).map((i,idx)=>({ ...i, sortNo: idx }))
  await updateLostFoundPortal(payload.id, payload)
  ElMessage.success('已保存')
  editOpen.value = false
  getList()
}
const solve = async (row) => { await solveLostFoundPortal(row.id); ElMessage.success('已标记为已解决'); getList() }
const remove = async (row) => {
  await ElMessageBox.confirm('确认删除该条目？', '提示')
  await removeLostFoundPortal(row.id)
  ElMessage.success('删除成功')
  getList()
}
const goCreate = () => router.push({ path: '/portal/lostfound/edit' })

onMounted(getList)
</script>
