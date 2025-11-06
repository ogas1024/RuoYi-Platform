<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="关键字">
        <el-input v-model="queryParams.keyword" placeholder="标题/作者/ISBN" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="全部">
          <el-option label="待审" :value="0"/>
          <el-option label="已通过" :value="1"/>
          <el-option label="驳回" :value="2"/>
          <el-option label="已下架" :value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="isbn13" label="ISBN-13" width="140"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="author" label="作者" width="160"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" text type="success" @click="approve(row)" v-if="row.status===0||row.status===3" v-hasPermi="['manage:library:approve']">通过</el-button>
          <el-button size="small" text type="warning" @click="openReject(row)" v-hasPermi="['manage:library:reject']">驳回</el-button>
          <el-button size="small" text type="info" @click="openOffline(row)" v-if="row.status===1" v-hasPermi="['manage:library:offline']">下架</el-button>
          <el-button size="small" text type="primary" @click="online(row)" v-if="row.status===2||row.status===3" v-hasPermi="['manage:library:online']">上架(待审)</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
  </div>

  <el-dialog v-model="offlineOpen" title="下架原因" width="460px" append-to-body>
    <el-form ref="offlineFormRef" :model="offlineForm" :rules="offlineRules" label-width="80px">
      <el-form-item label="原因" prop="reason">
        <el-input v-model="offlineForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="请输入下架原因（必填）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="cancelOffline">取消</el-button>
      <el-button type="primary" @click="submitOffline">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="rejectOpen" title="驳回原因" width="460px" append-to-body>
    <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
      <el-form-item label="原因" prop="reason">
        <el-input v-model="rejectForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="请输入驳回原因（必填）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="cancelReject">取消</el-button>
      <el-button type="primary" @click="submitReject">确 定</el-button>
    </template>
  </el-dialog>

</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { listLibrary, approveLibrary, rejectLibrary, offlineLibrary, onlineLibrary } from '@/api/manage/library'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, keyword: '', status: null })

const getList = async () => {
  loading.value = true
  try {
    const { rows, total: t } = await listLibrary(queryParams.value)
    list.value = rows || []
    total.value = t || 0
  } finally { loading.value = false }
}
const resetQuery = () => { queryParams.value = { pageNum: 1, pageSize: 10, keyword: '', status: null }; getList() }
const statusText = s => ({0:'待审',1:'已通过',2:'驳回',3:'已下架'})[s] || '-'
const statusType = s => ({0:'warning',1:'success',2:'danger',3:'info'})[s] || 'info'

const approve = async (row) => { await approveLibrary(row.id); getList() }
// 驳回理由弹窗
const rejectOpen = ref(false)
const rejectFormRef = ref()
const rejectForm = ref({ reason: '' })
const rejectRules = reactive({ reason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }] })
const rejectRow = ref(null)
const openReject = (row) => { rejectRow.value = row; rejectForm.value.reason=''; rejectOpen.value = true }
const cancelReject = () => { rejectOpen.value = false }
const submitReject = () => {
  rejectFormRef.value.validate(async valid => {
    if (!valid) return
    await rejectLibrary(rejectRow.value.id, rejectForm.value.reason)
    rejectOpen.value = false
    getList()
  })
}
// 下架理由弹窗
const offlineOpen = ref(false)
const offlineFormRef = ref()
const offlineForm = ref({ reason: '' })
const offlineRules = reactive({ reason: [{ required: true, message: '请输入下架原因', trigger: 'blur' }] })
const offlineRow = ref(null)
const openOffline = (row) => { offlineRow.value = row; offlineForm.value.reason = ''; offlineOpen.value = true }
const cancelOffline = () => { offlineOpen.value = false }
const submitOffline = () => {
  offlineFormRef.value.validate(async valid => {
    if (!valid) return
    await offlineLibrary(offlineRow.value.id, offlineForm.value.reason)
    offlineOpen.value = false
    getList()
  })
}
const online = async (row) => { await onlineLibrary(row.id); getList() }

onMounted(getList)
</script>
