<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="图书类别" prop="categoryId">
        <el-select v-model="queryParams.categoryId" style="width: 150px" placeholder="请选择类别" clearable>
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="图书名称" prop="bookName">
        <el-input
            v-model="queryParams.bookName"
            placeholder="请输入图书名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input
            v-model="queryParams.author"
            placeholder="请输入作者"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出版社" prop="publisher">
        <el-input
            v-model="queryParams.publisher"
            placeholder="请输入出版社"
            clearable
            @keyup.enter="handleQuery"
        />
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
        >加入购物车
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" border stripe :data="bookList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" width="60">
        <template #default="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图书类别" align="center" prop="categoryName"/>
      <el-table-column label="图书名称" align="center" prop="bookName" :show-overflow-tooltip="true"/>
      <el-table-column label="作者" align="center" prop="author"/>
      <el-table-column label="出版社" align="center" prop="publisher"/>
      <el-table-column label="价格" align="center" prop="price" width="60"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Plus" @click="handleUpdate(scope.row)">加入购物车</el-button>
          <el-button link type="info" icon="View" @click="handleView(scope.row, scope.index)">详细</el-button>
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

    <!-- 图书详情 -->
    <el-dialog title="图书详情" v-model="open" width="800px" append-to-body>
      <el-form :model="form" label-width="100px">

        <el-row :gutter="10">
          <el-col :span="6">
            <el-image
                :src="`/dev-api/${form.cover?.startsWith('/') ? form.cover.slice(1) : form.cover}`"
                fit="contain"
                style="width: 100%; height: 300px;"
            >
            </el-image>
          </el-col>
          <el-col :span="18">
            <el-descriptions column="1" border>
              <el-descriptions-item label="图书类别">{{ form.categoryName }}</el-descriptions-item>
              <el-descriptions-item label="图书名称">{{ form.bookName }}</el-descriptions-item>
              <el-descriptions-item label="作者">{{ form.author }}</el-descriptions-item>
              <el-descriptions-item label="出版社">{{ form.publisher }}</el-descriptions-item>
              <el-descriptions-item label="出版时间">{{ form.publishDate }}</el-descriptions-item>
              <el-descriptions-item label="价格">{{ form.price }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Book">
import {listBook, getBook, delBook, addBook, updateBook} from "@/api/manage/book"
import {listCategory} from "@/api/manage/category"

const {proxy} = getCurrentInstance()

const bookList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    categoryId: null,
    bookName: null,
    author: null,
    publisher: null,
  }
})

const {queryParams, form} = toRefs(data)

/** 查询图书列表列表 */
function getList() {
  loading.value = true
  listBook(queryParams.value).then(response => {
    bookList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

const loadAll = reactive({
  pageNum: 1,
  pageSize: 1000
})
const categoryList = ref([])

/** 查询类别列表下拉框 */
function getCategoryList() {
  listCategory(loadAll).then(response => {
    categoryList.value = response.rows
  })
}


// 表单重置
function reset() {
  form.value = {
    id: null,
    categoryId: null,
    bookName: null,
    cover: null,
    author: null,
    publisher: null,
    publishDate: null,
    price: null,
    quantity: null,
    createBy: null,
    createTime: null
  }
  proxy.resetForm("bookRef")
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
  title.value = "添加图书列表"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getBook(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改图书列表"
  })
}

function handleView(row) {
  console.log(row)
  open.value = true
  form.value = row
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["bookRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateBook(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBook(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除图书列表编号为"' + _ids + '"的数据项？').then(function () {
    return delBook(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

getList()
getCategoryList()
</script>
