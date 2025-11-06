<template>
  <div class="facility-portal">
    <el-card class="mb-12">
      <el-form :inline="true" label-width="64px" class="filters">
        <el-form-item label="楼房">
          <el-select v-model="selectedBuilding" placeholder="请选择楼房（先选楼房）" filterable style="width: 260px" @change="loadRooms">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input-number v-model="floorNo" :min="-5" :max="50" :step="1" @change="loadRooms" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRooms">查询房间</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="12">
      <el-col :span="8" v-for="r in rooms" :key="r.id">
        <el-card class="room-card" @click="goDetail(r.id)">
          <div class="title">{{ r.roomName }}</div>
          <div class="sub">容量：{{ r.capacity || '-' }} | 楼层：{{ r.floorNo }}</div>
          <div class="tags" v-if="r.equipmentTags">
            <el-tag v-for="t in (r.equipmentTags||'').split(',')" :key="t" size="small" class="mr-6">{{ t }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
  
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listBuildings, listRooms } from '@/api/portal/facility'
import { ElMessage } from 'element-plus'

const buildings = ref([])
const selectedBuilding = ref(null)
const floorNo = ref(1)
const rooms = ref([])

async function loadBuildings() {
  const { rows } = await listBuildings({ pageNum: 1, pageSize: 100 })
  buildings.value = rows || []
  if (buildings.value.length && !selectedBuilding.value) {
    selectedBuilding.value = buildings.value[0].id
    await loadRooms()
  }
}

async function loadRooms() {
  rooms.value = []
  if (!selectedBuilding.value || floorNo.value === null || floorNo.value === undefined) {
    return ElMessage.info('请选择楼房与楼层')
  }
  const { rows } = await listRooms({ buildingId: selectedBuilding.value, floorNo: floorNo.value, pageNum: 1, pageSize: 100 })
  rooms.value = rows || []
}

function goDetail(id) {
  // 使用门户路由
  window.location.href = `/portal/facility/detail?id=${id}`
}

onMounted(loadBuildings)
</script>

<style scoped>
.mb-12{ margin-bottom:12px; }
.ml-12{ margin-left:12px; }
.mr-6{ margin-right:6px; }
.filters{ display:flex; align-items:center; }
.room-card{ cursor:pointer; margin-bottom:12px; }
.room-card .title{ font-weight:600; font-size:16px; margin-bottom:6px; }
.room-card .sub{ color:#888; font-size:12px; margin-bottom:6px; }
</style>
