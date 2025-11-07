package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.service.ILostItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LostFoundControllerTest {

    @Mock
    ILostItemService service;

    @InjectMocks
    LostFoundController controller;

    @BeforeEach
    void setUp() {
        // No-op: @InjectMocks injects @Mock into controller
    }

    @Test
    void approve_success() {
        when(service.approve(eq(1L), any())).thenReturn(1);
        AjaxResult res = controller.approve(1L);
        assertNotNull(res);
        assertEquals(200, res.get(AjaxResult.CODE_TAG));
    }

    @Test
    void reject_reason_required_failure() {
        // body 为 null，应返回错误
        AjaxResult res = controller.reject(1L, null);
        assertNotNull(res);
        // BaseController.error(String) -> 500
        assertEquals(500, res.get(AjaxResult.CODE_TAG));
    }

    @Test
    void offline_conflict_returns_409() {
        when(service.offline(eq(2L), any(), any())).thenReturn(0);
        LostFoundController.ReasonBody body = new LostFoundController.ReasonBody();
        body.reason = "不合规";
        AjaxResult res = controller.offline(2L, body);
        assertNotNull(res);
        assertEquals(409, res.get(AjaxResult.CODE_TAG));
    }

    @Test
    void permission_annotation_present_on_list() throws Exception {
        Method m = LostFoundController.class.getMethod("list", com.ruoyi.manage.domain.LostItem.class);
        PreAuthorize ann = m.getAnnotation(PreAuthorize.class);
        assertNotNull(ann, "@PreAuthorize missing on list method");
        assertTrue(ann.value().contains("manage:lostfound:list"));
    }
}

