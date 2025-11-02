<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">排行榜</span>
      <el-select v-model="scope" style="width: 140px; margin-left: 12px" @change="handleQuery">
        <el-option label="全站" value="global" />
        <el-option label="按专业" value="major" />
        <el-option label="按课程" value="course" />
      </el-select>
      <el-input-number v-if="scope==='major'" v-model="majorId" :min="1" :step="1" placeholder="专业ID" style="margin-left: 8px" />
      <el-input-number v-if="scope==='course'" v-model="courseId" :min="1" :step="1" placeholder="课程ID" style="margin-left: 8px" />
      <el-input-number v-model="days" :min="1" :max="365" :step="1" style="margin-left: 8px" />
      <el-button type="primary" icon="Search" style="margin-left: 8px" @click="handleQuery">查询</el-button>
    </div>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column type="index" width="60" label="#" />
      <el-table-column label="资源名称" prop="resourceName" min-width="220" />
      <el-table-column label="专业/课程" min-width="220">
        <template #default="scope">{{ scope.row.majorName || scope.row.majorId }} / {{ scope.row.courseName || scope.row.courseId }}</template>
      </el-table-column>
      <el-table-column label="下载数" prop="downloadCount" width="120" />
      <el-table-column label="操作" fixed="right" width="120">
        <template #default="scope">
          <el-button link type="primary" icon="Download" @click="download(scope.row)">下载</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup name="ResourceTop">
import { ref } from 'vue'
import { topResourcePortal } from '@/api/portal/resource'

const loading = ref(false)
const list = ref([])
const scope = ref('global')
const majorId = ref()
const courseId = ref()
const days = ref(7)

const fetch = async () => {
  loading.value = true
  try {
    const params = { scope: scope.value, days: days.value }
    if (scope.value === 'major') params.majorId = majorId.value
    if (scope.value === 'course') params.courseId = courseId.value
    const resp = await topResourcePortal(params)
    list.value = resp?.data || []
  } finally { loading.value = false }
}

const handleQuery = () => fetch()
const download = (row) => {
  window.open(`${import.meta.env.VITE_APP_BASE_API}/portal/resource/${row.id}/download`, '_blank')
}

fetch()
</script>

<style scoped>
.hint { color: #909399; }
</style>
