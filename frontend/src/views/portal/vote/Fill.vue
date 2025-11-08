<template>
  <div class="portal-container" v-loading="loading">
    <h2>{{ detail?.title || '投票' }}</h2>
    <p v-if="detail"><b>截止：</b>{{ detail.deadline || '-' }}
      <el-tag v-if="detail.pinned===1" type="danger" effect="dark" style="margin-left:8px;">置顶</el-tag>
      <el-tag v-if="detail.status===2" type="info" style="margin-left:8px;">已归档</el-tag>
      <el-tag v-else-if="expired" type="danger" style="margin-left:8px;">已过期</el-tag>
    </p>
    <el-form v-if="detail" label-width="100px">
      <div v-for="(it,idx) in detail.items" :key="it.id" style="margin:10px 0; padding:10px; border:1px dashed var(--el-border-color); border-radius:6px;">
        <div style="margin-bottom:6px;">
          <b>#{{ idx+1 }} {{ it.title }}</b>
          <small v-if="it.required===1" style="color:#f56c6c">(必选)</small>
        </div>
        <div>
          <el-radio-group v-if="it.type===2 && answers[it.id]" v-model="answers[it.id].optionIds">
            <el-radio v-for="op in it.options" :key="op.id" :label="op.id">{{ op.label }}</el-radio>
          </el-radio-group>
          <el-checkbox-group v-else-if="it.type===3 && answers[it.id]" v-model="answers[it.id].optionIds">
            <el-checkbox v-for="op in it.options" :key="op.id" :label="op.id">{{ op.label }}</el-checkbox>
          </el-checkbox-group>
          <div v-else style="color:#909399;">加载中...</div>
        </div>
      </div>
      <el-button type="primary" @click="submit" :disabled="expired || detail.status===2">提交</el-button>
      <span v-if="expired" style="margin-left:8px; color:#f56c6c;">已过截止时间，不能提交</span>
      <span v-else-if="detail.status===2" style="margin-left:8px; color:#909399;">投票已归档，不能提交</span>
    </el-form>

    <!-- 投票结果：仅在投票结束（过期/归档）后展示 ECharts 柱状图 -->
    <div v-if="detail && (expired || detail.status===2)" style="margin-top:16px;">
      <el-divider>投票结果</el-divider>
      <div v-for="(it,idx) in detail.items" :key="'res-'+it.id" style="margin:10px 0;">
        <div style="margin-bottom:6px;"><b>#{{ idx+1 }} {{ it.title }}</b></div>
        <template v-if="(it.type===2 || it.type===3) && it.options && it.options.length>0">
          <div :ref="el => setChartEl(it.id, el)" style="width:100%; height:220px;"></div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, watch, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getVote, submitVote } from '@/api/portal/vote'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const route = useRoute()
const id = Number(route.query.id)
const loading = ref(false)
const detail = ref(null)
const answers = reactive({})
const showResult = computed(()=> detail.value && (expired.value || detail.value.status===2))
const chartElMap = {}
const chartMap = {}

const expired = computed(()=>{ if (!detail.value || !detail.value.deadline) return false; return new Date(detail.value.deadline).getTime() < Date.now() })

function initAnswers(){
  if (!detail.value) return
  for (const it of detail.value.items){
    const preset = detail.value.myAnswers?.[it.id]
    if (it.type===2){
      // 单选：若已有答卷，用首个选项回填为数字；否则为 null
      const v = Array.isArray(preset) && preset.length>0 ? preset[0] : null
      answers[it.id] = { itemId: it.id, optionIds: v ? v : null }
    } else if (it.type===3){
      // 多选：回填为数组
      answers[it.id] = { itemId: it.id, optionIds: Array.isArray(preset) ? preset : [] }
    } else {
      // 投票页仅出现选择题，这里兜底
      answers[it.id] = { itemId: it.id, optionIds: null }
    }
  }
}

function load(){
  loading.value = true
  getVote(id).then(res=>{
    detail.value = res.data
    // 规范 myAnswers 的 key 为数字，便于以 item.id 访问
    const fixed = {}
    if (detail.value.myAnswers){
      for (const k in detail.value.myAnswers){ fixed[Number(k)] = detail.value.myAnswers[k] }
      detail.value.myAnswers = fixed
    }
    initAnswers()
    // 结果区渲染：等待 DOM 与 ref 就绪
    nextTick(()=>{ if (showResult.value) setTimeout(renderCharts, 0) })
  }).finally(()=>loading.value=false)
}

function hasSubmitted(){
  const m = detail.value && detail.value.myAnswers
  if (!m) return false
  try { return Object.keys(m).length > 0 } catch(e){ return false }
}

function submit(){
  if (!detail.value) return
  // 前端必选校验（与后端一致）：防止空提交
  for (const it of detail.value.items||[]) {
    if ((it.type===2 || it.type===3) && it.required===1) {
      const a = answers[it.id]
      const arr = Array.isArray(a?.optionIds) ? a.optionIds : (typeof a?.optionIds==='number' ? [a.optionIds] : [])
      if (!arr || arr.length===0) {
        return ElMessage.error(`必选题未选择：${it.title}`)
      }
    }
  }
  const payload = Object.values(answers).map(x=>({ itemId:x.itemId, optionIds: Array.isArray(x.optionIds)? x.optionIds : (typeof x.optionIds==='number' ? [x.optionIds] : []) }))
  const wasSubmitted = hasSubmitted()
  submitVote(id, payload).then(()=>{ ElMessage.success(wasSubmitted ? '已修改' : '已提交'); load() })
}

onMounted(load)
watch(showResult, async (v)=>{ if (v) { await nextTick(); renderCharts() } })
watch(detail, async ()=>{ if (showResult.value) { await nextTick(); renderCharts() } })
onBeforeUnmount(()=>{ disposeCharts() })

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
    series: [{ type: 'bar', data: values, itemStyle: { color: '#67C23A' }, label: { show: true, position: 'right' } }]
  }
  chart.setOption(option)
  chart.resize()
}
function renderCharts(){ if (!detail.value || !Array.isArray(detail.value.items)) return; for (const it of detail.value.items) { if ((it.type===2 || it.type===3) && it.options && it.options.length>0) renderChartForItem(it) } }
function disposeCharts(){ for (const k in chartMap) { try { chartMap[k].dispose() } catch(e){} delete chartMap[k] } }
</script>
