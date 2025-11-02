<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">待审核资源</span>
    </div>
<el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程名">
        <el-input v-model="queryParams.courseName" placeholder="按课程名模糊" style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="资源名称" prop="resourceName" min-width="220"/>
      <el-table-column label="专业/课程" min-width="200">
        <template #default="scope">{{ scope.row.majorName || scope.row.majorId }} / {{ scope.row.courseName || scope.row.courseId }}</template>
      </el-table-column>
      <el-table-column label="上传者" prop="uploaderName" width="120"/>
      <el-table-column label="提交时间" prop="createTime" width="180"/>
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="scope">
          <el-button link type="success" icon="CircleCheck" @click="approve(scope.row)">通过</el-button>
          <el-button link type="danger" icon="Close" @click="reject(scope.row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
  </div>
</template>

<script setup name="ResourceAudit">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listResource, approveResource, rejectResource } from '@/api/manage/courseResource'
import { listMajor } from '@/api/manage/major'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, status: 0, majorId: undefined, courseId: undefined })
const majors = ref([])

const getList = async () => {
  loading.value = true
  try {
    const resp = await listResource(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally { loading.value = false }
}
const getMajors = async () => {
  const resp = await listMajor({ pageNum: 1, pageSize: 100 })
  majors.value = resp.rows || []
}
const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.majorId = undefined; queryParams.courseId = undefined; handleQuery() }
const approve = async (row) => { await approveResource(row.id); proxy.$modal.msgSuccess('已通过'); getList() }
const reject = async (row) => {
  const reason = await proxy.$prompt('请输入驳回理由', '驳回', { inputPattern: /.+/, inputErrorMessage: '必填' }).catch(() => null)
  if (!reason || !reason.value) return
  await rejectResource(row.id, reason.value)
  proxy.$modal.msgSuccess('已驳回')
  getList()
}

onMounted(async () => { await getMajors(); await getList() })
</script>

<style scoped>
.hint { color: #909399; }
</style>
