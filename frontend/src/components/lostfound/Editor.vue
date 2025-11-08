<template>
  <el-form :model="data" label-width="100px" style="max-width: 760px">
    <el-form-item label="类型">
      <el-radio-group v-model="data.type">
        <el-radio label="lost">丢失</el-radio>
        <el-radio label="found">捡到</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="标题">
      <el-input v-model="data.title" placeholder="2~50 字" maxlength="50" show-word-limit/>
    </el-form-item>
    <el-form-item label="正文">
      <el-input v-model="data.content" type="textarea" :rows="6" placeholder="尽量描述清楚，支持换行"/>
    </el-form-item>
    <el-form-item label="联系方式">
      <el-input v-model="data.contactInfo" placeholder="电话/微信等（≤50字，可选）" maxlength="50" show-word-limit/>
    </el-form-item>
    <el-form-item label="地点">
      <el-input v-model="data.location" placeholder="可选"/>
    </el-form-item>
    <el-form-item label="发生时间">
      <el-date-picker v-model="data.lostTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="可选"/>
    </el-form-item>

    <el-form-item label="图片（≤9）">
      <div class="img-uploader">
        <el-upload
            list-type="picture-card"
            :show-file-list="true"
            :file-list="fileList"
            :http-request="ossUpload"
            accept="image/*"
            :on-remove="onRemove"
            :on-preview="onPreview"
            :on-exceed="onExceed"
            :limit="maxImages"
            :class="{ hide: (fileList||[]).length >= maxImages }"
        >
          <el-icon>
            <Plus/>
          </el-icon>
        </el-upload>
      </div>
    </el-form-item>
  </el-form>
  <el-dialog v-model="previewOpen" title="图片预览" width="800px">
    <img :src="previewUrl" style="max-width:100%;display:block;margin:auto"/>
  </el-dialog>
</template>

<script setup>
import {reactive, ref, watch, nextTick} from 'vue'
import {Plus} from '@element-plus/icons-vue'
import {uploadOssPortal} from '@/api/portal/upload'
import {uploadOss} from '@/api/manage/upload'
import {ElMessage} from 'element-plus'

const props = defineProps({
  modelValue: {type: Object, default: () => ({})},
  maxImages: {type: Number, default: 9},
  // 上传作用域：portal 使用 /portal/upload/oss；manage 使用 /manage/upload/oss（需权限）
  uploadScope: {type: String, default: 'portal'},
  // 大小上限（MB）：门户默认 2MB；管理可提高
  maxSizeMb: {type: Number, default: 2}
})
const emit = defineEmits(['update:modelValue'])

const data = reactive({
  type: 'lost',
  title: '',
  content: '',
  contactInfo: '',
  location: '',
  lostTime: '',
  images: [], ...(props.modelValue || {})
})

// 仅在父值引用变更时同步（避免编辑时被深度 watch 覆盖导致“点了没反应”）
watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    Object.assign(data, val)
  }
}, {deep: false, immediate: true})

watch(data, (val) => {
  emit('update:modelValue', JSON.parse(JSON.stringify(val)))
}, {deep: true})

// 删除一张图片（由卡片删除触发）
const removeImg = (idx) => data.images.splice(idx, 1)

// 维护 el-upload 的受控文件列表，实现图片卡片即时显示/删除
const fileList = ref([])
watch(() => data.images, (imgs) => {
  fileList.value = (imgs || []).map((img, i) => ({
    name: img.url || `img-${i}`,
    url: img.url,
    status: 'success',
    uid: img.uid || `${i}-${img.url}`
  }))
}, {deep: true, immediate: true})
const onRemove = (file) => {
  const idx = (data.images || []).findIndex(i => i.url === file.url)
  if (idx >= 0) data.images.splice(idx, 1)
}
const previewUrl = ref('')
const previewOpen = ref(false)
const onPreview = (file) => {
  previewUrl.value = file.url;
  previewOpen.value = true
}

const ossUpload = async ({file, onSuccess, onError}) => {
  if ((data.images || []).length >= props.maxImages) {
    ElMessage.error('最多 9 张');
    return
  }
  if (!(file && file.type && file.type.startsWith('image/'))) {
    ElMessage.error('仅支持图片类型');
    return
  }
  const limit = (props.maxSizeMb || 2) * 1024 * 1024
  if (file.size > limit) {
    ElMessage.error(`图片大小不能超过 ${props.maxSizeMb}MB`);
    return
  }
  const fd = new FormData();
  fd.append('file', file)
  try {
    let res
    if (props.uploadScope === 'manage') {
      // 管理端上传（需 manage:upload:oss 权限）
      res = await uploadOss(fd, {dir: 'lostfound', publicUrl: true})
    } else {
      // 门户上传（场景：失物招领图片）
      res = await uploadOssPortal(fd, {scene: 'lostfound.image', dir: 'lostfound', publicUrl: true})
    }
    const url = res && (res.url || (res.data && res.data.url))
    if (url) {
      data.images.push({url, uid: `${Date.now()}-${Math.random().toString(36).slice(2)}`})
      if (onSuccess) onSuccess({code: 200, data: {url}})
      ElMessage.success('上传成功')
    } else {
      if (onError) onError(new Error('no url'))
      ElMessage.error('上传失败')
    }
  } catch (e) {
    if (onError) onError(e)
    ElMessage.error('上传失败')
  }
}
const onExceed = () => ElMessage.warning(`最多上传 ${props.maxImages} 张图片`)
</script>

<style scoped>
.img-uploader .tip {
  color: #909399;
  font-size: 12px;
  margin-top: 6px;
}

:deep(.hide .el-upload--picture-card) {
  display: none;
}
</style>
