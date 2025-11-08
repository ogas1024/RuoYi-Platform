<template>
  <div class="app-container">
    <div class="mb8">
      <el-button type="primary" icon="Plus" @click="showAdd=true" v-hasPermi="['manage:libraryLibrarian:add']">
        新增图书管理员
      </el-button>
    </div>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="username" label="用户名"/>
      <el-table-column prop="nickname" label="昵称"/>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" text size="small" @click="dismissOne(row)"
                     v-hasPermi="['manage:libraryLibrarian:remove']">卸任
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAdd" title="新增图书管理员" width="420px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userName" placeholder="请输入用户名（test01/学号）"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd=false">取消</el-button>
        <el-button type="primary" @click="appoint">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {listLibrarian, addLibrarian, removeLibrarian} from '@/api/manage/librarian'

const loading = ref(false)
const list = ref([])
const userName = ref('')
const showAdd = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const {rows} = await listLibrarian({pageNum: 1, pageSize: 100})
    list.value = rows || []
  } finally {
    loading.value = false
  }
}
const appoint = async () => {
  if (!userName.value) return;
  await addLibrarian(userName.value);
  userName.value = '';
  showAdd.value = false;
  getList()
}
const dismissOne = async (row) => {
  await removeLibrarian(row.userId);
  getList()
}

onMounted(getList)
</script>
