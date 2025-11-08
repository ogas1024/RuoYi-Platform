<template>
  <div class="app-container">
    <el-card>
      <div style="margin-bottom: 12px; display:flex; gap:8px; align-items:center;">
        <el-input v-model="query.title" placeholder="按标题搜索" style="width: 220px" @keyup.enter="load" />
        <el-select v-model="view" placeholder="视图" style="width: 200px" @change="load">
          <el-option value="published" label="已发布" />
          <el-option value="draft" label="草稿" />
          <el-option value="expired" label="已过期" />
          <el-option value="archived" label="已归档" />
          <el-option value="all" label="全部" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="success" @click="openCreate">新建投票</el-button>
      </div>
      <el-table :data="list" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="240">
          <template #default="scope">
            <div style="display:flex;align-items:center;gap:8px;">
              <span>{{ scope.row.title }}</span>
              <el-tag v-if="scope.row.pinned===1" type="success">置顶</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status===1" type="success">已发布</el-tag>
            <el-tag v-else-if="scope.row.status===2" type="info">已归档</el-tag>
            <el-tag v-else type="warning">草稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="420">
          <template #default="scope">
            <el-button size="small" @click="openDetail(scope.row)" v-hasPermi="['manage:survey:query']">详情</el-button>
            <template v-if="scope.row.status===0">
              <el-button size="small" type="primary" @click="$router.push({ name:'ManageVoteNew', query:{ id: scope.row.id } })" v-hasPermi="['manage:survey:edit']">编辑</el-button>
              <el-button size="small" type="success" @click="doPublish(scope.row)" v-hasPermi="['manage:survey:publish']">发布</el-button>
            </template>
            <template v-else>
              <el-button size="small" type="primary" :disabled="scope.row.status===2" @click="extend(scope.row)">延期</el-button>
              <el-button size="small" type="danger" :disabled="scope.row.status===2" @click="archive(scope.row)">归档</el-button>
              <el-button size="small" type="warning" @click="togglePin(scope.row)" v-hasPermi="['manage:survey:pin']">
                {{ scope.row.pinned===1 ? '取消置顶' : '置顶' }}
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load"/>
    </el-card>

    <!-- 详情（含柱状图） -->
    <el-dialog
      v-model="detailVisible"
      title="投票详情"
      width="960px"
      append-to-body
      destroy-on-close
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      :z-index="3000">
      <!-- 加载态：改用骨架屏，避免 v-loading 遮罩阻断关闭交互 -->
      <div v-if="detailLoading" style="padding: 12px 4px; pointer-events: none;">
        <el-skeleton :rows="6" animated />
      </div>
      <!-- 详情内容 -->
      <div v-else-if="detail" style="display:flex; gap:16px; align-items:flex-start;">
        <div style="flex:1;">
          <p><b>标题：</b>{{ detail.title }}</p>
          <p><b>截止：</b>{{ detail.deadline || '-' }}</p>
          <el-divider>投票项</el-divider>
          <el-empty v-if="!detail.items || !detail.items.length" description="暂无投票项" />
          <ol v-else>
            <li v-for="i in detail.items" :key="i.id" style="margin-bottom:8px;">
              <div>{{ i.title }} <small v-if="i.required===1" style="color:#f56c6c">(必选)</small></div>
            </li>
          </ol>
          <template v-if="detail.items && detail.items.length">
            <div v-for="i in detail.items" :key="'chart-'+i.id" style="margin-top:8px;">
              <template v-if="i.options && i.options.length>0">
                <div :ref="el => setChartEl(i.id, el)" style="width:100%; height:220px;"></div>
                <div style="margin-top:6px; display:flex; gap:6px; flex-wrap:wrap;">
                  <el-tag v-for="op in i.options" :key="op.id">{{ op.label }}（{{ Number(op.voteCount || 0) }}）</el-tag>
                </div>
              </template>
            </div>
          </template>
        </div>
        <div style="width:360px;">
          <el-card shadow="never">
            <template #header>
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <span>已提交用户</span>
                <el-tag type="info">{{ submitUsers.length }}</el-tag>
              </div>
            </template>
            <el-scrollbar height="50vh">
              <el-empty v-if="!submitUsers.length" description="暂无提交" />
              <el-timeline v-else>
                <el-timeline-item v-for="u in submitUsers" :key="u.userId" :timestamp="u.submitTime">
                  <el-link type="primary" @click="viewUserAns(u)">{{ u.nickName || u.userName }}<small style="margin-left:6px;color:#909399;">#{{ u.userId }}</small></el-link>
                </el-timeline-item>
              </el-timeline>
            </el-scrollbar>
          </el-card>
        </div>
      </div>
      <template #footer>
        <el-button @click="closeDetailDialog">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 延期对话框 -->
    <el-dialog v-model="extendVisible" title="延期" width="420px" append-to-body :z-index="3000">
      <div>
        <p v-if="extendRow"><b>当前截止：</b>{{ extendRow.deadline || '-' }}</p>
        <el-date-picker v-model="extendDeadline" type="datetime" placeholder="选择新的截止时间"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"
                        :teleported="true" popper-class="manage-extend-popper" />
        <p style="margin-top:8px; color:#909399;">新截止时间必须晚于当前截止时间</p>
      </div>
      <template #footer>
        <el-button @click="extendVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmExtend">确定</el-button>
      </template>
    </el-dialog>

    <!-- 用户答卷弹窗（用于从详情中点击某位用户查看作答） -->
    <el-dialog v-model="ansVisible" :title="'用户答卷：' + userAnsName" width="720px" append-to-body :z-index="3000">
      <div v-if="userAns">
        <div v-for="i in userAns.items" :key="i.id" style="margin-bottom:12px;">
          <div><b>{{ i.title }}</b> <small v-if="i.required===1" style="color:#f56c6c">(必填)</small></div>
          <div v-if="i.type===1" style="margin-top:6px; white-space:pre-wrap;">{{ userAns.myAnswers?.[i.id] || '-' }}</div>
          <div v-else style="margin-top:6px;">
            <el-tag v-for="oid in (userAns.myAnswers?.[i.id]||[])" :key="oid" style="margin-right:6px;">
              {{ findOptionLabel(i, oid) }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="ansVisible=false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { listVote, getVote, addVote, archiveVote, extendVote, listVoteSubmittedUsers, getVoteUserAnswers, pinVote, publishVote } from '@/api/manage/vote'
import { ElMessageBox, ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: null, title: '', includeExpired: false, expiredOnly: false })
const view = ref('published')
const route = useRoute()

const detailVisible = ref(false)
const detail = ref(null)
const detailLoading = ref(false)
// 图表实例管理
const chartsReady = ref(false)
const chartElMap = {}
const chartMap = {}
const submitUsers = ref([])
const userAns = ref(null)
const userAnsName = ref('')
const ansVisible = ref(false)
const extendVisible = ref(false)
const extendDeadline = ref('')
const extendRow = ref(null)

function applyRouteDefault() {
  if (route.path.endsWith('/archive')) view.value = 'archived'; else view.value = 'published'
}

function load() {
  loading.value = true
  if (view.value === 'published') { query.status = 1; query.includeExpired = false; query.expiredOnly = false }
  else if (view.value === 'draft') { query.status = 0; query.includeExpired = true; query.expiredOnly = false }
  else if (view.value === 'archived') { query.status = 2; query.includeExpired = true; query.expiredOnly = false }
  else if (view.value === 'expired') { query.status = null; query.includeExpired = true; query.expiredOnly = true }
  else { query.status = null; query.includeExpired = true; query.expiredOnly = false }
  listVote(query).then(res => { list.value = res.rows || []; total.value = res.total || 0 }).finally(() => loading.value = false)
}

function openCreate() { 
  // 跳到新建页（限制为单题选择题，Create.vue 内校验）
  window.location.href = '#/manage/vote/new'
}

async function openDetail(row) {
  userAns.value = null
  userAnsName.value = ''
  submitUsers.value = []
  // 重置图表缓存，确保每次打开都是干净状态
  disposeCharts();
  for (const k in chartElMap) { delete chartElMap[k] }
  detail.value = { title: '', items: [] }
  detailVisible.value = true
  detailLoading.value = true
  chartsReady.value = false
  const rid = row.id || row.surveyId
  // 超时兜底：接口异常卡住时，自动结束加载态，避免无法关闭
  const stopLoadingTimer = setTimeout(() => {
    if (detailLoading.value) detailLoading.value = false
  }, 8000)
  // 保险兜底：1.5s 后仍未切换到内容，也结束加载态（避免骨架屏长时间占位影响关闭）
  const quickTimer = setTimeout(() => {
    if (detailLoading.value) detailLoading.value = false
  }, 1500)
  try {
    const res = await getVote(rid)
    const data = (res && res.data) ? res.data : null
    if (!data) {
      ElMessage.error('未获取到投票详情')
      detail.value = { title: '', items: [] }
    } else {
      // 结构兜底，避免渲染阶段异常
      const items = Array.isArray(data.items) ? data.items : []
      for (const it of items) {
        if (!Array.isArray(it.options)) it.options = []
        for (const op of it.options) {
          // 票数数值化，避免图表渲染报错
          op.voteCount = Number(op.voteCount || 0)
        }
      }
      data.items = items
      detail.value = data
      await nextTick()
      chartsReady.value = true
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('无法查看详情：请确认已分配权限 manage:survey:query')
  } finally {
    clearTimeout(stopLoadingTimer)
    clearTimeout(quickTimer)
    detailLoading.value = false
    // 关键：在切换为内容视图后再渲染图表（等待 DOM 挂载 ref 就绪）
    nextTick(() => setTimeout(renderCharts, 0))
  }
  // 提交用户列表异步加载，不阻塞详情展示
  try {
    const ures = await listVoteSubmittedUsers(rid)
    submitUsers.value = ures?.data || []
  } catch (e) { /* 忽略 */ }
}

function closeDetailDialog(){ detailLoading.value = false; detailVisible.value = false; disposeCharts() }

function extend(row) { extendRow.value = row; extendDeadline.value = row.deadline || ''; extendVisible.value = true }

function archive(row) { ElMessageBox.confirm('确认归档后将不可再提交，是否继续？', '提示', { type: 'warning' }).then(()=>{ archiveVote(row.id).then(()=>{ ElMessage.success('已归档'); load() }) }) }

onMounted(() => { applyRouteDefault(); load() })
watch(() => route.path, () => { applyRouteDefault(); load() })

function viewUserAns(u){
  userAnsName.value = u.nickName || u.userName || ('#'+u.userId)
  getVoteUserAnswers(detail.value.id, u.userId).then(res => { userAns.value = res.data; ansVisible.value = true })
}

function togglePin(row){ const willPin = !(row.pinned===1); pinVote(row.id, willPin).then(()=>{ ElMessage.success(willPin? '已置顶':'已取消置顶'); load() }) }

function confirmExtend(){
  if (!extendDeadline.value) return ElMessage.error('请选择新的截止时间')
  if (extendRow.value && extendRow.value.deadline) {
    const oldTs = new Date(extendRow.value.deadline).getTime()
    const newTs = new Date(extendDeadline.value).getTime()
    if (!(newTs > oldTs)) return ElMessage.error('新截止时间必须晚于当前截止时间')
  }
  extendVote(extendRow.value.id, extendDeadline.value).then(()=>{ ElMessage.success('已延期'); extendVisible.value = false; load() })
}

function doPublish(row){ publishVote(row.id).then(()=>{ ElMessage.success('已发布'); load() }) }

// 工具：根据选项ID找出显示文本
function findOptionLabel(item, oid){
  const op = (item.options||[]).find(o=>o.id===oid)
  return op ? op.label : oid
}

// 直连 ECharts：管理多个题目的柱状图
function setChartEl(id, el){ if (el) { chartElMap[id] = el } else { delete chartElMap[id] } }
function renderChartForItem(it){
  const el = chartElMap[it.id]
  if (!el) return
  let chart = chartMap[it.id]
  if (!chart) { chart = echarts.init(el); chartMap[it.id] = chart }
  const labels = (it.options||[]).map(o=>o.label)
  const values = (it.options||[]).map(o=>Number(o.voteCount||0))
  const option = {
    grid: { left: 10, right: 20, top: 10, bottom: 10, containLabel: true },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'value', boundaryGap: [0, 0.01] },
    yAxis: { type: 'category', data: labels },
    series: [{ type: 'bar', data: values, itemStyle: { color: '#409EFF' }, label: { show: true, position: 'right' } }]
  }
  chart.setOption(option)
  chart.resize()
}
function renderCharts(){ if (!detail.value || !Array.isArray(detail.value.items)) return; for (const it of detail.value.items) { if (it.options && it.options.length>0) renderChartForItem(it) } }
function disposeCharts(){ for (const k in chartMap) { try { chartMap[k].dispose() } catch(e){} delete chartMap[k] } }
function onResize(){ for (const k in chartMap) { try { chartMap[k].resize() } catch(e){} } }
watch(detailVisible, async (v)=>{ if (v) { await nextTick(); setTimeout(renderCharts, 0) } else { disposeCharts() } })
watch(detail, async ()=>{ await nextTick(); renderCharts() })
onMounted(()=>{ window.addEventListener('resize', onResize) })
onBeforeUnmount(()=>{ window.removeEventListener('resize', onResize); disposeCharts() })
</script>

<style>
/* 提升延期弹窗内日期选择器的浮层层级，避免被 Dialog 遮挡 */
.manage-extend-popper { z-index: 4000 !important; }
</style>
