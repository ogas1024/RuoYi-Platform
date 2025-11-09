package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.IOssService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 门户上传网关（场景化策略）。
 * 说明：
 * - 统一入口 /portal/upload/oss，仅要求登录（isAuthenticated）。
 * - 通过 scene 参数区分业务场景，控制白名单、大小上限与默认目录/URL 策略。
 * - 向后兼容：若未传 scene，沿用“仅图片、≤2MB、dir=lostfound”的旧行为。
 */
@RestController
@RequestMapping("/portal/upload")
@RequiredArgsConstructor
public class PortalUploadController {

    private final IOssService ossService;

    // 默认图片策略（兼容旧行为）
    private static final Set<String> DEFAULT_IMG_EXT = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));
    private static final long DEFAULT_IMG_MAX = 2L * 1024 * 1024; // 2MB

    // 场景化策略
    private static final class Policy {
        final Set<String> exts;      // 允许的扩展名（小写）
        final long maxBytes;         // 大小上限
        final String defaultDir;     // 默认目录
        final boolean requireImage;  // 是否要求 image/* MIME（仅图片类）
        final boolean defaultPublicUrl; // 默认返回公开URL

        Policy(Set<String> exts, long maxBytes, String dir, boolean requireImage, boolean publicUrl) {
            this.exts = exts;
            this.maxBytes = maxBytes;
            this.defaultDir = dir;
            this.requireImage = requireImage;
            this.defaultPublicUrl = publicUrl;
        }
    }

    private static final java.util.Map<String, Policy> POLICIES = new java.util.HashMap<>();

    static {
        // 失物招领-图片
        POLICIES.put("lostfound.image", new Policy(new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp")), 2L * 1024 * 1024, "lostfound", true, true));
        // 通知公告-附件（门户一般无需，此处预留）
        POLICIES.put("notice.attachment", new Policy(new HashSet<>(Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "png", "jpg", "jpeg", "zip")), 20L * 1024 * 1024, "notice", false, true));
        // 数字图书馆-PDF 主文件
        POLICIES.put("library.pdf", new Policy(new HashSet<>(Arrays.asList("pdf")), 100L * 1024 * 1024, "library/pdf", false, true));
        // 数字图书馆-补充格式
        POLICIES.put("library.extra", new Policy(new HashSet<>(Arrays.asList("epub", "mobi", "zip")), 100L * 1024 * 1024, "library/extra", false, true));
        // 课程资源-压缩包
        POLICIES.put("resource.archive", new Policy(new HashSet<>(Arrays.asList("zip", "rar", "7z", "tar", "gz", "bz2", "xz")), 100L * 1024 * 1024, "resource", false, true));
    }

    /**
     * 门户统一上传入口（阿里云 OSS）
     * 路径：POST /portal/upload/oss
     * 权限：已登录（isAuthenticated）
     * 说明：通过 scene 选择策略；未传 scene 时按兼容图片策略处理。
     *
     * @param file      表单文件
     * @param scene     业务场景，如 lostfound.image/library.pdf/resource.archive 等（可选）
     * @param dir       目标目录（可选，覆盖默认）
     * @param publicUrl 是否返回公开URL（默认按策略）
     * @param request   HttpServletRequest（用于兜底解析 scene）
     * @return 上传结果（包含 URL、路径等）
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/oss")
    public AjaxResult upload(@RequestPart("file") MultipartFile file,
                             @RequestParam(value = "scene", required = false) String scene,
                             @RequestParam(value = "dir", required = false) String dir,
                             @RequestParam(value = "publicUrl", required = false) Boolean publicUrl,
                             HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("文件为空");
        }

        // 选择策略：未传 scene 时使用兼容图片策略；传了但未知则报错，避免误用
        String sc = scene;
        if ((sc == null || sc.trim().isEmpty()) && request != null) {
            try {
                // 兜底从原始请求参数中获取 scene/policy（忽略大小写）
                java.util.Map<String, String[]> params = request.getParameterMap();
                for (String key : params.keySet()) {
                    if (key != null && ("scene".equalsIgnoreCase(key) || "policy".equalsIgnoreCase(key))) {
                        String val = request.getParameter(key);
                        if (val != null && !val.trim().isEmpty()) {
                            sc = val;
                            break;
                        }
                    }
                }
                // 进一步兜底：从原始查询串解析（避免某些客户端未正确分离参数）
                if ((sc == null || sc.trim().isEmpty())) {
                    String qs = request.getQueryString();
                    if (qs != null) {
                        for (String part : qs.split("[&]")) {
                            if (part.toLowerCase(java.util.Locale.ROOT).startsWith("scene=")) {
                                sc = part.substring(part.indexOf('=') + 1);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (sc != null) {
            sc = sc.trim();
            // 若误把“scene=xxx, dir=...”整体填入一个字段，这里剥离“scene=”前缀与后续其它键值
            int eq = sc.indexOf('=');
            if (eq >= 0) sc = sc.substring(eq + 1).trim();
            int amp = sc.indexOf('&');
            if (amp >= 0) sc = sc.substring(0, amp).trim();
            int space = sc.indexOf(' ');
            if (space >= 0) sc = sc.substring(0, space).trim();
            int comma = sc.indexOf(',');
            if (comma >= 0) sc = sc.substring(0, comma).trim();
            sc = sc.replace('_', '.');
            sc = sc.toLowerCase(Locale.ROOT);
        }
        Policy policy;
        boolean compatibleImage;
        if (sc == null) {
            compatibleImage = true;
            policy = new Policy(DEFAULT_IMG_EXT, DEFAULT_IMG_MAX, "lostfound", true, true);
        } else {
            policy = POLICIES.get(sc);
            if (policy == null) {
                return AjaxResult.error(400, "未知的上传场景: " + sc);
            }
            compatibleImage = false;
        }

        // 大小校验
        if (file.getSize() > policy.maxBytes) {
            String tip = compatibleImage ? "图片大小不能超过2MB" : "文件大小超过限制";
            return AjaxResult.error(400, tip);
        }

        // 扩展名 + MIME 校验
        String original = file.getOriginalFilename();
        String ext = getExt(original);
        if (!policy.exts.contains(ext)) {
            return AjaxResult.error(400, compatibleImage ? "仅支持 jpg/jpeg/png/webp 图片" : "不支持的文件类型");
        }
        String contentType = StringUtils.defaultString(file.getContentType(), "");
        if (policy.requireImage && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return AjaxResult.error(400, "仅支持图片上传");
        }

        // 目录与 URL 策略
        String targetDir = StringUtils.isNotEmpty(dir) ? sanitizeDir(dir) : policy.defaultDir;
        boolean retPublic = publicUrl != null ? publicUrl.booleanValue() : policy.defaultPublicUrl;

        try {
            Map<String, Object> data = ossService.upload(file, targetDir, retPublic);
            return AjaxResult.success(data);
        } catch (ServiceException ex) {
            return AjaxResult.error(500, ex.getMessage());
        }
    }

    private static String getExt(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        if (i < 0 || i == name.length() - 1) return "";
        return name.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    // 目录规范化：去除首尾/，禁止 ..，仅保留安全字符
    private static String sanitizeDir(String dir) {
        String s = dir == null ? "" : dir.trim();
        s = s.replace('\\', '/');
        while (s.startsWith("/")) s = s.substring(1);
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        if (s.contains("..")) throw new ServiceException("非法目录");
        // 简单过滤非常规字符
        s = s.replaceAll("[^a-zA-Z0-9_./-]", "");
        return s.isEmpty() ? "uploads" : s;
    }
}
