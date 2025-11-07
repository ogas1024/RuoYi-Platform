<template>
  <div class="portal-container">
    <h2>问卷列表</h2>
    <div style="margin-bottom:10px; display:flex; gap:8px;">
      <el-input v-model="query.title" placeholder="按标题搜索" style="width:220px" @keyup.enter="load" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="goMy">我填写的</el-button>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="320">
        <template #default="scope">
          <div style="display:flex;align-items:center;gap:8px;">
            <span>{{ scope.row.title }}</span>
            <el-tag v-if="scope.row.pinned===1" type="danger" effect="dark">置顶</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="deadline" label="截止时间" width="180" />
      <el-table-column label="状态" width="180">
        <template #default="scope">
          <el-tag v-if="scope.row.status===2" type="info">已归档</el-tag>
          <el-tag v-else-if="isExpired(scope.row)" type="danger">已过期</el-tag>
          <el-tag v-else type="success">进行中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="填写" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.submitted===1" type="success">已填写</el-tag>
          <el-tag v-else-if="scope.row.status===1 && !isExpired(scope.row)" type="warning">未填写</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" type="primary" @click="fill(scope.row)" :disabled="scope.row.status===2 || isExpired(scope.row)">填写</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="load"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import router from '@/router'
import { listSurvey } from '@/api/portal/survey'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum:1, pageSize:10, title:'' })

function load(){
  loading.value=true
  listSurvey(query).then(res=>{
    list.value = res.rows || []
    total.value = res.total || 0
  }).finally(()=>loading.value=false)
}

function fill(row){ router.push({ name:'PortalSurveyFill', query:{ id: row.id } }) }
function goMy(){ router.push({ name:'PortalSurveyMy' }) }
function isExpired(row){ return row.deadline ? (new Date(row.deadline).getTime() < Date.now()) : false }
onMounted(load)
</script>
