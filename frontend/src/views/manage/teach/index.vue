<template>
  <div class="app-container">
    <el-table border stripe :data="teachList">
      <el-table-column label="序号" align="center" width="60">
        <template #default="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类别名称" align="center" prop="teachName"/>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getTeachList"
    />
  </div>
</template>
<script setup>
import {listTeach,delTeach} from '@/api/manage/teach'

const teachList = ref([])
const total = ref(0)
const { proxy } = getCurrentInstance()
const queryParams = reactive({
    pageNum: 1,
    pageSize: 10
})
function getTeachList() {
  listTeach(queryParams).then(response => {
    teachList.value = response.rows
    total.value = response.total
  })
}
function handleDelete(row) {
  console.log(row)
  proxy.$modal.confirm('是否确认删除编号为"' + row.id + '"的数据项？').then(function() {
    return delTeach(row.id)
  }).then(() => {
    getTeachList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getTeachList()
</script>
