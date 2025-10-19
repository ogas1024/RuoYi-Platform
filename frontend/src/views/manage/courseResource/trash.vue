<template>
  <div class="app-container">
    <div class="toolbar">
      <span class="hint">回收站（已驳回/已下架）</span>
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
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag type="danger" v-if="scope.row.status===2">已驳回</el-tag>
          <el-tag type="info" v-else>已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="驳回理由" prop="auditReason" min-width="200"/>
      <el-table-column label="更新时间" prop="updateTime" width="180"/>
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="scope">
          <el-button link type="success" icon="Top" @click="onlineRow(scope.row)" v-hasPermi="['manage:courseResource:online']">重新上架</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)" v-hasPermi="['manage:courseResource:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
  </div>
</template>

<script setup name="ResourceTrash">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listResource, delResource, onlineResource } from '@/api/manage/courseResource'
import { listMajor } from '@/api/manage/major'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, status: undefined, majorId: undefined, courseId: undefined })
const majors = ref([])

const getList = async () => {
  loading.value = true
  try {
    // 查询已驳回(2)+已下架(3)，这里简单分两次查询合并也可；为简化用两次调用拼接
    const [rej, off] = await Promise.all([
      listResource({ ...queryParams, status: 2 }),
      listResource({ ...queryParams, status: 3 })
    ])
    const rows = (rej.rows || []).concat(off.rows || [])
    list.value = rows
    total.value = rows.length
  } finally { loading.value = false }
}
const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.majorId = undefined; queryParams.courseId = undefined; handleQuery() }
const delRow = async (row) => { await delResource(row.id); proxy.$modal.msgSuccess('删除成功'); getList() }
const onlineRow = async (row) => { await onlineResource(row.id); proxy.$modal.msgSuccess('已提交上架审核'); getList() }

const getMajors = async () => { const resp = await listMajor({ pageNum: 1, pageSize: 100 }); majors.value = resp.rows || [] }
onMounted(async () => { await getMajors(); await getList() })
</script>

<style scoped>
.hint { color: #909399; }
</style>
