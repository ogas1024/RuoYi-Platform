<template>
  <!-- 视图切换（已发布/草稿/过期/归档）+ 列表 + 操作列（编辑/发布/延期/归档/置顶/AI汇总） + 分页 + 多个弹窗（创建/详情/用户答卷/AI汇总/延期） -->
  <div class="app-container">
    <el-card>
      <div style="margin-bottom: 12px; display:flex; gap:8px; align-items:center;">
        <el-input v-model="query.title" placeholder="按标题搜索" style="width: 220px" @keyup.enter="load"/>
        <el-select v-model="view" placeholder="视图" style="width: 200px" @change="load">
          <el-option value="published" label="已发布"/>
          <el-option value="draft" label="草稿"/>
          <el-option value="expired" label="已过期"/>
          <el-option value="archived" label="已归档"/>
          <el-option value="all" label="全部"/>
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="success" @click="openCreate">新建问卷</el-button>
      </div>
      <el-table :data="list" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="240">
          <template #default="scope">
            <div style="display:flex;align-items:center;gap:8px;">
              <span>{{ scope.row.title }}</span>
              <el-tag v-if="scope.row.pinned===1" type="success">置顶</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status===1" type="success">已发布</el-tag>
            <el-tag v-else-if="scope.row.status===2" type="info">已归档</el-tag>
            <el-tag v-else type="warning">草稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="520">
          <template #default="scope">
            <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
            <template v-if="scope.row.status===0">
              <el-button size="small" type="primary"
                         @click="$router.push({ name:'ManageSurveyNew', query:{ id: scope.row.id } })"
                         v-hasPermi="['manage:survey:edit']">编辑
              </el-button>
              <el-button size="small" type="success" @click="doPublish(scope.row)"
                         v-hasPermi="['manage:survey:publish']">发布
              </el-button>
            </template>
            <template v-else>
              <el-button size="small" type="primary" :disabled="scope.row.status===2" @click="extend(scope.row)">延期
              </el-button>
              <el-button size="small" type="danger" :disabled="scope.row.status===2" @click="archive(scope.row)">归档
              </el-button>
              <el-button size="small" type="warning" @click="togglePin(scope.row)" v-hasPermi="['manage:survey:pin']">
                {{ scope.row.pinned === 1 ? '取消置顶' : '置顶' }}
              </el-button>
              <el-button size="small" type="success" @click="openAiSummary(scope.row)" v-hasPermi="['manage:survey:summary']">AI汇总</el-button>
            </template>
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
    </el-card>

    <!-- 创建弹窗（最简动态题目） -->
    <el-dialog v-model="createVisible" title="新建问卷" width="760px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title"/>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择日期时间"
                          format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
        <el-form-item label="可见范围">
          <el-radio-group v-model="form.visibleAll">
            <el-radio :label="1">全员</el-radio>
            <el-radio :label="0">自定义</el-radio>
          </el-radio-group>
          <div v-if="form.visibleAll===0" style="margin-top:8px;">
            <el-alert type="info" :closable="false"
                      title="简化：在管理后台菜单配置按钮权限后，这里先不做选择器。后端支持 role/dept/post 范围。"/>
          </div>
        </el-form-item>
        <el-divider>题目</el-divider>
        <div>
          <el-button type="primary" @click="addItem(1)">+ 文本题</el-button>
          <el-button type="primary" @click="addItem(2)">+ 单选题</el-button>
          <el-button type="primary" @click="addItem(3)">+ 多选题</el-button>
        </div>
        <div v-for="(it,idx) in form.items" :key="idx"
             style="margin-top:10px; padding:10px; border:1px dashed var(--el-border-color); border-radius:6px;">
          <div style="display:flex; gap:10px; align-items:center;">
            <el-tag type="info">#{{ idx + 1 }}</el-tag>
            <el-input v-model="it.title" placeholder="题目/提示" style="flex:1"/>
            <el-select v-model="it.type" style="width:120px">
              <el-option :value="1" label="文本"/>
              <el-option :value="2" label="单选"/>
              <el-option :value="3" label="多选"/>
            </el-select>
            <el-checkbox v-model="it.required" :true-label="1" :false-label="0">必填</el-checkbox>
            <el-button type="danger" @click="removeItem(idx)">删除</el-button>
          </div>
          <div v-if="it.type===2 || it.type===3" style="margin-top:8px;">
            <el-button size="small" @click="addOption(it)">添加选项</el-button>
            <div v-for="(op, j) in it.options" :key="j" style="display:flex; gap:8px; margin-top:6px;">
              <el-input v-model="op.label" placeholder="选项"/>
              <el-button size="small" type="danger" @click="it.options.splice(j,1)">删</el-button>
            </div>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗（只读） -->
    <el-dialog
        v-model="detailVisible"
        title="问卷详情"
        width="960px"
        append-to-body
        destroy-on-close
        :before-close="onDetailBeforeClose"
        :close-on-click-modal="true"
        :close-on-press-escape="true"
        :z-index="3000">
      <!-- 加载态骨架屏：避免 v-loading 遮罩阻断关闭交互 -->
      <div v-if="detailLoading" style="padding: 12px 4px;">
        <el-skeleton :rows="6" animated/>
      </div>
      <div v-else-if="detail" style="display:flex; gap:16px; align-items:flex-start;">
        <div style="flex:1;">
          <p><b>标题：</b>{{ detail.title }}</p>
          <p><b>截止：</b>{{ detail.deadline || '-' }}</p>
          <el-divider>题目</el-divider>
          <ol>
            <li v-for="i in detail.items" :key="i.id" style="margin-bottom:8px;">
              <div>{{ i.title }} <small v-if="i.required===1" style="color:#f56c6c">(必填)</small>
                <span style="margin-left:8px; color:#909399;">[{{ typeLabel(i.type) }}]</span>
              </div>
              <!-- 仅显示选项，不做统计展示 -->
              <div v-if="i.options && i.options.length>0"
                   style="display:flex; gap:6px; flex-wrap:wrap; margin-top:6px;">
                <el-tag v-for="op in i.options" :key="op.id">{{ op.label }}</el-tag>
              </div>
            </li>
          </ol>
        </div>
        <div style="width:360px;">
          <el-card shadow="never">
            <template #header>
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <span>已提交用户</span>
                <el-tag type="info">{{ submitUsers.length }}</el-tag>
              </div>
            </template>
            <el-scrollbar height="50vh">
              <el-empty v-if="!submitUsers.length" description="暂无提交"/>
              <el-timeline v-else>
                <el-timeline-item v-for="u in submitUsers" :key="u.userId" :timestamp="u.submitTime">
                  <el-link type="primary" @click="viewUserAns(u)">{{ u.nickName || u.userName || '匿名' }}</el-link>
                </el-timeline-item>
              </el-timeline>
            </el-scrollbar>
          </el-card>
        </div>
      </div>
      <template #footer>
        <el-button v-hasPermi="['manage:survey:summary']" type="success" @click="openAiSummary" :disabled="!detail || !detail.id">AI汇总报告</el-button>
        <el-button @click="detailVisible=false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 用户答卷弹窗 -->
    <el-dialog v-model="ansVisible" :title="'用户答卷：' + userAnsName" width="720px" append-to-body :z-index="3600">
      <div v-if="userAns">
        <div v-for="i in userAns.items" :key="i.id" style="margin-bottom:12px;">
          <div><b>{{ i.title }}</b> <small v-if="i.required===1" style="color:#f56c6c">(必填)</small></div>
          <div v-if="i.type===1" style="margin-top:6px; white-space:pre-wrap;">{{
              userAns.myAnswers?.[i.id] || '-'
            }}
          </div>
          <div v-else style="margin-top:6px;">
            <el-tag v-for="oid in (userAns.myAnswers?.[i.id]||[])" :key="oid" style="margin-right:6px;">
              {{ findOptionLabel(i, oid) }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="ansVisible=false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- AI 汇总报告弹窗 -->
    <el-dialog v-model="aiVisible" title="AI汇总报告" width="820px" append-to-body :z-index="3500">
      <div>
        <el-alert type="info" :closable="false" style="margin-bottom:10px;"
                  title="将问卷配置与已提交结果整合为提示词发送给 AI（Zhipu）。需在后端配置环境变量 ZAI_API_KEY。"/>
        <el-input
            v-model="aiExtra"
            type="textarea"
            :rows="3"
            placeholder="可选：补充你的分析目标或格式要求（例如：以 Markdown 输出、加入改进建议清单）"/>
        <div style="margin-top:10px; display:flex; gap:8px; align-items:center;">
          <el-button type="primary" @click="genAiNow" :loading="aiLoading" :disabled="aiLoading">生成</el-button>
          <el-button @click="copyAi" :disabled="!aiText">复制</el-button>
        </div>
        <el-divider/>
        <div v-if="aiLoading" style="padding: 12px 4px;">
          <el-skeleton :rows="8" animated/>
        </div>
        <el-scrollbar v-else height="50vh">
          <pre style="white-space:pre-wrap; font-family: var(--el-font-family);">{{ aiText || '（生成后在此展示 AI 汇总报告）' }}</pre>
        </el-scrollbar>
      </div>
      <template #footer>
        <el-button @click="aiVisible=false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 延期对话框：使用时间选择器 -->
    <el-dialog v-model="extendVisible" title="延期" width="420px" append-to-body :z-index="3000">
      <div>
        <p v-if="extendRow"><b>当前截止：</b>{{ extendRow.deadline || '-' }}</p>
        <el-date-picker v-model="extendDeadline" type="datetime" placeholder="选择新的截止时间"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"
                        :teleported="true" popper-class="manage-extend-popper"/>
        <p style="margin-top:8px; color:#909399;">新截止时间必须晚于当前截止时间</p>
      </div>
      <template #footer>
        <el-button @click="extendVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmExtend">确定</el-button>
      </template>
    </el-dialog>
  </div>

</template>

<script setup>
// listSurvey/getSurvey/addSurvey/publishSurvey/archiveSurvey/extendSurvey/pinSurvey/listSubmittedUsers/getUserAnswers/aiSummary -> /manage/survey/**

// - load：根据视图设置查询条件并分页；save：创建问卷并提交题目；openDetail：并行拉取详情和提交用户列表；genAiNow：生成AI汇总
import {ref, reactive, onMounted, watch} from 'vue'
import {useRoute} from 'vue-router'
import {
  listSurvey,
  getSurvey,
  addSurvey,
  archiveSurvey,
  extendSurvey,
  listSubmittedUsers,
  getUserAnswers,
  pinSurvey,
  publishSurvey
} from '@/api/manage/survey'
import { aiSummary } from '@/api/manage/survey'
import {ElMessageBox, ElMessage} from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({pageNum: 1, pageSize: 10, status: null, title: '', includeExpired: false, expiredOnly: false})
const view = ref('published')
const route = useRoute()

const createVisible = ref(false)
const form = reactive({title: '', deadline: '', visibleAll: 1, items: []})

const detailVisible = ref(false)
const detail = ref(null)
const detailLoading = ref(false)
const submitUsers = ref([])
const userAns = ref(null)
const userAnsName = ref('')
const ansVisible = ref(false)
const extendVisible = ref(false)
const extendDeadline = ref('')
const extendRow = ref(null)

// AI 汇总
const aiVisible = ref(false)
const aiLoading = ref(false)
const aiExtra = ref('')
const aiText = ref('')
const aiTargetId = ref(null)

function applyRouteDefault() {
  // 根据路由区分默认视图：/index -> 已发布；/archive -> 已归档
  if (route.path.endsWith('/archive')) {
    view.value = 'archived'
  } else {
    view.value = 'published'
  }
}

function load() {
  loading.value = true
  // 根据视图设置查询参数
  if (view.value === 'published') {
    query.status = 1;
    query.includeExpired = false;
    query.expiredOnly = false
  } else if (view.value === 'draft') {
    query.status = 0;
    query.includeExpired = true;
    query.expiredOnly = false
  } else if (view.value === 'archived') {
    query.status = 2;
    query.includeExpired = true;
    query.expiredOnly = false
  } else if (view.value === 'expired') {
    query.status = null;
    query.includeExpired = true;
    query.expiredOnly = true
  } else {
    query.status = null;
    query.includeExpired = true;
    query.expiredOnly = false
  }
  listSurvey(query).then(res => {
    list.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => loading.value = false)
}

function openCreate() {
  form.title = ''
  form.deadline = ''
  form.visibleAll = 1
  form.items = []
  createVisible.value = true
}

function addItem(t) {
  form.items.push({title: '', type: t, required: 0, options: []})
}

function removeItem(idx) {
  form.items.splice(idx, 1)
}

function addOption(it) {
  if (!it.options) it.options = []
  it.options.push({label: ''})
}

function save() {
  if (!form.title) return ElMessage.error('请填写标题')
  if (!form.items.length) return ElMessage.error('至少添加一道题')
  for (const it of form.items) {
    if (!it.title || !it.title.trim()) return ElMessage.error('题目提示不能为空')
  }
  // 边界校验：选择题必须至少一个选项，且选项文本不能为空
  for (const it of form.items) {
    if (it.type === 2 || it.type === 3) {
      if (!it.options || it.options.length === 0) return ElMessage.error('选择题至少需要一个选项')
      for (const op of it.options) {
        if (!op.label || !op.label.trim()) return ElMessage.error('选项文本不能为空')
      }
    }
  }
  // 简化：仅传 label，后端会持久化
  addSurvey({
    title: form.title,
    deadline: form.deadline || null,
    visibleAll: form.visibleAll,
    items: form.items.map((x, idx) => ({
      title: x.title, type: x.type, required: x.required, sortNo: idx,
      options: (x.type === 2 || x.type === 3) ? (x.options || []).map((o, k) => ({label: o.label, sortNo: k})) : []
    }))
  }).then(() => {
    ElMessage.success('创建成功')
    createVisible.value = false
    load()
  })
}

function openDetail(row) {
  userAns.value = null
  userAnsName.value = ''
  submitUsers.value = []
  detail.value = {title: '', items: []}
  detailVisible.value = true
  detailLoading.value = true
  const rid = row.id
  // 超时兜底，避免遮罩长时间存在
  const stopLoadingTimer = setTimeout(() => {
    if (detailLoading.value) detailLoading.value = false
  }, 8000)
  getSurvey(rid)
      .then(res => {
        const data = (res && res.data) ? res.data : null
        if (!data) {
          ElMessage.error('未获取到问卷详情')
          detail.value = {title: '', items: []}
        } else {
          const items = Array.isArray(data.items) ? data.items : []
          for (const it of items) {
            if (!Array.isArray(it.options)) it.options = []
          }
          data.items = items
          detail.value = data
        }
      })
      .catch(() => {
        ElMessage.error('无法查看详情：请检查数据或权限')
      })
      .finally(() => {
        clearTimeout(stopLoadingTimer);
        detailLoading.value = false
      })
  // 提交用户列表异步加载
  listSubmittedUsers(rid).then(res => {
    submitUsers.value = res.data || []
  }).catch(() => {
  })
}

function onDetailBeforeClose(done) {
  detailLoading.value = false;
  done && done()
}

function extend(row) {
  extendRow.value = row
  extendDeadline.value = row.deadline || ''
  extendVisible.value = true
}

function archive(row) {
  ElMessageBox.confirm('确认归档后将不可再提交，是否继续？', '提示', {type: 'warning'}).then(() => {
    archiveSurvey(row.id).then(() => {
      ElMessage.success('已归档');
      load()
    })
  })
}

function typeLabel(t) {
  return t === 1 ? '文本' : (t === 2 ? '单选' : '多选')
}

onMounted(() => {
  applyRouteDefault();
  load()
})
watch(() => route.path, () => {
  applyRouteDefault();
  load()
})

function viewUserAns(u) {
  userAnsName.value = u.nickName || u.userName || '匿名'
  getUserAnswers(detail.value.id, u.userId).then(res => {
    userAns.value = res.data;
    ansVisible.value = true
  })
}

function findOptionLabel(item, oid) {
  const op = (item.options || []).find(o => o.id === oid)
  return op ? op.label : oid
}

function togglePin(row) {
  const willPin = !(row.pinned === 1)
  pinSurvey(row.id, willPin).then(() => {
    ElMessage.success(willPin ? '已置顶' : '已取消置顶');
    load()
  })
}

function confirmExtend() {
  if (!extendDeadline.value) return ElMessage.error('请选择新的截止时间')
  if (extendRow.value && extendRow.value.deadline) {
    const oldTs = new Date(extendRow.value.deadline).getTime()
    const newTs = new Date(extendDeadline.value).getTime()
    if (!(newTs > oldTs)) return ElMessage.error('新截止时间必须晚于当前截止时间')
  }
  extendSurvey(extendRow.value.id, extendDeadline.value).then(() => {
    ElMessage.success('已延期')
    extendVisible.value = false
    load()
  })
}

function doPublish(row) {
  publishSurvey(row.id).then(() => {
  ElMessage.success('已发布');
  load()
})
}

function openAiSummary(row) {
  // 支持从列表直接打开，或从详情弹窗打开
  aiExtra.value = ''
  aiText.value = ''
  aiTargetId.value = (row && row.id) ? row.id : (detail.value && detail.value.id)
  if (!aiTargetId.value) {
    ElMessage.error('未获取到问卷ID')
    return
  }
  aiVisible.value = true
}

function genAiNow() {
  if (!aiTargetId.value) {
    return ElMessage.error('未获取到问卷ID')
  }
  aiLoading.value = true
  aiText.value = ''
  aiSummary(aiTargetId.value, aiExtra.value).then(res => {
    aiText.value = (res && res.data && res.data.text) ? res.data.text : ''
    if (!aiText.value) ElMessage.warning('AI 未返回内容，请稍后重试')
  }).catch(e => {
    ElMessage.error((e && e.msg) || '生成失败，请检查后端日志与 ZAI_API_KEY 配置')
  }).finally(() => aiLoading.value = false)
}

function copyAi() {
  if (!aiText.value) return
  if (navigator && navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(aiText.value).then(() => ElMessage.success('已复制'))
        .catch(() => ElMessage.error('复制失败'))
  } else {
    // 兜底
    try {
      const ta = document.createElement('textarea')
      ta.value = aiText.value
      document.body.appendChild(ta)
      ta.select()
      document.execCommand('copy')
      document.body.removeChild(ta)
      ElMessage.success('已复制')
    } catch (e) {
      ElMessage.error('复制失败')
    }
  }
}
</script>

<style>
/* 提升延期弹窗内日期选择器的浮层层级，避免被 Dialog 遮挡 */
.manage-extend-popper {
  z-index: 4000 !important;
}
</style>
