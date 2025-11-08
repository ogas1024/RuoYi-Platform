package com.ruoyi.manage.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.config.OssProperties;
import com.ruoyi.manage.service.IOssService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云 OSS 上传实现，遵循官方最佳实践：
 * - 统一对象命名：basePath/yyyy/MM/dd/uuid.ext
 * - 校验大小与扩展名
 * - 设置 Content-Type、Content-Disposition（inline），便于浏览器预览
 * - 可选服务器端加密（SSE）
 * - 私有桶返回预签名 URL（默认 1 小时）
 */
@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class OssServiceImpl implements IOssService {

    private final OSS ossClient;
    private final OssProperties props;

    @Override
    public Map<String, Object> upload(MultipartFile file, String dir, boolean publicUrl) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件为空");
        }
        ensureProps();

        String original = Objects.requireNonNull(file.getOriginalFilename(), "missing filename");
        String ext = getExtension(original);
        validateExtension(ext);
        validateSize(file);

        String objectKey = buildObjectKey(ext, dir);

        ObjectMetadata meta = new ObjectMetadata();
        String contentType = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");
        meta.setContentType(contentType);
        // inline 预览，保留原文件名
        // 兼容 JDK8：URLEncoder(String, String) 抛出受检异常
        String encodedName;
        try {
            encodedName = java.net.URLEncoder.encode(original, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 总是存在；理论上不会到这里，降级为原始文件名
            encodedName = original;
        }
        meta.setContentDisposition("inline; filename=\"" + encodedName + "\"");
        if (StringUtils.isNotBlank(props.getSse())) {
            meta.setServerSideEncryption(props.getSse());
        }

        // 计算 SHA-256（供资源去重使用）
        String sha256Hex;
        try (InputStream calcIn = file.getInputStream()) {
            sha256Hex = sha256Hex(calcIn);
        } catch (Exception e) {
            throw new ServiceException("计算文件哈希失败：" + e.getMessage());
        }

        String etag = null;
        try (InputStream in = file.getInputStream()) {
            // putObject 会读取输入流，此处由 try-with-resources 负责关闭
            PutObjectResult putRes = ossClient.putObject(props.getBucket(), objectKey, in, meta);
            if (putRes != null) {
                etag = putRes.getETag();
            }
        } catch (Exception e) {
            throw new ServiceException("上传失败：" + e.getMessage());
        }

        String url = buildAccessUrl(objectKey, publicUrl);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("url", url);
        result.put("bucket", props.getBucket());
        result.put("objectKey", objectKey);
        result.put("size", file.getSize());
        result.put("contentType", contentType);
        // 前端课程资源提交将优先取 data.sha256，其次 data.etag
        result.put("sha256", "sha256:" + sha256Hex);
        if (etag != null) {
            result.put("etag", etag);
        }
        return result;
    }

    private void ensureProps() {
        if (!props.isEnabled()) {
            throw new ServiceException("OSS 未启用，请设置 aliyun.oss.enabled=true 并配置密钥");
        }
        if (StringUtils.isAnyBlank(props.getEndpoint(), props.getAccessKeyId(), props.getAccessKeySecret(), props.getBucket())) {
            throw new ServiceException("OSS 配置不完整，请设置 endpoint/accessKeyId/accessKeySecret/bucket 环境变量");
        }
    }

    private void validateSize(MultipartFile file) {
        long maxBytes = props.getMaxSizeMb() * 1024 * 1024L;
        if (file.getSize() > maxBytes) {
            throw new ServiceException("文件过大，限制 " + props.getMaxSizeMb() + "MB");
        }
    }

    private void validateExtension(String ext) {
        if (StringUtils.isBlank(ext)) {
            throw new ServiceException("无法识别的文件扩展名");
        }
        List<String> allow = props.getAllowedExtensions();
        if (allow == null || allow.isEmpty()) return; // 空白名单时不校验
        if (!allow.contains(ext.toLowerCase(Locale.ROOT))) {
            throw new ServiceException("不支持的文件类型：" + ext);
        }
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private String buildObjectKey(String ext, String dir) {
        LocalDate d = LocalDate.now();
        String datePath = String.format("%04d/%02d/%02d", d.getYear(), d.getMonthValue(), d.getDayOfMonth());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String base = StringUtils.isNotBlank(props.getBasePath()) ? props.getBasePath().replaceAll("^/+|/+$", "") : "uploads";
        String sub = StringUtils.isNotBlank(dir) ? dir.replaceAll("^/+|/+$", "") + "/" : "";
        String name = uuid + (StringUtils.isBlank(ext) ? "" : ("." + ext));
        return base + "/" + sub + datePath + "/" + name;
    }

    private String buildAccessUrl(String objectKey, boolean publicUrl) {
        // 优先自定义域名（如 CDN 域）
        if (publicUrl) {
            if (StringUtils.isNotBlank(props.getCustomDomain())) {
                String domain = props.getCustomDomain().replaceAll("/+$", "");
                return domain + "/" + objectKey;
            }
            // 使用默认域名（仅适用于公共读桶）
            // https://<bucket>.<endpoint-host>/<objectKey>
            String endpointHost = props.getEndpoint().replaceFirst("^https?://", "");
            return String.format("https://%s.%s/%s", props.getBucket(), endpointHost, objectKey);
        }
        // 私有桶：返回预签名 URL
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(props.getPresignedExpireSeconds()));
        URL signed = ossClient.generatePresignedUrl(props.getBucket(), objectKey, expiration, HttpMethod.GET);
        return signed.toString();
    }

    private static String sha256Hex(InputStream in) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) != -1) {
            md.update(buf, 0, len);
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}
