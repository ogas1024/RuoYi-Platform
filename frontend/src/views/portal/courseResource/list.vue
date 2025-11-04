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
    <div class="card-grid" v-loading="loading">
      <div v-for="item in list" :key="item.id" class="res-card" @click="openDetail(item)">
        <div v-if="item.isBest===1" class="badge-best">最佳</div>
        <div class="title">{{ item.resourceName }}</div>
        <div class="desc" :title="item.description">{{ item.description || '（无简介）' }}</div>
        <div class="meta">
          <span class="uploader">{{ item.uploaderName }}</span>
          <span class="dot">·</span>
          <span class="time">{{ item.createTime }}</span>
        </div>
        <div class="footer">
          <span class="label">下载数</span>
          <span class="count">{{ item.downloadCount || 0 }}</span>
          <el-button size="small" type="primary" text @click.stop="handleDownload(item)">下载</el-button>
        </div>
      </div>
    </div>
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

    <el-dialog v-model="detailVisible" title="资源详情" width="640px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资源名称">{{ detail.resourceName }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ detail.resourceType===1?'外链':'文件' }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ majorName }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ courseName }}</el-descriptions-item>
        <el-descriptions-item label="最佳">{{ detail.isBest===1?'是':'否' }}</el-descriptions-item>
        <el-descriptions-item label="上传者">{{ detail.uploaderName }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="上架时间">{{ detail.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="下载次数">{{ detail.downloadCount || 0 }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-desc">
        <div class="detail-label">资源简介</div>
        <div class="detail-content">{{ detail.description || '（无）' }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible=false">关闭</el-button>
        <el-button type="primary" @click="handleDownload(detail)">下载</el-button>
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
const detailVisible = ref(false)
const detail = ref({})
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

const openDetail = (row) => {
  detail.value = row
  detailVisible.value = true
}

onMounted(getList)
</script>

<style scoped>
.toolbar { margin-bottom: 10px; }
.hint { margin-left: 8px; color: #909399; }
.card-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 12px; }
.res-card { border: 1px solid #ebeef5; border-radius: 8px; padding: 12px; cursor: pointer; transition: box-shadow .2s ease; background: #fff; }
.res-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.badge-best { position: absolute; right: 12px; top: 12px; background: #f56c6c; color: #fff; font-size: 12px; padding: 2px 6px; border-radius: 3px; }
.res-card { position: relative; }
.title { font-size: 16px; font-weight: 600; color: #303133; margin-bottom: 6px; }
.desc { color: #606266; font-size: 13px; line-height: 1.5; max-height: 3.9em; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; }
.meta { margin-top: 8px; color: #909399; font-size: 12px; }
.meta .dot { margin: 0 6px; }
.footer { margin-top: 10px; display: flex; align-items: center; justify-content: space-between; color: #409EFF; }
.count { margin-left: 4px; color: #606266; }
.label { color: #909399; }
.detail-desc { margin-top: 14px; }
.detail-label { font-weight: 600; margin-bottom: 6px; }
.detail-content { white-space: pre-wrap; line-height: 1.6; }
</style>
