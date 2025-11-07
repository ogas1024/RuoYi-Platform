package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.ISurveyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PortalSurveyControllerSubmitTest {

    @Mock
    private ISurveyService surveyService;

    @InjectMocks
    private PortalSurveyController controller;

    @Test
    void submit_success() {
        Map<String, Object> body = new HashMap<>();
        java.util.Map<String, Object> one = new java.util.HashMap<>();
        one.put("itemId", 1L);
        one.put("valueText", "hi");
        body.put("answers", java.util.Arrays.asList(one));
        Mockito.when(surveyService.submit(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(1);
        AjaxResult res = controller.submit(1L, body);
        Assertions.assertEquals(200, (int) res.get("code"));
    }

    @Test
    void submit_failure_past_deadline() {
        Map<String, Object> body = new HashMap<>();
        body.put("answers", java.util.Collections.emptyList());
        Mockito.when(surveyService.submit(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()))
                .thenThrow(new ServiceException("已过截止时间，无法提交"));
        AjaxResult res = controller.submit(1L, body);
        Assertions.assertEquals(400, (int) res.get("code"));
        Assertions.assertTrue(String.valueOf(res.get("msg")).contains("截止"));
    }
}
