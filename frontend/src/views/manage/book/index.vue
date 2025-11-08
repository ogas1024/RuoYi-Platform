<template>
  <div class="app-container">
    <el-form
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        v-show="showSearch"
        label-width="68px"
        class="mb8"
    >
      <el-form-item label="图书类别" prop="categoryId">
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
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery"></right-toolbar>
    </el-row>

    <el-tabs
        v-model="activeTab"
        @tab-change="handleTabChange"
        class="mb8"
    >
      <el-tab-pane label="待审核" name="0"></el-tab-pane>
      <el-tab-pane label="审核通过" name="1"></el-tab-pane>
      <el-tab-pane label="审核拒绝" name="2"></el-tab-pane>
      <el-tab-pane label="已上架" name="3"></el-tab-pane>
      <el-tab-pane label="已下架" name="4"></el-tab-pane>
      <el-tab-pane label="全部" name="all"></el-tab-pane>
    </el-tabs>
    <el-table v-loading="loading" border stripe :data="bookList">
      <el-table-column label="序号" align="center" width="55">
        <template #default="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图书类别" align="center" prop="categoryName"/>
      <el-table-column label="图书名称" align="center" prop="bookName" :show-overflow-tooltip="true"/>
      <el-table-column label="作者" align="center" prop="author" :show-overflow-tooltip="true"/>
      <el-table-column label="出版社" align="center" prop="publisher" width="180"/>
      <el-table-column label="价格" align="center" prop="price" width="60"/>
      <el-table-column label="库存" align="center" prop="quantity" width="60">
        <template #default="scope">
          <span :style="{ color: scope.row.quantity < 50 ? 'red' : '' }">{{ scope.row.quantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="70" prop="status">
        <template #default="scope">
          <span
              :style="{
              color: scope.row.status === 0 ? 'orange' :
                     scope.row.status === 1 ? 'green' :
                     scope.row.status === 2 ? 'red' :
                     scope.row.status === 3 ? 'blue' :
                     scope.row.status === 4 ? 'purple' : 'gray'
            }"
          >
            <span v-if="scope.row.status === 0">待审核</span>
            <span v-else-if="scope.row.status === 1">通过</span>
            <span v-else-if="scope.row.status === 2">拒绝</span>
            <span v-else-if="scope.row.status === 3">上架</span>
            <span v-else-if="scope.row.status === 4">下架</span>
            <span v-else>未知</span>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" v-if="scope.row.status !== 3" icon="Edit" @click="handleUpdate(scope.row)">
            修改
          </el-button>
          <el-button link type="primary" v-if="scope.row.status !== 3" icon="Delete" @click="handleDelete(scope.row)">
            删除
          </el-button>
          <el-button
              v-if="scope.row.status === 1 || scope.row.status === 4"
              link type="success"
              icon="Upload"
              @click="handleUp(scope.row)"
          >
            上架
          </el-button>
          <el-button
              v-if="scope.row.status === 3"
              link type="warning"
              icon="Download"
              @click="handleDown(scope.row)"
          >
            下架
          </el-button>
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
      <el-form ref="bookRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="图书名称" prop="bookName">
              <el-input v-model="form.bookName" placeholder="请输入图书名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="图书类别" prop="categoryId">
              <el-select v-model="form.categoryId" style="width: 100%" placeholder="请选择类别" clearable>
                <el-option
                    v-for="item in categoryList"
                    :key="item.id"
                    :label="item.categoryName"
                    :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="请输入作者"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="出版社" prop="publisher">
              <el-input v-model="form.publisher" placeholder="请输入出版社"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版日期" prop="publishDate">
              <el-date-picker clearable
                              v-model="form.publishDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择出版日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" placeholder="请输入价格"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入库存数量"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="封面" prop="cover">
              <image-upload v-model="form.cover"/>
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

<script setup name="Book">
import {listBook, getBook, delBook, addBook, updateBook} from "@/api/manage/book"
import {listCategory} from "@/api/manage/category"
import {ref, reactive, computed, getCurrentInstance} from "vue"

const {proxy} = getCurrentInstance()
const total = ref(0)
const bookList = ref([])
const activeTab = ref("0")
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const title = ref("")
const categoryList = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    categoryId: null,
    bookName: null,
    author: null,
    status: null
  },
  rules: {
    categoryId: [{required: true, message: "图书类别不能为空", trigger: "blur"}],
    bookName: [{required: true, message: "图书名称不能为空", trigger: "blur"}],
    author: [{required: true, message: "作者不能为空", trigger: "blur"}],
    publisher: [{required: true, message: "出版社不能为空", trigger: "blur"}],
    price: [{required: true, message: "价格不能为空", trigger: "blur"}],
    quantity: [{required: true, message: "库存数量不能为空", trigger: "blur"}],
  }
})
const {form, rules, queryParams} = toRefs(data)

function getList() {
  loading.value = true
  if (activeTab.value !== 'all') {
    queryParams.value.status = Number(activeTab.value)
  } else
    queryParams.value.status = null
  console.log(queryParams.value.status)
  listBook(queryParams.value).then(response => {
    bookList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function handleTabChange() {
  queryParams.value.pageNum = 1
  getList()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery();
}

/** 查询类别列表 */
function getCategoryList() {
  const loadAll = {pageNum: 1, pageSize: 1000}
  listCategory(loadAll).then(response => {
    categoryList.value = response.rows
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
  if (proxy.$refs.bookRef) {
    proxy.resetForm("bookRef")
  }
}


/** 新增操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加图书列表"
}

/** 修改操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getBook(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改图书列表"
  })
}

/** 提交操作 */
function submitForm() {
  proxy.$refs["bookRef"].validate(valid => {
    if (valid) {
      const request = form.value.id ? updateBook(form.value) : addBook(form.value)
      request.then(() => {
        proxy.$modal.msgSuccess(form.value.id ? "修改成功" : "新增成功")
        open.value = false
        getList()  // 刷新当前标签页数据
      }).catch(() => {
        proxy.$modal.msgError(form.value.id ? "修改失败" : "新增失败")
      })
    }
  })
}

/** 删除操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除选中的图书？').then(() => {
    return delBook(_ids)
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()  // 刷新当前标签页数据
  }).catch(() => {
  })
}

/** 导出操作 */
function handleExport() {

}

/** 上架操作 */
function handleUp(row) {
  updateBook({id: row.id, status: 3}).then(() => {
    proxy.$modal.msgSuccess("上架成功")
    getList()
  }).catch(() => {
    proxy.$modal.msgError("上架失败")
  })
}

/** 下架操作 */
function handleDown(row) {
  updateBook({id: row.id, status: 4}).then(() => {
    proxy.$modal.msgSuccess("下架成功")
    getList()
  }).catch(() => {
    proxy.$modal.msgError("下架失败")
  })
}

// 初始化加载
getList()
getCategoryList()
</script>
