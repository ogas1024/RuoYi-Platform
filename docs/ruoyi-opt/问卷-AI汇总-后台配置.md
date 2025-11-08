# RuoYi 后台操作：问卷管理新增“AI 汇总报告”按钮权限

> 目标：在管理端问卷详情页显示“AI汇总报告”按钮，并确保有权限的角色才能调用 `POST /manage/survey/{id}/ai-summary`。

- 前提：后端已上线本功能；前端已更新（按钮使用 `v-hasPermi=['manage:survey:summary']`）。

## 步骤 1：新增按钮权限
- 进入：系统管理 → 菜单管理 → 搜索并选中“问卷管理”对应的菜单节点（与 `frontend/src/views/manage/survey/List.vue` 路由一致）。
- 点击“新增” → 选择“按钮”。
- 建议配置：
  - 菜单名称：AI汇总报告
  - 权限标识：`manage:survey:summary`
  - 显示排序：按需
  - 其它保持默认
- 保存。

## 步骤 2：分配角色
- 进入：系统管理 → 角色管理 → 选择需要使用本功能的角色 → 分配菜单。
- 勾选刚才新增的“AI汇总报告”按钮权限（以及基础的 `manage:survey:query` 等查询权限）。
- 确认保存后，提示重新登录生效。

## 步骤 3：环境变量配置（服务端）
- 在部署服务器（或本地开发环境）设置环境变量 `ZAI_API_KEY`：
  - Linux/macOS：`export ZAI_API_KEY=xxx`
  - Windows PowerShell：`setx ZAI_API_KEY "xxx"`
- 重启应用服务。

## 常见校验
- 前端按钮不可见：检查是否为有权限的角色登录，或浏览器需重新登录刷新权限。
- 点击生成后报错：检查后端日志，确认 `ZAI_API_KEY` 是否有效；或网络是否可达智普接口。

