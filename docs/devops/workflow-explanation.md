# GitHub Actions Workflow 说明

## Workflow 触发规则

本项目配置了两个 GitHub Actions 工作流，分别有不同的用途和触发规则：

### 1. Build and Deploy (`deploy.yml`)

**触发条件**:
```yaml
on:
  push:
    branches:
      - main        # 推送到 main 分支
      - develop     # 推送到 develop 分支
  pull_request:
    branches:
      - main        # 创建到 main 的 PR
  workflow_dispatch:  # 手动触发
```

**执行结果**:
- ✅ 自动在推送代码时触发
- ✅ 支持手动触发
- ✅ 支持可选自动部署

### 2. Fast Build (`build-only.yml`)

**触发条件**:
```yaml
on:
  workflow_dispatch:  # 仅手动触发
```

**执行结果**:
- ✅ 仅手动触发（避免重复）
- ✅ 构建速度更快
- 仅构建 linux/amd64 架构
- 只推送 latest 标签

---

## 为什么需要区分这两个 Workflow？

### 避免重复触发

如果两个 workflow 都配置 `push` 触发，一次 push 会同时运行两个构建：

```
git push origin main
          │
          ├─▶ 触发 deploy.yml
          │
          └─▶ 触发 build-only.yml  ❌ 重复！
```

**解决方案**：
- `deploy.yml` 配置自动触发（push + manual）
- `build-only.yml` 配置仅手动触发（manual only）

```
git push origin main
          │
          └─▶ 触发 deploy.yml ✅ 单一构建
```

### 使用场景对比

| 场景 | 推荐使用 | 原因 |
|------|---------|------|
| 推送代码自动构建 | `deploy.yml` | 自动化构建和可选部署 |
| 快速测试构建 | `build-only.yml` | 构建速度更快 |
| 需要多标签/多架构 | `deploy.yml` | 功能完整 |
| 生产环境部署 | `deploy.yml` | 支持自动部署 |

---

## Workflow 执行流程对比

### Build and Deploy 流程

```
触发条件:
  推送代码 (main / develop)
  └─▶ 或手动触发 (可选，可选择是否部署)

构建阶段:
  ├─▶ 前端 Job: build-frontend
  │     ├─▶ Check out code
  │     ├─▶ Set up Docker Buildx
  │     ├─▶ Extract metadata (自动生成多标签: latest / 分支 / SHA)
  │     ├─▶ Login to Docker Hub (非 PR 场景)
  │     └─▶ Build and push frontend image
  │            - Dockerfile: ./frontend/Dockerfile（内部使用 pnpm + Vite）
  │            - 架构: linux/amd64, linux/arm64
  │            - 缓存: GitHub Actions GHA 层缓存
  │
  └─▶ 后端 Job: build-backend
        ├─▶ Check out code
        ├─▶ Set up Docker Buildx
        ├─▶ Extract metadata (同上，多标签)
        ├─▶ Login to Docker Hub (非 PR 场景)
        └─▶ Build and push backend image
               - Dockerfile: ./backend/Dockerfile（内部多阶段 + Maven 构建）
               - 架构: linux/amd64, linux/arm64
               - 缓存: GitHub Actions GHA 层缓存

部署阶段 (仅在手动触发且勾选 deploy 时执行):
  ├─▶ 使用 appleboy/ssh-action SSH 连接服务器
  ├─▶ cd /home/ogas/Code/Web/RuoYi-Platform
  ├─▶ git fetch origin main && git checkout main && git pull origin main
  ├─▶ docker compose pull
  ├─▶ docker compose up -d
  └─▶ docker image prune -af  # 清理悬挂镜像

总耗时: ~8-15 分钟（取决于网络与缓存命中情况）
```

### Fast Build 流程

```
触发条件:
  └─▶ 仅手动触发（Actions → Fast Build → Run workflow）

构建阶段:
  ├─▶ 前端 Job: build-frontend-fast
  │     ├─▶ Check out code
  │     ├─▶ Set up Docker Buildx
  │     ├─▶ Login to Docker Hub
  │     └─▶ Build and push frontend (fast)
  │            - 标签: latest
  │            - 架构: linux/amd64
  │            - 缓存: GitHub Actions GHA
  │
  └─▶ 后端 Job: build-backend-fast
        ├─▶ Check out code
        ├─▶ Set up Docker Buildx
        ├─▶ Login to Docker Hub
        └─▶ Build and push backend (fast)
               - 标签: latest
               - 架构: linux/amd64
               - 缓存: GitHub Actions GHA

部署阶段:
  └─▶ ❌ 无（只负责构建最新镜像，不做部署）

总耗时: ~5-10 分钟
```

---

## 如何选择使用哪个 Workflow？

### 推荐: 大多数情况使用 Build and Deploy

```bash
# 推送代码自动触发
git add .
git commit -m "feat: 添加新功能"
git push origin main

# 查看进度
# https://github.com/ogas1024/RuoYi-Platform/actions
```

**优点**:
- 自动化，无需手动操作
- 功能完整，标签管理清晰
- 支持自动部署

**适用场景**:
- 日常开发
- 功能开发完成
- 生产环境部署

### 备用: 需要快速构建时使用 Fast Build

```bash
# 前往 GitHub
# https://github.com/ogas1024/RuoYi-Platform/actions

1. 选择 "Fast Build"
2. 点击 "Run workflow"
3. 选择分支（默认 main）
4. 点击绿色按钮
```

**优点**:
- 构建速度更快
- 资源占用更少
- 适合快速验证

**适用场景**:
- 紧急修复验证
- 快速测试构建
- 不需要多标签/多架构

---

## Workflow 触发示例

### 示例 1: 正常开发流程

```bash
# 开发完成
git add .
git commit -m "feat: 添加用户管理功能"
git push origin main

# 结果：
# ✅ 触发 deploy.yml 一次
# ✅ 构建前端 + 后端
# ✅ 推送 latest, main, main-sha 标签
# ❌ 不触发 build-only.yml
```

### 示例 2: 需要快速验证

```bash
# 推送代码（不触发 Fast Build）
git add .
git commit -m "fix: 紧急修复 bug"
git push origin main

# 手动触发 Fast Build（GitHub 网站）
# 快速验证构建是否成功
```

### 示例 3: 手动触发完整构建

```bash
# GitHub 网站
# 选择 "Build and Deploy"
# 点击 "Run workflow"
# 勾选 "Deploy to server after build"
# ✅ 构建并自动部署
```

---

## 查看 Workflow 历史

### GitHub Actions 界面

```
访问：https://github.com/ogas1024/RuoYi-Platform/actions

你会看到两个 workflow：

┌─────────────────────────────────────────┐
│  All workflows  ▾                       │
│                                         │
│  ✓ Build and Deploy                    │
│    └─▶ 最近运行: main 分支, 5分钟前    │
│    └─▶ 5 次运行                          │
│                                         │
│  ✓ Fast Build                          │
│    └─▶ 最近运行: 手动触发, 1小时前      │
│    └─▶ 2 次运行                          │
└─────────────────────────────────────────┘
```

### Workflow 详情页

点击某个 workflow，可以查看：
- 每次运行的状态（成功/失败）
- 运行时间
- 触发方式（push / manual）
- 构建日志

---

## 常见问题

### Q1: 为什么 push 时只触发了一个 workflow？

**正常现象**：
- 触发 `Build and Deploy` ✅
- 不触发 `Fast Build` ✅

**原因**：`Fast Build` 配置为仅手动触发

### Q2: 如何查看构建进度？

```bash
# GitHub 网站
https://github.com/ogas1024/RuoYi-Platform/actions

# 或使用 GitHub CLI
gh run list
gh run watch <run-id>
```

### Q3: 构建失败怎么办？

1. 点击失败的 workflow
2. 查看失败的任务（如 build-backend）
3. 查看日志详情
4. 根据错误信息修复
5. 重新推送代码触发构建

### Q4: 为什么需要两个 workflow？

| 场景 | 如果只用一个 | 当前方案 |
|------|------------|---------|
| 日常开发 | ❌ 无法自动触发 | ✅ `deploy.yml` 自动触发 |
| 快速验证 | ❌ 构建慢 | ✅ `build-only.yml` 更快 |
| 生产部署 | ❌ 功能不完整 | ✅ 支持完整流程 |

**结论**：两个 workflow 兼顾自动化和灵活性

---

## 建议的工作流程

### 开发新功能

```
本地开发
  ├─▶ push 到 main
  │       └─▶ 自动触发 deploy.yml
  │              └─▶ 构建镜像 ✅
  │
  └─▶ 需要快速验证
          └─▶ 手动触发 build-only.yml
                 └─▶ 快速构建 ✅
```

### 紧急修复

```
本地修复
  ├─▶ push 到 main
  │       └─▶ 自动触发 deploy.yml
  │              └─▶ 构建并部署 ✅
  │
  └─▶ 修复构建问题（如失败）
          └─▶ 本地调试 → 手动触发 build-only.yml
                 └─▶ 快速验证 ✅
```

---

## 相关文档

- GitHub Actions 部署指南: `docs/github-actions-deployment.md`
- GitHub Secrets 配置: `docs/github-secrets-setup.md`
- 环境变量系统对比: `docs/environment-variables-guide.md`

---

## 总结

| 特性 | Build and Deploy | Fast Build |
|------|-----------------|------------|
| 自动触发 | ✅ Push, PR | ❌ 无 |
| 手动触发 | ✅ 支持 | ✅ 支持 |
| 构建速度 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 多标签 | ✅ 支持 | ❌ 只有 latest |
| 多架构 | ✅ 支持 | ❌ 只有 amd64 |
| 自动部署 | ✅ 支持 | ❌ 不支持 |
| 推荐度 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ 备用 |

**一句话总结**：
> 日常开发用 `Build and Deploy`，紧急情况用 `Fast Build`
