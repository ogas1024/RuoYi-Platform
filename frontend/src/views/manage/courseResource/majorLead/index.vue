<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="openAdd" v-hasPermi="['manage:majorLead:add']">新增负责人
      </el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <template v-if="isAllRoleView">
        <el-table-column label="用户名" prop="userName" width="180"/>
        <el-table-column label="昵称" prop="nickName" width="180"/>
        <el-table-column label="已绑定专业" min-width="220">
          <template #default="scope">
            <span>{{ scope.row.majorNames || scope.row.majorIds || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <el-button link type="primary" icon="Plus" @click="openAdd(scope.row.userName)"
                       v-hasPermi="['manage:majorLead:add']">绑定到专业
            </el-button>
            <el-button link type="danger" icon="Remove" @click="retire(scope.row.userId)"
                       v-hasPermi="['manage:majorLead:remove']">卸任
            </el-button>
          </template>
        </el-table-column>
      </template>
      <template v-else>
        <el-table-column label="专业" prop="majorName" width="180"/>
        <el-table-column label="用户名" prop="userName" width="160"/>
        <el-table-column label="昵称" prop="nickName" width="160"/>
        <el-table-column label="备注" prop="remark" min-width="200"/>
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="scope">
            <el-button link type="danger" icon="Delete" @click="delRow(scope.row)"
                       v-hasPermi="['manage:majorLead:remove']">删除
            </el-button>
          </template>
        </el-table-column>
      </template>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
                @pagination="getList"/>

    <el-dialog v-model="open" title="新增专业负责人" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="专业" prop="majorId">
          <el-select v-model="form.majorId" clearable placeholder="选择专业">
            <el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名（test01/学号）"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MajorLeadManage">
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import {
  listMajorLead,
  addMajorLead,
  delMajorLead,
  listMajorLeadRoleUsers,
  retireMajorLeadUser
} from '@/api/manage/majorLead'
import {listMajor} from '@/api/manage/major'

const {proxy} = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const isAllRoleView = ref(true)
const queryParams = reactive({pageNum: 1, pageSize: 10, majorId: undefined, userName: ''})
const majors = ref([])

const open = ref(false)
const formRef = ref()
const form = reactive({majorId: undefined, userName: '', remark: ''})
const rules = {
  majorId: [{required: true, message: '请填写专业ID', trigger: 'blur'}],
  userName: [{required: true, message: '请填写用户名', trigger: 'blur'}]
}

const getList = async () => {
  loading.value = true
  try {
    // 当未选择专业且未按用户名过滤时，展示所有拥有 major_lead 角色的用户
    isAllRoleView.value = !queryParams.majorId && !queryParams.userName
    if (isAllRoleView.value) {
      const resp = await listMajorLeadRoleUsers({
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        majorId: undefined
      })
      list.value = resp.rows || []
      total.value = resp.total || 0
    } else {
      const resp = await listMajorLead(queryParams)
      list.value = resp.rows || []
      total.value = resp.total || 0
    }
  } finally {
    loading.value = false
  }
}
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList()
}
const resetQuery = () => {
  queryParams.majorId = undefined;
  queryParams.userName = '';
  handleQuery()
}
const openAdd = (uname) => {
  Object.assign(form, {majorId: queryParams.majorId, userName: uname || '', remark: ''});
  open.value = true
}
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    await addMajorLead(form)
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
const delRow = async (row) => {
  await delMajorLead(row.id);
  proxy.$modal.msgSuccess('删除成功');
  getList()
}
const retire = async (userId) => {
  await retireMajorLeadUser(userId);
  proxy.$modal.msgSuccess('已卸任');
  getList()
}

const getMajors = async () => {
  const resp = await listMajor({pageNum: 1, pageSize: 100});
  majors.value = resp.rows || []
}
onMounted(async () => {
  await getMajors();
  await getList()
})
</script>

<style scoped>
.hint {
  color: #909399;
}
</style>
