<template>
  <div class="facility-detail">
    <el-page-header @back="goBack" content="功能房详情" />

    <el-card class="mb-12">
      <div class="info">
        <div class="title">{{ room?.roomName }}</div>
        <div class="meta">楼层：{{ room?.floorNo }} | 容量：{{ room?.capacity || '-' }}</div>
        <div class="tags" v-if="room?.equipmentTags">
          <el-tag v-for="t in (room.equipmentTags||'').split(',')" :key="t" size="small" class="mr-6">{{ t }}</el-tag>
        </div>
        <el-button type="primary" class="mt-12" @click="showCreate = true">预约</el-button>
      </div>
    </el-card>

    <el-card>
      <div class="tl-header">
        <span>时间轴（近30天）</span>
        <el-button size="small" class="ml-12" @click="loadTimeline">刷新</el-button>
      </div>
      <TimelineChart :data="segments" :from="range.from" :to="range.to" @segment-click="openDetail" />
      <div class="tl-legend">
        <el-tag type="info">pending</el-tag>
        <el-tag type="success" class="ml-6">approved/ongoing</el-tag>
        <el-tag type="warning" class="ml-6">completed</el-tag>
      </div>
    </el-card>

    <el-dialog v-model="showCreate" title="提交预约" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="使用目的"><el-input v-model="form.purpose" maxlength="200" show-word-limit /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="使用人ID(逗号)"><el-input v-model="userIds" placeholder="含自己在内≥3人，如：110,111,112"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate=false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 预约详情（仅信息展示） -->
    <el-dialog v-model="infoOpen" title="预约详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申请ID">{{ info.id }}</el-descriptions-item>
        <el-descriptions-item label="房间">{{ infoMeta.roomName || '-' }}（ID: {{ info.roomId }}）</el-descriptions-item>
        <el-descriptions-item label="楼房">{{ infoMeta.buildingName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ infoMeta.applicantName || '-' }}（ID: {{ info.applicantId }}）</el-descriptions-item>
        <el-descriptions-item label="状态">{{ infoMeta.statusText || info.status }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ info.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ info.endTime }}</el-descriptions-item>
        <el-descriptions-item label="目的">{{ info.purpose }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt-12">
        <div style="font-weight:600;margin:8px 0;">使用人</div>
        <el-table :data="infoUsers" size="small" border>
          <el-table-column prop="userId" label="用户ID" width="160"/>
          <el-table-column prop="isApplicant" label="申请人" width="100">
            <template #default="{ row }"><el-tag :type="row.isApplicant==='1'?'success':'info'">{{ row.isApplicant==='1'?'是':'否' }}</el-tag></template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="infoOpen=false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getRoom, getTimeline, createBooking } from '@/api/portal/facility'
import { ElMessage } from 'element-plus'
import TimelineChart from '@/components/facility/TimelineChart.vue'

const route = useRoute()
const id = Number(route.query.id)
const room = ref(null)
const range = ref({ from: null, to: null })
const segments = ref([])
const showCreate = ref(false)
const form = ref({ purpose: '', startTime: '', endTime: '' })
const userIds = ref('')

function goBack(){ history.back() }


async function loadInfo(){
  room.value = (await getRoom(id)).data
}

async function loadTimeline(){
  const res = await getTimeline(id, {})
  range.value = res.data?.range || {}
  segments.value = res.data?.segments || []
}

async function submit(){
  if (!form.value.purpose || !form.value.startTime || !form.value.endTime) return ElMessage.error('请完整填写表单')
  const list = (userIds.value||'').split(',').map(s=>Number(s.trim())).filter(Boolean)
  const data = { roomId: id, purpose: form.value.purpose, startTime: form.value.startTime, endTime: form.value.endTime, userIdList: list }
  await createBooking(data)
  ElMessage.success('提交成功')
  showCreate.value = false
  await loadTimeline()
}

// 点击段查看详情（仅信息展示，无管理动作）
const infoOpen = ref(false)
const info = ref({})
const infoUsers = ref([])
const infoMeta = ref({})
import { getBooking as getPortalBooking } from '@/api/portal/facility'
function openDetail(id){
  const { data } = await getPortalBooking(id)
  info.value = data.booking || {}
  infoUsers.value = data.users || []
  infoMeta.value = data.meta || {}
  infoOpen.value = true
}

onMounted(async()=>{ await loadInfo(); await loadTimeline() })
</script>

<style scoped>
.mb-12{ margin-bottom:12px; }
.mr-6{ margin-right:6px; }
.mt-12{ margin-top:12px; }
.title{ font-size:18px; font-weight:600; }
.meta{ color:#888; margin-top:4px; }
.tl-header{ display:flex; align-items:center; margin-bottom:8px; }
.tl-legend{ margin-top:8px; }
.ml-12{ margin-left:12px; }
.ml-6{ margin-left:6px; }
</style>
