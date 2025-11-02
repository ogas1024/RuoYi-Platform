package com.ruoyi.manage.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CourseResourceLog;
import com.ruoyi.manage.mapper.CourseResourceLogMapper;
import com.ruoyi.manage.mapper.CourseResourceMapper;
import com.ruoyi.manage.service.impl.CourseResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseResourceServiceImplTest {

    @Mock
    private CourseResourceMapper mapper;
    @Mock
    private CourseResourceLogMapper logMapper;

    @InjectMocks
    private CourseResourceServiceImpl service;

    @BeforeEach
    void setUpSecurity() {
        SysUser user = new SysUser();
        user.setUserId(123L);
        user.setUserName("tester");
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(123L);
        loginUser.setUser(user);
        loginUser.setPermissions(Collections.emptySet());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void insert_shouldSetPendingAndLog() {
        CourseResource data = new CourseResource();
        data.setCourseId(4101L);
        data.setMajorId(4L);
        data.setResourceName("测试");
        data.setResourceType(0);
        data.setFileUrl("http://oss/file.zip");
        data.setFileHash("sha256:xx");
        data.setFileSize(1000L);
        data.setDescription("desc");

        when(mapper.insert(any())).thenAnswer(inv -> {
            CourseResource arg = inv.getArgument(0);
            arg.setId(1L);
            return 1;
        });
        when(logMapper.insert(any(CourseResourceLog.class))).thenReturn(1);

        int rows = service.insert(data);
        assertEquals(1, rows);
        assertEquals(0, data.getStatus()); // 待审
        verify(mapper, times(1)).insert(any());
        verify(logMapper, atLeastOnce()).insert(any());
    }

    @Test
    void delete_shouldRejectApprovedForStudent() {
        CourseResource r = new CourseResource();
        r.setId(99L);
        r.setUploaderId(123L);
        r.setStatus(1); // 已通过
        when(mapper.selectById(99L)).thenReturn(r);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.deleteByIds(new Long[]{99L}, 123L, false));
        assertTrue(ex.getMessage().contains("已通过资源"));
        verify(mapper, never()).deleteById(anyLong());
    }

    @Test
    void approve_shouldUpdateAndLog() {
        when(mapper.approve(eq(7L), anyString(), any())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);
        int rows = service.approve(7L, "auditor");
        assertEquals(1, rows);
        verify(mapper, times(1)).approve(eq(7L), anyString(), any());
        verify(logMapper, atLeastOnce()).insert(any());
    }

    @Test
    void reject_shouldUpdateAndLog() {
        when(mapper.reject(eq(8L), anyString(), any(), anyString())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);
        int rows = service.reject(8L, "lead", "内容不合规");
        assertEquals(1, rows);
        verify(mapper, times(1)).reject(eq(8L), anyString(), any(), anyString());
        verify(logMapper, atLeastOnce()).insert(any());
    }
}
