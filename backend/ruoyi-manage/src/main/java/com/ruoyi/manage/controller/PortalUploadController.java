package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.IOssService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 门户上传接口（仅图片）。
 * 避免普通用户依赖管理端按钮权限 manage:upload:oss。
 */
@RestController
@RequestMapping("/portal/upload")
@RequiredArgsConstructor
public class PortalUploadController {

    private final IOssService ossService;

    private static final Set<String> ALLOW_EXT = new HashSet<>(Arrays.asList("jpg","jpeg","png","webp"));
    private static final long MAX_SIZE_BYTES = 2L * 1024 * 1024; // 2MB

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/oss")
    public AjaxResult uploadImage(@RequestPart("file") MultipartFile file,
                                  @RequestParam(value = "dir", required = false, defaultValue = "lostfound") String dir) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("文件为空");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            return AjaxResult.error(400, "图片大小不能超过2MB");
        }
        // 校验图片类型
        String original = file.getOriginalFilename();
        String ext = getExt(original);
        if (!ALLOW_EXT.contains(ext)) {
            return AjaxResult.error(400, "仅支持 jpg/jpeg/png/webp 图片");
        }
        String contentType = StringUtils.defaultString(file.getContentType(), "");
        if (!contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return AjaxResult.error(400, "仅支持图片上传");
        }
        try {
            Map<String, Object> data = ossService.upload(file, dir, true);
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
}

