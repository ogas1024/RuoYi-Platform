# GitHub Actions 自动构建与部署指南

本文档介绍如何使用 GitHub Actions 自动构建 RuoYi-Platform 的 Docker 镜像，解决云服务器资源不足导致的构建缓慢问题。

## 目录

- [GitHub Actions 自动构建与部署指南](#github-actions-自动构建与部署指南)
  - [目录](#目录)
  - [方案优势](#方案优势)
  - [前置准备](#前置准备)
  - [配置步骤](#配置步骤)
    - [1. 注册 Docker Hub 账号](#1-注册-docker-hub-账号)
    - [2. 创建 GitHub Secrets](#2-创建-github-secrets)
    - [3. 配置镜像命名空间](#3-配置镜像命名空间)
  - [使用方法](#使用方法)
    - [方案一：自动触发构建](#方案一自动触发构建)
    - [方案二：手动触发构建](#方案二手动触发构建)
    - [方案三：自动部署到服务器](#方案三自动部署到服务器)
  - [服务器部署](#服务器部署)
    - [使用预构建镜像](#使用预构建镜像)
    - [传统本地构建](#传统本地构建)
  - [工作流说明](#工作流说明)
  - [常见问题](#常见问题)
  - [进阶配置](#进阶配置)

## 方案优势

1. **免费构建资源**：GitHub 提供免费的 Action 运行器，无需占用服务器资源
2. **并行构建**：前端和后端可同时构建，大幅缩短总耗时
3. **缓存加速**：自动缓存 npm 和 Maven 依赖，后续构建更快
4. **多平台支持**：自动构建支持 amd64 和 arm64 架构
5. **版本管理**：自动生成版本标签（latest, sha, branch）
6. **CI/CD 集成**：支持自动部署到服务器

## 前置准备

1. **GitHub 账号**（已有代码仓库）
2. **Docker Hub 账号**（或其他镜像仓库）
3. **可 SSH 访问的服务器**（如需要自动部署）

## 配置步骤

### 1. 注册 Docker Hub 账号

访问 [Docker Hub](https://hub.docker.com) 注册免费账号。

### 2. 创建 GitHub Secrets

在 GitHub 仓库中配置以下 Secrets：

1. 进入仓库 → Settings → Secrets and variables → Actions
2. 点击 "New repository secret"

需要创建以下 Secrets：

| Secret | 说明 | 如何获取 |
|--------|------|----------|
| `DOCKER_HUB_USERNAME` | Docker Hub 用户名 | Docker Hub 个人资料页面查看 |
| `DOCKER_HUB_TOKEN` | Docker Hub 访问令牌 | Docker Hub → Account Settings → Security → Access Tokens |
| `SERVER_HOST` | 服务器 IP 或域名 | 你的服务器地址 |
| `SERVER_USER` | SSH 登录用户名 | 如 `root` 或 `ubuntu` |
| `SERVER_SSH_KEY` | SSH 私钥（推荐）或密码 | 生成 SSH 密钥：`ssh-keygen -t ed25519 -C "your-email@example.com"` |
| `SERVER_PORT` | SSH 端口（可选） | 默认为 22 |

#### 生成 Docker Hub Access Token

1. 登录 Docker Hub
2. 右上角头像 → Account Settings → Security → Access Tokens
3. 点击 "New Access Token"
4. 填写描述，选择 "Read & Write" 权限
5. 点击 "Generate" 并复制令牌（只显示一次）

#### 生成 SSH 密钥（推荐）

```bash
# 在本地生成密钥对
ssh-keygen -t ed25519 -C "your-email@example.com" -f ~/.ssh/ruoyi-deploy

# 将公钥添加到服务器的 authorized_keys
cat ~/.ssh/ruoyi-deploy.pub | ssh your-server-user@your-server-ip "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"

# 查看私钥内容（添加到 GitHub Secrets）
cat ~/.ssh/ruoyi-deploy
```

### 3. 配置镜像命名空间

创建 `.env` 文件（如果不存在）：

```bash
# 镜像仓库配置
DOCKER_REGISTRY=docker.io
IMAGE_NAMESPACE=your-docker-username  # 修改为 Docker Hub 用户名

# 应用配置
MYSQL_ROOT_PASSWORD=ruoyi_root
MYSQL_DATABASE=ruoyi
MYSQL_USER=ruoyi
MYSQL_PASSWORD=ruoyi123
TZ=Asia/Shanghai
```

**注意**：将 `your-docker-username` 替换为你的 Docker Hub 用户名。

## 使用方法

### 方案一：自动触发构建

**适用场景**：推送代码后自动构建镜像

**配置方法**：

```bash
# 推送代码到 main 或 develop 分支
git add .
git commit -m "feat: 添加新功能"
git push origin main
```

GitHub Actions 会自动：
1. 检出代码
2. 构建前端镜像（使用 pnpm，比 npm 快 2-3 倍）
3. 构建后端镜像（使用 Maven 缓存）
4. 推送到 Docker Hub
5. 生成多个标签：`latest`, `main-sha`, `main`

**触发条件**：
- 推送到 `main` 或 `develop` 分支
- 创建 Pull Request

### 方案二：手动触发构建

**适用场景**：快速构建并推送最新镜像

1. 进入 GitHub 仓库 → Actions → Fast Build
2. 点击 "Run workflow"
3. 选择分支（如 `main`）
4. 点击 "Run workflow"

**特点**：
- 简化的构建流程
- 仅构建 `linux/amd64` 架构（更快）
- 只推送 `latest` 标签

### 方案三：自动部署到服务器

**适用场景**：推送代码后自动构建并部署

1. 进入 GitHub 仓库 → Actions → Build and Deploy
2. 点击 "Run workflow"
3. 勾选 "Deploy to server after build"
4. 点击 "Run workflow"

**执行流程**：
1. 构建前端和后端镜像
2. 推送到 Docker Hub
3. SSH 连接到服务器
4. 拉取最新代码
5. 拉取最新镜像
6. 重新部署服务
7. 清理旧镜像

## 服务器部署

### 使用预构建镜像

**前提**：已在 GitHub Actions 中成功构建并推送镜像

#### 方法一：使用扩展配置文件（推荐）

```bash
# 1. 确保 .env 文件已配置镜像命名空间
cat > .env << EOF
DOCKER_REGISTRY=docker.io
IMAGE_NAMESPACE=your-docker-username
MYSQL_ROOT_PASSWORD=ruoyi_root
MYSQL_DATABASE=ruoyi
MYSQL_USER=ruoyi
MYSQL_PASSWORD=ruoyi123
TZ=Asia/Shanghai
EOF

# 2. 拉取并启动服务（自动使用预构建镜像）
docker-compose -f docker-compose.yml -f docker-compose.prebuilt.yml pull
docker-compose -f docker-compose.yml -f docker-compose.prebuilt.yml up -d

# 3. 查看日志
docker-compose logs -f
```

#### 方法二：手动拉取镜像

```bash
# 1. 拉取最新镜像
docker pull your-docker-username/ruoyi-backend:latest
docker pull your-docker-username/ruoyi-frontend:latest

# 2. 标记镜像（可选，便于本地使用）
docker tag your-docker-username/ruoyi-backend:latest ruoyi/ruoyi-backend:latest
docker tag your-docker-username/ruoyi-frontend:latest ruoyi/ruoyi-frontend:latest

# 3. 启动服务
docker-compose up -d
```

### 传统本地构建

如果仍然需要在服务器上构建：

```bash
# 使用 pnpm 加速构建（已在 Dockerfile 中配置）
docker-compose build --parallel

# 启动服务
docker-compose up -d
```

**注意**：首次构建可能会比较慢，后续构建会使用缓存。

## 工作流说明

### `.github/workflows/deploy.yml`

完整 CI/CD 工作流：

- **触发条件**：推送代码、PR、手动触发
- **功能**：
  - 构建前端和后端镜像
  - 推送到 Docker Hub
  - 可选自动部署到服务器
  - 生成多架构镜像（amd64, arm64）
  - 生成多个标签（latest, sha, branch）

### `.github/workflows/build-only.yml`

快速构建工作流：

- **触发条件**：推送代码、PR、手动触发
- **特点**：
  - 简化的构建流程
  - 仅构建 `linux/amd64`
  - 只推送 `latest` 标签
  - 构建速度更快

## 常见问题

### Q1: 构建失败，提示 "denied: requested access to the resource is denied"

**原因**：Docker Hub 认证失败

**解决**：
1. 检查 `DOCKER_HUB_USERNAME` 和 `DOCKER_HUB_TOKEN` 是否正确
2. 确保 Token 有 Read & Write 权限
3. 检查 Docker Hub 账号是否被限制

### Q2: 如何跳过某些步骤？

**前端构建**：修改 workflow 文件，注释或删除前端构建 job

**后端构建**：修改 workflow 文件，注释或删除后端构建 job

**自动部署**：不勾选 "Deploy to server after build"

### Q3: 构建速度仍然很慢

**优化建议**：

1. **启用缓存**：workflow 已配置 `cache-from` 和 `cache-to`
2. **使用 pnpm**：前端 Dockerfile 已切换到 pnpm
3. **使用 Maven 缓存**：后端 workflow 已配置 `cache: 'maven'`
4. **减少层数**：检查 Dockerfile 中是否有不必要的步骤

### Q4: 如何在其他平台使用（如阿里云镜像服务）？

**修改 GitHub Actions workflow**：

```yaml
# 修改登录步骤
- name: Login to Aliyun Container Registry
  uses: docker/login-action@v3
  with:
    registry: registry.cn-hangzhou.aliyuncs.com
    username: ${{ secrets.ALIYUN_REGISTRY_USERNAME }}
    password: ${{ secrets.ALIYUN_REGISTRY_PASSWORD }}

# 修改镜像名称
env:
  BACKEND_IMAGE_NAME: registry.cn-hangzhou.aliyuncs.com/your-namespace/ruoyi-backend
  FRONTEND_IMAGE_NAME: registry.cn-hangzhou.aliyuncs.com/your-namespace/ruoyi-frontend
```

**修改服务器配置**：

```bash
# .env 文件
DOCKER_REGISTRY=registry.cn-hangzhou.aliyuncs.com
IMAGE_NAMESPACE=your-namespace
```

### Q5: 如何回滚到旧版本？

**回滚步骤**：

```bash
# 查看可用标签
docker images your-docker-username/ruoyi-backend

# 拉取特定版本
docker pull your-docker-username/ruoyi-backend:main-abcd123

# 重新打标签
docker tag your-docker-username/ruoyi-backend:main-abcd123 your-docker-username/ruoyi-backend:latest

# 重启服务
docker-compose up -d
```

## 进阶配置

### 自动清理旧镜像

在 `.github/workflows/deploy.yml` 的 deploy job 中已包含：

```yaml
- name: Clean up old images
  run: docker image prune -af
```

### 通知构建结果

添加 Slack/DingTalk 通知：

```yaml
- name: Notify build result
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### 多环境部署

创建多个 workflow 文件：

- `deploy-production.yml` - 生产环境
- `deploy-staging.yml` - 预发布环境
- `deploy-development.yml` - 开发环境

### 镜像安全扫描

```yaml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: ${{ env.BACKEND_IMAGE_NAME }}:latest
    format: 'table'
```

## 总结

通过 GitHub Actions 自动构建，你可以：

1. ✅ 解决服务器资源不足导致的构建缓慢问题
2. ✅ 享受免费的 GitHub 构建资源
3. ✅ 实现全自动化的 CI/CD 流程
4. ✅ 支持多架构、多标签的镜像管理
5. ✅ 可选自动部署到服务器

如有问题，请查看 GitHub Actions 的运行日志进行调试。
