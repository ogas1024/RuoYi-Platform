<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button link type="primary" icon="Back" @click="$router.back()">返回</el-button>
      <span class="hint">当前专业：{{ majorName }}</span>
    </div>
    <div class="mb8" style="margin: 10px 0;">
      <el-input v-model="courseName" placeholder="按课程名模糊" style="width: 220px" @keyup.enter="fetchCourses"/>
      <el-button type="primary" icon="Search" style="margin-left:8px" @click="fetchCourses">搜索</el-button>
    </div>
    <el-row :gutter="16">
      <el-col v-for="c in courses" :key="c.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="course-card" shadow="hover" @click="goResource(c)">
          <div class="title">{{ c.courseName }}</div>
          <div class="meta">编码：{{ c.courseCode || '-' }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="ResourceCourseGrid">
import {ref, onMounted} from 'vue'
import {listCoursePortal} from '@/api/portal/course'
import {useRoute, useRouter} from 'vue-router'

const route = useRoute()
const router = useRouter()
const majorId = Number(route.query.majorId)
const majorName = route.query.majorName || ''
const courses = ref([])
const courseName = ref('')

const fetchCourses = async () => {
  const {rows} = await listCoursePortal({majorId, courseName: courseName.value, pageNum: 1, pageSize: 100})
  courses.value = rows || []
}

const goResource = (c) => {
  router.push({
    path: '/portal/course-resource/list',
    query: {majorId, courseId: c.id, courseName: c.courseName, majorName}
  })
}

onMounted(fetchCourses)
</script>

<style scoped>
.toolbar {
  margin-bottom: 10px;
}

.course-card {
  cursor: pointer;
  margin-bottom: 16px;
}

.title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 8px;
}

.meta {
  color: #909399;
  font-size: 12px;
}
</style>
