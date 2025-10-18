package com.ruoyi.manage.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

class UploadControllerAuthTest {

    @Test
    void upload_has_preAuthorize_annotation() throws NoSuchMethodException {
        Method m = UploadController.class.getDeclaredMethod("upload", org.springframework.web.multipart.MultipartFile.class, String.class, boolean.class);
        PreAuthorize anno = m.getAnnotation(PreAuthorize.class);
        Assertions.assertNotNull(anno, "@PreAuthorize 缺失");
        Assertions.assertTrue(anno.value().contains("manage:upload:oss"));
    }
}

