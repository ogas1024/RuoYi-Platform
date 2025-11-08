<template>
  <div ref="root" :style="{ width: '100%', height: height + 'px' }"></div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  labels: { type: Array, default: () => [] },
  values: { type: Array, default: () => [] },
  height: { type: Number, default: 220 },
  color: { type: String, default: '#409EFF' },
  title: { type: String, default: '' }
})

const root = ref(null)
let chart = null

function render() {
  if (!chart) return
  const labels = Array.isArray(props.labels) ? props.labels : []
  const values = Array.isArray(props.values) ? props.values : []
  const option = {
    title: props.title ? { text: props.title, left: 'center', textStyle: { fontSize: 14 } } : undefined,
    grid: { left: 10, right: 20, top: props.title ? 36 : 10, bottom: 10, containLabel: true },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'value', boundaryGap: [0, 0.01] },
    yAxis: { type: 'category', data: labels },
    series: [
      {
        type: 'bar',
        data: values,
        itemStyle: { color: props.color },
        label: { show: true, position: 'right' }
      }
    ]
  }
  chart.setOption(option)
}

function init() {
  if (root.value) {
    chart = echarts.init(root.value)
    render()
    window.addEventListener('resize', onResize)
  }
}

function onResize() { if (chart) chart.resize() }

onMounted(init)
onBeforeUnmount(() => { window.removeEventListener('resize', onResize); if (chart) chart.dispose(); chart = null })

watch(() => [props.labels, props.values, props.color, props.title], render, { deep: true })
</script>

