<template>
  <div class="app-container">
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="申请ID">
        <el-input v-model="q.bookingId" clearable style="width:150px"/>
      </el-form-item>
      <el-form-item label="楼房">
        <el-select v-model="q.buildingId" clearable filterable style="width:220px" @change="getList">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="房间ID">
        <el-input v-model="q.roomId" clearable style="width:150px"/>
      </el-form-item>
      <el-form-item label="申请人ID">
        <el-input v-model="q.applicantId" clearable style="width:150px"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="roomId" label="房间ID" width="100"/>
      <el-table-column prop="applicantId" label="申请人" width="100"/>
      <el-table-column prop="startTime" label="开始时间" width="180"/>
      <el-table-column prop="endTime" label="结束时间" width="180"/>
      <el-table-column prop="purpose" label="目的"/>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" text type="success" @click="approve(row)"
                     v-hasPermi="['manage:facility:booking:audit:approve']">通过
          </el-button>
          <el-button size="small" text type="warning" @click="openReject(row)"
                     v-hasPermi="['manage:facility:booking:audit:reject']">驳回
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="page.pageNum" v-model:limit="page.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="rejectOpen" title="驳回理由" width="520px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" maxlength="200" show-word-limit/>
      <template #footer>
        <el-button @click="rejectOpen=false">取消</el-button>
        <el-button type="warning" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {buildingList, auditList, approveBooking, rejectBooking} from '@/api/manage/facility'
import {ElMessage} from 'element-plus'

const buildings = ref([])
const q = reactive({bookingId: '', buildingId: null, roomId: '', applicantId: ''})
const page = reactive({pageNum: 1, pageSize: 10})
const list = ref([])
const total = ref(0)
const loading = ref(false)

async function loadBuildings() {
  const {rows} = await buildingList({pageNum: 1, pageSize: 100});
  buildings.value = rows || []
}

async function getList() {
  loading.value = true
  try {
    const {rows, total: t} = await auditList({...q, ...page})
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}

function reset() {
  Object.assign(q, {buildingId: null, roomId: '', applicantId: ''});
  getList()
}

async function approve(row) {
  await approveBooking(row.id);
  ElMessage.success('已通过');
  getList()
}

const rejectOpen = ref(false)
const rejectRow = ref(null)
const rejectReason = ref('')

function openReject(row) {
  rejectRow.value = row;
  rejectReason.value = '';
  rejectOpen.value = true
}

async function submitReject() {
  if (!rejectReason.value) return ElMessage.error('请输入理由');
  await rejectBooking(rejectRow.value.id, rejectReason.value);
  rejectOpen.value = false;
  ElMessage.success('已驳回');
  getList()
}

onMounted(async () => {
  await loadBuildings();
  await getList()
})
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>
