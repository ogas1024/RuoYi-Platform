<template>
  <div class="app-container">
    <el-card>
      <h3>新建投票</h3>
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title"/>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择日期时间"
                          format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
        <el-form-item label="可见范围">
          <el-radio-group v-model="form.visibleAll">
            <el-radio :label="1">全员</el-radio>
            <el-radio :label="0">自定义</el-radio>
          </el-radio-group>
          <div v-if="form.visibleAll===0" style="margin-top:8px;">
            <el-alert type="info" :closable="false" title="范围选择器后续补充；当前后端已支持 role/dept/post。"/>
          </div>
        </el-form-item>
        <el-divider>投票项（仅一题，单选或多选）</el-divider>
        <div style="margin-bottom:8px;">
          <el-button type="primary" @click="addItem(2)" :disabled="form.items.length>0">+ 单选题</el-button>
          <el-button type="primary" @click="addItem(3)" :disabled="form.items.length>0">+ 多选题</el-button>
          <span v-if="form.items.length>0" style="margin-left:8px; color:#909399;">已添加题目，投票仅允许一题</span>
        </div>
        <div v-for="(it,idx) in form.items" :key="idx"
             style="margin-top:10px; padding:10px; border:1px dashed var(--el-border-color); border-radius:6px;">
          <div style="display:flex; gap:10px; align-items:center;">
            <el-tag type="info">#{{ idx + 1 }}</el-tag>
            <el-input v-model="it.title" placeholder="投票项标题" style="flex:1"/>
            <el-select v-model="it.type" style="width:120px">
              <el-option :value="2" label="单选"/>
              <el-option :value="3" label="多选"/>
            </el-select>
            <el-checkbox v-model="it.required" :true-label="1" :false-label="0">必选</el-checkbox>
            <el-button type="danger" @click="removeItem(idx)">删除</el-button>
          </div>
          <div style="margin-top:8px;">
            <el-button size="small" @click="addOption(it)">添加选项</el-button>
            <div v-for="(op, j) in it.options" :key="j" style="display:flex; gap:8px; margin-top:6px;">
              <el-input v-model="op.label" placeholder="选项"/>
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
import {reactive, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {addVote, getVote, updateVote, publishVote} from '@/api/manage/vote'
import {ElMessage} from 'element-plus'

const route = useRoute()
const router = useRouter()
const form = reactive({id: null, title: '', deadline: '', visibleAll: 1, items: []})

function addItem(t) {
  if (form.items.length > 0) return;
  form.items.push({title: '', type: t, required: 1, options: []})
}

function removeItem(idx) {
  form.items.splice(idx, 1)
}

function addOption(it) {
  if (!it.options) it.options = [];
  it.options.push({label: ''})
}

function baseCheck() {
  if (!form.title) return ElMessage.error('请填写标题')
  if (form.items.length !== 1) return ElMessage.error('投票仅允许一题（单选或多选）')
  const it = form.items[0]
  if (!it.title || !it.title.trim()) return ElMessage.error('投票项标题不能为空')
  if (!(it.type === 2 || it.type === 3)) return ElMessage.error('投票题型必须为单选或多选')
  if (!it.options || it.options.length === 0) return ElMessage.error('至少添加一个选项')
  for (const op of it.options) {
    if (!op.label || !op.label.trim()) return ElMessage.error('选项文本不能为空')
  }
  return true
}

function save() {
  if (baseCheck() !== true) return;
  const payload = {
    id: form.id || undefined, title: form.title, deadline: form.deadline || null, visibleAll: form.visibleAll,
    items: form.items.map((x, idx) => ({
      title: x.title, type: x.type, required: x.required, sortNo: idx,
      options: (x.options || []).map((o, k) => ({label: o.label, sortNo: k}))
    }))
  }
  const api = form.id ? updateVote : addVote
  api(payload).then(() => {
    ElMessage.success('已保存（草稿）')
  })
}

function publish() {
  if (baseCheck() !== true) return;
  if (!form.id) {
    const payload = {
      title: form.title, deadline: form.deadline || null, visibleAll: form.visibleAll, status: 1,
      items: form.items.map((x, idx) => ({
        title: x.title, type: x.type, required: x.required, sortNo: idx,
        options: (x.options || []).map((o, k) => ({label: o.label, sortNo: k}))
      }))
    }
    addVote(payload).then(() => {
      ElMessage.success('已发布');
      router.push({name: 'ManageVoteIndex'})
    })
  } else {
    publishVote(form.id).then(() => {
      ElMessage.success('已发布');
      router.push({name: 'ManageVoteIndex'})
    })
  }
}

onMounted(() => {
  const id = route.query.id ? Number(route.query.id) : null
  if (id) {
    getVote(id).then(res => {
      const d = res.data
      form.id = d.id;
      form.title = d.title;
      form.deadline = d.deadline;
      form.visibleAll = d.visibleAll
      const first = (d.items || [])[0]
      if (first) {
        form.items = [{
          id: first.id, title: first.title, type: first.type, required: first.required,
          options: (first.options || []).map(o => ({id: o.id, label: o.label}))
        }]
      }
    })
  }
})
</script>
