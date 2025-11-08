package com.ruoyi.manage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * 阿里云 OSS 配置项（通过环境变量/配置文件注入）。
 * 建议仅通过环境变量设置，避免将密钥写入仓库：
 * - ALIYUN_OSS_ENDPOINT
 * - ALIYUN_OSS_ACCESS_KEY_ID
 * - ALIYUN_OSS_ACCESS_KEY_SECRET
 * - ALIYUN_OSS_BUCKET
 * 可选：
 * - ALIYUN_OSS_CUSTOM_DOMAIN（CDN/自定义域名，形如：https://cdn.example.com）
 * - ALIYUN_OSS_BASE_PATH（对象前缀，如：app/uploads）
 * - ALIYUN_OSS_PRESIGNED_EXPIRE_SECONDS（私有桶预签名有效期）
 * - ALIYUN_OSS_ALLOWED_EXTENSIONS（逗号分隔扩展名白名单）
 * - ALIYUN_OSS_MAX_SIZE_MB（大小上限，MB）
 * - ALIYUN_OSS_SSE（服务器端加密：AES256 或留空）
 */
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    /**
     * 是否启用 OSS 客户端（未配置密钥时可禁用以避免启动失败）
     */
    private boolean enabled = false;

    /**
     * 例如：https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    /**
     * 桶名
     */
    private String bucket;

    /**
     * 自定义域名（优先用于拼接公开 URL）
     */
    private String customDomain;
    /**
     * 业务前缀（如 app/uploads），最终 key 规则：basePath/yyyy/MM/dd/uuid.ext
     */
    private String basePath = "uploads";

    /**
     * 私有桶预签名 URL 有效期（秒）
     */
    private long presignedExpireSeconds = 3600;

    /**
     * 允许的扩展名白名单（小写，无点）
     */
    private List<String> allowedExtensions = Arrays.asList(
            // 对齐 RuoYi 默认 + 常见文档
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "mp3", "mp4", "avi", "mov",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md",
            "zip", "rar", "7z"
    );

    /**
     * 单文件大小上限（MB）
     */
    private long maxSizeMb = 50;

    /**
     * 服务器端加密（"AES256" 或空）
     */
    private String sse;
}
