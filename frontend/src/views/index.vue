<template>
  <div class="app-container dashboard">
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card>
          <div class="metric">
            <div class="metric-title">课程资源</div>
            <div class="metric-value">{{ stat.crTotal }}</div>
            <div class="metric-sub">待审 {{ stat.crPending }} · 上架 {{ stat.crApproved }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card>
          <div class="metric">
            <div class="metric-title">数字图书</div>
            <div class="metric-value">{{ stat.libTotal }}</div>
            <div class="metric-sub">待审 {{ stat.libPending }} · 上架 {{ stat.libApproved }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card>
          <div class="metric">
            <div class="metric-title">失物招领</div>
            <div class="metric-value">{{ stat.lostOnline }}</div>
            <div class="metric-sub">待审 {{ stat.lostAudit }} · 发布中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card>
          <div class="metric">
            <div class="metric-title">快捷操作</div>
            <div class="metric-value">
              <el-button type="primary" :loading="loading" @click="refresh">刷新</el-button>
            </div>
            <div class="quick-actions">
              <el-button size="small" plain type="primary" @click="$router.push('/manage/course-resource/audit')">资源审核</el-button>
              <el-button size="small" plain type="primary" @click="$router.push('/manage/library/audit')">图书审核</el-button>
              <el-button size="small" plain type="primary" @click="$router.push('/manage/lostfound/audit')">失物审核</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部占比（饼图）：课程资源 专业占比 / 课程占比 -->
    <el-row :gutter="16" class="module-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd"><span>课程资源上传·专业占比（近30天）</span></div>
          </template>
          <div ref="crMajorPieRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd"><span>课程资源上传·课程占比（近30天）</span></div>
          </template>
          <div ref="crCoursePieRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程资源：上传/下载 并排 -->
    <el-row :gutter="16" class="module-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>近{{ trendDays }}天课程资源上传趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="loadTrend">
                <el-radio-button :label="7">7天</el-radio-button>
                <el-radio-button :label="30">30天</el-radio-button>
                <el-radio-button :label="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>近{{ trendDays }}天课程资源下载趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="loadDownTrend">
                <el-radio-button :label="7">7天</el-radio-button>
                <el-radio-button :label="30">30天</el-radio-button>
                <el-radio-button :label="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="downRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程资源：排行（下载Top5 / 用户积分Top5）并排 -->
    <el-row :gutter="16" class="module-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>课程资源下载榜（Top5）</span>
            </div>
          </template>
          <div ref="crTopRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>课程资源用户下载榜（Top5）</span>
            </div>
          </template>
          <div ref="crUserDlTopRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数字图书：上传/下载 并排 -->
    <el-row :gutter="16" class="module-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>近{{ libTrendDays }}天数字图书上传趋势</span>
              <el-radio-group v-model="libTrendDays" size="small" @change="loadLibTrend">
                <el-radio-button :label="7">7天</el-radio-button>
                <el-radio-button :label="30">30天</el-radio-button>
                <el-radio-button :label="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="libTrendRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>近{{ libTrendDays }}天数字图书下载趋势</span>
              <el-radio-group v-model="libTrendDays" size="small" @change="loadLibDownTrend">
                <el-radio-button :label="7">7天</el-radio-button>
                <el-radio-button :label="30">30天</el-radio-button>
                <el-radio-button :label="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="libDownRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数字图书：排行（图书Top5 / 用户Top5）并排 -->
    <el-row :gutter="16" class="module-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>数字图书下载榜（Top5）</span>
            </div>
          </template>
          <div ref="libBookTopRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>用户下载榜（Top5）</span>
            </div>
          </template>
          <div ref="libUserTopRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 贡献榜与上传榜（底部并排）：课程资源用户积分Top5 / 数字图书用户上传Top5 -->
    <el-row :gutter="16" class="module-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>课程资源用户贡献榜（Top5）</span>
            </div>
          </template>
          <div ref="crUserScoreTopRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-hd">
              <span>数字图书用户上传榜（Top5）</span>
            </div>
          </template>
          <div ref="libUserUploadTopRef" style="height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
import { uploadTrend as apiUploadTrend } from '@/api/manage/courseResource'
import { downloadTrend as apiDownloadTrend } from '@/api/manage/courseResource'
import { topResources as crTopResources } from '@/api/manage/courseResource'
import { topScoreUsers as crTopUsers } from '@/api/manage/courseResource'
import { topDownloadUsers as crTopDlUsers } from '@/api/manage/courseResource'
import { majorShare as crMajorShare } from '@/api/manage/courseResource'
import { courseShare as crCourseShare } from '@/api/manage/courseResource'
import { uploadTrend as libUploadTrend } from '@/api/manage/library'
import { downloadTrend as libDownloadTrend } from '@/api/manage/library'
import { topBooks as libTopBooks } from '@/api/manage/library'
import { topDownloadUsers as libTopUsers } from '@/api/manage/library'
import { topUploadUsers as libTopUploadUsers } from '@/api/manage/library'

const stat = ref({
  crTotal: 0, crPending: 0, crApproved: 0,
  libTotal: 0, libPending: 0, libApproved: 0,
  lostAudit: 0, lostOnline: 0
})
const loading = ref(false)

const trendRef = ref(null)
const downRef = ref(null)
let trendChart = null
let downChart = null
const trendDays = ref(30)

// 课程资源排行（Top5）
const crTopRef = ref(null)
const crUserDlTopRef = ref(null)
const crUserScoreTopRef = ref(null)
let crTopChart = null
let crUserDlTopChart = null
let crUserScoreTopChart = null

const libTrendRef = ref(null)
const libDownRef = ref(null)
let libTrendChart = null
let libDownChart = null
const libTrendDays = ref(30)

const libBookTopRef = ref(null)
const libUserTopRef = ref(null)
let libBookTopChart = null
let libUserTopChart = null
const libUserUploadTopRef = ref(null)
let libUserUploadTopChart = null

// 课程资源占比（饼图）
const crMajorPieRef = ref(null)
const crCoursePieRef = ref(null)
let crMajorPieChart = null
let crCoursePieChart = null

async function getCount(url, params = {}) {
  const res = await request.get(url, { params: { pageNum: 1, pageSize: 1, ...params }, silent: true })
  return res && typeof res.total === 'number' ? res.total : 0
}

async function loadStats() {
  loading.value = true
  try {
    const [crTotal, crPending, crApproved, libTotal, libPending, libApproved, lostAudit, lostOnline] = await Promise.all([
      getCount('/manage/courseResource/list', {}),
      getCount('/manage/courseResource/list', { status: 0 }),
      getCount('/manage/courseResource/list', { status: 1 }),
      getCount('/manage/library/list', {}),
      getCount('/manage/library/list', { status: 0 }),
      getCount('/manage/library/list', { status: 1 }),
      getCount('/manage/lostfound/audit/list', {}),
      getCount('/manage/lostfound/list', {})
    ])
    stat.value = { crTotal, crPending, crApproved, libTotal, libPending, libApproved, lostAudit, lostOnline }
  } finally {
    loading.value = false
  }
}

async function loadTrend() {
  try {
    const res = await apiUploadTrend(trendDays.value, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const days = list.map(d => d.day)
    const counts = list.map(d => d.count)
    if (!trendChart) trendChart = echarts.init(trendRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: days, boundaryGap: false },
      yAxis: { type: 'value' },
      series: [
        { type: 'line', data: counts, smooth: true, areaStyle: { opacity: 0.15 }, showSymbol: false, color: '#409EFF' }
      ]
    })
  } catch (e) {
    if (trendChart) trendChart.clear()
  }
}

async function loadDownTrend() {
  try {
    const res = await apiDownloadTrend(trendDays.value, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const days = list.map(d => d.day)
    const counts = list.map(d => d.count)
    if (!downChart) downChart = echarts.init(downRef.value)
    downChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: days, boundaryGap: false },
      yAxis: { type: 'value' },
      series: [
        { type: 'line', data: counts, smooth: true, areaStyle: { opacity: 0.15 }, showSymbol: false, color: '#67C23A' }
      ]
    })
  } catch (e) {
    if (downChart) downChart.clear()
  }
}

async function loadLibTrend() {
  try {
    const res = await libUploadTrend(libTrendDays.value, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const days = list.map(d => d.day)
    const counts = list.map(d => d.count)
    if (!libTrendChart) libTrendChart = echarts.init(libTrendRef.value)
    libTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: days, boundaryGap: false },
      yAxis: { type: 'value' },
      series: [
        { type: 'line', data: counts, smooth: true, areaStyle: { opacity: 0.15 }, showSymbol: false, color: '#E6A23C' }
      ]
    })
  } catch (e) {
    if (libTrendChart) libTrendChart.clear()
  }
}

async function loadLibDownTrend() {
  try {
    const res = await libDownloadTrend(libTrendDays.value, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const days = list.map(d => d.day)
    const counts = list.map(d => d.count)
    if (!libDownChart) libDownChart = echarts.init(libDownRef.value)
    libDownChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: days, boundaryGap: false },
      yAxis: { type: 'value' },
      series: [
        { type: 'line', data: counts, smooth: true, areaStyle: { opacity: 0.15 }, showSymbol: false, color: '#F56C6C' }
      ]
    })
  } catch (e) {
    if (libDownChart) libDownChart.clear()
  }
}

async function loadLibTopBooks() {
  try {
    const res = await libTopBooks(5, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const names = list.map(i => i.title || i.bookName || `#${i.id}`)
    const counts = list.map(i => (i.downloadCount ?? i.count ?? 0))
    if (!libBookTopChart) libBookTopChart = echarts.init(libBookTopRef.value)
    libBookTopChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 20, top: 20, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      series: [{ type: 'bar', data: counts, barWidth: 18, itemStyle: { color: '#409EFF' } }]
    })
  } catch (e) {
    if (libBookTopChart) libBookTopChart.clear()
  }
}

async function loadLibTopUsers() {
  try {
    const res = await libTopUsers(5, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const names = list.map(i => i.nickname || i.username || `用户#${i.userId}`)
    const counts = list.map(i => (i.downloadCount ?? i.passedCount ?? 0))
    if (!libUserTopChart) libUserTopChart = echarts.init(libUserTopRef.value)
    libUserTopChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 20, top: 20, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      series: [{ type: 'bar', data: counts, barWidth: 18, itemStyle: { color: '#67C23A' } }]
    })
  } catch (e) {
    if (libUserTopChart) libUserTopChart.clear()
  }
}

async function loadCrTopResources() {
  try {
    const res = await crTopResources(5, 30, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const names = list.map(i => i.resourceName || `#${i.id}`)
    const counts = list.map(i => (i.downloadCount ?? 0))
    if (!crTopChart) crTopChart = echarts.init(crTopRef.value)
    crTopChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 20, top: 20, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      series: [{ type: 'bar', data: counts, barWidth: 18, itemStyle: { color: '#909399' } }]
    })
  } catch (e) {
    if (crTopChart) crTopChart.clear()
  }
}

async function loadCrTopScoreUsers() {
  try {
    const res = await crTopUsers(5, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const names = list.map(i => i.username || `用户#${i.userId}`)
    const counts = list.map(i => (i.totalScore ?? 0))
    if (!crUserScoreTopChart) crUserScoreTopChart = echarts.init(crUserScoreTopRef.value)
    crUserScoreTopChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 20, top: 20, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      series: [{ type: 'bar', data: counts, barWidth: 18, itemStyle: { color: '#a6c' } }]
    })
  } catch (e) {
    if (crUserScoreTopChart) crUserScoreTopChart.clear()
  }
}

async function loadCrTopDownloadUsers() {
  try {
    const res = await crTopDlUsers(5, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const names = list.map(i => i.nickname || i.username || `用户#${i.userId}`)
    const counts = list.map(i => (i.passedCount ?? i.downloadCount ?? 0))
    if (!crUserDlTopChart) crUserDlTopChart = echarts.init(crUserDlTopRef.value)
    crUserDlTopChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 20, top: 20, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      series: [{ type: 'bar', data: counts, barWidth: 18, itemStyle: { color: '#91CC75' } }]
    })
  } catch (e) {
    if (crUserDlTopChart) crUserDlTopChart.clear()
  }
}

async function loadLibTopUploadUsers() {
  try {
    const res = await libTopUploadUsers(5, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const names = list.map(i => i.nickname || i.username || `用户#${i.userId}`)
    const counts = list.map(i => (i.passedCount ?? 0))
    if (!libUserUploadTopChart) libUserUploadTopChart = echarts.init(libUserUploadTopRef.value)
    libUserUploadTopChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 20, top: 20, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      series: [{ type: 'bar', data: counts, barWidth: 18, itemStyle: { color: '#FAC858' } }]
    })
  } catch (e) {
    if (libUserUploadTopChart) libUserUploadTopChart.clear()
  }
}

async function loadCrMajorShare() {
  try {
    const res = await crMajorShare(30, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const data = list.map(i => ({ name: i.name, value: i.value }))
    if (!crMajorPieChart) crMajorPieChart = echarts.init(crMajorPieRef.value)
    crMajorPieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { top: 'bottom' },
      series: [{ type: 'pie', radius: '70%', data }]
    })
  } catch (e) {
    if (crMajorPieChart) crMajorPieChart.clear()
  }
}

async function loadCrCourseShare() {
  try {
    const res = await crCourseShare(30, { headers: { silent: true } })
    const list = (res && Array.isArray(res.data)) ? res.data : []
    const data = list.map(i => ({ name: i.name, value: i.value }))
    if (!crCoursePieChart) crCoursePieChart = echarts.init(crCoursePieRef.value)
    crCoursePieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { top: 'bottom' },
      series: [{ type: 'pie', radius: '70%', data }]
    })
  } catch (e) {
    if (crCoursePieChart) crCoursePieChart.clear()
  }
}

function resize() { if (trendChart) trendChart.resize(); if (downChart) downChart.resize() }
function resizeLib() { if (libTrendChart) libTrendChart.resize(); if (libDownChart) libDownChart.resize(); if (libBookTopChart) libBookTopChart.resize(); if (libUserTopChart) libUserTopChart.resize(); if (libUserUploadTopChart) libUserUploadTopChart.resize() }
function resizeCr() { if (crTopChart) crTopChart.resize(); if (crUserDlTopChart) crUserDlTopChart.resize(); if (crUserScoreTopChart) crUserScoreTopChart.resize(); if (crMajorPieChart) crMajorPieChart.resize(); if (crCoursePieChart) crCoursePieChart.resize() }
function resizeAll() { resize(); resizeLib(); resizeCr() }
function refresh() { loadStats(); loadTrend(); loadDownTrend(); loadCrTopResources(); loadCrTopDownloadUsers(); loadCrTopScoreUsers(); loadCrMajorShare(); loadCrCourseShare(); loadLibTrend(); loadLibDownTrend(); loadLibTopBooks(); loadLibTopUsers(); loadLibTopUploadUsers() }

onMounted(() => {
  loadStats()
  loadTrend()
  loadDownTrend()
  loadCrTopResources()
  loadCrTopDownloadUsers()
  loadCrTopScoreUsers()
  loadCrMajorShare()
  loadCrCourseShare()
  loadLibTrend()
  loadLibDownTrend()
  loadLibTopBooks()
  loadLibTopUsers()
  loadLibTopUploadUsers()
  window.addEventListener('resize', resizeAll)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeAll)
  if (trendChart) { trendChart.dispose(); trendChart = null }
  if (downChart) { downChart.dispose(); downChart = null }
  if (libTrendChart) { libTrendChart.dispose(); libTrendChart = null }
  if (libDownChart) { libDownChart.dispose(); libDownChart = null }
  if (libBookTopChart) { libBookTopChart.dispose(); libBookTopChart = null }
  if (libUserTopChart) { libUserTopChart.dispose(); libUserTopChart = null }
  if (libUserUploadTopChart) { libUserUploadTopChart.dispose(); libUserUploadTopChart = null }
  if (crTopChart) { crTopChart.dispose(); crTopChart = null }
  if (crUserDlTopChart) { crUserDlTopChart.dispose(); crUserDlTopChart = null }
  if (crUserScoreTopChart) { crUserScoreTopChart.dispose(); crUserScoreTopChart = null }
  if (crMajorPieChart) { crMajorPieChart.dispose(); crMajorPieChart = null }
  if (crCoursePieChart) { crCoursePieChart.dispose(); crCoursePieChart = null }
})
</script>

<style scoped>
.dashboard { padding: 0; }
.metric { text-align: left; }
.metric-title { font-size: 14px; color: #909399; }
.metric-value { font-size: 28px; font-weight: 600; margin: 4px 0; }
.metric-sub { font-size: 12px; color: #909399; }
.quick-actions { margin-top: 6px; display: flex; gap: 6px; flex-wrap: wrap; }
.module-row { margin-top: 16px; }
.card-hd { display: flex; align-items: center; justify-content: space-between; }
</style>
