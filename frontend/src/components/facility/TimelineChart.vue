<template>
  <div ref="root" :style="{ width: '100%', height: height + 'px' }"></div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, watch, nextTick} from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {type: Array, default: () => []}, // [{ id, start, end, status }]
  height: {type: Number, default: 320},
  from: {type: [String, Number, Date], default: null},
  to: {type: [String, Number, Date], default: null}
})
const emit = defineEmits(['segment-click'])

const root = ref(null)
let chart

function renderItem(params, api) {
  const categoryIndex = api.value(2)
  const start = api.coord([api.value(0), categoryIndex])
  const end = api.coord([api.value(1), categoryIndex])
  const height = 20
  const rectShape = echarts.graphic.clipRectByRect({
    x: start[0],
    y: params.coordSys.y + (params.coordSys.height - height) / 2,
    width: end[0] - start[0],
    height
  }, {x: params.coordSys.x, y: params.coordSys.y, width: params.coordSys.width, height: params.coordSys.height})
  const st = api.value(3)
  let color = '#909399'
  if (st === 'approved' || st === 'ongoing') color = '#67C23A'
  if (st === 'completed') color = '#E6A23C'
  if (st === 'pending') color = '#909399'
  return rectShape && {type: 'rect', shape: rectShape, style: api.style({fill: color})}
}

// 统一且安全地解析时间为时间戳（毫秒）
function parseTs(v) {
  if (!v) return null
  if (v instanceof Date) {
    const t = v.getTime()
    return isNaN(t) ? null : t
  }
  if (typeof v === 'number') return v
  if (typeof v === 'string') {
    const s = v.trim()
    if (!s) return null
    if (/^\d{10,13}$/.test(s)) {
      const n = Number(s)
      return s.length === 13 ? n : n * 1000
    }
    const [datePart, timePart = '00:00:00'] = s.replace(/\//g, '-').split(/[ T]/)
    const dms = datePart.split('-').map(Number)
    const tms = timePart.split(':').map(Number)
    if (dms.length >= 3) {
      const [Y, M, D] = dms
      const [h = 0, m = 0, sec = 0] = tms
      const t = new Date(Y, (M || 1) - 1, D || 1, h, m, sec).getTime()
      return isNaN(t) ? null : t
    }
    const t = new Date(s).getTime()
    return isNaN(t) ? null : t
  }
  return null
}

function toSeriesData(list) {
  return (list || []).map(s => ({
    value: [parseTs(s.start), parseTs(s.end), 0, s.status],
    meta: {id: s.id, start: s.start, end: s.end, status: s.status}
  }))
}

const toTs = parseTs

function init() {
  if (!root.value) return
  chart = echarts.init(root.value)
  chart.setOption({
    tooltip: {
      trigger: 'item', formatter: p => {
        const d = p.data && p.data.meta
        if (!d) return ''
        const map = {pending: '待审', approved: '已通过', ongoing: '进行中', completed: '已完成', unknown: '未知'}
        const st = map[d.status] || d.status
        return `${st}<br/>${d.start} ~ ${d.end}`
      }
    },
    grid: {left: 40, right: 20, bottom: 30, top: 30},
    xAxis: {type: 'time', min: toTs(props.from), max: toTs(props.to)},
    yAxis: {type: 'category', data: ['占用'], axisTick: {show: false}, axisLine: {show: false}},
    series: [{type: 'custom', renderItem, encode: {x: [0, 1], y: 2}, data: toSeriesData(props.data)}]
  })
  // 初次渲染后尝试调整尺寸（常见于父容器切换显示后）
  nextTick(() => resize())
  chart.on('click', (params) => {
    const id = params?.data?.meta?.id;
    if (id) emit('segment-click', id)
  })
}

function update() {
  if (!chart) return
  chart.setOption({
    xAxis: {min: toTs(props.from), max: toTs(props.to)},
    series: [{data: toSeriesData(props.data)}]
  })
  // 数据或容器变化后，确保尺寸正确
  resize()
}

onMounted(() => {
  init();
  update();
  window.addEventListener('resize', resize)
})
onBeforeUnmount(() => {
  if (chart) {
    chart.dispose();
    chart = null
  }
  window.removeEventListener('resize', resize)
})

function resize() {
  if (chart) chart.resize()
}

watch(() => props.data, update, {deep: true})
watch(() => [props.from, props.to], update)

// 暴露方法，便于父组件在 el-dialog opened 后主动触发
defineExpose({ resize })
</script>
