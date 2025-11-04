<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="openAdd" v-hasPermi="['manage:course:add']">新增课程</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="mb8">
      <el-form-item label="专业">
        <el-select v-model="queryParams.majorId" clearable placeholder="选择专业" style="width: 220px">
          <el-option v-for="m in allowedMajors" :key="m.id" :label="m.majorName" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程名">
        <el-input v-model="queryParams.courseName" placeholder="课程名称" style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" border stripe :data="list">
      <el-table-column label="ID" prop="id" width="80"/>
      <el-table-column label="专业" min-width="200" prop="majorName">
        <template #default="scope">{{ scope.row.majorName || '-' }}</template>
      </el-table-column>
      <el-table-column label="课程名称" prop="courseName" min-width="200"/>
      <el-table-column label="课程编码" prop="courseCode" min-width="160"/>
      <el-table-column label="状态" prop="status" width="100">
        <template #default="scope"><el-tag :type="scope.row.status==='0'?'success':'info'">{{ scope.row.status==='0'?'正常':'停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="openEdit(scope.row)" v-hasPermi="['manage:course:edit']">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="delRow(scope.row)" v-hasPermi="['manage:course:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <el-dialog v-model="open" :title="form.id?'编辑课程':'新增课程'" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="专业" prop="majorId">
          <el-select v-model="form.majorId" placeholder="选择专业">
            <el-option v-for="m in allowedMajors" :key="m.id" :label="m.majorName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" maxlength="255"/></el-form-item>
        <el-form-item label="课程编码"><el-input v-model="form.courseCode" maxlength="64"/></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open=false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="CourseManage">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { listCourse, addCourse, updateCourse, delCourse } from '@/api/manage/course'
import { listMajor } from '@/api/manage/major'
import { listMyMajors } from '@/api/manage/majorLead'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, majorId: undefined, courseName: '' })
const allowedMajors = ref([])
const userStore = useUserStore()

const open = ref(false)
const formRef = ref()
const form = reactive({ id: undefined, majorId: undefined, courseName: '', courseCode: '', status: '0' })
const rules = { majorId: [{ required: true, message: '请选择专业', trigger: 'change' }], courseName: [{ required: true, message: '请填写课程名称', trigger: 'blur' }] }

const getList = async () => {
  loading.value = true
  try {
    const resp = await listCourse(queryParams)
    list.value = resp.rows || []
    total.value = resp.total || 0
  } finally { loading.value = false }
}
const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.majorId = undefined; queryParams.courseName = ''; handleQuery() }
const openAdd = () => { Object.assign(form, { id: undefined, majorId: undefined, courseName: '', courseCode: '', status: '0' }); open.value = true }
const openEdit = (row) => { Object.assign(form, row); open.value = true }
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.id) await updateCourse(form); else await addCourse(form)
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
const delRow = async (row) => { await delCourse(row.id); proxy.$modal.msgSuccess('删除成功'); getList() }

const getAllowedMajors = async () => {
  const roles = userStore.roles || []
  const isAdmin = roles.includes('admin') || roles.includes('super_admin')
  if (isAdmin) {
    const resp = await listMajor({ pageNum: 1, pageSize: 100 });
    allowedMajors.value = resp.rows || []
  } else if (roles.includes('major_lead')) {
    const resp = await listMyMajors();
    allowedMajors.value = resp.data || resp.rows || []
  } else {
    allowedMajors.value = []
  }
}
onMounted(async () => { await getAllowedMajors(); await getList() })
</script>

<style scoped>
.hint { color: #909399; }
</style>
