<template>
  <div class="app-container">
    <el-form :inline="true" :model="q" class="mb8">
      <el-form-item label="用户名">
        <el-input v-model="q.username" clearable style="width:200px"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="q.status" clearable style="width:160px">
          <el-option label="生效" value="0"/>
          <el-option label="失效" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openAdd" v-hasPermi="['manage:facility:ban:add']">新增封禁</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="username" label="用户名" width="160"/>
      <el-table-column prop="nickname" label="昵称"/>
      <el-table-column prop="reason" label="原因"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status==='0'?'danger':'info'">{{ row.status === '0' ? '生效' : '失效' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expireTime" label="到期时间" width="180"/>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" text type="danger" @click="remove(row)" v-hasPermi="['manage:facility:ban:remove']">
            解除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="page.pageNum" v-model:limit="page.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" title="新增封禁" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名（test01/学号）"/>
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="form.reason" type="textarea" :rows="3"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">生效</el-radio>
            <el-radio label="1">失效</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="到期时间">
          <el-date-picker v-model="form.expireTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {banList, banAdd, banRemove} from '@/api/manage/facility'
import {ElMessage, ElMessageBox} from 'element-plus'

const q = reactive({username: '', status: ''})
const page = reactive({pageNum: 1, pageSize: 10})
const loading = ref(false)
const list = ref([])
const total = ref(0)

async function getList() {
  loading.value = true;
  try {
    const {rows, total: t} = await banList({...q, ...page});
    list.value = rows || [];
    total.value = t || 0
  } finally {
    loading.value = false
  }
}

function reset() {
  Object.assign(q, {username: '', status: ''});
  getList()
}

const open = ref(false)
const form = reactive({username: '', reason: '', status: '0', expireTime: ''})

function openAdd() {
  Object.assign(form, {username: '', reason: '', status: '0', expireTime: ''});
  open.value = true
}

async function submit() {
  if (!form.username || !form.reason) return ElMessage.error('请完善表单');
  await banAdd(form);
  ElMessage.success('已保存');
  open.value = false;
  getList()
}

async function remove(row) {
  await ElMessageBox.confirm('确认解除？', '提示');
  await banRemove(row.id);
  ElMessage.success('已解除');
  getList()
}

onMounted(getList)
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>
