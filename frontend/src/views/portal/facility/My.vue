<template>
  <div class="my-bookings">
    <el-card>
      <div class="filter">
        <el-select v-model="status" placeholder="全部状态" style="width:220px" @change="load">
          <el-option label="全部" value=""/>
          <el-option label="待审核" value="0"/>
          <el-option label="已批准" value="1"/>
          <el-option label="已驳回" value="2"/>
          <el-option label="已取消" value="3"/>
          <el-option label="进行中" value="4"/>
          <el-option label="已完成" value="5"/>
        </el-select>
      </div>
      <el-table :data="list" style="width:100%" size="small">
        <el-table-column label="房间" prop="roomId" width="100"/>
        <el-table-column label="开始时间" prop="startTime" width="180"/>
        <el-table-column label="结束时间" prop="endTime" width="180"/>
        <el-table-column label="状态" prop="status" width="100"/>
        <el-table-column label="目的" prop="purpose"/>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button v-if="canCancel(scope.row)" type="danger" size="small" @click="doCancel(scope.row)">取消</el-button>
            <el-button v-if="canEdit(scope.row)" size="small" @click="openEdit(scope.row)">修改</el-button>
            <el-button v-if="canEndEarly(scope.row)" size="small" type="warning" @click="openEnd(scope.row)">提前结束</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load"/>
    </el-card>

    <el-dialog v-model="showEdit" title="修改预约" width="520px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="使用目的"><el-input v-model="editForm.purpose"/></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="editForm.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"/></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="editForm.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit=false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEnd" title="提前结束" width="420px">
      <el-form :model="endForm" label-width="100px">
        <el-form-item label="结束时间"><el-date-picker v-model="endForm.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEnd=false">取消</el-button>
        <el-button type="warning" @click="saveEnd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { myBookings, cancelBooking, updateBooking, endEarly } from '@/api/portal/facility'
import { ElMessage, ElMessageBox } from 'element-plus'

const status = ref('')
const query = reactive({ pageNum: 1, pageSize: 10 })
const total = ref(0)
const list = ref([])

async function load(){
  const { rows, total: t } = await myBookings({ ...query, status: status.value })
  list.value = rows || []
  total.value = t || 0
}

function canCancel(r){ return (r.status === '0' || r.status === '1') && new Date(r.startTime) > new Date() }
function canEdit(r){ return (r.status === '0' || r.status === '1' || r.status === '2') && new Date(r.startTime) > new Date() }
function canEndEarly(r){ return r.status === '1' || r.status === '4' }

async function doCancel(r){
  await ElMessageBox.confirm('确认要取消该预约？','提示')
  await cancelBooking(r.id)
  ElMessage.success('已取消')
  load()
}

const showEdit = ref(false)
const editForm = reactive({ id: null, purpose: '', startTime: '', endTime: '' })
function openEdit(r){ Object.assign(editForm, { id: r.id, purpose: r.purpose, startTime: r.startTime, endTime: r.endTime }); showEdit.value = true }
async function saveEdit(){ await updateBooking(editForm.id, editForm); ElMessage.success('已保存'); showEdit.value=false; load() }

const showEnd = ref(false)
const endForm = reactive({ id: null, endTime: '' })
function openEnd(r){ endForm.id = r.id; endForm.endTime = r.endTime; showEnd.value = true }
async function saveEnd(){ await endEarly(endForm.id, { endTime: endForm.endTime }); ElMessage.success('操作成功'); showEnd.value=false; load() }

onMounted(load)
</script>

<style scoped>
.filter{ margin-bottom:12px; }
</style>

