package com.ruoyi.manage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * OSS 上传服务接口。
 * 约定：
 * - dir：业务子目录，内部会进行简单安全校验与规范化；
 * - publicUrl=true 时返回公开可直达的 URL，否则返回限时预签名地址；
 * - 返回包含存储位置与便于前端展示的关键信息。
 */
public interface IOssService {

    /**
     * 上传文件至 OSS。
     *
     * @param file      表单文件
     * @param dir       业务子目录（可选），会拼接在 basePath 之后
     * @param publicUrl 是否返回公开 URL（true：按自定义域名或公共域名拼接；false：返回限时预签名）
     * @return 包含 url、bucket、objectKey、size、contentType 等信息
     */
    Map<String, Object> upload(MultipartFile file, String dir, boolean publicUrl);
}
