# 模块：OSS 上传

## 概览
- 业务说明：提供通用的阿里云 OSS 文件上传能力，支持图片/文档等常见类型，统一对象命名，返回公开 URL 或私有预签名 URL。
- 角色权限：`manage:upload:oss`（按钮/接口权限一致）
- 依赖表：无（凭据不入库）

## 路由清单

### 接口：上传文件
- 方法：POST
- 路径：`/manage/upload/oss`
- 权限：`manage:upload:oss`
- 请求（multipart/form-data）
  - file (file) 必需：上传文件
  - dir (string) 可选：业务子目录，如 `images/avatar`
  - publicUrl (boolean) 可选：是否返回公开 URL，默认 false 返回预签名 URL
- 响应
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "url": "https://cdn.example.com/uploads/images/2024/10/16/xxxx.png",
    "bucket": "bucket-name",
    "objectKey": "uploads/images/2024/10/16/xxxx.png",
    "size": 12345,
    "contentType": "image/png"
  }
}
```

### 常见错误码
- 401 未授权/未登录
- 403 权限不足（缺少 `manage:upload:oss`）
- 400 参数错误：上传文件为空、类型不允许、超出大小限制
- 500 上传失败：OSS 客户端异常

## 备注
- 命名规则：`basePath/yyyy/MM/dd/uuid.ext`，其中 basePath 默认 `uploads`，可在服务端按需设置 `dir` 子路径。
- 类型与大小：默认允许 `jpg/png/gif/jpeg/webp/pdf/doc/xls/ppt/zip` 等；大小默认 50MB，可在环境变量配置。
- 私有桶：默认返回 1 小时预签名 URL；若需长期公开访问，请设置 `customDomain` 并将对象或桶设为公共读。

