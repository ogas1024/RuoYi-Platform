<template>
  <div class="portal-container">
    <h2>我填写的</h2>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="title" label="问卷标题" min-width="300"/>
      <el-table-column prop="submitTime" label="提交时间" width="180"/>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button size="small" @click="fill(scope.row)">查看/修改</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="pageNum" v-model:limit="pageSize" @pagination="load"/>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import router from '@/router'
import {mySurvey} from '@/api/portal/survey'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

function load() {
  loading.value = true
  mySurvey({pageNum: pageNum.value, pageSize: pageSize.value}).then(res => {
    list.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => loading.value = false)
}

function fill(row) {
  router.push({name: 'PortalSurveyFill', query: {id: row.surveyId}})
}

onMounted(load)
</script>

