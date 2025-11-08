<template>
  <div class="app-container" v-loading="loading">
    <div class="toolbar">
      <el-button link type="primary" icon="Back" @click="$router.back()">返回</el-button>
      <span class="hint">编辑资源（保存后进入待审）</span>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="edit-form">
      <el-form-item label="资源名称" prop="resourceName">
        <el-input v-model="form.resourceName" maxlength="255" show-word-limit placeholder="请填写资源名称"/>
      </el-form-item>
      <el-form-item label="资源类型" prop="resourceType">
        <el-radio-group v-model="form.resourceType">
          <el-radio :label="0">OSS 文件上传</el-radio>
          <el-radio :label="1">外链 URL</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="form.resourceType===0" label="当前文件">
        <template #default>
          <div v-if="form.fileUrl" class="file-card">
            <el-link :href="form.fileUrl" target="_blank" type="primary">查看当前文件</el-link>
            <span v-if="form.fileSize" class="size">{{ (form.fileSize / 1024 / 1024).toFixed(2) }} MB</span>
            <el-popconfirm title="移除当前文件？移除后需重新上传或切换外链" @confirm="removeFile">
              <template #reference>
                <el-button text type="danger" size="small">移除</el-button>
              </template>
            </el-popconfirm>
          </div>
          <div v-else class="mt8">
            <el-upload :http-request="doUpload" :limit="1" :auto-upload="true" :show-file-list="true" drag>
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">拖拽或<em>点击上传</em>（zip/rar/7z/tar/tar.gz/tar.bz2/tar.xz，≤100MB）</div>
            </el-upload>
          </div>
        </template>
      </el-form-item>

      <el-form-item v-else label="外链 URL" prop="linkUrl">
        <el-input v-model="form.linkUrl" placeholder="https://..."/>
        <el-button v-if="form.linkUrl" class="ml8" text type="danger" size="small" @click="removeLink">清空</el-button>
      </el-form-item>

      <el-form-item label="资源简介" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit
                  placeholder="请填写简介"/>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="save">保存修改</el-button>
        <el-button @click="reload">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import {ref, onMounted, getCurrentInstance} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {getResourcePortal, updateResourcePortal} from '@/api/portal/resource'
import {uploadOssPortal} from '@/api/portal/upload'

const route = useRoute()
const router = useRouter()
const {proxy} = getCurrentInstance()

const loading = ref(false)
const formRef = ref()
const form = ref({
  id: undefined,
  resourceName: '',
  resourceType: 0,
  fileUrl: '',
  fileHash: '',
  fileSize: 0,
  linkUrl: '',
  description: ''
})
const rules = {
  resourceName: [{required: true, message: '请填写资源名称', trigger: 'blur'}],
  resourceType: [{required: true, message: '请选择资源类型', trigger: 'change'}],
  linkUrl: [{required: () => form.value.resourceType === 1, message: '请填写外链 URL', trigger: 'blur'}]
}

const load = async () => {
  loading.value = true
  try {
    const id = route.query.id
    const {data} = await getResourcePortal(id)
    form.value = {
      id: data.id,
      resourceName: data.resourceName,
      resourceType: data.resourceType,
      fileUrl: data.fileUrl,
      fileHash: data.fileHash,
      fileSize: data.fileSize,
      linkUrl: data.linkUrl,
      description: data.description
    }
  } finally {
    loading.value = false
  }
}

const reload = () => load()

const save = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    // 校验：文件型必须有文件或刚完成上传
    if (form.value.resourceType === 0 && !form.value.fileUrl) {
      return proxy.$modal.msgError('请先上传文件')
    }
    // 外链型清理文件字段；文件型清理外链字段
    const payload = {...form.value}
    if (payload.resourceType === 0) {
      payload.linkUrl = null
    } else {
      payload.fileUrl = null;
      payload.fileHash = null;
      payload.fileSize = null
    }
    await updateResourcePortal(payload)
    proxy.$modal.msgSuccess('已保存修改并提交审核')
    router.push({path: '/portal/course-resource/my'})
  })
}

const removeFile = () => {
  form.value.fileUrl = '';
  form.value.fileHash = '';
  form.value.fileSize = 0
}
const removeLink = () => {
  form.value.linkUrl = ''
}

const doUpload = async (options) => {
  const file = options.file
  const sizeLimit = 100 * 1024 * 1024
  const allowed = ['zip', 'rar', '7z', 'tar', 'gz', 'bz2', 'xz']
  const ext = file.name.split('.').pop().toLowerCase()
  if (!allowed.includes(ext)) {
    proxy.$modal.msgError('仅允许上传压缩包文件');
    return options.onError(new Error('invalid ext'))
  }
  if (file.size > sizeLimit) {
    proxy.$modal.msgError('文件超过 100MB，请改为外链方式');
    return options.onError(new Error('too large'))
  }
  const fd = new FormData()
  fd.append('file', file)
  try {
    const {data} = await uploadOssPortal(fd, {
      scene: 'resource.archive',
      dir: `resource/edit/${form.value.id || 'temp'}`,
      publicUrl: true
    })
    form.value.fileUrl = data.url || data.publicUrl || ''
    form.value.fileHash = data.sha256 || data.etag || ''
    form.value.fileSize = file.size
    options.onSuccess(data)
  } catch (e) {
    options.onError(e)
  }
}

onMounted(load)
</script>

<style scoped>
.hint {
  margin-left: 8px;
  color: #909399;
}

.edit-form {
  max-width: 720px;
}

.file-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
}

.size {
  color: #909399;
  font-size: 12px;
}

.ml8 {
  margin-left: 8px;
}

.mt8 {
  margin-top: 8px;
}
</style>
