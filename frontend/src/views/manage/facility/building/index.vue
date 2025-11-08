<template>
  <div class="app-container">
    <el-form :inline="true" :model="query" class="mb8">
      <el-form-item label="名称">
        <el-input v-model="query.buildingName" placeholder="楼房名称" clearable @keyup.enter="getList"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部">
          <el-option label="正常" value="0"/>
          <el-option label="停用" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openAdd" v-hasPermi="['manage:facility:building:add']">新增</el-button>
        <el-button type="danger" :disabled="!selection.length" @click="batchRemove"
                   v-hasPermi="['manage:facility:building:remove']">删除
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border @selection-change="sel=>selection = sel">
      <el-table-column type="selection" width="50"/>
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="buildingName" label="名称"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status==='0'?'success':'info'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注"/>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openEdit(row)"
                     v-hasPermi="['manage:facility:building:edit']">编辑
          </el-button>
          <el-button size="small" text type="danger" @click="remove(row)"
                     v-hasPermi="['manage:facility:building:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" :title="form.id?'编辑楼房':'新增楼房'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.buildingName"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark"/>
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
import {buildingList, buildingAdd, buildingUpdate, buildingRemove} from '@/api/manage/facility'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const selection = ref([])
const query = reactive({pageNum: 1, pageSize: 10, buildingName: '', status: ''})

async function getList() {
  loading.value = true
  try {
    const {rows, total: t} = await buildingList(query)
    list.value = rows || []
    total.value = t || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, {pageNum: 1, pageSize: 10, buildingName: '', status: ''});
  getList()
}

const open = ref(false)
const form = reactive({id: null, buildingName: '', status: '0', remark: ''})

function openAdd() {
  Object.assign(form, {id: null, buildingName: '', status: '0', remark: ''});
  open.value = true
}

function openEdit(row) {
  Object.assign(form, row);
  open.value = true
}

async function submit() {
  if (!form.buildingName) return ElMessage.error('请输入名称')
  if (form.id) await buildingUpdate(form); else await buildingAdd(form)
  open.value = false
  ElMessage.success('保存成功')
  getList()
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除该楼房？', '提示');
  await buildingRemove(row.id);
  ElMessage.success('删除成功');
  getList()
}

async function batchRemove() {
  await ElMessageBox.confirm('确认删除选中楼房？', '提示');
  const ids = selection.value.map(i => i.id).join(',');
  await buildingRemove(ids);
  ElMessage.success('删除成功');
  getList()
}

onMounted(getList)
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>

