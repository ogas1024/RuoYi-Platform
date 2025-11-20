# GitHub Actions + Docker 一键部署总览（精简版）

> 目标：让弱配置云服务器只负责 `docker compose up -d`，所有前后端构建都在 GitHub Actions 完成。

本文件只讲 **你需要做的事情**，细节拆到两个附录：

- Secrets 详解：`docs/github-secrets-setup.md`
- 环境变量说明：`docs/environment-variables-guide.md`
- Workflow 行为细节：`docs/workflow-explanation.md`

---

## 一、前置条件检查

1. 你有一个 GitHub 仓库（本项目）。  
2. 你有一个 Docker Hub 账号（免费版即可）。  
3. 你有一台能 SSH 的云服务器（上面已安装 Docker 和 Docker Compose v2）。  
4. 服务器上项目路径固定为：`/home/ogas/Code/Web/RuoYi-Platform`。

---

## 二、一次性配置流程（只做一遍）

### 步骤 1：配置 GitHub Secrets（5 个）

进入仓库：Settings → Secrets and variables → Actions → New repository secret。

按 `docs/github-secrets-setup.md` 配置以下 5 个：

- `DOCKER_HUB_USERNAME`：你的 Docker Hub 用户名（例如 `ogas1024`）
- `DOCKER_HUB_TOKEN`：Docker Hub Access Token（Read & Write）
- `SERVER_HOST`：云服务器 IP 或域名
- `SERVER_USER`：SSH 用户名（如 `root`、`ubuntu`、`ogas`）
- `SERVER_SSH_KEY`：用于部署的 SSH 私钥（`~/.ssh/ruoyi-deploy` 全部内容）

> 这一步只影响 **构建 / 部署阶段**，不会影响你本地开发。

### 步骤 2：在服务器上配置 `.env`

SSH 登录你的云服务器：

```bash
cd /home/ogas/Code/Web/RuoYi-Platform

cat > .env << 'EOF'
# 镜像仓库配置
DOCKER_REGISTRY=docker.io
IMAGE_NAMESPACE=your-docker-username   # TODO: 改成你的 Docker Hub 用户名

# MySQL 配置
MYSQL_ROOT_PASSWORD=ruoyi_root
MYSQL_DATABASE=ruoyi
MYSQL_USER=ruoyi
MYSQL_PASSWORD=ruoyi123

# 时区
TZ=Asia/Shanghai
EOF
```

> 完整可选项见：`docs/environment-variables-guide.md` 和 `.env.example`，其中包含 Redis、OSS、智谱 AI 等。

### 步骤 3：验证 Docker Compose 文件

确认根目录有这几个文件：

- `docker-compose.yml`：**默认直接使用预构建镜像**，生产环境只需要它。  
- `docker-compose.build.yml`：本地开发时想自己构建镜像再用（服务器一般不用）。  

`docker-compose.yml` 中的关键配置（简要）：

- `mysql` / `redis`：使用官方镜像。  
- `backend` / `frontend`：只指定 `image`，不再在服务器上 `build`。  
  - 后端镜像：`${DOCKER_REGISTRY}/${IMAGE_NAMESPACE}/ruoyi-backend:latest`  
  - 前端镜像：`${DOCKER_REGISTRY}/${IMAGE_NAMESPACE}/ruoyi-frontend:latest`

到这里，一次性配置就完成了。

---

## 三、完整工作流（从开发到上线）

### 场景 A：正常开发 + 按需上线

**开发阶段（本地）：**

```bash
# 本地编码 & 提交
git add .
git commit -m "feat: xxx"
git push origin main
```

- push 到 `main` 或 `develop` 时，`Build and Deploy` workflow 会自动跑“构建阶段”：  
  - 使用 GitHub Actions 在云端构建前端/后端 Docker 镜像。  
  - 构建完成后，将镜像推送到 Docker Hub：  
    - `<用户名>/ruoyi-backend:*`  
    - `<用户名>/ruoyi-frontend:*`  
  - 推送哪些标签、支持哪些架构，详见 `docs/workflow-explanation.md`。

**上线阶段（手动）：**

1. 打开 GitHub → Actions → 选择 `Build and Deploy`。  
2. 点击右上角 **Run workflow**：  
   - 选择分支（通常是 `main`）。  
   - 勾选 `Deploy to server after build`。  
3. 等待 workflow 跑完（包含构建 + 部署）。

部署 job 做的事（在服务器上执行）：

```bash
cd /home/ogas/Code/Web/RuoYi-Platform
git fetch origin main && git checkout main && git pull origin main
docker compose pull
docker compose up -d
docker image prune -af
```

> 效果：服务器只做 **拉镜像 + 跑容器**，不会在弱配置机器上编译前端或打包后端。

### 场景 B：只想快速刷新 latest 镜像（不部署）

1. 打开 GitHub → Actions → 选择 `Fast Build`（`build-only.yml`）。  
2. 点击 **Run workflow**，选择分支并运行。

这个 Workflow 只做两件事：

- 在 GitHub Actions 上构建前端/后端镜像。  
- 推送到 Docker Hub（只打 `latest` 标签，单架构 `linux/amd64`，速度快）。

服务器上你可以稍后手动：

```bash
cd /home/ogas/Code/Web/RuoYi-Platform
docker compose pull
docker compose up -d
```

### 场景 C：你想在本机自己构建镜像再跑

仅适合你本地电脑，**不推荐在云服务器上这么干**：

```bash
# 本机（资源相对充足）
cd /path/to/RuoYi-Platform

docker compose -f docker-compose.yml -f docker-compose.build.yml up --build
```

`docker-compose.build.yml` 会为 backend/frontend 增加 `build` 配置，并复用 `backend/Dockerfile` 和 `frontend/Dockerfile`。

---

## 四、你需要记住的 3 条命令

1. **本地开发完成并触发构建：**

```bash
git add .
git commit -m "feat: 描述本次改动"
git push origin main
```

2. **服务器一键拉起前后端 + 数据库（首次或更新后）：**

```bash
cd /home/ogas/Code/Web/RuoYi-Platform
docker compose pull
docker compose up -d
```

3. **需要“CI 帮我重新打 latest 镜像”时：**

- GitHub → Actions → 选择 `Fast Build` → Run workflow。

---

## 五、遇到问题时从哪里下手

- 构建失败（CI 红了）：  
  → 打开 GitHub Actions 日志，看哪个 job 失败（`build-frontend` / `build-backend`），按日志修。  
- 部署失败（CI 构建成功，但服务起不来）：  
  → SSH 到服务器：  
  ```bash
  cd /home/ogas/Code/Web/RuoYi-Platform
  docker compose ps
  docker compose logs backend
  docker compose logs frontend
  ```  
- 环境变量/密码问题：  
  → 检查服务器 `.env`（运行时配置），参考：`docs/environment-variables-guide.md`。  
- Secrets 配置问题（CI 提示 token/SSH 错误）：  
  → 检查 GitHub 仓库 Settings → Secrets，参考：`docs/github-secrets-setup.md`。

---

## 六、和本项目其他文档的关系

- **本文件**：你专用的“Docker + GitHub Actions 一键部署”入口文档。  
- `docs/github-secrets-setup.md`：教你如何一次性把 5 个 Secrets 配好。  
- `docs/environment-variables-guide.md`：解释 `.env` 每个变量的含义。  
- `docs/workflow-explanation.md`：详细拆解 `deploy.yml` / `build-only.yml` 的行为和触发逻辑。  

如果只想“能跑起来”，你只需要反复看这一份：`docs/github-actions-deployment.md`。  
其他三份，可以当作你以后出问题时查的“附录”。 
