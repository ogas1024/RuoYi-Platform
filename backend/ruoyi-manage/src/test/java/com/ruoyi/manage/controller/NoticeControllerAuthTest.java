package com.ruoyi.manage.controller;

import com.ruoyi.manage.domain.Notice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

class NoticeControllerAuthTest {

    @Test
    void list_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("list", Notice.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno, "@PreAuthorize 缺失");
        Assertions.assertTrue(anno.value().contains("manage:notice:list"));
    }

    @Test
    void get_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("getInfo", Long.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:get"));
    }

    @Test
    void add_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("add", Notice.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:add"));
    }

    @Test
    void edit_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("edit", Notice.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:edit"));
    }

    @Test
    void remove_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("remove", Long[].class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:remove"));
    }

    @Test
    void publish_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("publish", Long.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:publish"));
    }

    @Test
    void retract_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("retract", Long.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:publish"));
    }

    @Test
    void pin_has_permit() throws NoSuchMethodException {
        Method m = NoticeController.class.getDeclaredMethod("pin", Long.class, NoticeController.PinBody.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:notice:pin"));
    }
}

