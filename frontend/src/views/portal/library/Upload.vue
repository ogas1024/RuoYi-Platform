<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button link type="primary" icon="Back" @click="$router.back()">返回</el-button>
      <span class="hint">上传图书</span>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="upload-form">
      <el-form-item label="ISBN-13" prop="isbn13">
        <el-input v-model="form.isbn13" maxlength="13" show-word-limit placeholder="仅数字，13位"/>
      </el-form-item>
      <el-form-item label="书名" prop="title">
        <el-input v-model="form.title" maxlength="256" show-word-limit placeholder="请输入书名"/>
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input v-model="form.author" maxlength="256" show-word-limit placeholder="请输入作者"/>
      </el-form-item>
      <el-form-item label="出版社">
        <el-input v-model="form.publisher" placeholder="可选"/>
      </el-form-item>
      <el-form-item label="出版年份">
        <el-input-number v-model="form.publish_year" :min="0" :max="2100"/>
      </el-form-item>
      <el-form-item label="语种">
        <el-input v-model="form.language" placeholder="zh/en..."/>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="form.keywords" placeholder="逗号分隔"/>
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.summary" type="textarea" :rows="4" maxlength="1000" show-word-limit
                  placeholder="请输入简介"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交审核</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-divider>PDF（必传）</el-divider>
    <div class="asset-area">
      <el-form-item label="书籍 PDF" required>
        <div v-if="pdfAsset" class="pdf-card">
          <div class="pdf-info">
            <span class="fmt">PDF</span>
            <el-link :href="pdfAsset.fileUrl" target="_blank" type="primary">查看链接</el-link>
            <span class="size">{{ (pdfAsset.fileSize / 1024 / 1024).toFixed(2) }} MB</span>
          </div>
          <el-button type="danger" text size="small" @click="removePdf">移除</el-button>
        </div>
        <div v-else>
          <el-upload :http-request="doUploadPdf" :limit="1" :auto-upload="true" :show-file-list="true" drag>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将 PDF 拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">仅 PDF，≤100MB；未上传将无法提交</div>
            </template>
          </el-upload>
        </div>
      </el-form-item>
    </div>

    <el-divider>补充格式 / 外链（可选）</el-divider>
    <div class="asset-area">
      <div class="mt8">
        <el-upload :http-request="doUploadExtra" :limit="5" :auto-upload="true" :show-file-list="true" multiple drag>
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">拖拽或<em>点击上传</em>（epub/mobi/zip，≤100MB）</div>
        </el-upload>
      </div>
      <div class="mt8">
        <el-input v-model="extra.link_url" placeholder="https://...（可选外链）" style="max-width: 420px"/>
        <el-button class="ml8" type="primary" @click="pushLinkExtra">追加外链</el-button>
      </div>
      <el-table :data="extraAssets" class="mt12" size="small" border>
        <el-table-column prop="assetType" label="类型" width="80">
          <template #default="{ row }">{{ row.assetType === '0' ? '文件' : '外链' }}</template>
        </el-table-column>
        <el-table-column prop="format" label="格式" width="100"/>
        <el-table-column prop="fileUrl" label="文件URL"/>
        <el-table-column prop="linkUrl" label="外链URL"/>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="removeExtra(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {ref, getCurrentInstance} from 'vue'
import {normalizeExternalUrl} from '@/utils/url'
import {addLibraryPortalFull} from '@/api/portal/library'
import {uploadOssPortal} from '@/api/portal/upload'

const formRef = ref()
const form = ref({
  isbn13: '',
  title: '',
  author: '',
  publisher: '',
  publish_year: null,
  language: '',
  keywords: '',
  summary: ''
})
const rules = {
  isbn13: [{required: true, message: '请输入 ISBN-13', trigger: 'blur'}, {
    min: 13,
    max: 13,
    message: '长度 13 位',
    trigger: 'blur'
  }],
  title: [{required: true, message: '请输入书名', trigger: 'blur'}],
  author: [{required: true, message: '请输入作者', trigger: 'blur'}]
}
// 必传 PDF
const pdfAsset = ref(null)
// 可选补充资产
const extraAssets = ref([])
const extra = ref({link_url: ''})

const submit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    if (!pdfAsset.value) {
      return proxy.$modal.msgError('请先上传书籍 PDF（必传）')
    }
    const combined = [pdfAsset.value, ...extraAssets.value]
    const payload = {
      ...form.value, assets: combined.map((a, idx) => ({
        assetType: a.assetType,
        format: a.format,
        fileUrl: a.fileUrl,
        fileSize: a.fileSize,
        fileHash: a.fileHash,
        linkUrl: a.linkUrl,
        sort: idx
      }))
    }
    await addLibraryPortalFull(payload)
    proxy.$modal.msgSuccess('提交成功，等待审核')
    reset()
  })
}
const reset = () => {
  form.value = {
    isbn13: '',
    title: '',
    author: '',
    publisher: '',
    publish_year: null,
    language: '',
    keywords: '',
    summary: ''
  }
  pdfAsset.value = null
  extraAssets.value = []
  extra.value = {link_url: ''}
}

const {proxy} = getCurrentInstance()
// 上传必传 PDF
const doUploadPdf = async (options) => {
  const file = options.file
  const sizeLimit = 100 * 1024 * 1024
  const ext = file.name.split('.').pop().toLowerCase()
  if (ext !== 'pdf') {
    proxy.$modal.msgError('仅允许上传 PDF 作为主文件')
    return options.onError(new Error('invalid ext'))
  }
  if (file.size > sizeLimit) {
    proxy.$modal.msgError('文件超过 100MB，请改为外链方式')
    return options.onError(new Error('too large'))
  }
  const fd = new FormData()
  fd.append('file', file)
  try {
    const {data} = await uploadOssPortal(fd, {scene: 'library.pdf', dir: 'library/pdf', publicUrl: true})
    const url = data.url || data.publicUrl || data
    pdfAsset.value = {
      id: Date.now(),
      assetType: '0',
      format: 'pdf',
      fileUrl: url,
      fileSize: file.size,
      fileHash: data.sha256 || data.etag || ''
    }
    options.onSuccess(data)
  } catch (e) {
    options.onError(e)
  }
}
const removePdf = () => {
  pdfAsset.value = null
}

// 上传补充文件（非 PDF）
const doUploadExtra = async (options) => {
  const file = options.file
  const sizeLimit = 100 * 1024 * 1024
  const allowed = ['epub', 'mobi', 'zip']
  const ext = file.name.split('.').pop().toLowerCase()
  if (!allowed.includes(ext)) {
    proxy.$modal.msgError('仅允许上传 epub/mobi/zip')
    return options.onError(new Error('invalid ext'))
  }
  if (file.size > sizeLimit) {
    proxy.$modal.msgError('文件超过 100MB，请改为外链方式')
    return options.onError(new Error('too large'))
  }
  const fd = new FormData()
  fd.append('file', file)
  try {
    const {data} = await uploadOssPortal(fd, {scene: 'library.extra', dir: 'library/extra', publicUrl: true})
    const url = data.url || data.publicUrl || data
    extraAssets.value.push({
      id: Date.now(),
      assetType: '0',
      format: ext,
      fileUrl: url,
      fileSize: file.size,
      fileHash: data.sha256 || data.etag || ''
    })
    options.onSuccess(data)
  } catch (e) {
    options.onError(e)
  }
}
const pushLinkExtra = () => {
  if (!extra.value.link_url) return proxy.$modal.msgError('请填写外链URL')
  const fixed = normalizeExternalUrl(extra.value.link_url)
  extraAssets.value.push({
    id: Date.now(),
    assetType: '1',
    format: '',
    fileUrl: '',
    fileSize: 0,
    fileHash: '',
    linkUrl: fixed
  })
  extra.value.link_url = ''
}
const removeExtra = (row) => {
  extraAssets.value = extraAssets.value.filter(a => a.id !== row.id)
}
</script>

<style scoped>
.upload-form {
  max-width: 720px;
}

.mt8 {
  margin-top: 8px;
}

.mt12 {
  margin-top: 12px;
}

.ml8 {
  margin-left: 8px;
}

.hint {
  margin-left: 8px;
  color: #909399;
}

.tip {
  color: #909399;
  margin-top: 8px;
}

.pdf-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
  max-width: 640px;
}

.pdf-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.fmt {
  display: inline-block;
  padding: 2px 6px;
  background: #409EFF;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.size {
  color: #909399;
  font-size: 12px;
}
</style>
