<template>
  <div class="app-container">
    <el-card>
      <el-radio-group v-model="period" @change="load">
        <el-radio-button label="7d">近7天</el-radio-button>
        <el-radio-button label="30d">近30天</el-radio-button>
      </el-radio-group>
    </el-card>

    <el-row :gutter="12" class="mt12">
      <el-col :span="12">
        <el-card>
          <div class="card-title">房间预约时长 Top</div>
          <el-table :data="rooms" size="small" border>
            <el-table-column prop="roomId" label="房间ID" width="100"/>
            <el-table-column prop="buildingName" label="楼房"/>
            <el-table-column prop="roomName" label="房间"/>
            <el-table-column prop="totalMinutes" label="累计分钟" width="120"/>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div class="card-title">用户预约总时长 Top</div>
          <el-table :data="users" size="small" border>
            <el-table-column prop="userName" label="用户名" width="160"/>
            <el-table-column prop="nickName" label="昵称/名称"/>
            <el-table-column prop="totalMinutes" label="累计分钟" width="120"/>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {topRooms, topUsers} from '@/api/manage/facility'

const period = ref('7d')
const rooms = ref([])
const users = ref([])

async function load() {
  rooms.value = (await topRooms({period: period.value})).data || []
  users.value = (await topUsers({period: period.value})).data || []
}

onMounted(load)
</script>

<style scoped>
.mt12 {
  margin-top: 12px;
}

.card-title {
  font-weight: 600;
  margin-bottom: 8px;
}
</style>
