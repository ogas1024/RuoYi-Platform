<template>
  <div class="app-container">
    <el-form :inline="true" :model="query" class="mb8">
      <el-form-item label="名称">
        <el-input v-model="query.buildingName" placeholder="楼房名称" clearable @keyup.enter="getList"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部">
          <el-option label="正常" value="0"/>
          <el-option label="停用" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openAdd" v-hasPermi="['manage:facility:building:add']">新增</el-button>
        <el-button type="danger" :disabled="!selection.length" @click="batchRemove"
                   v-hasPermi="['manage:facility:building:remove']">删除
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border @selection-change="sel=>selection = sel">
      <el-table-column type="selection" width="50"/>
      <el-table-column prop="buildingName" label="名称"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status==='0'?'success':'info'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注"/>
      <el-table-column label="操作" width="320">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openEdit(row)"
                     v-hasPermi="['manage:facility:building:edit']">编辑
          </el-button>
          <el-button size="small" text type="danger" @click="remove(row)"
                     v-hasPermi="['manage:facility:building:remove']">删除
          </el-button>
          <el-button size="small" text type="success" @click="openGantt(row)"
                     v-hasPermi="['manage:facility:building:gantt']">甘特图
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" :title="form.id?'编辑楼房':'新增楼房'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.buildingName"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 楼房甘特图弹窗 -->
    <el-dialog v-model="ganttOpen" title="楼房甘特图（可选时间范围）" width="1080px" @opened="onGanttOpened">
      <div class="mb8">
        <el-date-picker v-model="ganttRange" type="datetimerange" range-separator="至"
                        start-placeholder="开始日期" end-placeholder="结束日期"
                        value-format="YYYY-MM-DD HH:mm:ss"/>
        <el-button class="ml8" @click="loadGantt">刷新</el-button>
      </div>
      <MultiTimelineChart ref="ganttRef" :items="ganttItems" :from="ganttRange && ganttRange[0]" :to="ganttRange && ganttRange[1]"
                          :row-height="32" @segment-click="openDetail"/>
      <template #footer>
        <el-button @click="ganttOpen=false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 预约详情弹窗（复用） -->
    <BookingDetailDialog
        v-model="infoOpen"
        :booking-id="currentBookingId"
        :fetcher="fetchManageBooking"
        :show-actions="true"
        @approve="onApprove"
        @reject="onReject"
        @cancel="onCancel"
    />
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, nextTick} from 'vue'
import {
  buildingList,
  buildingAdd,
  buildingUpdate,
  buildingRemove,
  buildingGantt,
  getBooking,
  approveBooking,
  rejectBooking,
  cancelBookingManage
} from '@/api/manage/facility'
import {ElMessage, ElMessageBox} from 'element-plus'
import MultiTimelineChart from '@/components/facility/MultiTimelineChart.vue'
import BookingDetailDialog from '@/components/facility/BookingDetailDialog.vue'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const selection = ref([])
const query = reactive({pageNum: 1, pageSize: 10, buildingName: '', status: ''})

async function getList() {
  loading.value = true
  try {
    const {rows, total: t} = await buildingList(query)
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, {pageNum: 1, pageSize: 10, buildingName: '', status: ''});
  getList()
}

const open = ref(false)
const form = reactive({id: null, buildingName: '', status: '0', remark: ''})

function openAdd() {
  Object.assign(form, {id: null, buildingName: '', status: '0', remark: ''});
  open.value = true
}

function openEdit(row) {
  Object.assign(form, row);
  open.value = true
}

async function submit() {
  if (!form.buildingName) return ElMessage.error('请输入名称')
  if (form.id) await buildingUpdate(form); else await buildingAdd(form)
  open.value = false
  ElMessage.success('保存成功')
  getList()
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除该楼房？', '提示');
  await buildingRemove(row.id);
  ElMessage.success('删除成功');
  getList()
}

async function batchRemove() {
  await ElMessageBox.confirm('确认删除选中楼房？', '提示');
  const ids = selection.value.map(i => i.id).join(',');
  await buildingRemove(ids);
  ElMessage.success('删除成功');
  getList()
}

onMounted(getList)

// 甘特图（楼房维度）
const ganttOpen = ref(false)
const currentBuildingId = ref(null)
const ganttRange = ref([])
const ganttItems = ref([])
const ganttRef = ref(null)

function openGantt(row) {
  currentBuildingId.value = row.id
  ganttOpen.value = true
  loadGantt()
}

async function loadGantt() {
  if (!currentBuildingId.value) return
  const {data} = await buildingGantt(currentBuildingId.value, {from: ganttRange.value?.[0], to: ganttRange.value?.[1]})
  ganttItems.value = data?.items || []
}

function onGanttOpened() {
  // 弹窗完成展示后触发图表重算尺寸，避免初始化时容器不可见导致宽高为0
  nextTick(() => {
    ganttRef.value && ganttRef.value.resize && ganttRef.value.resize()
  })
}

// 预定详情复用（点击段）
const infoOpen = ref(false)
const currentBookingId = ref(null)
function openDetail(id) {
  currentBookingId.value = id
  infoOpen.value = true
}
const fetchManageBooking = (id) => getBooking(id)
async function onApprove(id) {
  await approveBooking(id)
  ElMessage.success('已通过')
  infoOpen.value = false
  loadGantt()
}
async function onReject({id, reason}) {
  await rejectBooking(id, reason)
  ElMessage.success('已驳回')
  infoOpen.value = false
  loadGantt()
}
async function onCancel(id) {
  await ElMessageBox.confirm('确认取消该预约？', '提示')
  await cancelBookingManage(id)
  ElMessage.success('已取消')
  infoOpen.value = false
  loadGantt()
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
.ml8 {
  margin-left: 8px;
}
</style>
