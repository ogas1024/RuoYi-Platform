# Apifox 指南（手把手）：OSS 文件上传

本指南带你从 0 到 1 完成“获取 token → 配置权限 → 用 Apifox 发起上传 → 验证结果”的全流程。适合第一次实践的同学照着一步步完成。

参考实现代码位置（仅供确认）：
- 控制器与权限：`backend/ruoyi-manage/src/main/java/com/ruoyi/manage/controller/UploadController.java:30`
- 登录发 token：`backend/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLoginController.java:56`
- 放行匿名接口：`backend/ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java:114`
- OSS 配置项：`backend/ruoyi-manage/src/main/java/com/ruoyi/manage/config/OssProperties.java:1`

---

## 0. 环境准备（WSL）
- baseURL: `http://localhost:8080`
- 认证：`Authorization: Bearer <token>`（通过登录接口获取）
- 必要环境变量（不要写入 yml，避免泄露）：
  - `export ALIYUN_OSS_ENABLED=true`
  - `export ALIYUN_OSS_ENDPOINT=https://oss-cn-<region>.aliyuncs.com`
  - `export ALIYUN_OSS_ACCESS_KEY_ID=你的AK`
  - `export ALIYUN_OSS_ACCESS_KEY_SECRET=你的SK`
  - `export ALIYUN_OSS_BUCKET=你的Bucket`
  - 可选：`export ALIYUN_OSS_CUSTOM_DOMAIN=https://cdn.example.com`
  - 可选（默认即可）：`ALIYUN_OSS_ALLOWED_EXTENSIONS`、`ALIYUN_OSS_MAX_SIZE_MB`、`ALIYUN_OSS_PRESIGNED_EXPIRE_SECONDS`

建议做法：在当前终端先 `export ...`，确认无误后再写入 `~/.zshrc` 方便后续自动加载。

启动项目（任选其一）：
- 打包：`cd backend && mvn -T 1C clean package -DskipTests`
- 开发运行：`mvn -f backend/pom.xml spring-boot:run -pl ruoyi-admin -am`

---

## 1. RuoYi 后台权限配置（必须）
接口本身已在控制器声明权限注解：`@PreAuthorize("@ss.hasPermi('manage:upload:oss')")`（见 `UploadController.java:31`）。还需要在后台“菜单/按钮”里声明同名权限并分配给角色：

1) 登录后台 → 系统管理 → 菜单管理 → 新增
- 菜单类型：按钮
- 上级菜单：建议挂到“系统工具/文件管理”或你选的模块菜单下
- 菜单名称：OSS 上传
- 权限标识：`manage:upload:oss`
- 路由地址/组件：留空（按钮无需）
- 显示排序：100，状态：正常

2) 系统管理 → 角色管理 → 选择要测试的角色（如 admin 或自定义角色）→ 分配菜单
- 勾选刚新增的按钮权限 → 保存
- 重新登录使权限生效

前端页面若需要按钮级控制，使用：`v-hasPermi=['manage:upload:oss']`

---

## 2. Apifox 环境与全局变量
1) 新建项目（如：Book MIS）→ 新建环境（local）
- 变量 `baseURL = http://localhost:8080`
- 变量 `token = （留空，登录后自动写入）`

1) 全局请求头（可选，或仅给分组/接口设置）
- `Authorization: Bearer {{token}}`

1) 登录接口的“后执行脚本”用于提取并保存 token：
```
const json = pm.response.json();
pm.environment.set('token', json.token);
```

---

## 3. 获取 token（含验证码两种路径）
RuoYi 默认开启验证码，登录时需带 `code` 与 `uuid`；如你已在“系统设置 → 参数设置”里关闭验证码，可直接走简化路径。

方案 A：验证码开启（默认）
1) GET `/captchaImage`
- 期望响应：`code=200`，`uuid` 存在，`img` 为 base64 图片
- 手动查看验证码：复制 `img` 去任意 base64 图片查看器（或在线工具）得到算式结果（math 模式形如 8+7=？）
2) POST `/login`
- Body(JSON)：`{"username":"admin","password":"admin123","code":"上一步算出的结果","uuid":"上一步的uuid"}`
- 断言：`status=200`、`body.code=200`、`body.token` 存在
- 后执行脚本会把 `token` 写入环境变量

方案 B：验证码关闭（更快）
- 先在后台“系统设置 → 参数设置”将 `sys.account.captchaEnabled` 设为 `false`
- 直接 POST `/login`，Body(JSON)：`{"username":"admin","password":"admin123"}`
- 断言同上，并提取 `token`

注意：不需要 Cookie；后续所有受保护接口都用 `Authorization: Bearer {{token}}` 即可。

---

## 4. 发起上传（multipart/form-data）
接口定义：`POST /manage/upload/oss`（见 `UploadController.java:30`），参数：
- 表单：`file`（必填，选择本地文件）
- Query：`dir`（可选，业务子目录，如 `images/avatar`）
- Query：`publicUrl`（可选，true=返回公开 URL；false=返回预签名 URL；默认 false）

Apifox 操作步骤：
1) 新建接口：POST `{{baseURL}}/manage/upload/oss`
2) Headers：`Authorization: Bearer {{token}}`
3) Params：
- `dir=images`
- `publicUrl=true`（如你配置了 `ALIYUN_OSS_CUSTOM_DOMAIN` 或桶为公共读）
4) Body → `form-data`：
- Key: `file`，类型：File，选择 `/path/to/demo.png`
5) 发送请求

期望响应（示例）：
```
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "url": "https://cdn.example.com/uploads/images/2025/10/18/xxxx.png",
    "bucket": "your-bucket",
    "objectKey": "uploads/images/2025/10/18/xxxx.png",
    "size": 12345,
    "contentType": "image/png"
  }
}
```

---

## 5. 断言与自动校验
在接口的“断言”里新增：
- `status == 200`
- `body.code == 200`
- `body.data.url` 存在
- `body.data.objectKey` 包含 `images/`（或你设置的 dir）

建议把上述断言复制到“私有文件”用例（`publicUrl=false`），额外断言：`body.data.url` 包含 `Signature=`（预签名）

---

## 6. 常见失败与排查清单
- 401：未登录或 token 为空/过期 → 重新执行“获取 token”，确认 `Authorization` 头随请求发送
- 403：角色未分配 `manage:upload:oss` → 菜单管理新增按钮权限并分配到角色，重新登录
- 400：
  - `上传文件为空` → Body 需使用 form-data，键名必须是 `file`
  - `类型不允许` → 检查扩展名是否在白名单（`ALIYUN_OSS_ALLOWED_EXTENSIONS`），文件后缀需小写
  - `超过大小上限` → 调整 `ALIYUN_OSS_MAX_SIZE_MB` 或压缩文件
- 500：
  - `OSS 未启用` → 确认 `ALIYUN_OSS_ENABLED=true`
  - `上传失败：...` → 检查 `endpoint/bucket/AK/SK` 是否匹配同一地域；校验网络连通性
  - 公开 URL 404 → 若未配置自定义域名且桶非公共读，`publicUrl=true` 会返回默认域名，但对象不可匿名访问；可改 `publicUrl=false` 或配置公共读/CDN

---

## 7. 附录：cURL 与前端联调
- 获取 token（验证码关闭情形）：
```
curl -X POST http://localhost:8080/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```
- 上传文件：
```
curl -X POST 'http://localhost:8080/manage/upload/oss?dir=images&publicUrl=true' \
  -H 'Authorization: Bearer <token>' \
  -F 'file=@/path/to/demo.png'
```

前端按钮权限：`v-hasPermi=['manage:upload:oss']`；接口封装：`POST /manage/upload/oss`，`Content-Type: multipart/form-data`。

---

## 小结
- 需要的凭据：仅 `Authorization: Bearer <token>`，无需 Cookie
- 权限位置：Controller 注解 + 菜单管理新增“按钮权限”并分配角色
- Apifox 要点：环境变量 baseURL/token、登录后执行脚本提取 token、form-data 上传

