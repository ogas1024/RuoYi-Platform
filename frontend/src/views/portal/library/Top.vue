<template>
  <div class="app-container">
    <el-tabs v-model="active">
      <el-tab-pane label="下载榜" name="book">
        <el-table :data="books" border>
          <el-table-column type="index" width="60" label="#"/>
          <el-table-column prop="title" label="标题"/>
          <el-table-column prop="author" label="作者" width="160"/>
          <el-table-column prop="uploaderName" label="上传者" width="140">
            <template #default="{ row }">{{ row.uploaderName ?? row.uploader_name ?? '-' }}</template>
          </el-table-column>
          <el-table-column prop="download_count" label="下载数" width="100">
            <template #default="{ row }">{{ row.downloadCount ?? row.download_count ?? 0 }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" text @click="openDetail(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="用户贡献榜" name="user">
        <el-table :data="users" border>
          <el-table-column type="index" width="60" label="#"/>
          <el-table-column prop="username" label="用户"/>
          <el-table-column prop="passedCount" label="通过书籍数" width="140"/>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {topLibraryPortal, topUsersLibraryPortal} from '@/api/portal/library'

const router = useRouter()
const active = ref('book')
const books = ref([])
const users = ref([])

const fetchBooks = async () => {
  const {data} = await topLibraryPortal(10);
  books.value = data || []
}
const fetchUsers = async () => {
  const {data} = await topUsersLibraryPortal(10);
  users.value = data || []
}
const openDetail = (row) => router.push({path: '/portal/library/detail', query: {id: row.id}})

onMounted(() => {
  fetchBooks();
  fetchUsers()
})
</script>
