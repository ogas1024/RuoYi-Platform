### 总原则
- 新建文件编码统一为 UTF-8（无 BOM），沟通、注释与文档统一使用中文。
- 以模块为单位快速交付，做到可运行、可测试、可提交。
- 你的个性化要求优先：小白友好、逐模块推进、你先建表、我生成代码但遵循 RuoYi 规范。
- 后端仅改动 `backend/ruoyi-manage`；前端仅改动 `frontend`；全部 SQL 写到根目录 `book-mis.sql`，表名均以 `tb_` 前缀。
- 技术栈：**Ruoyi-Vue 框架 + Spring Boot 3 + Vue3 + Element-Plus + MySQL + Redis + 阿里云 OSS**（但尽量弱化对 RuoYi 的强依赖；代码由代理生成、遵循 RuoYi 目录与风格）如需任何 RuoYi 后台手动操作，我提供详细教程。
- 系统环境为CachyOS

### 角色分工
- 你（学生）：给出需求、确认边界、创建表（我先出表设计草案）、在 Apifox 手工验证、按教程在 RuoYi 后台配置菜单/权限、执行 `git commit`。
- 我（Agent）：需求澄清、产出表结构草案、生成 API 文档、编写前后端代码与测试、撰写 Apifox 测试指南与模块报告、准备提交信息。

### 仓库结构与运行
- 后端 `backend/`：RuoYi Maven 多模块。仅在 `backend/ruoyi-manage` 编写业务代码。核心配置在 `ruoyi-admin/src/main/resources/application.yml`、`application-druid.yml`。
- 前端 `frontend/`：Vite + Vue3 + Element‑Plus。代码集中 `frontend/src` 与 `frontend/src/api`。
- 文档与数据：设计文档 `docs/requirements/`；API 文档 `docs/api/`；Apifox 指南 `docs/apifox/<模块>.md`；模块报告 `docs/report/<模块>.md`；SQL `book-mis.sql`（含建表、注释、示例数据）。
- 构建与运行:
  - 后端：`cd backend && mvn -T 1C clean package -DskipTests`；开发运行 `mvn -f backend/pom.xml spring-boot:run -pl ruoyi-admin -am` 或 `java -jar backend/ruoyi-admin/target/ruoyi-admin.jar`
  - 前端：`cd frontend && pnpm i && pnpm run dev`；构建 `pnpm run build:prod`；预览 `pnpm run preview`

### ruoyi框架相关说明
- 在 `backend/ruoyi-manage` 中, 已有的代码是老师给的示例代码, 依托于ruoyi框架实现了一个较为完善的网上书店模块. 可以参考老师给的实现, 学习如何对ruoyi框架进行利用
- 尽量弱化对 RuoYi 的强依赖；代码由AI生成、遵循 RuoYi 目录与风格）如需任何 RuoYi 后台手动操作，我提供详细教程。

### 模块化工作流（小白友好）
1. 需求对齐（必须）：根据 `docs/requirements/总体设计.md` 与你逐条确认功能、角色权限、边界用例和 MVP 范围, 用户使用流程，冻结本模块清单。与你讨论确认后, 将该模块的具体设计输出在 `docs/requirements/<模块>.md`
2. 表结构草案：我基于需求给出 `tb_` 前缀的表设计（含字段、索引、约束、审计字段与备注），写入 `book-mis.sql` 草案文件**尾部**；你确认后再由你执行建表。务必与我确认后进行下一步!!!
3. API 文档：依据 `docs/requirements/<模块>.md` 生成 `docs/api/<模块>.md`（路径、参数、响应、错误码、示例）, 需要有良好的markdown格式api文档实践；同步输出 Apifox 导入建议。
4. 任务编排：列出细粒度任务清单（后端接口/Service/Mapper/Domain、前端页面/组件/接口方法、权限与菜单、上传与 OSS、Redis 缓存等），逐项打勾完成。
5. 编码实现：
  - 后端仅在 `ruoyi-manage`：Controller/Service/Mapper/Domain 按 RuoYi 目录规范，命名与权限标识统一。
  - 前端在 `frontend/src` 中, 重点完善 `api` 与 `views` ：视图、组件、接口封装、状态与错误提示。
6. 测试设计：
  - 后端在 `ruoyi-manage/src/test/java`：每个接口至少覆盖 成功/失败/权限 三类场景。
  - 前端可写接口用例或以手工验证为主，在 PR/报告中列出用例。
7. Apifox 指南：在 `docs/apifox/<模块>.md` 写明环境 `baseURL=http://localhost:8080`、`Authorization: <token>`、接口示例与断言等必要步骤。
8. 自检与走查：对照设计与 API 文档逐项核对，补齐边界用例。
9. 提交：我给出 `feat|fix|docs|refactor|chore(<范围>): <说明>` 的提交信息，你执行 `git commit`。
10. 模块报告：在 `docs/report/<模块>.md` 说明领域模型、流程、权限、已知限制与后续计划。
11. 准备下一个模块：按优先级推进。

### Definition of Done（每个模块的完成标准）
- 表已创建（`book-mis.sql` 中含注释与示例数据）。
- API 文档与 Apifox 指南齐全、可用。
- 后端接口与权限可用，前端页面可操作。
- 测试通过（至少后端三类场景）。
- 报告完成，提交信息规范。

### MCP 调用规则

- 为 Codex 提供3项 MCP 服务（Sequential Thinking、Context7）的选择与调用规范，控制查询粒度、速率与输出格式，保证可追溯与安全。

#### 全局策略

- 工具选择：根据任务意图选择最匹配的 MCP 服务；避免无意义并发调用。
- 结果可靠性：默认返回精简要点 + 必要引用来源；标注时间与局限。
- 单轮单工具：每轮对话最多调用 1 种外部服务；确需多种时串行并说明理由。
- 最小必要：收敛查询范围（tokens/结果数/时间窗/关键词），避免过度抓取与噪声。
- 可追溯性：统一在答复末尾追加“工具调用简报”（工具、输入摘要、参数、时间、来源/重试）。
- 安全合规：默认离线优先；外呼须遵守 robots/ToS 与隐私要求，必要时先征得授权。
- 降级优先：失败按“失败与降级”执行，无法外呼时提供本地保守答案并标注不确定性。
- 冲突处理：遵循“冲突与优先级”的顺序，出现冲突时采取更保守策略。


#### Sequential Thinking（规划分解）

- 触发：分解复杂问题、规划步骤、生成执行计划、评估方案。
- 输入：简要问题、目标、约束；限制步骤数与深度。
- 输出：仅产出可执行计划与里程碑，不暴露中间推理细节。
- 约束：步骤上限 6-10；每步一句话；可附工具或数据依赖的占位符。


#### Context7（技术文档知识聚合）

- 触发：查询 SDK/API/框架官方文档、快速知识提要、参数示例片段。
- 流程：先 resolve-library-id；确认最相关库；再 get-library-docs。
- 主题与查询：提供 topic/关键词聚焦；tokens 默认 5000，按需下调以避免冗长（示例 topic：hooks、routing、auth）。
- 筛选：多库匹配时优先信任度高与覆盖度高者；歧义时请求澄清或说明选择理由。
- 输出：精炼答案 + 引用文档段落链接或出处标识；标注库 ID/版本；给出关键片段摘要与定位（标题/段落/路径）；避免大段复制。
- 限制：网络受限或未授权不调用；遵守许可与引用规范。
- 失败与回退：无法 resolve 或无结果时，请求澄清或基于本地经验给出保守答案并标注不确定性。
- 无 Key 策略：可直接调用；若限流则提示并降级到 DuckDuckGo（优先官方站点）。

### 编码规范（对齐 RuoYi 习惯，弱化代码生成依赖）
- 后端（Java，4 空格）：
  - 包结构：`controller`、`domain`、`mapper`（`*Mapper.java` + `resources/mapper/**.xml`）、`service`（`INameService` + `NameServiceImpl`）
  - Controller 路由：`@RequestMapping("/manage/<entity>")`；方法命名 `list/getInfo/add/edit/remove`；分页入参沿用 RuoYi 通用 Page。
  - 权限标识：`@PreAuthorize("@ss.hasPermi('manage:<entity>:<op>')")`，如 `manage:resource:list`
  - 审计字段：`create_by/create_time/update_by/update_time/del_flag`；软删使用 `del_flag`。
  - 事务：写操作加 `@Transactional`；并发点加锁或唯一约束，必要时加幂等键。
  - 错误码：对齐 RuoYi 统一响应结构，领域错误使用明确提示。
- 前端（Vue 3，2 空格）：
  - 组件 PascalCase；路由页面置于 `src/views/**/index.vue`；通用组件 `src/components/*`
  - API 封装：`src/api/<module>.ts`，方法名 `list/get/add/update/remove` 对应后端；统一异常提示。
  - 环境：；鉴权通过 RuoYi 内置 token 头。
- API 路由（示例）：
  - GET `/manage/<entity>/list`（分页）
  - GET `/manage/<entity>/{id}`（详情）
  - POST `/manage/<entity>`（新增）
  - PUT `/manage/<entity>`（修改）
  - DELETE `/manage/<entity>/{ids}`（删除，支持逗号分隔）
  - 扩展动作用语义化动词：如 `PUT /manage/resource/{id}/approve`、`POST /manage/upload/oss`

### 数据库规范（全部写在 book-mis.sql）
- 表名：统一 `tb_<模块>_<实体>`，如 `tb_course_resource`
- 字段：小写下划线；必含审计字段与 `del_flag`；主键 `id` BIGINT
- 约束：必要的唯一索引（如 ISBN、上传记录幂等键）；外键可用逻辑约束 + 索引替代
- 注释：表注释与字段注释必须完整；附最小示例数据
- 变更：每次变更以注释块标明日期、模块与变更说明
- 示例: 
```sql
-- tb_book: 存储图书基础信息（id, title, author, price, stock, status）
CREATE TABLE tb_book (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  title VARCHAR(255) NOT NULL COMMENT '书名',
  author VARCHAR(128) NOT NULL COMMENT '作者名',
  price DECIMAL(10,2) NOT NULL COMMENT '价格，元',
  stock INT NOT NULL DEFAULT 0 COMMENT '库存',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '上架状态:0-草稿 1-上架 2-下架',
  seller_id BIGINT NOT NULL COMMENT '卖家ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';
```

### 权限与角色（至少 3 角色，4 模块）
- 角色集合（建议映射 RuoYi 角色）：
  - 普通用户（user）
  - 学生工作者（staff）
  - 图书管理员（librarian）
  - 专业负责人（major_lead，十个专业各一）
  - 管理员（admin）
  - 超级管理员（super_admin）
- 权限标识约定：`manage:<module>:<op>`，按钮级权限与后端一致；菜单路由与视图一一对应。
- 典型操作映射：
  - 专业负责人：本专业资源审核与“最佳推荐”；拥有本专业资源的增删改查
  - 图书管理员：数字图书馆完整增删改查
  - 学生工作者：发布/删除自己公告
  - 管理员：所有模块的增删改查
  - 超级管理员：系统开关与用户管理

### RuoYi 后台操作教程

若需在 RuoYi 后台做菜单/权限设置，代理会生成一步步操作说明, 输出在 `docs/ruoyi-opt` 下，例如：

1. 登录 RuoYi 后台（管理员账号）。  
2. 系统管理 → 菜单管理 → 点击“新增”  
   - 菜单名称：图书管理  
   - 路由地址：`/manage/book`  
   - 权限标识：`manage:book:list`  
3. 系统管理 → 角色管理 → 选择 Seller → 分配 `manage:book:add,manage:book:update,manage:book:remove`  
4. 用户管理 → 给测试用户绑定 Seller 角色。  

代理会根据你提供的截图或实际页面版本调整具体字段说明

- 新增目录：
  - 系统管理 → 菜单管理 → 新增
  - 菜单类型：目录；
  - 菜单名称
  - 路由地址: 
- 新增菜单：
  - 系统管理 → 菜单管理 → 新增
  - 菜单类型：菜单；
  - 菜单名称
  - 路由路径与前端路由一致
  - 权限标识：如 `manage:resource:list`、`manage:resource:add`
- 新增按钮：
  - 系统管理 → 菜单管理 → 新增
  - 菜单类型：按钮；
  - 菜单名称
  - 权限标识：如 `manage:resource:list`、`manage:resource:add`
- 角色授权：
  - 系统管理 → 角色管理 → 新增/编辑角色 → 分配菜单
  - 用户管理 → 分配角色
- 常见校验：
  - 前端按钮 `v-hasPermi="['manage:resource:add']"` 与后端 `@PreAuthorize` 一致
  - 角色菜单勾选后重新登录生效

### 文档模板与示例
#### API 文档模板（`docs/api/<模块>.md`）
- 概览：业务说明、角色权限、依赖表
- 路由清单：路径/方法/权限/请求/响应/错误码
- 示例：请求与响应 JSON
- 备注：分页/排序/过滤约定，幂等等
- 示例段落:
```markdown
### 接口：列表
- 方法：GET
- 路径：/manage/book/list
- 权限：manage:book:list
- 请求参数（query）：
  - page (int) 必需
  - size (int) 必需
  - keyword (string) 可选
- 返回示例：
\`\`\`json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "total": 123,
    "items": [{ "id":1, "title":"..." }]
  }
}
\`\`\`

* 常见错误码：

  * 401 未授权
  * 400 参数错误：page 必需

### 接口：新增图书

...

```

#### Apifox 指南模板（`docs/apifox-<模块>.md`）
- 固定说明：
    - baseURL: `http://localhost:8080`
    - 请求头示例：
        - `Authorization: Bearer <token>`（说明如何获取 token：登录接口）
    - 每个接口写明：
        1. 环境选择（local / dev）  
        2. 前置步骤（如需先新增分类）  
        3. 请求示例（Headers + Body）  
        4. 断言（状态码 / 响应字段 / 数据库校验手动步骤）  
        5. 常见失败点与解决办法

- 示例段落：
```markdown

步骤 1：获取 token
POST /auth/login
Body: { "username": "admin", "password": "123456" }
断言：code == 200, 响应 data.token 不为空

步骤 2：新增图书
POST /manage/book
Headers: Authorization: Bearer {{token}}
Body: { "title":"测试书", "author":"作者", "price":10.00, "stock":10 }
断言：code == 200, 返回 data.id 存在

```
#### 模块报告模板（`docs/report/<模块>.md`）
使用大量的逻辑清晰的mermaid图标来辅助描述
- 领域模型：实体关系图/表说明
- 关键流程：时序或状态流转
- 权限说明：角色-操作矩阵
- 已知限制与风险：后续迭代项

### 关键技术约定
- OSS 上传（阿里云）：后端提供 `POST /manage/upload/oss`，返回 URL；配置置于本地私密文件或环境变量，不提交密钥
- Redis：缓存排行榜与热点列表；统一 key 前缀如 `app:<module>:<scene>`
- 并发与事务：预约/投票等关键路径使用事务与唯一约束；必要时加悲观或乐观锁
- 大文件：限制大小与类型（压缩包/图片/电子书），后端与 Nginx/网关一致设置
- 审计与日志：重要操作打审计日志，便于统计与追责

### 实现优先级（MVP 颗粒度）
- 课程资源分享 >> 通知公告 >> 功能房预约 >>> 数字图书馆 > 失物招领 >>>>>>>> 投票 > 问卷 > 事件日历 >>>>>>>>>>>>>>>>>>>>>>> 帖子
- 每个模块优先实现：最少可用列表/上传或发布/审核或状态流转/排行榜或统计；其余高级能力排期跟进

### 版本与兼容性
- 当前 RuoYi 版本通常基于 Spring Boot 2.5.x。你更熟悉 Spring Boot 3。
- 建议：短期维持现有版本以保证按期交付；如需升级到 Boot 3，另开分支评估 Jakarta 包迁移、依赖兼容与测试成本。

### Git 规范
本项目遵循一套标准化的 Git 工作流和提交规范，以确保代码库的清晰、可维护，并支持高效的开发与回滚。所有代码提交都必须严格遵守本指南。

#### 1. 提交标准 (Can I Commit?)

只有在满足以下**至少一个**条件时，你才应该创建一个新的 Commit：

1.  **功能原子性**: 完成了一个最小但完整的功能单元或逻辑修改。例如，实现了一个 API 端点，或完成了一个 UI 组件的布局。禁止将多个无关的功能混在一个 Commit 中。
2.  **Bug 修复**: 成功修复了一个明确的 Bug。
3.  **代码重构**: 在不改变外部行为的前提下，改进了现有代码的结构、可读性或性能。
4.  **初始生成**: 完整生成了一个新模块或组件的“毛坯”代码，以此作为后续修改的干净起点。
5.  **测试与文档**: 添加了新的测试用例，或更新了项目文档。

**核心原则**：确保每一次提交后，代码库都处于一个稳定、可运行的状态。绝不提交无法通过编译或会破坏核心功能的半成品代码。


#### 2. 提交规范 (Commit Convention)

所有 Commit Message 必须遵循 **Conventional Commits** 格式。

##### 格式模板:

```markdown
feat(auth): 实现刷新令牌机制并新增用户配置接口

### 动机
旧的 JWT 认证机制使用了永不过期的令牌，存在安全风险。本次变更引入了访问令牌（Access Token）与刷新令牌（Refresh Token）的分离与轮换策略，以增强系统安全性。同时，新增了前端急需的用户个人资料获取接口。

### 变更详情
- **新增 (Added):**
  - 新增了 `POST /api/auth/refresh` 接口，用于刷新即将过期的访问令牌。
  - 新增了 `GET /api/auth/profile` 接口，用于获取当前登录用户的个人信息。
  - 为 `User` 模型增加了 `refreshToken` 字段，并创建了相应的数据库迁移文件。

- **修改 (Modified):**
  - 修改了 `POST /api/auth/login` 接口的响应结构，现在返回包含 `accessToken` 和 `refreshToken` 的对象。
  - 更新了认证中间件，使其只验证 `accessToken` 的有效性。

- **移除 (Removed):**
  - 移除了已废弃的 `generateLegacyToken()` 工具函数及其相关逻辑。

### 附注
客户端现在需要安全地存储 `refreshToken`（建议使用 HttpOnly Cookie），并在 `accessToken` 过期时调用刷新接口以获取新的访问令牌。

BREAKING CHANGE: 登录接口 `/api/auth/login` 的响应体结构发生改变。旧的响应 `{ "token": "..." }` 已被新的响应 `{ "accessToken": "...", "refreshToken": "..." }` 替代。所有客户端必须更新以适配新的数据结构。
Closes: #42
```

commit message body需要通过 `git diff` 来获取差异并总结

##### 字段说明:

* **`<type>` (必须)**: 提交类型，必须是以下之一：
    * `feat`: 新增功能。
    * `fix`: 修复 Bug。
    * `refactor`: 代码重构（不增加新功能，也不修复 Bug）。
    * `test`: 增加或修改测试用例。
    * `docs`: 只修改了文档。
    * `style`: 修改代码格式，不影响代码逻辑（如：空格、格式化）。
    * `chore`: 项目配置、构建流程或辅助工具的变动。
    * `perf`: 提升性能的修改。

* **`<scope>` (必须)**: 本次提交影响的模块或范围。应使用名词，如 `user`, `book`, `order`, `database`, `auth` 等。

* **`<subject>` (必须)**: 对本次提交的简短、清晰的描述，不超过 50 个字符，使用现在时态，动词开头。

##### 示例:

* **新增功能**: `feat(user): 实现用户JWT登录认证接口`
* **修复 Bug**: `fix(book): 修复图书列表分页总数错误的问题`
* **代码重构**: `refactor(order): 优化订单创建服务的代码结构`
* **添加测试**: `test(user): 为用户注册服务添加单元测试`

---

#### 3. Git 开发工作流 (Git Workflow)

##### 分支策略:
* `main`: 生产分支，受保护。
* `dev`: 主开发分支，用于集成所有功能。
* `feature/<模块>/<功能描述>`: 功能开发分支。
* `fix/<模块>/<问题描述>`: Bug 修复分支。

##### 标准开发流程:

1.  **同步最新代码**:
    ```bash
    git checkout dev
    git pull origin dev
    ```

2.  **创建功能分支**: 从 `dev` 分支创建新的 `feature` 分支。分支命名需清晰地描述其目的。
    ```bash
    # 示例：开发用户认证功能
    git checkout -b feature/user/authentication
    ```

3.  **编码与提交**: 在功能分支上进行开发。遵循上述的**提交标准**和**提交规范**，进行多次、原子性的提交。
    ```bash
    # ... 进行代码修改 ...
    git add .
    git commit -m "feat(user): 添加用户注册API的基础路由和控制器"
    
    # ... 继续修改 ...
    git add .
    git commit -m "feat(user): 集成bcrypt进行密码加密存储"
    ```

4.  **完成功能开发**: 功能开发完毕且自测通过后，准备合并。

5.  **合并回 `dev` 分支**:
    ```bash
    # 切换回 dev 分支
    git checkout dev
    
    # 拉取最新的 dev 代码，以防冲突
    git pull origin dev
    
    # 合并你的功能分支 (使用 --no-ff 保留分支历史)
    git merge --no-ff feature/user/authentication
    
    # 推送到远程
    git push origin dev
    ```

6.  **删除已合并的分支** (可选，保持整洁):
    ```bash
    git branch -d feature/user/authentication
    git push origin --delete feature/user/authentication
    ```

### 常见陷阱与提醒
- 先有表后有代码：未建表先别写 Mapper
- 权限串前后端一致：否则按钮显示异常或 403
- OSS/Redis 凭据不入库不入 Git
- 分页与排序参数对齐：避免前后端不一致
- 并发与唯一约束：预约/投票必须落库校验