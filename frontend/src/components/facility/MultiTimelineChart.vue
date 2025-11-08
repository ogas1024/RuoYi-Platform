<template>
  <div ref="root" :style="{ width: '100%', height: computedHeight + 'px' }"></div>
  <!-- 容器高度随行数自适应，避免滚动条遮挡 -->
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, watch, computed, nextTick} from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  items: {type: Array, default: () => []}, // [{ roomId, roomName, floorNo, segments: [{id,start,end,status}]}]
  from: {type: [String, Number, Date], default: null},
  to: {type: [String, Number, Date], default: null},
  rowHeight: {type: Number, default: 32}, // 每行高度
  minHeight: {type: Number, default: 260}
})
const emit = defineEmits(['segment-click'])

const root = ref(null)
let chart

const computedHeight = computed(() => {
  const rows = Array.isArray(props.items) ? props.items.length : 0
  return Math.max(props.minHeight, 80 + rows * props.rowHeight)
})

function colorByStatus(st) {
  if (st === 'approved' || st === 'ongoing') return '#67C23A'
  if (st === 'completed') return '#E6A23C'
  if (st === 'pending') return '#909399'
  if (st === 'rejected' || st === 'canceled') return '#F56C6C'
  return '#909399'
}

function renderItem(params, api) {
  const categoryIndex = api.value(2)
  const start = api.coord([api.value(0), categoryIndex])
  const end = api.coord([api.value(1), categoryIndex])
  // 按分类轴带宽自适应高度，确保每行都能居中显示
  const bandSize = api.size([0, 1])[1] || props.rowHeight
  const h = Math.min(20, Math.max(6, bandSize * 0.6))
  const x = Math.min(start[0], end[0])
  const w = Math.max(1, Math.abs(end[0] - start[0]))
  const yCenter = start[1]
  const y = yCenter - h / 2
  const rectShape = echarts.graphic.clipRectByRect({ x, y, width: w, height: h }, {
    x: params.coordSys.x,
    y: params.coordSys.y,
    width: params.coordSys.width,
    height: params.coordSys.height
  })
  const st = api.value(3)
  return rectShape && { type: 'rect', shape: rectShape, style: api.style({ fill: colorByStatus(st) }) }
}

// 统一且安全地解析时间为时间戳（毫秒）
function parseTs(v) {
  if (!v) return null
  // 直接 Date
  if (v instanceof Date) {
    const t = v.getTime()
    return isNaN(t) ? null : t
  }
  // 数字或数字字符串（时间戳）
  if (typeof v === 'number') return v
  if (typeof v === 'string') {
    const s = v.trim()
    if (!s) return null
    if (/^\d{10,13}$/.test(s)) {
      const n = Number(s)
      return s.length === 13 ? n : n * 1000
    }
    // 常见后端格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd
    // 采用手动拆分，避免各浏览器对 new Date('yyyy-MM-dd HH:mm:ss') 解析不一致
    const [datePart, timePart = '00:00:00'] = s.replace(/\//g, '-').split(/[ T]/)
    const dms = datePart.split('-').map(Number)
    const tms = timePart.split(':').map(Number)
    if (dms.length >= 3) {
      const [Y, M, D] = dms
      const [h = 0, m = 0, sec = 0] = tms
      const t = new Date(Y, (M || 1) - 1, D || 1, h, m, sec).getTime()
      return isNaN(t) ? null : t
    }
    // 兜底：交给 Date 自行解析
    const t = new Date(s).getTime()
    return isNaN(t) ? null : t
  }
  return null
}

const toTs = parseTs

function buildData() {
  const categories = (props.items || []).map((it, idx) => it.roomName || `房间${it.roomId || idx + 1}`)
  const data = []
  ;(props.items || []).forEach((it, idx) => {
    ;(it.segments || []).forEach(seg => {
      const start = parseTs(seg.start)
      const end = parseTs(seg.end)
      if (!isNaN(start) && !isNaN(end)) {
        data.push({
          value: [start, end, idx, seg.status],
          meta: {id: seg.id, start: seg.start, end: seg.end, status: seg.status, roomId: it.roomId}
        })
      }
    })
  })
  return {categories, data}
}

function init() {
  if (!root.value) return
  chart = echarts.init(root.value)
  const {categories, data} = buildData()
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: p => {
        const d = p.data && p.data.meta
        if (!d) return ''
        const map = {pending: '待审', approved: '已通过', ongoing: '进行中', completed: '已完成', rejected: '已驳回', canceled: '已取消'}
        const st = map[d.status] || d.status
        return `${st}<br/>${d.start} ~ ${d.end}`
      }
    },
    grid: {left: 120, right: 20, bottom: 30, top: 30, containLabel: true},
    xAxis: {type: 'time', min: toTs(props.from), max: toTs(props.to)},
    yAxis: {
      type: 'category',
      data: categories,
      axisTick: {show: false},
      axisLine: {show: true}
    },
    series: [{
      type: 'custom',
      renderItem,
      encode: {x: [0, 1], y: 2},
      data
    }]
  })
  // 初次渲染后尝试调整尺寸（尤其在对话框中初次为 display:none 的场景）
  nextTick(() => resize())
  chart.on('click', (params) => {
    const id = params?.data?.meta?.id
    if (id) emit('segment-click', id)
  })
}

function update() {
  if (!chart) return
  const {categories, data} = buildData()
  chart.setOption({
    xAxis: {min: toTs(props.from), max: toTs(props.to)},
    yAxis: {data: categories},
    series: [{data}]
  })
  // 数据或高度改变后确保重算尺寸
  resize()
}

onMounted(() => {
  init()
  update()
  window.addEventListener('resize', resize)
})
onBeforeUnmount(() => {
  if (chart) {
    chart.dispose()
    chart = null
  }
  window.removeEventListener('resize', resize)
})

function resize() {
  if (chart) chart.resize()
}

watch(() => props.items, update, {deep: true})
watch(() => [props.from, props.to, props.rowHeight], update)
// 行数变化会影响容器高度，下一帧触发 resize
watch(() => computedHeight.value, () => nextTick(() => resize()))

// 暴露给父组件在弹窗 opened 后主动触发
defineExpose({ resize })
</script>
