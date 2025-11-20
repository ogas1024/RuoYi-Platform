# GitHub Secrets 配置指南

## 概念区分

### `.env` 文件（已配置）
- **位置**：项目根目录 `.env`
- **作用**：运行时的容器环境变量
- **给谁用**：MySQL、Redis、Spring Boot 容器

### GitHub Secrets（需要配置）
- **位置**：GitHub 网站 → Settings → Secrets
- **作用**：CI/CD 流程的认证信息
- **给谁用**：GitHub Actions 构建和部署

---

## GitHub Secrets 配置清单

需要配置以下 5 个 Secret：

### 1. DOCKER_HUB_USERNAME
- **说明**：Docker Hub 用户名
- **如何获取**：登录 Docker Hub → 右上角头像 → Account Settings → General

### 2. DOCKER_HUB_TOKEN
- **说明**：Docker Hub 访问令牌（密码）
- **如何获取**：
  1. 登录 Docker Hub → https://hub.docker.com
  2. 右上角头像 → Account Settings
  3. 左侧菜单 → Security → Access Tokens
  4. 点击 "New Access Token"
     - 名称：ruoyi-platform
     - 权限：选择 **Read & Write**
     - 点击 "Generate"
  5. **立即复制令牌**（只显示一次！）

### 3. SERVER_HOST
- **说明**：你的云服务器 IP 地址或域名
- **示例**：`192.168.1.100` 或 `your-server.com`

### 4. SERVER_USER
- **说明**：SSH 登录用户名
- **常见值**：`root` 或 `ubuntu` 或 `ogas`

### 5. SERVER_SSH_KEY
- **说明**：SSH 私钥（用于 GitHub Actions 连接你的服务器）
- **生成方法**：运行以下命令

```bash
# 生成新的 SSH 密钥对（在本地电脑执行）
ssh-keygen -t ed25519 -C "ruoyi-deploy" -f ~/.ssh/ruoyi-deploy

# 将公钥添加到服务器的 authorized_keys
# 替换 your-server-user 和 your-server-ip 为实际值
cat ~/.ssh/ruoyi-deploy.pub | ssh your-server-user@your-server-ip \
  "mkdir -p ~/.ssh && chmod 700 ~/.ssh && cat >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys"

# 查看私钥内容（粘贴到 GitHub Secret）
cat ~/.ssh/ruoyi-deploy
```

**输出示例**（复制全部内容）：
```
-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAFwAAAAdzc2gtcn
NhAAAAAwEAAQAAAQEAvd3T5d5t0S3...（很长）
-----END OPENSSH PRIVATE KEY-----
```

---

## 配置步骤详解

### 步骤 1：打开仓库设置

访问：https://github.com/ogas1024/RuoYi-Platform/settings/secrets/actions

### 步骤 2：点击 "New repository secret"

### 步骤 3：填写 Secret 信息

**示例：添加 DOCKER_HUB_USERNAME**

```
Name: DOCKER_HUB_USERNAME
Secret: your-docker-username
```

**示例：添加 DOCKER_HUB_TOKEN**

```
Name: DOCKER_HUB_TOKEN
Secret: dckr_pat_abc123...（刚才生成的令牌）
```

**示例：添加 SERVER_SSH_KEY**

```
Name: SERVER_SSH_KEY
Secret: （将 cat ~/.ssh/ruoyi-deploy 的全部输出粘贴到这里）
```

⚠️ **重要提示**：
- Secret 添加后无法查看，只能更新或删除
- 建议一次性配置好所有 Secrets
- 如果配置错误，重新添加同名 Secret 即可覆盖

---

## 验证配置

配置完成后，可以触发一次构建测试：

### 方法一：推送代码测试

```bash
echo "# 测试 GitHub Actions" >> README.md
git add README.md
git commit -m "test: 测试 GitHub Actions 构建"
git push origin main
```

然后访问：https://github.com/ogas1024/RuoYi-Platform/actions

查看工作流是否运行成功。

### 方法二：手动触发测试

1. 打开：https://github.com/ogas1024/RuoYi-Platform/actions
2. 选择 "Fast Build" 工作流
3. 点击 "Run workflow" → 选择 `main` 分支
4. 点击绿色按钮运行

---

## 常见问题

### Q: 提示 "DOCKER_HUB_TOKEN is not set"

**原因**：GitHub Secret 没有配置

**解决**：检查是否所有 5 个 Secrets 都已添加

### Q: 提示 "Permission denied (publickey)"

**原因**：SERVER_SSH_KEY 配置错误或服务器的 authorized_keys 没有添加公钥

**解决**：
1. 重新执行 SSH 密钥生成命令
2. 确保公钥已添加到服务器的 ~/.ssh/authorized_keys
3. 更新 GitHub Secret: SERVER_SSH_KEY

### Q: 提示 "denied: requested access to the resource is denied"

**原因**：DOCKER_HUB_USERNAME 或 DOCKER_HUB_TOKEN 错误

**解决**：
1. 检查 Docker Hub 用户名是否正确
2. 重新生成 Access Token（记得选择 Read & Write 权限）
3. 更新 GitHub Secret: DOCKER_HUB_TOKEN

---

## 配置示例截图

```
最终配置效果：

Repository secrets
─────────────────────────────────────────
Name                    Last updated
─────────────────────────────────────────
DOCKER_HUB_USERNAME     刚刚
DOCKER_HUB_TOKEN        刚刚
SERVER_HOST             刚刚
SERVER_USER             刚刚
SERVER_SSH_KEY          刚刚
─────────────────────────────────────────
```

---

## 下一步

配置完成后：

1. 查看主文档：`docs/github-actions-deployment.md`
2. 触发一次构建测试
3. 在服务器上使用预构建镜像

祝使用愉快！🎉
