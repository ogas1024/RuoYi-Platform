<template>
  <el-dialog :model-value="modelValue" @update:modelValue="$emit('update:modelValue', $event)" :title="title"
             width="600px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="申请ID">{{ info.id }}</el-descriptions-item>
      <el-descriptions-item label="房间">{{ meta.roomName || '-' }}（ID: {{ info.roomId }}）</el-descriptions-item>
      <el-descriptions-item label="楼房">{{ meta.buildingName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="申请人">{{ meta.applicantName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ meta.statusText || statusText(info.status) }}</el-descriptions-item>
      <el-descriptions-item label="开始时间">{{ info.startTime }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ info.endTime }}</el-descriptions-item>
      <el-descriptions-item label="目的">{{ info.purpose }}</el-descriptions-item>
    </el-descriptions>
    <div class="mt-12">
      <div style="font-weight:600;margin:8px 0;">使用人</div>
      <el-table :data="users" size="small" border>
        <el-table-column prop="isApplicant" label="申请人" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isApplicant==='1'?'success':'info'">{{ row.isApplicant === '1' ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <!-- 不展示用户ID，后端如需可扩展返回用户名/昵称字段后再补充展示 -->
      </el-table>
    </div>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">关闭</el-button>
      <el-button v-if="showActions && canApprove" type="success" @click="$emit('approve', info.id)"
                 v-hasPermi="['manage:facility:booking:audit:approve']">通过
      </el-button>
      <el-button v-if="showActions && canApprove" type="warning" @click="onReject"
                 v-hasPermi="['manage:facility:booking:audit:reject']">驳回
      </el-button>
      <el-button v-if="showActions && canCancel" type="danger" @click="$emit('cancel', info.id)"
                 v-hasPermi="['manage:facility:booking:cancel']">取消
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, watch, computed} from 'vue'
import {ElMessageBox} from 'element-plus'

const props = defineProps({
  modelValue: {type: Boolean, default: false},
  bookingId: {type: [Number, String], default: null},
  fetcher: {type: Function, required: true}, // (id) => Promise<{data:{booking, users, meta}}>
  title: {type: String, default: '预约详情'},
  showActions: {type: Boolean, default: false}
})
const emit = defineEmits(['update:modelValue', 'approve', 'reject', 'cancel'])

const info = ref({})
const users = ref([])
const meta = ref({})

const canApprove = computed(() => String(info.value?.status) === '0')
const canCancel = computed(() => {
  const st = String(info.value?.status)
  if (!(st === '0' || st === '1')) return false
  try {
    return new Date(info.value?.startTime) > new Date()
  } catch (e) {
    return false
  }
})

async function load() {
  if (!props.modelValue || !props.bookingId) return
  const {data} = await props.fetcher(props.bookingId)
  info.value = data.booking || {}
  users.value = data.users || []
  meta.value = data.meta || {}
}

async function onReject() {
  const {value, action} = await ElMessageBox.prompt('请输入驳回理由', '驳回', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '理由',
    inputType: 'textarea'
  }).catch(() => ({action: 'cancel'}))
  if (action === 'cancel') return
  emit('reject', {id: info.value.id, reason: value || ''})
}

watch(() => [props.modelValue, props.bookingId], load)

// 用户友好状态文本（设施预约）
function statusText(s) {
  const map = {'0': '待审核', '1': '已批准', '2': '已驳回', '3': '已取消', '4': '进行中', '5': '已完成'}
  return map[String(s)] || String(s || '-')
}
</script>

<style scoped>
.mt-12 {
  margin-top: 12px;
}
</style>
