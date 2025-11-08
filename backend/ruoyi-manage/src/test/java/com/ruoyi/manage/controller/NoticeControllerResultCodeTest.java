package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.service.INoticeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class NoticeControllerResultCodeTest {

    static class StubService implements INoticeService {
        @Override
        public java.util.List<com.ruoyi.manage.domain.Notice> list(com.ruoyi.manage.domain.Notice query) {
            return null;
        }

        @Override
        public java.util.Map<String, Object> getDetailAndRecordRead(Long id) {
            return null;
        }

        @Override
        public int add(com.ruoyi.manage.domain.Notice notice) {
            return 0;
        }

        @Override
        public int edit(com.ruoyi.manage.domain.Notice notice) {
            return 0;
        }

        @Override
        public int removeByIds(Long[] ids) {
            return 0;
        }

        @Override
        public int publish(Long id) {
            return -409;
        } // 模拟状态冲突

        @Override
        public int retract(Long id) {
            return -404;
        } // 模拟不存在

        @Override
        public int pin(Long id, boolean pinned) {
            return -409;
        } // 模拟状态冲突
    }

    @Test
    void controller_maps_negative_codes() throws Exception {
        NoticeController c = new NoticeController();
        // 通过反射注入 stub service
        Field f = NoticeController.class.getDeclaredField("noticeService");
        f.setAccessible(true);
        f.set(c, new StubService());

        AjaxResult r1 = c.publish(1L);
        Assertions.assertEquals(409, r1.get(AjaxResult.CODE_TAG));

        AjaxResult r2 = c.retract(2L);
        Assertions.assertEquals(404, r2.get(AjaxResult.CODE_TAG));

        AjaxResult r3 = c.pin(3L, new NoticeController.PinBody());
        Assertions.assertEquals(409, r3.get(AjaxResult.CODE_TAG));
    }
}

