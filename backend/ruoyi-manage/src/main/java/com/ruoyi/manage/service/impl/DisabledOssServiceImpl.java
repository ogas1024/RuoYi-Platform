package com.ruoyi.manage.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.IOssService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

/**
 * 当未启用或未配置 OSS 时的占位实现，避免应用启动失败。
 */
@Service
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledOssServiceImpl implements IOssService {

    @Override
    public Map<String, Object> upload(MultipartFile file, String dir, boolean publicUrl) {
        throw new ServiceException("OSS 未启用，请设置 aliyun.oss.enabled=true 并配置必要的 endpoint/ak/sk/bucket");
    }
}

