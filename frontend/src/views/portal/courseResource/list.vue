<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button link type="primary" icon="Back" @click="$router.back()">返回</el-button>
      <span class="hint">{{ majorName }} / {{ courseName }}</span>
      <el-button type="primary" icon="Plus" style="margin-left: 12px" @click="openDialog">分享资源</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="关键字">
        <el-input v-model="queryParams.keyword" placeholder="资源名/简介/上传者" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="资源名称" prop="resourceName" min-width="200" :show-overflow-tooltip="true"/>
      <el-table-column label="上传者" prop="uploaderName" width="120"/>
      <el-table-column label="下载次数" prop="downloadCount" width="100"/>
      <el-table-column label="创建时间" prop="createTime" width="180"/>
      <el-table-column label="更新时间" prop="updateTime" width="180"/>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Download" @click="handleDownload(scope.row)">下载</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <el-dialog v-model="open" title="分享资源" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="form.resourceName" maxlength="255" show-word-limit placeholder="请填写资源名称"/>
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-radio-group v-model="form.resourceType">
            <el-radio :label="0">OSS 文件上传</el-radio>
            <el-radio :label="1">外链 URL</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.resourceType===0" label="上传压缩包" prop="fileUrl">
          <el-upload :http-request="doUpload" :limit="1" :auto-upload="true" :show-file-list="true" drag>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">仅 zip/rar/7z/tar/tar.gz/tar.bz2/tar.xz，大小 ≤ 100MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item v-else label="外链 URL" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="https://..."/>
        </el-form-item>
        <el-form-item label="资源简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="请填写简介"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ResourceList">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { useRoute } from 'vue-router'
import { listResourcePortal } from '@/api/portal/resource'
import { addResource } from '@/api/manage/courseResource'
import { uploadOss } from '@/api/manage/upload'

const route = useRoute()
const majorId = Number(route.query.majorId)
const courseId = Number(route.query.courseId)
const majorName = route.query.majorName || ''
const courseName = route.query.courseName || ''

const { proxy } = getCurrentInstance()

const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, majorId, courseId, status: 1, keyword: '' })

const open = ref(false)
const formRef = ref()
const form = reactive({ majorId, courseId, resourceName: '', resourceType: 0, fileUrl: '', fileHash: '', fileSize: 0, linkUrl: '', description: '' })
const rules = {
  resourceName: [{ required: true, message: '请填写资源名称', trigger: 'blur' }],
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
  fileUrl: [{ required: () => form.resourceType===0, message: '请上传压缩包', trigger: 'change' }],
  linkUrl: [{ required: () => form.resourceType===1, message: '请填写外链URL', trigger: 'blur' }],
  description: [{ required: true, message: '请填写资源简介', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const resp = await listResourcePortal(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.keyword = ''; handleQuery() }
const openDialog = () => { open.value = true }

const doUpload = async (options) => {
  const file = options.file
  const sizeLimit = 100 * 1024 * 1024
  const allowed = ['zip','rar','7z','tar','gz','bz2','xz']
  const ext = file.name.split('.').pop().toLowerCase()
  if (!allowed.includes(ext)) {
    proxy.$modal.msgError('仅允许上传压缩包文件')
    return options.onError(new Error('invalid ext'))
  }
  if (file.size > sizeLimit) {
    proxy.$modal.msgError('文件超过 100MB，请改为外链方式')
    return options.onError(new Error('too large'))
  }
  const fd = new FormData()
  fd.append('file', file)
  try {
    const { data } = await uploadOss(fd, { dir: `resource/${majorId}/${courseId}`, publicUrl: true })
    form.fileUrl = data.url || data.publicUrl || ''
    form.fileHash = data.sha256 || data.etag || ''
    form.fileSize = file.size
    options.onSuccess(data)
  } catch (e) {
    options.onError(e)
  }
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const payload = { ...form }
    if (payload.resourceType === 0) {
      if (!payload.fileHash) return proxy.$modal.msgError('缺少文件哈希，请重新上传')
      delete payload.linkUrl
    } else {
      delete payload.fileUrl; delete payload.fileHash; delete payload.fileSize
    }
    await addResource(payload)
    proxy.$modal.msgSuccess('提交成功，等待审核')
    open.value = false
    getList()
  })
}

const handleDownload = (row) => {
  window.open(`${import.meta.env.VITE_APP_BASE_API}/portal/resource/${row.id}/download`, '_blank')
}

onMounted(getList)
</script>

<style scoped>
.toolbar { margin-bottom: 10px; }
.hint { margin-left: 8px; color: #909399; }
</style>
