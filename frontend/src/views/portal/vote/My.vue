<template>
  <div class="portal-container">
    <h2>我的投票</h2>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="title" label="投票标题" min-width="300"/>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag v-if="ended(scope.row)" type="info">已结束</el-tag>
          <el-tag v-else type="success">进行中</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="180"/>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" type="primary" v-if="!ended(scope.row)" @click="modify(scope.row)">修改投票
          </el-button>
          <el-button size="small" v-else @click="view(scope.row)">查看结果</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="pageNum" v-model:limit="pageSize" @pagination="load"/>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {myVote, getVote} from '@/api/portal/vote'
import {useRouter} from 'vue-router'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
// 记录每个投票是否已结束（过期/归档）
const endMap = ref({})

function load() {
  loading.value = true
  myVote({pageNum: pageNum.value, pageSize: pageSize.value}).then(res => {
    const rows = res.rows || []
    list.value = rows
    total.value = res.total || 0
    // 拉取每条的当前状态（仅获取必要字段）
    const ids = Array.from(new Set(rows.map(r => r.surveyId).filter(Boolean)))
    ids.forEach(id => {
      getVote(id).then(r => {
        const d = r.data
        const isExpired = !!(d && d.deadline && new Date(d.deadline).getTime() < Date.now())
        const isArchived = d && d.status === 2
        endMap.value[id] = isExpired || isArchived
      }).catch(() => {
        endMap.value[id] = false
      })
    })
  }).finally(() => loading.value = false)
}

function ended(row) {
  const val = endMap.value[row.surveyId];
  return !!val
}

function modify(row) {
  router.push({name: 'PortalVoteFill', query: {id: row.surveyId}})
}

function view(row) {
  router.push({name: 'PortalVoteFill', query: {id: row.surveyId}})
}

onMounted(load)
</script>
