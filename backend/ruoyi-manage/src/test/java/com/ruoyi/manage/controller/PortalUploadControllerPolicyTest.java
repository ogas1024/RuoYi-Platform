package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.IOssService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PortalUploadControllerPolicyTest {

    @Mock
    private IOssService ossService;

    @InjectMocks
    private PortalUploadController controller;

    private Map<String, Object> ok(String url) {
        Map<String, Object> m = new HashMap<>();
        m.put("url", url);
        return m;
    }

    @BeforeEach
    void setup() {
        // no-op
    }

    @Test
    void default_image_policy_success() {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[1024]);
        Mockito.when(ossService.upload(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(ok("https://oss/mock/a.png"));
        AjaxResult res = controller.upload(file, null, "lostfound", true);
        Assertions.assertEquals(200, (int) res.get("code"));
    }

    @Test
    void default_image_policy_invalid_ext() {
        MockMultipartFile file = new MockMultipartFile("file", "a.gif", "image/gif", new byte[1024]);
        AjaxResult res = controller.upload(file, null, "lostfound", true);
        Assertions.assertEquals(400, (int) res.get("code"));
        Assertions.assertTrue(String.valueOf(res.get("msg")).contains("jpg"));
    }

    @Test
    void library_pdf_oversize() {
        byte[] big = new byte[101 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile("file", "b.pdf", "application/pdf", big);
        AjaxResult res = controller.upload(file, "library.pdf", "library/pdf", true);
        Assertions.assertEquals(400, (int) res.get("code"));
    }

    @Test
    void resource_archive_invalid_ext() {
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", "text/plain", new byte[1024]);
        AjaxResult res = controller.upload(file, "resource.archive", "resource", true);
        Assertions.assertEquals(400, (int) res.get("code"));
        Assertions.assertTrue(String.valueOf(res.get("msg")).contains("不支持的文件类型"));
    }

    @Test
    void resource_archive_success() {
        MockMultipartFile file = new MockMultipartFile("file", "r.zip", "application/zip", new byte[1024]);
        Mockito.when(ossService.upload(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(ok("https://oss/mock/r.zip"));
        AjaxResult res = controller.upload(file, "resource.archive", "resource", true);
        Assertions.assertEquals(200, (int) res.get("code"));
    }
}

