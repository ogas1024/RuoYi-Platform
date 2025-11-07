<template>
  <div class="app-container">
    <el-card>
      <template #header>发布/编辑失物</template>
      <LostFoundEditor :key="formKey" v-model="form" />
      <div>
        <el-button @click="goBack" :disabled="loading">取消</el-button>
        <el-button type="primary" @click="submit" :loading="loading">提交审核</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addLostFoundPortal, updateLostFoundPortal, getLostFoundPortal } from '@/api/portal/lostfound'
import { ElMessage } from 'element-plus'
import LostFoundEditor from '@/components/lostfound/Editor.vue'

const router = useRouter()
const route = useRoute()
const getRouteId = () => {
  const qid = route.query?.id
  const pid = route.params?.id
  const raw = qid ?? pid
  const n = Number(raw)
  return Number.isFinite(n) && n > 0 ? n : null
}
const id = ref(getRouteId())
const loading = ref(false)
const defaultForm = () => ({ type:'lost', title:'', content:'', contactInfo:'', location:'', lostTime:'', images: [] })
const form = ref(defaultForm())
// 强制在切换“新增/编辑”或不同ID时重建编辑器，避免缓存导致不刷新
const formKey = computed(() => id.value || 'new')

const load = async () => {
  if (!id.value) return
  loading.value = true
  try {
    const res = await getLostFoundPortal(id.value)
    const data = res.data || res
    const item = data.item || {}
    const images = data.images || []
    form.value = { ...defaultForm(), ...item, images }
  } finally { loading.value = false }
}
onMounted(load)

// 监听路由参数变化，支持“我的发布”跳转到编辑
watch(() => [route.query.id, route.params.id], () => {
  id.value = getRouteId()
  if (id.value) {
    load()
  } else {
    // 新发布：重置表单
    form.value = defaultForm()
  }
})

const submit = async () => {
  loading.value = true
  const payload = JSON.parse(JSON.stringify(form.value))
  // 清理空 URL（后端会在保存时按顺序补齐 sortNo）
  payload.images = (payload.images||[]).filter(i => i.url)
  try {
    if (id.value) await updateLostFoundPortal(id.value, payload)
    else {
      const res = await addLostFoundPortal(payload)
      id.value = res && res.id
    }
    ElMessage.success('提交成功，待审核')
    goBack()
  } finally { loading.value = false }
}
const goBack = () => router.back()
</script>
