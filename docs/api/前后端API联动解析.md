# 目标与结构

**目标**：通过两个“点击按钮提交表单”的完整链路，掌握门户侧与管理侧 API 的前后端调用逻辑及数据库落库流程。

**场景：**
- **门户侧**：功能房预约提交  
- **管理侧**：课程资源编辑保存/下架  

**重点：**  
前端视角（表单组件、事件处理、API 封装、鉴权拦截器、请求与分页对齐）  
后端视角（Controller 路由、权限、Service 事务与校验、统一响应）

文中附真实代码路径（可点击），并尽量对齐 **RuoYi** 规范。

---

## 快速定位指南：从按钮到后端（3 步走）

> 目标：从“页面上点一个按钮”，用最少的动作定位到其前端实现、API 封装与后端 Controller/Service/Mapper。

### 第 1 步：浏览器直接取证（最快）
- 打开 DevTools → Network 面板，点击按钮，记录：`Method`、`URL`、`Status`、`Request Payload`。
- 如按钮没有触发请求，切换 Console，查看点击堆栈中的 `.vue` 文件路径与函数名（常见如 `handleAdd/handleUpdate/submitForm/handleDelete`）。
- 若按钮没有文本而是图标/下拉项，先看元素的 `title/aria-label` 或同一工具栏旁的权限标识（见第 2 步）。

### 第 2 步：前端定位（.vue 与 /src/api）
- 依据“按钮文案/权限码/事件名”三选一进行代码搜索：
  - 按钮文案（最直观）：
    - `rg -n "新增|编辑|删除|下架|提交|保存" frontend/src`
  - 权限码（最稳定，贯通前后端）：
    - `rg -n "v-hasPermi=\\['manage:[a-zA-Z0-9:-]+\\]' frontend/src`
    - 已知模块时：`rg -n "manage:courseResource:(list|add|edit|remove|offline)" frontend/src`
  - 事件名（高频约定）：
    - `rg -n "@click=\\\"(handle|submit|on)[A-Z]" frontend/src`
- 在命中的 `.vue` 打开：
  - 找 `@click="xxx"` 的方法定义位置（`setup()`/`methods`）。
  - 查看是否从 `@/api/<module>` 引入函数（如 `import { addResource } from '@/api/manage/courseResource'`）。
  - 跳到 `frontend/src/api/<module>.ts|js` 对应导出函数，确认 `url/method/params`。

常见约定（RuoYi）
- 视图：`src/views/**/index.vue`（管理侧列表页），或模块分屏如 `approved.vue/pending.vue`。
- API：`src/api/<module>.ts`，方法名与后端语义一致：`list/get/add/update/remove/approve/offline`。
- 按钮权限：`v-hasPermi="['manage:<entity>:<op>']"`，示例 `manage:courseResource:edit`。

### 第 3 步：后端定位（Controller → Service → Mapper）
- 用第 1 步拿到的 URL 直接搜 Controller 路由：
  - `rg -n "@RequestMapping\("/manage/[^"]+"\)" backend/ruoyi-manage`
  - 或精确到模块：`rg -n "@RequestMapping\("/manage/courseResource"\)" backend/ruoyi-manage`
- 用权限码（与前端一致）直达具体方法：
  - `rg -n "hasPermi\('manage:courseResource:(list|add|edit|remove|offline)'\)" backend/ruoyi-manage`
- 打开 Controller 方法（`@GetMapping/@PostMapping/@PutMapping/@DeleteMapping`）：
  - 记下方法名，进入对应 `I*Service`/`*ServiceImpl`。
  - 如需看 SQL，继续进 `*Mapper.java` 与 `resources/mapper/**.xml`。

后端目录与约定（RuoYi）
- 仅改动业务模块：`backend/ruoyi-manage`
- 典型包：`controller`、`service`（`INameService/NameServiceImpl`）、`mapper`（`NameMapper.java` + `resources/mapper/**.xml`）、`domain`（实体/VO/查询对象）。
- 响应：`AjaxResult/TableDataInfo`；权限：`@PreAuthorize("@ss.hasPermi('manage:<entity>:<op>')")`。

### “一把梭”两条捷径
- 用“权限码”串起前后端：
  - 前端按钮：`v-hasPermi="['manage:xxx:op']"` → 搜同权限码即可在前端与后端同时命中。
  - 命令：`rg -n "manage:courseResource:edit" frontend backend/ruoyi-manage`
- 用“Network URL”直入后端：
  - 复制 `/manage/<entity>/**` → 在 `ruoyi-manage` 搜 `@RequestMapping("/manage/<entity>")` 与具体 `@*Mapping`。

### 常见变体与排错
- 工具栏按钮没有文案：看同行的 `v-hasPermi` 或 Tooltip；或搜 `RightToolbar`/`DictTag` 之类通用组件。
- 批量操作：`handleSelectionChange/handleDelete`，后端多为 `DELETE /{ids}`。
- 导出/导入：`/export`、`/importData`，有时带 `headers: { isToken: true, responseType: 'blob' }`。
- 分页参数：通常为 `pageNum/pageSize`；注意前后端对齐，Network 面板可直观确认。

### 命令速查（复制即用）
- 前端：
  - 按钮文案：`rg -n "新增|编辑|删除|下架|提交|保存" frontend/src`
  - 权限码：`rg -n "v-hasPermi=\\['manage:[a-zA-Z0-9:-]+\\]' frontend/src`
  - 事件名：`rg -n "@click=\\\"(handle|submit|on)[A-Z]" frontend/src`
  - API Path：`rg -n "url:\\s*'?/manage/" frontend/src/api`
- 后端：
  - 模块路由：`rg -n "@RequestMapping\("/manage/[^"]+"\)" backend/ruoyi-manage`
  - 权限码：`rg -n "hasPermi\('manage:[a-zA-Z0-9:-]+'\)" backend/ruoyi-manage`
  - 指定模块：`rg -n "CourseResourceController|ICourseResourceService|CourseResourceMapper" backend/ruoyi-manage`

---

## 通用总览（前后端如何“握手”）

### 前端请求基址
- 开发环境：`VITE_APP_BASE_API = '/dev-api'`，由 Vite 代理到后端 `http://localhost:8080`  
  （见 `frontend/vite.config.js (line 1)`、`frontend/.env.development (line 9)`）
- 生产环境：`/prod-api`，由 Nginx 反代到后端。

### 统一请求封装
所有 API 统一经过 axios 实例与拦截器处理：
- 自动携带 `Authorization: Bearer <token>`
- GET 参数序列化、防重复提交、统一错误提示  
  （`frontend/src/utils/request.js (line 14, 23, 74)`）

Token 存储在 Cookie（`frontend/src/utils/auth.js (line 3)`），用户信息存于 Pinia（`frontend/src/store/modules/user.js (line 10)`）。

### 鉴权与权限对齐
**后端**
- 管理侧：`@PreAuthorize("@ss.hasPermi('manage:<module>:<op>')")`
- 门户侧：`@PreAuthorize("isAuthenticated()")`

**前端**
- 请求拦截器统一注入 Authorization
- 按钮权限：`v-hasPermi="['manage:xxx:op']"`
- 路由与后端菜单权限对齐

### 统一响应结构
```json
列表: { code: 200, msg: "成功", rows: [...], total: 123 }
详情: { code: 200, msg: "成功", data: {...} }
操作: { code: 200, msg: "成功" }
````

前端拦截器（`frontend/src/utils/request.js (line 74)`）直接解析成功响应，错误时按 code 弹提示。

---

## 门户侧示例：功能房预约（点击“提交”按钮）

### 视图与按钮事件

* 页面：`frontend/src/views/portal/facility/Detail.vue (line 46)`
* 按钮：`<el-button type="primary" @click="submit">提交</el-button>`
* 表单模型：`(line 19)` 包含 `purpose、startTime、endTime、userIds`
* 提交逻辑：`(line 87)`
  校验 → 构造请求体 → 调用 `createBooking(data)` → 成功提示与刷新时间轴

### 门户侧 API 封装

`frontend/src/api/portal/facility.js`

```js
createBooking(data) → POST /portal/facility/booking
getRoom(id) → GET /portal/facility/room/{id}
getTimeline(id, params) → GET /portal/facility/room/{id}/timeline
myBookings() → GET /portal/facility/booking/my/list

```
开发环境通过 Vite 代理 `/dev-api/portal/...` → `http://localhost:8080/portal/...`

### 后端 Controller

`PortalFacilityController.java`

* 路由：`/portal/facility`
* 新建预约：`@PostMapping("/booking")`
* 鉴权：`@PreAuthorize("isAuthenticated()")`
* 入参：`FacilityBookingCreateRequest`
* 逻辑：`bookingService.create(...)` → `AjaxResult(toAjax(n))`

分页统一用 `startPage()` 与 `getDataTable(list)`。

### Service 层（校验与事务）

`FacilityBookingServiceImpl.java`

* 校验：房间状态、封禁状态、时间段冲突、使用人合法性
* 成功后插入预约与参与人明细
* `@Transactional` 确保一致性
* 异常：`ServiceException("时间段冲突...")` → 前端统一弹出

### Apifox 验证

* 登录获取 token：`POST /login`
* Header：`Authorization: Bearer {{token}}`
* 创建预约：

```json
POST /portal/facility/booking
{
  "roomId":1,
  "purpose":"课程讨论",
  "startTime":"2025-10-20 19:00:00",
  "endTime":"2025-10-20 21:00:00",
  "userIdList":[110,111,112]
}
```

断言：`code == 200`

---

## 管理侧示例：课程资源编辑保存/下架

### 页面与按钮事件

* 页面：`frontend/src/views/manage/courseResource/approved.vue`
* 分页：`queryParams.pageNum/pageSize` 双向绑定
* 编辑：`submitForm()` → `updateResource(form)`
* 下架：`submitOffline()` → `offlineResource(id, reason)`
* 按钮权限：

  * 编辑：`v-hasPermi="['manage:courseResource:edit']"`
  * 下架：`v-hasPermi="['manage:courseResource:offline']"`

### 管理侧 API 封装

`frontend/src/api/manage/courseResource.js`

```js
listResource → GET /manage/courseResource/list
getResource → GET /manage/courseResource/{id}
updateResource → PUT /manage/courseResource
offlineResource → PUT /manage/courseResource/{id}/offline
```

部分统计接口使用 `headers: { silent: true }` 抑制弹窗。

### 后端 Controller

`CourseResourceController.java`

* 路由：`/manage/courseResource`
* 编辑：`PUT /manage/courseResource`
* 权限：`manage:courseResource:edit`
* 下架：`PUT /manage/courseResource/{id}/offline`
* 权限：`manage:courseResource:offline`
* 所有方法返回 `AjaxResult` 或 `TableDataInfo`

### Service 层

`ICourseResourceService.java`

* `update(...)`：普通用户仅能改本人资源；已通过需下架再改
* `offline(...)`：校验操作者身份，记录原因，控制状态流转
* `delete(...)`：管理员/负责人可删任意，普通用户删本人且状态限制
* 职责：Controller 做路由与权限，Service 做业务规则与事务。

### Apifox 验证

* 登录：`POST /login` 获取 token
* 编辑资源：

```json
PUT /manage/courseResource
{
  "id":123,
  "resourceName":"新标题",
  "description":"新简介"
}
```

* 下架：

```json
PUT /manage/courseResource/{id}/offline
{
  "reason":"内容需调整"
}
```

* 断言：`code == 200`

---

## 前端深讲（关键实践）

### 表单与校验

* `<el-form :model="form" :rules="rules">`
* `formRef.validate(valid => ...)` 提交
* 校验通过 → 调 API → 成功提示与刷新列表

### API 模块组织

* 位置：`frontend/src/api/<模块>.js`
* 每个函数仅负责 axios 请求定义，UI 逻辑独立。

### axios 拦截器

* 注入 token：`Authorization: Bearer <token>`
* GET 参数序列化
* 防重复提交：1 秒窗口内检查相同请求
* 统一错误处理（401 重登，500 弹消息，`silent=true` 静默）

### 权限与菜单

* 按钮：`v-hasPermi="['manage:module:op']"`
* 路由与后端菜单权限完全一致。

### 分页参数

* 约定字段：`pageNum / pageSize`
* 后端：`startPage() + getDataTable(list)`
* 前端 pagination 组件双向绑定页码与大小。

### 文件下载与上传

* 下载：`window.open(${BASE}/path?token=${token})` 或 `request.download()`
* 上传：`el-upload` headers 写入 Authorization。

---

## 后端简述（按 RuoYi 规范）

### 目录结构

`controller / service(impl) / mapper(xml) / domain / vo`

### Controller

* 路由：`/manage/<entity>` 或 `/portal/<module>`
* 权限：`@PreAuthorize("@ss.hasPermi('manage:<entity>:<op>')")`
* 分页：`startPage()` + `getDataTable(list)`
* 响应：统一 `AjaxResult`

### Service

* 写操作加 `@Transactional`
* 并发与校验逻辑集中在 Service
* 抛出 `ServiceException` 返回友好提示
* 审计字段：`create_by / update_by / time / del_flag`

### Mapper

* 专注数据读写
* 复杂查询返回 VO
* 状态流转由 Service 控制

---

## 前后端“对齐清单”

| 项目     | 约定                                                                                                                                                                                                                     |        |        |         |      |         |
| ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------ | ------ | ------- | ---- | ------- |
| URL 命名 | 列表 GET `/manage/<entity>/list` <br> 详情 GET `/manage/<entity>/{id}` <br> 新增 POST `/manage/<entity>` <br> 修改 PUT `/manage/<entity>` <br> 删除 DELETE `/manage/<entity>/{ids}` <br> 语义动作 PUT `/manage/<entity>/{id}/approve | reject | online | offline | best | unbest` |
| 权限标识   | 前端 `v-hasPermi="['manage:xxx:op']"` 与后端 `@PreAuthorize("@ss.hasPermi('manage:xxx:op')")` 完全一致                                                                                                                          |        |        |         |      |         |
| 分页     | 参数：`pageNum/pageSize`；附加查询条件 query string                                                                                                                                                                              |        |        |         |      |         |
| 鉴权与跨域  | Header：`Authorization: Bearer <token>`；Vite 代理 `/dev-api`                                                                                                                                                              |        |        |         |      |         |
| 错误提示   | Service 抛 `ServiceException` → 统一处理为 `{code:500,msg:...}` → 前端弹窗                                                                                                                                                       |        |        |         |      |         |
| 下载认证   | 若用 `window.open`，带 token 参数；或 `request.download()` 处理 Blob 流                                                                                                                                                           |        |        |         |      |         |

---

