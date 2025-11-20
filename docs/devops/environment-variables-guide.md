# 环境变量系统对比说明

## 两个独立的环境变量系统

本项目使用两套独立的环境变量系统，服务于不同目的，切勿混淆：

```
┌─────────────────────────┐         ┌─────────────────────────┐
│                         │         │                         │
│  1️⃣ GitHub Secrets      │         │  2️⃣ .env 文件           │
│  (构建阶段)              │         │  (运行阶段)              │
│                         │         │                         │
│  用途：CI/CD 认证        │         │  用途：容器配置          │
│  位置：GitHub 网站       │         │  位置：项目根目录        │
│  给谁用：GitHub Actions  │         │  给谁用：Docker 容器     │
│                         │         │                         │
└────────────┬────────────┘         └────────────┬────────────┘
             │                                    │
             │  构建镜像                            │ 运行容器
             ▼                                    ▼
        ┌────────────────┐               ┌────────────────┐
        │                │               │                │
        │  Docker 镜像   │ ─────────────►│  Docker 容器   │
        │  (ruoyi-*)     │    部署       │  (运行应用)    │
        │                │               │                │
        └────────────────┘               └────────────────┘
```

---

## 详细对比表

| 特性 | GitHub Secrets | .env 文件 |
|------|---------------|-----------|
| **生命周期** | 构建阶段 | 运行阶段 |
| **存储位置** | GitHub 服务器 | 服务器本地文件 |
| **配置方式** | GitHub 网站设置 | 文本文件 |
| **访问方式** | `${{ secrets.XXX }}` | `docker compose` 自动读取 |
| **主要用途** | 认证、密钥、凭据 | 应用配置、数据库参数 |
| **常见变量** | DOCKER_HUB_TOKEN, SSH_KEY | MYSQL_PASSWORD, TZ |
| **安全性** | ⭐⭐⭐⭐⭐ 加密存储 | ⭐⭐⭐ 明文文件，需 `.gitignore` |
| **查看方式** | 无法查看，只能更新 | 直接查看文件内容 |

---

## GitHub Secrets 清单（必须配置）

位置：**GitHub 网站**

```bash
# 访问路径：
https://github.com/ogas1024/RuoYi-Platform/settings/secrets/actions

# 需要添加的 Secrets：
DOCKER_HUB_USERNAME=xxx          # Docker Hub 用户名
DOCKER_HUB_TOKEN=xxx             # Docker Hub 访问令牌
SERVER_HOST=xxx.xxx.xxx.xxx      # 服务器 IP
SERVER_USER=xxx                  # SSH 用户名（如 ubuntu）
SERVER_SSH_KEY=xxx               # SSH 私钥（完整内容）
```

**为什么必须在 GitHub 配置？**
- 构建镜像时需要推送镜像到 Docker Hub → 需要 DOCKER_HUB_TOKEN
- 部署时需要 SSH 连接到服务器 → 需要 SERVER_SSH_KEY
- 这些敏感信息不能写在代码中

---

## .env 文件清单（已配置）

位置：**服务器上的项目根目录**

```bash
# 文件：/home/ogas/Code/Web/RuoYi-Platform/.env

# Docker 镜像配置
DOCKER_REGISTRY=docker.io
IMAGE_NAMESPACE=ogas1024         # 你的 Docker Hub 用户名

# MySQL 配置
MYSQL_ROOT_PASSWORD=ruoyi_root
MYSQL_DATABASE=ruoyi
MYSQL_USER=ruoyi
MYSQL_PASSWORD=ruoyi123

# Redis 配置（暂无密码）
# REDIS_PASSWORD=xxx

# OSS 配置（可选）
ALIYUN_OSS_ENABLED=false
# ALIYUN_OSS_ENDPOINT=xxx
# ALIYUN_OSS_ACCESS_KEY_ID=xxx
# ALIYUN_OSS_ACCESS_KEY_SECRET=xxx
# ALIYUN_OSS_BUCKET=xxx
# ALIYUN_OSS_CUSTOM_DOMAIN=xxx
# ALIYUN_OSS_BASE_PATH=uploads

# 智谱 AI 配置（可选）
# ZAI_API_KEY=xxx

# 时区
TZ=Asia/Shanghai
```

**用途说明：**
- `docker-compose.yml` 自动读取 `.env` 文件
- 设置 MySQL、Redis、Spring Boot 的运行参数
- 配置阿里云 OSS 和智谱 AI（可选）

---

## 配置检查清单

### ✅ 第 1 步：配置 GitHub Secrets（5 个）

```bash
# 打开 GitHub → Settings → Secrets → Actions
# 点击 "New repository secret"

须添加：
☐ DOCKER_HUB_USERNAME
☐ DOCKER_HUB_TOKEN
☐ SERVER_HOST
☐ SERVER_USER
☐ SERVER_SSH_KEY
```

**文档**：`docs/github-secrets-setup.md`

### ✅ 第 2 步：配置 .env 文件（已完成）

```bash
# 检查文件是否存在
cat /home/ogas/Code/Web/RuoYi-Platform/.env

应该包含：
☐ DOCKER_REGISTRY
☐ IMAGE_NAMESPACE    # 修改为 Docker Hub 用户名
☐ MYSQL_ROOT_PASSWORD
☐ MYSQL_DATABASE
☐ MYSQL_USER
☐ MYSQL_PASSWORD
☐ TZ
```

**文档**：`docs/github-actions-deployment.md`

---

## 实际工作流程示例

### 场景：推送代码自动构建和部署

```
你 (本地电脑)
  │
  ├─▶ git push origin main
  │
  ▼
GitHub Actions
  │
  ├─── 从 Secrets 读取 DOCKER_HUB_TOKEN
  ├─── 登录 Docker Hub
  ├─── 构建前端镜像 (使用 Dockerfile)
  ├─── 构建后端镜像 (使用 Dockerfile)
  ├─── 推送到 Docker Hub
  │    镜像名：ogas1024/ruoyi-backend:latest
  │    镜像名：ogas1024/ruoyi-frontend:latest
  │
  └─▶ SSH 连接到服务器 (使用 SERVER_SSH_KEY)
       │
       ├─▶ cd /home/ogas/Code/Web/RuoYi-Platform
       ├─▶ git fetch origin main && git checkout main && git pull origin main
       ├─▶ docker compose pull
       ├─▶ docker compose up -d
       └─▶ docker image prune -af (清理旧镜像)
            │
            ▼ 使用 .env 文件配置：
              - MYSQL_PASSWORD (连接数据库)
              - TZ (时区)
              - 其他应用配置
```

---

## 关键要点

### ❌ 常见误区

**错误 1：把 Secrets 写入 .env**
```bash
# 不要把 GitHub Secrets 写入 .env 文件
.env 文件中添加：
DOCKER_HUB_TOKEN=dckr_pat_xxx  ❌ 错误！
```

**原因**：`.env` 文件在代码库中，如果提交到 Git，会泄露凭据

**正确做法**：在 GitHub 网站配置 Secrets

---

**错误 2：在 GitHub Secrets 中配置 MYSQL_PASSWORD**
```bash
# 没必要把 MYSQL_PASSWORD 放入 GitHub Secrets
GitHub Secrets 中：
MYSQL_PASSWORD=ruoyi123  ❌ 多余！
```

**原因**：构建阶段不需要 MySQL 密码

**正确做法**：在服务器本地的 .env 文件中配置

---

**错误 3：.env 文件提交到 Git**
```bash
cat .gitignore  # 确保包含 .env
# .env  ✅ 正确（被忽略）
# .env.example ✅ 正确（可以提交）
# .env ✗ 错误（如果提交）
```

---

## 快速参考

### 何时配置 GitHub Secrets？

配置 CI/CD 之前，配置：
- ✅ DOCKER_HUB_USERNAME
- ✅ DOCKER_HUB_TOKEN
- ✅ SERVER_HOST
- ✅ SERVER_USER
- ✅ SERVER_SSH_KEY

### 何时配置 .env？

项目部署到服务器时，配置：
- ✅ MYSQL_* (数据库密码)
- ✅ DOCKER_REGISTRY (docker.io)
- ✅ IMAGE_NAMESPACE (Docker Hub 用户名)
- ✅ TZ (时区)
- ✅ REDIS_PASSWORD (如果需要)
- ✅ OSS 配置 (可选)
- ✅ ZAI_API_KEY (可选)

### GitHub Secrets 如何读取？

在 `.github/workflows/*.yml` 中：
```yaml
# 正确 ✅
${{ secrets.DOCKER_HUB_USERNAME }}
${{ secrets.SERVER_HOST }}

# 错误 ❌
${DOCKER_HUB_USERNAME}
$DOCKER_HUB_USERNAME
```

### .env 如何读取？

在 `docker-compose.yml` 中：
```yaml
# 正确 ✅
${MYSQL_PASSWORD}
${IMAGE_NAMESPACE}

# 默认值 ✅
${MYSQL_PASSWORD:-ruoyi123}
```

---

## 记忆技巧

```
GitHub Secrets  →  给 GitHub 用的   →  推送/部署
      ↓                    ↓                    ↓
  敏感认证信息         CI/CD 流程         自动构建

.env 文件        →  给 Docker 用的  →  运行容器
      ↓                    ↓                    ↓
   应用配置           docker compose      应用运行
```

---

## 总结

| 场景 | 使用什么 | 在哪里配置 |
|------|---------|-----------|
| 构建镜像推送到 Docker Hub | GitHub Secrets | GitHub 网站 |
| SSH 部署到服务器 | GitHub Secrets | GitHub 网站 |
| MySQL 密码 | .env 文件 | 服务器本地 |
| 应用参数配置 | .env 文件 | 服务器本地 |
| OSS 访问密钥 | .env 文件 | 服务器本地 |

**一句话总结**:
> **GitHub Secrets** 管"构建和部署"，`.env` 管"运行时配置"

---

## 相关文档

- 详细配置步骤：`docs/github-secrets-setup.md`
- 完整部署指南：`docs/github-actions-deployment.md`

---

**配置完成这两个系统后，就可以在弱配置的云服务器上享受快速的部署体验了！**
