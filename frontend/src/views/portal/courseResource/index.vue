<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col v-for="m in majors" :key="m.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="major-card" shadow="hover" @click="goCourse(m)">
          <div class="title">{{ m.majorName }}</div>
          <div class="meta">状态：<el-tag :type="m.status==='0'?'success':'info'" size="small">{{ m.status==='0'?'正常':'停用' }}</el-tag></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
  </template>

<script setup name="ResourceMajorGrid">
import { ref, onMounted } from 'vue'
import { listMajorPortal } from '@/api/portal/major'
import { useRouter } from 'vue-router'

const majors = ref([])
const router = useRouter()

const fetchMajors = async () => {
  const { rows } = await listMajorPortal({ pageNum: 1, pageSize: 100 })
  majors.value = rows || []
}

const goCourse = (m) => {
  router.push({ path: '/p/course', query: { majorId: m.id, majorName: m.majorName } })
}

onMounted(fetchMajors)
</script>

<style scoped>
.major-card { cursor: pointer; margin-bottom: 16px; }
.title { font-weight: 600; font-size: 16px; margin-bottom: 8px; }
.meta { color: #909399; font-size: 12px; }
</style>
