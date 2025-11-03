package com.ruoyi.manage.service;

import com.ruoyi.manage.mapper.NoticeAttachmentMapper;
import com.ruoyi.manage.mapper.NoticeMapper;
import com.ruoyi.manage.mapper.NoticeReadMapper;
import com.ruoyi.manage.mapper.NoticeScopeMapper;
import com.ruoyi.manage.service.impl.NoticeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    private NoticeMapper noticeMapper;
    @Mock
    private NoticeAttachmentMapper attachmentMapper;
    @Mock
    private NoticeScopeMapper scopeMapper;
    @Mock
    private NoticeReadMapper readMapper;

    @InjectMocks
    private NoticeServiceImpl service;

    @BeforeEach
    void setup() {
        // 无需显式赋值，@InjectMocks 完成注入
    }

    @Test
    void publish_success_and_failure() {
        Mockito.when(noticeMapper.publish(1L)).thenReturn(1);
        Mockito.when(noticeMapper.publish(2L)).thenReturn(0);

        Assertions.assertEquals(1, service.publish(1L), "发布成功应返回1");
        Assertions.assertEquals(0, service.publish(2L), "发布失败应返回0");
        Mockito.verify(noticeMapper, Mockito.times(1)).publish(1L);
        Mockito.verify(noticeMapper, Mockito.times(1)).publish(2L);
    }

    @Test
    void retract_success_and_failure() {
        Mockito.when(noticeMapper.retract(3L)).thenReturn(1);
        Mockito.when(noticeMapper.retract(4L)).thenReturn(0);

        Assertions.assertEquals(1, service.retract(3L));
        Assertions.assertEquals(0, service.retract(4L));
        Mockito.verify(noticeMapper).retract(3L);
        Mockito.verify(noticeMapper).retract(4L);
    }

    @Test
    void pin_true_calls_pin_false_calls_unpin() {
        Mockito.when(noticeMapper.pin(5L)).thenReturn(1);
        Mockito.when(noticeMapper.unpin(6L)).thenReturn(1);

        Assertions.assertEquals(1, service.pin(5L, true));
        Assertions.assertEquals(1, service.pin(6L, false));

        Mockito.verify(noticeMapper).pin(5L);
        Mockito.verify(noticeMapper).unpin(6L);
    }

    @Test
    void removeByIds_calls_mapper() {
        Long[] ids = new Long[]{7L, 8L};
        Mockito.when(noticeMapper.deleteByIds(ids)).thenReturn(2);
        Assertions.assertEquals(2, service.removeByIds(ids));
        Mockito.verify(noticeMapper).deleteByIds(ids);
    }

    @Test
    void publish_idempotent_when_already_published() {
        com.ruoyi.manage.domain.Notice n = new com.ruoyi.manage.domain.Notice();
        n.setStatus(1);
        Mockito.when(noticeMapper.selectById(9L, null)).thenReturn(n);

        int ret = service.publish(9L);
        Assertions.assertEquals(1, ret, "已发布再次发布应幂等成功");
        Mockito.verify(noticeMapper, Mockito.never()).publish(9L);
    }

    @Test
    void retract_conflict_when_not_published() {
        com.ruoyi.manage.domain.Notice n = new com.ruoyi.manage.domain.Notice();
        n.setStatus(2); // 已撤回
        Mockito.when(noticeMapper.selectById(10L, null)).thenReturn(n);

        int ret = service.retract(10L);
        Assertions.assertEquals(-409, ret, "非已发布状态撤回应返回冲突");
        Mockito.verify(noticeMapper, Mockito.never()).retract(10L);
    }

    @Test
    void pin_conflict_when_not_published() {
        com.ruoyi.manage.domain.Notice n = new com.ruoyi.manage.domain.Notice();
        n.setStatus(0); // 草稿
        Mockito.when(noticeMapper.selectById(11L, null)).thenReturn(n);

        int ret = service.pin(11L, true);
        Assertions.assertEquals(-409, ret, "非已发布状态置顶应返回冲突");
        Mockito.verify(noticeMapper, Mockito.never()).pin(11L);
    }

    @Test
    void getDetailAndRecordRead_incr_only_on_first_time() {
        // 构造登录态
        SysUser sysUser = new SysUser();
        sysUser.setUserId(100L);
        sysUser.setUserName("tester");
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(sysUser);
        loginUser.setUserId(100L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, null)
        );

        // stub 基本数据
        com.ruoyi.manage.domain.Notice n = new com.ruoyi.manage.domain.Notice();
        n.setId(12L);
        n.setStatus(1);
        n.setDelFlag("0");
        Mockito.when(noticeMapper.selectById(12L, 100L)).thenReturn(n);
        Mockito.when(attachmentMapper.selectByNoticeId(12L)).thenReturn(java.util.Collections.emptyList());
        Mockito.when(scopeMapper.selectByNoticeId(12L)).thenReturn(java.util.Collections.emptyList());

        // 第一次阅读：insertIgnore=1，应自增
        Mockito.when(readMapper.insertIgnore(Mockito.any())).thenReturn(1);
        service.getDetailAndRecordRead(12L);
        Mockito.verify(noticeMapper, Mockito.times(1)).incrReadCount(12L);

        // 第二次阅读：insertIgnore=0，不应再次自增
        Mockito.when(readMapper.insertIgnore(Mockito.any())).thenReturn(0);
        service.getDetailAndRecordRead(12L);
        Mockito.verify(noticeMapper, Mockito.times(1)).incrReadCount(12L);
    }
}
