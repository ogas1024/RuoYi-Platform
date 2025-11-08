<template>
  <div class="app-container">
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="楼房">
        <el-select v-model="q.buildingId" placeholder="选择楼房" filterable style="width:220px" @change="getList">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="楼层">
        <el-input-number v-model="q.floorNo" :min="-5" :max="50" @change="getList"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openAdd" v-hasPermi="['manage:facility:room:add']">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="roomName" label="房间"/>
      <el-table-column prop="capacity" label="容量" width="100"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status==='0'?'success':'info'">{{ row.status === '0' ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="equipmentTags" label="设备"/>
      <el-table-column label="操作" width="340">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openEdit(row)" v-hasPermi="['manage:facility:room:edit']">
            编辑
          </el-button>
          <el-button size="small" text @click="toggleEnable(row)" v-hasPermi="['manage:facility:room:enable']">
            {{ row.status === '0' ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" text type="danger" @click="remove(row)" v-hasPermi="['manage:facility:room:remove']">
            删除
          </el-button>
          <el-button size="small" text type="success" @click="openTimeline(row)"
                     v-hasPermi="['manage:facility:room:timeline']">时间轴
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="page.pageNum" v-model:limit="page.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" :title="form.id?'编辑房间':'新增房间'" width="680px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="楼房">
          <el-select v-model="form.buildingId" placeholder="选择楼房" filterable style="width:220px">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input-number v-model="form.floorNo" :min="-5" :max="50"/>
        </el-form-item>
        <el-form-item label="房间名称">
          <el-input v-model="form.roomName"/>
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="form.capacity" :min="1"/>
        </el-form-item>
        <el-form-item label="设备标签">
          <el-input v-model="form.equipmentTags" placeholder="逗号分隔"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
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

    <!-- 时间轴 ECharts -->
    <el-dialog v-model="timelineOpen" title="时间轴（近30天）" width="880px">
      <div class="mb8">
        <el-date-picker v-model="timelineRange" type="daterange" range-separator="至" start-placeholder="开始日期"
                        end-placeholder="结束日期" value-format="YYYY-MM-DD HH:mm:ss"/>
        <el-button class="ml8" @click="loadTimeline">刷新</el-button>
      </div>
      <TimelineChart :data="segments" :from="timelineRange && timelineRange[0]" :to="timelineRange && timelineRange[1]"
                     @segment-click="openDetail"/>
    </el-dialog>

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
import {ref, reactive, onMounted} from 'vue'
import {
  buildingList,
  roomList,
  roomAdd,
  roomUpdate,
  roomEnable,
  roomRemove,
  roomTimeline,
  getBooking,
  approveBooking,
  rejectBooking,
  cancelBookingManage
} from '@/api/manage/facility'
import {ElMessage, ElMessageBox} from 'element-plus'
import TimelineChart from '@/components/facility/TimelineChart.vue'
import BookingDetailDialog from '@/components/facility/BookingDetailDialog.vue'

const buildings = ref([])
const q = reactive({buildingId: null, floorNo: 1})
const page = reactive({pageNum: 1, pageSize: 10})
const list = ref([])
const total = ref(0)
const loading = ref(false)

async function loadBuildings() {
  const {rows} = await buildingList({pageNum: 1, pageSize: 100, status: '0'})
  buildings.value = rows || []
  if (buildings.value.length && !q.buildingId) q.buildingId = buildings.value[0].id
}

async function getList() {
  if (!q.buildingId || q.floorNo === undefined || q.floorNo === null) return
  loading.value = true
  try {
    const {rows, total: t} = await roomList({buildingId: q.buildingId, floorNo: q.floorNo, ...page})
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(q, {buildingId: buildings.value[0]?.id || null, floorNo: 1});
  getList()
}

const open = ref(false)
const form = reactive({
  id: null,
  buildingId: null,
  floorNo: 1,
  roomName: '',
  capacity: null,
  equipmentTags: '',
  status: '0',
  remark: ''
})

function openAdd() {
  Object.assign(form, {
    id: null,
    buildingId: q.buildingId,
    floorNo: q.floorNo,
    roomName: '',
    capacity: null,
    equipmentTags: '',
    status: '0',
    remark: ''
  });
  open.value = true
}

function openEdit(row) {
  Object.assign(form, row);
  open.value = true
}

async function submit() {
  if (!form.roomName || !form.buildingId) return ElMessage.error('请完善表单');
  if (form.id) await roomUpdate(form); else await roomAdd(form);
  open.value = false;
  ElMessage.success('保存成功');
  getList()
}

async function toggleEnable(row) {
  await roomEnable(row.id, row.status !== '0');
  ElMessage.success('已更新');
  getList()
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除该房间？', '提示');
  await roomRemove(row.id);
  ElMessage.success('删除成功');
  getList()
}

// 时间轴数据（复用组件）
const timelineOpen = ref(false)
const currentRoomId = ref(null)
const timelineRange = ref([])
const segments = ref([])

function openTimeline(row) {
  currentRoomId.value = row.id;
  timelineOpen.value = true;
  loadTimeline()
}

async function loadTimeline() {
  if (!currentRoomId.value) return
  const res = await roomTimeline(currentRoomId.value, {from: timelineRange.value?.[0], to: timelineRange.value?.[1]})
  segments.value = res.data?.segments || []
}

// 点击段查询详情，复用 BookingDetailDialog（含操作按钮）
const infoOpen = ref(false)
const currentBookingId = ref(null)

function openDetail(id) {
  currentBookingId.value = id;
  infoOpen.value = true
}

const fetchManageBooking = (id) => getBooking(id)

async function onApprove(id) {
  await approveBooking(id);
  ElMessage.success('已通过');
  infoOpen.value = false;
  loadTimeline()
}

async function onReject({id, reason}) {
  await rejectBooking(id, reason);
  ElMessage.success('已驳回');
  infoOpen.value = false;
  loadTimeline()
}

async function onCancel(id) {
  await ElMessageBox.confirm('确认取消该预约？', '提示');
  await cancelBookingManage(id);
  ElMessage.success('已取消');
  infoOpen.value = false;
  loadTimeline()
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

.ml8 {
  margin-left: 8px;
}
</style>
