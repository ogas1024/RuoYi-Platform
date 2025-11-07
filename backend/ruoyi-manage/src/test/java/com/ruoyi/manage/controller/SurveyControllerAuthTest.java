package com.ruoyi.manage.controller;

import com.ruoyi.manage.domain.Survey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

class SurveyControllerAuthTest {

    @Test
    void list_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("list", Survey.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:list"));
    }

    @Test
    void get_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("getInfo", Long.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:query"));
    }

    @Test
    void add_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("add", Survey.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:add"));
    }

    @Test
    void archive_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("archive", Long.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:archive"));
    }

    @Test
    void extend_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("extend", Long.class, java.util.Map.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:extend"));
    }

    @Test
    void pin_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("pin", Long.class, java.util.Map.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:pin"));
    }

    @Test
    void edit_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("edit", com.ruoyi.manage.domain.Survey.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:edit"));
    }

    @Test
    void publish_has_permit() throws NoSuchMethodException {
        Method m = SurveyController.class.getDeclaredMethod("publish", Long.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno);
        Assertions.assertTrue(anno.value().contains("manage:survey:publish"));
    }
}
