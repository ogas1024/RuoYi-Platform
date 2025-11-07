<template>
  <div class="app-container">
    <el-card>
      <h3>新建问卷</h3>
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择日期时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="可见范围">
          <el-radio-group v-model="form.visibleAll">
            <el-radio :label="1">全员</el-radio>
            <el-radio :label="0">自定义</el-radio>
          </el-radio-group>
          <div v-if="form.visibleAll===0" style="margin-top:8px;">
            <el-alert type="info" :closable="false" title="本期简化：范围选择请在后续迭代补充；后端已支持 role/dept/post 三类范围。" />
          </div>
        </el-form-item>
        <el-divider>题目</el-divider>
        <div style="margin-bottom:8px;">
          <el-button type="primary" @click="addItem(1)">+ 文本题</el-button>
          <el-button type="primary" @click="addItem(2)">+ 单选题</el-button>
          <el-button type="primary" @click="addItem(3)">+ 多选题</el-button>
        </div>
        <div v-for="(it,idx) in form.items" :key="idx" style="margin-top:10px; padding:10px; border:1px dashed var(--el-border-color); border-radius:6px;">
          <div style="display:flex; gap:10px; align-items:center;">
            <el-tag type="info">#{{ idx+1 }}</el-tag>
            <el-input v-model="it.title" placeholder="题目/提示" style="flex:1" />
            <el-select v-model="it.type" style="width:120px">
              <el-option :value="1" label="文本"/>
              <el-option :value="2" label="单选"/>
              <el-option :value="3" label="多选"/>
            </el-select>
            <el-checkbox v-model="it.required" :true-label="1" :false-label="0">必填</el-checkbox>
            <el-button type="danger" @click="removeItem(idx)">删除</el-button>
          </div>
          <div v-if="it.type===2 || it.type===3" style="margin-top:8px;">
            <el-button size="small" @click="addOption(it)">添加选项</el-button>
            <div v-for="(op, j) in it.options" :key="j" style="display:flex; gap:8px; margin-top:6px;">
              <el-input v-model="op.label" placeholder="选项" />
              <el-button size="small" type="danger" @click="it.options.splice(j,1)">删</el-button>
            </div>
          </div>
        </div>
        <div style="margin-top:16px; display:flex; gap:8px;">
          <el-button type="primary" @click="save">保存草稿</el-button>
          <el-button type="success" @click="publish">发布</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { addSurvey, getSurvey, updateSurvey, publishSurvey } from '@/api/manage/survey'
import { ElMessage } from 'element-plus'

const route = useRoute()
const form = reactive({ id: null, title: '', deadline: '', visibleAll: 1, items: [] })

function addItem(t){ form.items.push({ title:'', type:t, required:0, options:[] }) }
function removeItem(idx){ form.items.splice(idx,1) }
function addOption(it){ if(!it.options) it.options=[]; it.options.push({ label:'' }) }

function save(){
  if(!form.title) return ElMessage.error('请填写标题')
  if(form.items.length===0) return ElMessage.error('至少添加一道题')
  for (const it of form.items) {
    if (!it.title || !it.title.trim()) return ElMessage.error('题目提示不能为空')
  }
  // 边界校验：选择题必须至少一个选项，且选项文本不能为空
  for (const it of form.items) {
    if (it.type===2 || it.type===3) {
      if (!it.options || it.options.length===0) return ElMessage.error('选择题至少需要一个选项')
      for (const op of it.options) {
        if (!op.label || !op.label.trim()) return ElMessage.error('选项文本不能为空')
      }
    }
  }
  const payload = {
    id: form.id || undefined,
    title: form.title,
    deadline: form.deadline || null,
    visibleAll: form.visibleAll,
    items: form.items.map((x, idx) => ({
      title: x.title, type: x.type, required: x.required, sortNo: idx,
      options: (x.type===2||x.type===3) ? (x.options||[]).map((o,k)=>({label:o.label, sortNo:k})) : []
    }))
  }
  const api = form.id ? updateSurvey : addSurvey
  api(payload).then(()=>{ ElMessage.success('已保存（草稿）') })
}

function publish(){
  // 新建：直接按已发布创建；编辑：调用发布接口
  if (!form.id) {
    // 先校验同 save，然后传 status=1
    const payload = {
      title: form.title,
      deadline: form.deadline || null,
      visibleAll: form.visibleAll,
      status: 1,
      items: form.items.map((x, idx) => ({
        title: x.title, type: x.type, required: x.required, sortNo: idx,
        options: (x.type===2||x.type===3) ? (x.options||[]).map((o,k)=>({label:o.label, sortNo:k})) : []
      }))
    }
    addSurvey(payload).then(()=>{ ElMessage.success('已发布') })
  } else {
    publishSurvey(form.id).then(()=>{ ElMessage.success('已发布') })
  }
}

onMounted(()=>{
  const id = route.query.id ? Number(route.query.id) : null
  if (id) {
    getSurvey(id).then(res=>{
      const d = res.data
      form.id = d.id
      form.title = d.title
      form.deadline = d.deadline
      form.visibleAll = d.visibleAll
      form.items = (d.items||[]).map((i)=>({
        id: i.id,
        title: i.title,
        type: i.type,
        required: i.required,
        options: (i.options||[]).map(o=>({id:o.id,label:o.label}))
      }))
    })
  }
})
</script>
