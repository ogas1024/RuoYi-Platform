package com.ruoyi.manage.service.impl;

import com.aliyun.oss.OSS;
import com.ruoyi.manage.config.OssProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class OssServiceImplTest {

    private OSS oss;
    private OssProperties props;
    private OssServiceImpl service;

    @BeforeEach
    void setUp() {
        oss = Mockito.mock(OSS.class);
        props = new OssProperties();
        props.setEnabled(true);
        props.setEndpoint("https://oss-cn-hangzhou.aliyuncs.com");
        props.setAccessKeyId("ak");
        props.setAccessKeySecret("sk");
        props.setBucket("bucket-test");
        props.setCustomDomain("https://cdn.example.com");
        props.setAllowedExtensions(Arrays.asList("png", "jpg"));
        props.setBasePath("uploads");
        service = new OssServiceImpl(oss, props);
    }

    @Test
    void upload_success_publicUrl() {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[]{1, 2, 3});

        Map<String, Object> res = service.upload(file, "images", true);

        Mockito.verify(oss, Mockito.times(1))
                .putObject(Mockito.eq("bucket-test"), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());

        Assertions.assertTrue(res.get("url").toString().startsWith("https://cdn.example.com/"));
        Assertions.assertEquals("bucket-test", res.get("bucket"));
        Assertions.assertEquals("image/png", res.get("contentType"));
    }

    @Test
    void upload_fail_extension_not_allowed() {
        MockMultipartFile file = new MockMultipartFile("file", "a.exe", "application/octet-stream", new byte[]{1});
        Assertions.assertThrows(RuntimeException.class, () -> service.upload(file, null, false));
    }
}

