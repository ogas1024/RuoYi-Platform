package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.service.IOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * OSS 上传接口。
 * 路由前缀遵循 RuoYi 约定：/manage/upload
 */
@RestController
@RequestMapping("/manage/upload")
@RequiredArgsConstructor
public class UploadController {

    private final IOssService ossService;

    /**
     * 上传文件至 OSS。
     *
     * @param file      表单文件
     * @param dir       业务子目录（可选）
     * @param publicUrl 是否返回公开 URL（默认 false 返回预签名 URL）
     */
    @PostMapping("/oss")
    @PreAuthorize("@ss.hasPermi('manage:upload:oss')")
    public AjaxResult upload(@RequestPart("file") MultipartFile file,
                             @RequestParam(value = "dir", required = false) String dir,
                             @RequestParam(value = "publicUrl", required = false, defaultValue = "false") boolean publicUrl) {
        Map<String, Object> data = ossService.upload(file, dir, publicUrl);
        return AjaxResult.success(data);
    }
}

