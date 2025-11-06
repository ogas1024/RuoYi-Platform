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

    <el-tabs
        v-model="activeTab"
        @tab-change="handleTabChange"
        class="mb8"
    >
      <el-tab-pane label="待审核" name="0"></el-tab-pane>
      <el-tab-pane label="审核通过" name="1"></el-tab-pane>
      <el-tab-pane label="审核拒绝" name="2"></el-tab-pane>
    </el-tabs>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            v-show="activeTab === '0' || activeTab === '2'"
            type="primary"
            plain
            icon="Plus"
            :disabled="multiple"
            @click="handleBatchAudit(1)"
        >通过
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            v-show="activeTab === '0' || activeTab === '1'"
            type="warning"
            plain
            icon="Download"
            :disabled="multiple"
            @click="handleBatchAudit(2)"
        >拒绝
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery"></right-toolbar>
    </el-row>
    <el-table @selection-change="handleSelectionChange"  v-loading="loading"  border stripe :data="bookList">
      <el-table-column label="序号" align="center" width="55">
        <template #default="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="商家" align="center" prop="createBy"/>
      <el-table-column label="图书类别" align="center" prop="categoryName"/>
      <el-table-column label="图书名称" align="center" prop="bookName" :show-overflow-tooltip="true"/>
      <el-table-column label="作者" align="center" prop="author" :show-overflow-tooltip="true"/>
      <el-table-column label="出版社" align="center" prop="publisher" width="180" />
      <el-table-column label="价格" align="center" prop="price" width="60"/>
      <el-table-column label="状态" align="center" width="70" prop="status">
        <template #default="scope">
          <span
              :style="{
              color: scope.row.status === 0 ? 'orange' :
                     scope.row.status === 1 ? 'green' :
                     scope.row.status === 2 ? 'red' : 'gray'
            }"
          >
            <span v-if="scope.row.status === 0">待审核</span>
            <span v-else-if="scope.row.status === 1">通过</span>
            <span v-else-if="scope.row.status === 2">拒绝</span>
            <span v-else>未知</span>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
              v-if="scope.row.status === 0 || scope.row.status === 2"
              link type="success"
              icon="Upload"
              @click="handleAudit(scope.row,1)"
          >
            通过
          </el-button>
          <el-button
              v-if="scope.row.status === 0 || scope.row.status === 1"
              link type="warning"
              icon="Download"
              @click="handleAudit(scope.row,2)"
          >
            拒绝
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
import {listBook, updateBook, batchAuditBook} from "@/api/manage/library"
import {listCategory} from "@/api/manage/category"
import {ref, reactive, getCurrentInstance} from "vue"
import {delOrders} from "@/api/manage/orders.js";

const {proxy} = getCurrentInstance()
const total = ref(0)
const bookList = ref([])
const activeTab = ref("0")
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const title = ref("")
const categoryList = ref([])
const ids = ref([])
const single = ref(true)
const multiple = ref(true)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    categoryId: null,
    bookName: null,
    author: null,
    status: null
  }
})
const {form, queryParams} = toRefs(data)

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}


function getList() {
  loading.value = true
  if (activeTab.value !== 'all') {
    queryParams.value.status = Number(activeTab.value)
  } else
    queryParams.value.status = null
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
  handleQuery()
}

/** 查询类别列表 */
function getCategoryList() {
  const loadAll = {pageNum: 1, pageSize: 1000}
  listCategory(loadAll).then(response => {
    categoryList.value = response.rows
  })
}

/** 导出操作 */
function handleExport() {

}

function handleAudit(row, status) {
  updateBook({id: row.id, status: status}).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {
    proxy.$modal.msgError("审核失败")
  })
}

function handleBatchAudit(status) {
  const _ids = ids.value
  batchAuditBook(_ids, status).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {
    proxy.$modal.msgError("审核失败")
  })
}

// 初始化加载
getList()
getCategoryList()
</script>
