<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">我上传的资源</span>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="待审" :value="0"/>
          <el-option label="已通过" :value="1"/>
          <el-option label="驳回" :value="2"/>
          <el-option label="已下架" :value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list" @selection-change="onSel">
      <el-table-column type="selection" width="50"/>
      <el-table-column label="资源名称" min-width="240">
        <template #default="scope">
          <el-tag v-if="scope.row.isBest===1" type="warning" size="small" effect="dark" style="margin-right:6px">最佳
          </el-tag>
          <span>{{ scope.row.resourceName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程/专业" min-width="200">
        <template #default="scope">{{ scope.row.majorName || '-' }} / {{
            scope.row.courseName || scope.row.courseId
          }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status===0" type="warning">待审</el-tag>
          <el-tag v-else-if="scope.row.status===1" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.status===2" type="danger">驳回</el-tag>
          <el-tag v-else type="info">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下载数" prop="downloadCount" width="100"/>
      <el-table-column label="原因" min-width="200">
        <template #default="scope">
          <span v-if="scope.row.status===2 || scope.row.status===3">{{ scope.row.auditReason || '无' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="240">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="editRow(scope.row)">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)">删除</el-button>
          <el-button link type="warning" icon="Bottom" v-if="scope.row.status===1" @click="openOffline(scope.row)">
            下架
          </el-button>
          <el-button link type="success" icon="Top" v-if="scope.row.status!==1" @click="online(scope.row)">重新上架
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" title="编辑资源" width="520px" append-to-body>
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
          <div v-if="form.fileUrl" class="mt8">
            当前文件：
            <el-link :href="form.fileUrl" target="_blank" type="primary">查看</el-link>
            <span v-if="form.fileSize" style="margin-left:8px;color:#909399">{{
                (form.fileSize / 1024 / 1024).toFixed(2)
              }} MB</span>
            <el-button text type="danger" size="small" style="margin-left:8px" @click="clearFile">移除</el-button>
          </div>
        </el-form-item>
        <el-form-item v-else label="外链 URL" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="https://..."/>
          <el-button v-if="form.linkUrl" text type="danger" size="small" style="margin-left:8px"
                     @click="form.linkUrl=''">清空
          </el-button>
        </el-form-item>
        <el-form-item label="资源简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit
                    placeholder="请填写简介"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交审核</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="offlineOpen" title="下架原因" width="460px" append-to-body>
      <el-form ref="offlineFormRef" :model="offlineForm" :rules="offlineRules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input v-model="offlineForm.reason" type="textarea" :rows="3" maxlength="200" show-word-limit
                    placeholder="请输入下架原因（必填）"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelOffline">取消</el-button>
        <el-button type="primary" @click="submitOffline">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MyCourseResource">
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import useUserStore from '@/store/modules/user'
import {
  myListResourcePortal,
  updateResourcePortal,
  removeResourcePortal,
  offlineResourcePortal,
  onlineResourcePortal
} from '@/api/portal/resource'
import {uploadOssPortal} from '@/api/portal/upload'

const {proxy} = getCurrentInstance()
const userStore = useUserStore()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const selIds = ref([])
const queryParams = reactive({pageNum: 1, pageSize: 10, uploaderId: userStore.id, status: undefined})

const open = ref(false)
const formRef = ref()
const form = reactive({
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
  linkUrl: [{required: () => form.resourceType === 1, message: '请填写外链 URL', trigger: 'blur'}],
  description: [{required: true, message: '请填写简介', trigger: 'blur'}]
}

const getList = async () => {
  loading.value = true
  try {
    const resp = await myListResourcePortal(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally {
    loading.value = false
  }
}
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList()
}
const resetQuery = () => {
  queryParams.status = undefined;
  handleQuery()
}
const onSel = (rows) => {
  selIds.value = rows.map(r => r.id)
}

const editRow = (row) => {
  Object.assign(form, {
    id: row.id,
    resourceName: row.resourceName,
    resourceType: row.resourceType,
    fileUrl: row.fileUrl,
    fileHash: row.fileHash,
    fileSize: row.fileSize,
    linkUrl: row.linkUrl,
    description: row.description
  })
  open.value = true
}
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const payload = {...form}
    if (payload.resourceType === 0) {
      if (!payload.fileHash) return proxy.$modal.msgError('缺少文件哈希，请重新上传')
      payload.linkUrl = null
    } else {
      payload.fileUrl = null;
      payload.fileHash = null;
      payload.fileSize = null
    }
    await updateResourcePortal(payload)
    proxy.$modal.msgSuccess('修改已提交审核')
    open.value = false
    getList()
  })
}
const delRow = async (row) => {
  await proxy.$modal.confirm('确认删除该资源吗？（待审/驳回/下架可删除）')
  await removeResourcePortal(row.id)
  proxy.$modal.msgSuccess('删除成功')
  getList()
}
// 下架弹窗
const offlineOpen = ref(false)
const offlineFormRef = ref()
const offlineForm = ref({reason: ''})
const offlineRules = reactive({reason: [{required: true, message: '请输入下架原因', trigger: 'blur'}]})
const offlineRowRef = ref(null)
const openOffline = (row) => {
  offlineRowRef.value = row;
  offlineForm.value.reason = '';
  offlineOpen.value = true
}
const cancelOffline = () => {
  offlineOpen.value = false
}
const submitOffline = () => {
  offlineFormRef.value.validate(async valid => {
    if (!valid) return
    await offlineResourcePortal(offlineRowRef.value.id, offlineForm.value.reason)
    offlineOpen.value = false
    proxy.$modal.msgSuccess('已下架')
    getList()
  })
}
const online = async (row) => {
  await onlineResourcePortal(row.id);
  proxy.$modal.msgSuccess('已提交上架审核');
  getList()
}

// 上传压缩包（与上传对话框一致）
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
      dir: `resource/${userStore.id}/edit/${form.id || 'temp'}`,
      publicUrl: true
    })
    form.fileUrl = data.url || data.publicUrl || ''
    form.fileHash = data.sha256 || data.etag || ''
    form.fileSize = file.size
    options.onSuccess(data)
  } catch (e) {
    options.onError(e)
  }
}

// 清空已选文件（允许用户改为外链或重新上传）
const clearFile = () => {
  form.fileUrl = '';
  form.fileHash = '';
  form.fileSize = 0
}

onMounted(getList)
</script>

<style scoped>
.hint {
  color: #909399;
}
</style>
