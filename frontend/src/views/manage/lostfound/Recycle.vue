<template>
  <div class="app-container">
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="类型">
        <el-select v-model="q.type" clearable placeholder="全部">
          <el-option label="丢失" value="lost"/>
          <el-option label="捡到" value="found"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type==='lost'?'danger':'success'">{{ row.type === 'lost' ? '丢失' : '捡到' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="220"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status===3" type="danger">驳回</el-tag>
          <el-tag v-else type="info">下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rejectReason" label="驳回原因" min-width="160"/>
      <el-table-column prop="offlineReason" label="下架原因" min-width="160"/>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="restore(row)" v-hasPermi="['manage:lostfound:restore']">
            恢复待审
          </el-button>
          <el-button size="small" text type="danger" @click="remove(row)" v-hasPermi="['manage:lostfound:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="page.pageNum" v-model:limit="page.pageSize"
                @pagination="getList"/>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {listRecycleLostFound, restoreLostFound, removeLostFound} from '@/api/manage/lostfound'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = reactive({pageNum: 1, pageSize: 10})
const q = ref({type: ''})

const getList = async () => {
  loading.value = true
  try {
    const {rows, total: t} = await listRecycleLostFound({...q.value, ...page})
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}
const resetQuery = () => {
  q.value = {type: ''};
  page.pageNum = 1;
  getList()
}

const restore = async (row) => {
  await restoreLostFound(row.id);
  ElMessage.success('已恢复为待审');
  getList()
}
const remove = async (row) => {
  await ElMessageBox.confirm('确认删除？', '提示');
  await removeLostFound(row.id);
  ElMessage.success('已删除');
  getList()
}

onMounted(getList)
</script>

