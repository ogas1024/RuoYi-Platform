<template>
  <div class="portal-container" v-loading="loading">
    <h2>{{ detail?.title || '问卷' }}</h2>
    <p v-if="detail"><b>截止：</b>{{ detail.deadline || '-' }}
      <el-tag v-if="detail.pinned===1" type="danger" effect="dark" style="margin-left:8px;">置顶</el-tag>
      <el-tag v-if="detail.status===2" type="info" style="margin-left:8px;">已归档</el-tag>
      <el-tag v-else-if="expired" type="danger" style="margin-left:8px;">已过期</el-tag>
    </p>
    <el-form v-if="detail" label-width="100px">
      <div v-for="(it,idx) in detail.items" :key="it.id" style="margin:10px 0; padding:10px; border:1px dashed var(--el-border-color);
        border-radius:6px;">
        <div style="margin-bottom:6px;">
          <b>#{{ idx+1 }} {{ it.title }}</b>
          <small v-if="it.required===1" style="color:#f56c6c">(必填)</small>
          <small style="margin-left:8px; color:#909399">[{{ typeLabel(it.type) }}]</small>
        </div>
        <div>
          <el-input v-if="it.type===1" v-model="answers[it.id].valueText" type="textarea" :rows="2" placeholder="请输入" />
          <el-radio-group v-else-if="it.type===2" v-model="answers[it.id].optionIds">
            <el-radio v-for="op in it.options" :key="op.id" :label="op.id">{{ op.label }}</el-radio>
          </el-radio-group>
          <el-checkbox-group v-else-if="it.type===3" v-model="answers[it.id].optionIds">
            <el-checkbox v-for="op in it.options" :key="op.id" :label="op.id">{{ op.label }}</el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      <el-button type="primary" @click="submit" :disabled="expired || detail.status===2">提交</el-button>
      <span v-if="expired" style="margin-left:8px; color:#f56c6c;">已过截止时间，不能提交</span>
      <span v-else-if="detail.status===2" style="margin-left:8px; color:#909399;">问卷已归档，不能提交</span>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getSurvey, submitSurvey } from '@/api/portal/survey'
import { ElMessage } from 'element-plus'

const route = useRoute()
const id = Number(route.query.id)
const loading = ref(false)
const detail = ref(null)
const answers = reactive({})

const expired = computed(()=>{
  if (!detail.value || !detail.value.deadline) return false
  return new Date(detail.value.deadline).getTime() < Date.now()
})

function initAnswers(){
  if (!detail.value) return
  for (const it of detail.value.items){
    const preset = detail.value.myAnswers?.[it.id]
    if (it.type===1){
      answers[it.id] = { itemId: it.id, valueText: typeof preset==='string'? preset: '' }
    } else if (it.type===2){
      const v = Array.isArray(preset) && preset.length>0 ? preset[0] : null
      answers[it.id] = { itemId: it.id, optionIds: v? v: null }
    } else {
      answers[it.id] = { itemId: it.id, optionIds: Array.isArray(preset)? preset: [] }
    }
  }
}

function load(){
  loading.value = true
  getSurvey(id).then(res=>{
    detail.value = res.data
    // 规范 myAnswers 的 key 为数字
    const fixed = {}
    if (detail.value.myAnswers){
      for (const k in detail.value.myAnswers){ fixed[Number(k)] = detail.value.myAnswers[k] }
      detail.value.myAnswers = fixed
    }
    initAnswers()
  }).finally(()=>loading.value=false)
}

function submit(){
  const payload = Object.values(answers).map(x=>{
    if (Array.isArray(x.optionIds)) return { itemId:x.itemId, optionIds:x.optionIds }
    if (typeof x.optionIds === 'number') return { itemId:x.itemId, optionIds:[x.optionIds] }
    return { itemId:x.itemId, valueText:x.valueText||'' }
  })
  submitSurvey(id, payload).then(()=>{
    ElMessage.success('已提交')
    load()
  })
}

function typeLabel(t){ return t===1?'文本':(t===2?'单选':'多选') }

onMounted(load)
</script>
