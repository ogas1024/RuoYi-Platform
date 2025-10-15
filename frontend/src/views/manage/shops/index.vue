<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="店名" prop="shopName">
        <el-input
            v-model="queryParams.shopName"
            placeholder="请输入店名"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主营类别" prop="categoryId">
        <el-select
            v-model="queryParams.categoryId"
            style="width: 150px"
            placeholder="请选择类别"
            clearable
        >
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" border stripe :data="shopsList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" align="center" width="55">
        <template #default="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center" prop="userName"/>
      <el-table-column label="店名" align="center" prop="shopName"/>
      <el-table-column label="主营类别" align="center" prop="categoryName"/>
      <el-table-column label="联系人" align="center" prop="contactPerson"/>
      <el-table-column label="联系电话" align="center" prop="contactPhone"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form :model="form" :rules="rules" ref="shopsRef" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.id == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.id == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20"
                        show-password/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" maxlength="30"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系方式"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="店名" prop="shopName">
              <el-input v-model="form.shopName" placeholder="请输入店名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主营类别" prop="categoryId">
              <el-select v-model="form.categoryId"   style="width: 100%"    placeholder="请选择类别"  clearable  >
                <el-option
                    v-for="item in categoryList"
                    :key="item.id"
                    :label="item.categoryName"
                    :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Shops">
import {listShops, getShops, delShops, addShops, updateShops} from "@/api/manage/shops"
import { listCategory } from "@/api/manage/category"
const {proxy} = getCurrentInstance()

const shopsList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const categoryList = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    shopName: null,
    contactPerson: null,
    categoryId: null,
    contactPhone: null
  },
  rules: {
    userName: [
      { required: true, message: "用户名称不能为空", trigger: "blur" }
    ],
    password: [
      { required: true, message: "密码不能为空", trigger: "blur" }
    ],
    shopName: [
      { required: true, message: "店名不能为空", trigger: "blur" }
    ],
    contactPerson: [
      { required: true, message: "联系人不能为空", trigger: "blur" }
    ],
    categoryId: [
      { required: true, message: "主营类别不能为空", trigger: "blur" }
    ],
    contactPhone: [
      { required: true, message: "联系方式不能为空", trigger: "blur" }
    ]
  }
})

const {queryParams, form, rules} = toRefs(data)

/** 查询商铺信息列表 */
function getList() {
  loading.value = true
  listShops(queryParams.value).then(response => {
    shopsList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    shopName: null,
    contactPerson: null,
    categoryId: null,
    contactPhone: null,
    userName: null,
    password: null
  }
  proxy.resetForm("shopsRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加商铺信息"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getShops(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改商铺信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["shopsRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateShops(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addShops(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除商铺名为"' + row.shopName + '"的数据？').then(function () {
    return delShops(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('manage/shops/export', {
    ...queryParams.value
  }, `shops_${new Date().getTime()}.xlsx`)
}

function getCategoryList() {
  const loadAll = { pageNum: 1, pageSize: 1000 }
  listCategory(loadAll).then(response => {
    categoryList.value = response.rows
  })
}
getList()
getCategoryList()
</script>