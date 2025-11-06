<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="资源排行" name="resource">
        <div class="toolbar">
          <span class="hint">资源排行</span>
          <el-select v-model="scope" style="width: 140px; margin-left: 12px" @change="handleQuery">
            <el-option label="全站" value="global" />
            <el-option label="按专业" value="major" />
            <el-option label="按课程" value="course" />
          </el-select>
          <el-select v-if="scope==='major'" v-model="majorId" clearable placeholder="选择专业" style="margin-left: 8px; width: 220px">
            <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
          </el-select>
          <template v-if="scope==='course'">
            <el-select v-model="majorId" clearable placeholder="选择专业" style="margin-left: 8px; width: 220px" @change="loadCourses">
              <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
            </el-select>
            <el-select v-model="courseId" clearable placeholder="选择课程" style="margin-left: 8px; width: 240px">
              <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
            </el-select>
          </template>
          <el-input-number v-model="days" :min="1" :max="365" :step="1" style="margin-left: 8px" />
          <el-button type="primary" icon="Search" style="margin-left: 8px" @click="handleQuery">查询</el-button>
        </div>
        <el-table v-loading="loading" border stripe :data="list" size="small">
          <el-table-column type="index" width="60" label="#" />
          <el-table-column label="资源名称" min-width="240" show-overflow-tooltip>
            <template #default="scope">
              <el-tag v-if="scope.row.isBest===1" type="warning" size="small" effect="dark" style="margin-right:6px">最佳</el-tag>
              <span>{{ scope.row.resourceName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="专业" min-width="160" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.majorName || scope.row.majorId }}</template>
          </el-table-column>
          <el-table-column label="课程" min-width="180" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.courseName || scope.row.courseId }}</template>
          </el-table-column>
          <el-table-column label="上传者" prop="uploaderName" width="140" />
          <el-table-column label="上传时间" width="180">
            <template #default="scope">{{ (scope.row.publishTime || scope.row.createTime) || '-' }}</template>
          </el-table-column>
          <el-table-column label="下载数" prop="downloadCount" width="100" align="right" />
          <el-table-column label="操作" fixed="right" width="160">
            <template #default="scope">
              <el-button link type="primary" icon="View" @click="openDetail(scope.row)">详情</el-button>
              <el-divider direction="vertical" />
              <el-button link type="primary" icon="Download" @click="download(scope.row)">下载</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="用户积分" name="score">
        <div class="toolbar">
          <span class="hint">用户积分排行榜</span>
          <el-select v-model="scoreMajorId" clearable placeholder="选择专业（不选=全站）" style="margin-left: 12px; width: 260px">
            <el-option label="全站" :value="0" />
            <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
          </el-select>
          <el-button type="primary" icon="Search" style="margin-left: 8px" @click="handleScoreQuery">查询</el-button>
        </div>
        <el-table v-loading="scoreLoading" border stripe :data="scoreList" size="small">
          <el-table-column label="#" prop="rank" width="60" />
          <el-table-column label="用户ID" prop="userId" width="100" />
          <el-table-column label="用户" min-width="140">
            <template #default="scope">
              <el-link type="primary" @click="openAllWorks(scope.row)">{{ scope.row.username }}</el-link>
            </template>
          </el-table-column>
          <el-table-column label="代表作" min-width="420">
            <template #default="scope">
              <span v-if="scope.row.repWorks && scope.row.repWorks.length">
                <el-space wrap>
                  <el-tooltip v-for="w in scope.row.repWorks.slice(0,5)" :key="w.id" :content="`${w.courseName || '-'} | ${w.publishTime || '-'}`" placement="top">
                    <el-tag size="small" :type="w.isBest===1 ? 'warning' : 'info'" effect="plain" style="cursor:pointer" @click="openDetailById(w.id)">
                      <span v-if="w.isBest===1">[最佳]</span>{{ w.resourceName }} ({{ w.downloadCount }})
                    </el-tag>
                  </el-tooltip>
                </el-space>
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="积分" prop="totalScore" width="100" align="right" />
          <el-table-column label="通过次数" prop="approveCount" width="100" align="right" />
          <el-table-column label="最佳次数" prop="bestCount" width="100" align="right" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 资源详情 -->
    <el-dialog v-model="detailVisible" title="资源详情" width="680px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="资源名称" :span="2">{{ detail.resourceName }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ detail.majorName || detail.majorId }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ detail.courseName || detail.courseId }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.resourceType===1 ? '外链' : '文件' }}</el-descriptions-item>
        <el-descriptions-item label="大小">{{ detail.fileSize || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传者">{{ detail.uploaderName }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ (detail.publishTime || detail.createTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="下载次数">{{ detail.downloadCount }}</el-descriptions-item>
        <el-descriptions-item label="最佳">{{ detail.isBest===1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item v-if="(detail.status===2||detail.status===3) && detail.auditReason" label="原因" :span="2">
          <span class="reason-strong">{{ detail.auditReason }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible=false">关 闭</el-button>
          <el-button type="primary" @click="download(detail)">下 载</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 代表作抽屉：显示该用户全部代表作 -->
    <el-drawer v-model="worksDrawerVisible" :title="`代表作 - ${worksDrawerUser?.username || ''}`" size="60%">
      <div class="toolbar" style="margin-bottom:8px">
        <el-form :inline="true" :model="worksQuery">
          <el-form-item label="关键字">
            <el-input v-model="worksQuery.keyword" placeholder="资源名关键词" clearable style="width:200px" />
          </el-form-item>
          <el-form-item label="课程">
            <el-select v-model="worksQuery.courseId" placeholder="课程" clearable :disabled="worksCourses.length === 0" style="width:220px">
              <el-option v-for="c in worksCourses" :key="c.id" :label="c.courseName" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="worksQuery.type" placeholder="全部" clearable style="width:120px">
              <el-option label="文件" :value="0" />
              <el-option label="外链" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="worksQuery.onlyBest">仅最佳</el-checkbox>
          </el-form-item>
          <el-form-item label="排序">
            <el-radio-group v-model="worksSort">
              <el-radio-button label="download">按下载</el-radio-button>
              <el-radio-button label="time">按时间</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="applyWorksFilter">筛选</el-button>
            <el-button @click="resetWorksFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="worksDrawerList" border stripe size="small">
        <el-table-column label="#" type="index" width="60" />
        <el-table-column label="资源名称" min-width="260" show-overflow-tooltip>
          <template #default="scope">
            <el-tag v-if="scope.row.isBest===1" type="warning" size="small" effect="dark" style="margin-right:4px">最佳</el-tag>
            <span>{{ scope.row.resourceName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程" min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.courseName || '-' }}</template>
        </el-table-column>
        
        <el-table-column label="上传时间" width="180">
          <template #default="scope">{{ scope.row.publishTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="下载数" prop="downloadCount" width="100" align="right" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="openDetailById(scope.row.id)">详情</el-button>
            <el-divider direction="vertical" />
            <el-button link type="primary" icon="Download" @click="download(scope.row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="ResourceTop">
import { ref, watch } from 'vue'
import { topResourcePortal, getResourcePortal, listResourcePortal } from '@/api/portal/resource'
import { listMajorPortal } from '@/api/portal/major'
import { listCoursePortal } from '@/api/portal/course'
import { listScoreRankPortal } from '@/api/portal/score'
import request from '@/utils/request'

const loading = ref(false)
const list = ref([])
const activeTab = ref('resource')
const scope = ref('global')
const majorId = ref()
const courseId = ref()
const majors = ref([])
const courses = ref([])
const days = ref(7)
// 用户积分
const scoreLoading = ref(false)
const scoreList = ref([])
const scoreMajorId = ref()
// 详情
const detailVisible = ref(false)
const detail = ref({})
// 代表作抽屉
const worksDrawerVisible = ref(false)
const worksDrawerUser = ref(null)
const worksDrawerFullList = ref([]) // 原始
const worksDrawerList = ref([])     // 过滤后
const worksCourses = ref([])
const worksQuery = ref({ keyword: '', courseId: undefined, type: undefined, onlyBest: false })
const worksSort = ref('download') // download | time

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
const handleScoreQuery = async () => {
  scoreLoading.value = true
  try {
    const params = { pageNum: 1, pageSize: 20 }
    if (scoreMajorId.value !== undefined && scoreMajorId.value !== null) params.majorId = scoreMajorId.value
    const { rows } = await listScoreRankPortal(params)
    scoreList.value = rows || []
    await loadRepWorks(scoreList.value)
  } finally { scoreLoading.value = false }
}
const loadMajors = async () => {
  const { rows } = await listMajorPortal({ pageNum: 1, pageSize: 200 })
  majors.value = rows || []
}
const loadCourses = async () => {
  courses.value = []
  if (!majorId.value) return
  const { rows } = await listCoursePortal({ majorId: majorId.value, pageNum: 1, pageSize: 500 })
  courses.value = rows || []
}
const download = async (row) => {
  try {
    // 1) 调用下载计数接口（携带认证头；静默处理跨域302导致的 Network Error 提示）
    await request({ url: `/portal/resource/${row.id}/download`, method: 'get', silent: true }).catch(() => {})
    // 2) 拉取最新详情，更新下载数（确保与服务端一致）
    const { data } = await getResourcePortal(row.id)
    if (data && typeof data.downloadCount === 'number') {
      row.downloadCount = data.downloadCount
    }
  } finally {
    // 3) 打开直链（使用行数据中的 fileUrl/linkUrl，避免Header丢失导致的401）
    const target = row.resourceType === 1 ? row.linkUrl : row.fileUrl
    if (target) window.open(target, '_blank')
  }
}

const onTabChange = () => {
  if (activeTab.value === 'resource') {
    fetch()
  } else {
    handleScoreQuery()
  }
}

const openDetail = async (row) => {
  try {
    const { data } = await getResourcePortal(row.id)
    detail.value = data || row
    detailVisible.value = true
  } catch (e) {
    // 忽略
  }
}

// 打开资源详情（通过ID）
const openDetailById = async (id) => {
  try {
    const { data } = await getResourcePortal(id)
    detail.value = data || {}
    detailVisible.value = true
  } catch (e) {}
}

// 打开代表作抽屉（全部）
const openAllWorks = async (row) => {
  worksDrawerUser.value = row
  worksDrawerVisible.value = true
  // 课程下拉：按当前积分榜选择的专业加载
  if (scoreMajorId.value && Number(scoreMajorId.value) > 0) {
    try {
      const { rows } = await listCoursePortal({ majorId: Number(scoreMajorId.value), pageNum: 1, pageSize: 500 })
      worksCourses.value = rows || []
    } catch (e) { worksCourses.value = [] }
  } else { worksCourses.value = [] }
  try {
    if (row.repWorks && row.repWorks.length && 'courseName' in row.repWorks[0]) {
      worksDrawerFullList.value = row.repWorks
      // 基于当前代表作构造课程选项（全站模式或缺少专业筛选时使用）
      worksCourses.value = Array.from(new Map((worksDrawerFullList.value || [])
        .filter(x => x.courseId && x.courseName)
        .map(x => [x.courseId, { id: x.courseId, courseName: x.courseName }])
      ).values())
      applyWorksFilter()
      return
    }
    const params = { pageNum: 1, pageSize: 200, uploaderId: row.userId }
    if (scoreMajorId.value && Number(scoreMajorId.value) > 0) params.majorId = Number(scoreMajorId.value)
    const { rows: list } = await listResourcePortal(params)
    const sorted = (list || []).slice().sort((a, b) => (b.downloadCount || 0) - (a.downloadCount || 0))
    worksDrawerFullList.value = sorted.map(it => ({ id: it.id, resourceName: it.resourceName, downloadCount: it.downloadCount || 0, courseName: it.courseName, courseId: it.courseId, isBest: it.isBest, publishTime: it.publishTime, resourceType: it.resourceType }))
    // 构造课程选项（当未选专业或接口未返回课程列表时）
    if (!worksCourses.value || worksCourses.value.length === 0) {
      worksCourses.value = Array.from(new Map((worksDrawerFullList.value || [])
        .filter(x => x.courseId && x.courseName)
        .map(x => [x.courseId, { id: x.courseId, courseName: x.courseName }])
      ).values())
    }
    applyWorksFilter()
  } catch (e) {
    worksDrawerFullList.value = []
    worksDrawerList.value = []
  }
}

function applyWorksFilter() {
  let arr = [...(worksDrawerFullList.value || [])]
  const q = worksQuery.value
  if (q.keyword) arr = arr.filter(x => (x.resourceName || '').toLowerCase().includes(q.keyword.toLowerCase()))
  if (q.courseId) arr = arr.filter(x => Number(x.courseId) === Number(q.courseId))
  if (q.type === 0 || q.type === 1) arr = arr.filter(x => Number(x.resourceType) === Number(q.type))
  if (q.onlyBest) arr = arr.filter(x => Number(x.isBest) === 1)
  if (worksSort.value === 'time') {
    arr.sort((a, b) => new Date(b.publishTime || 0) - new Date(a.publishTime || 0))
  } else {
    arr.sort((a, b) => (b.downloadCount || 0) - (a.downloadCount || 0))
  }
  worksDrawerList.value = arr
}

function resetWorksFilter() {
  worksQuery.value = { keyword: '', courseId: undefined, type: undefined, onlyBest: false }
  worksSort.value = 'download'
  applyWorksFilter()
}

// 监听 scope/majorId，按需加载课程下拉
watch(scope, (val) => {
  if (val === 'course') {
    courses.value = []
    courseId.value = undefined
    if (majorId.value) loadCourses()
  } else {
    courses.value = []
    courseId.value = undefined
  }
})
watch(majorId, (val) => {
  if (scope.value === 'course') {
    courses.value = []
    courseId.value = undefined
    if (val) loadCourses()
  }
})

loadMajors().then(() => {
  fetch()
  handleScoreQuery()
})

// 为积分榜加载代表作（按下载数降序，动态全部展示）
async function loadRepWorks(rows) {
  const majorFilter = (scoreMajorId.value !== undefined && scoreMajorId.value !== null && Number(scoreMajorId.value) > 0)
    ? Number(scoreMajorId.value) : undefined
  const tasks = (rows || []).map(async (r) => {
    const params = { pageNum: 1, pageSize: 200, uploaderId: r.userId }
    if (majorFilter) params.majorId = majorFilter
    try {
      const { rows: list } = await listResourcePortal(params)
      const sorted = (list || []).slice().sort((a, b) => (b.downloadCount || 0) - (a.downloadCount || 0))
      r.repWorks = sorted.map(it => ({ id: it.id, resourceName: it.resourceName, downloadCount: it.downloadCount || 0, isBest: it.isBest, courseName: it.courseName, courseId: it.courseId, publishTime: it.publishTime }))
    } catch (e) {
      r.repWorks = []
    }
  })
  await Promise.all(tasks)
}
</script>

<style scoped>
.hint { color: #909399; }
.reason-strong { color: #d43f3a; font-weight: 600; }
</style>
