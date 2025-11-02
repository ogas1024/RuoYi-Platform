<template>
  <div class="app-container">
    <el-page-header @back="$router.back()" content="公告编辑" />

    <el-form :model="form" label-width="100px" class="mt16" ref="formRef">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" maxlength="200" show-word-limit placeholder="请输入标题" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type" style="width: 160px">
          <el-option :value="1" label="通知" />
          <el-option :value="2" label="公告" />
        </el-select>
      </el-form-item>
      <el-form-item label="富文本" required>
        <editor v-model="form.contentHtml" :min-height="220" />
      </el-form-item>
      <el-form-item label="有效期">
        <el-date-picker v-model="form.expireTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="不选则永久有效" />
      </el-form-item>

      <el-divider>可见范围</el-divider>
      <el-form-item label="可见性">
        <el-radio-group v-model="form.visibleAll">
          <el-radio :value="1">全员</el-radio>
          <el-radio :value="0">自定义</el-radio>
        </el-radio-group>
      </el-form-item>
      <template v-if="form.visibleAll===0">
        <el-form-item label="角色">
          <el-select v-model="scopeRoleIds" multiple filterable placeholder="选择角色" style="width: 480px">
            <el-option v-for="r in roleOptions" :key="r.roleId" :label="r.roleName" :value="r.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select v-model="scopeDeptIds" :data="deptTree" multiple check-strictly show-checkbox :props="{ value:'id', label:'label', children:'children' }" style="width: 480px" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="scopePostIds" multiple filterable placeholder="选择岗位" style="width: 480px">
            <el-option v-for="p in postOptions" :key="p.postId" :label="p.postName" :value="p.postId" />
          </el-select>
        </el-form-item>
      </template>

      <el-divider>附件</el-divider>
      <el-form-item label="上传">
        <input ref="fileRef" type="file" @change="handleFileChange" style="display:none" />
        <el-button type="primary" @click="() => fileRef.click()">从本地上传</el-button>
        <el-button @click="addUrlAttachment">添加链接</el-button>
        <span class="tip">白名单：pdf/docx/xlsx/png/jpg/zip；单文件≤20MB</span>
      </el-form-item>
      <el-form-item label="附件列表" v-if="attachments.length">
        <el-table :data="attachments" size="small">
          <el-table-column type="index" width="50" />
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="fileType" label="类型" width="100" />
          <el-table-column prop="fileSize" label="大小(B)" width="120" />
          <el-table-column prop="fileUrl" label="URL" min-width="260" />
          <el-table-column label="操作" width="120">
            <template #default="{ $index }">
              <el-button link type="danger" @click="attachments.splice($index,1)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="onSubmit">保存</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Editor from '@/components/Editor/index.vue'
import { addNotice, updateNotice, getNotice } from '@/api/manage/notice'
import { listRole } from '@/api/system/role'
import { deptTreeSelect as getDeptTree } from '@/api/system/user'
import { listPost } from '@/api/system/post'
import { uploadOss } from '@/api/manage/upload'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const formRef = ref()
const fileRef = ref()
const form = reactive({ id: undefined, title: '', type: 2, contentHtml: '', visibleAll: 1, expireTime: undefined })
const attachments = reactive([])
const roleOptions = ref([])
const deptTree = ref([])
const postOptions = ref([])
const scopeRoleIds = ref([])
const scopeDeptIds = ref([])
const scopePostIds = ref([])

function loadMeta() {
  listRole({ pageNum:1, pageSize:100 }).then(r => { roleOptions.value = r.rows || r.data || [] })
  getDeptTree().then(r => { deptTree.value = r.data || [] })
  listPost({ pageNum:1, pageSize:100 }).then(r => { postOptions.value = r.rows || r.data || [] })
}

function buildScopes() {
  const scopes = []
  scopeRoleIds.value.forEach(id => scopes.push({ scopeType: 0, refId: id }))
  scopeDeptIds.value.forEach(id => scopes.push({ scopeType: 1, refId: id }))
  scopePostIds.value.forEach(id => scopes.push({ scopeType: 2, refId: id }))
  return scopes
}

function addUrlAttachment() {
  ElMessageBox.prompt('请输入文件URL', '添加附件链接', { inputPattern: /https?:\/\//i, inputErrorMessage: 'URL 必须以 http(s) 开头' })
    .then(({ value }) => {
      attachments.push({ fileName: value.split('/').pop(), fileUrl: value, fileType: (value.split('.').pop()||'').toLowerCase(), fileSize: null, sort: attachments.length+1 })
    }).catch(()=>{})
}

function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const ext = (file.name.split('.').pop()||'').toLowerCase()
  const allow = ['pdf','doc','docx','xlsx','xls','png','jpg','jpeg','zip']
  if (!allow.includes(ext)) { ElMessage.error('不支持的文件类型'); return }
  if (file.size > 20*1024*1024) { ElMessage.error('文件超过 20MB'); return }
  const fd = new FormData(); fd.append('file', file)
  uploadOss(fd, { dir:'notice', publicUrl: true }).then(res => {
    const data = res.data || res
    attachments.push({ fileName: file.name, fileUrl: data.url || data.data?.url || '', fileType: ext, fileSize: file.size, sort: attachments.length+1 })
    fileRef.value.value = ''
  })
}

function onSubmit() {
  if (!form.title || !form.contentHtml) { ElMessage.error('标题与内容必填'); return }
  const payload = { ...form, attachments: [...attachments] }
  if (form.visibleAll === 0) payload.scopes = buildScopes()
  const api = form.id ? updateNotice : addNotice
  api(payload).then(r => {
    if (!form.id) form.id = (r.data && r.data.id) || form.id
    ElMessage.success('保存成功')
  })
}

function loadDetail(id) {
  getNotice(id).then(res => {
    const d = res.data || {}
    Object.assign(form, d.notice || {})
    attachments.splice(0, attachments.length, ...(d.attachments || []))
    const scopes = d.scopes || []
    scopeRoleIds.value = scopes.filter(i=>i.scopeType===0).map(i=>i.refId)
    scopeDeptIds.value = scopes.filter(i=>i.scopeType===1).map(i=>i.refId)
    scopePostIds.value = scopes.filter(i=>i.scopeType===2).map(i=>i.refId)
  })
}

onMounted(() => {
  loadMeta()
  const id = route.query.id
  if (id) loadDetail(id)
})
</script>

<style scoped>
.mt16{ margin-top:16px }
.tip{ margin-left:10px;color:#909399 }
</style>

