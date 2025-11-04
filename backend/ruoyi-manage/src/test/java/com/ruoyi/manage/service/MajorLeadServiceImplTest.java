package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.MajorLead;
import com.ruoyi.manage.mapper.MajorLeadMapper;
import com.ruoyi.manage.mapper.SysLinkageMapper;
import com.ruoyi.manage.service.impl.MajorLeadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.ruoyi.common.exception.ServiceException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MajorLeadServiceImplTest {
    private MajorLeadMapper majorLeadMapper;
    private SysLinkageMapper sysLinkageMapper;
    private MajorLeadServiceImpl service;

    @BeforeEach
    void setUp() {
        majorLeadMapper = Mockito.mock(MajorLeadMapper.class);
        sysLinkageMapper = Mockito.mock(SysLinkageMapper.class);
        service = new MajorLeadServiceImpl();
        // 反射注入
        try {
            java.lang.reflect.Field f1 = MajorLeadServiceImpl.class.getDeclaredField("mapper");
            f1.setAccessible(true);
            f1.set(service, majorLeadMapper);
            java.lang.reflect.Field f2 = MajorLeadServiceImpl.class.getDeclaredField("sysLinkageMapper");
            f2.setAccessible(true);
            f2.set(service, sysLinkageMapper);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void insert_grants_role_when_absent() {
        MajorLead ml = new MajorLead();
        ml.setMajorId(1L); ml.setUserId(7L);
        when(sysLinkageMapper.existsUser(7L)).thenReturn(1);
        when(majorLeadMapper.insert(any())).thenReturn(1);
        when(sysLinkageMapper.selectRoleIdByKey("major_lead")).thenReturn(103L);
        when(sysLinkageMapper.existsUserRole(7L, 103L)).thenReturn(0);

        service.insert(ml);

        verify(sysLinkageMapper).insertUserRole(7L, 103L);
    }

    @Test
    void deleteByMajorAndUser_revokes_role_when_no_more_binding() {
        when(majorLeadMapper.deleteByMajorAndUser(1L, 7L)).thenReturn(1);
        when(sysLinkageMapper.selectRoleIdByKey("major_lead")).thenReturn(103L);
        when(majorLeadMapper.countByUserId(7L)).thenReturn(0);

        service.deleteByMajorAndUser(1L, 7L);

        verify(sysLinkageMapper).deleteUserRole(7L, 103L);
    }

    @Test
    void deleteByIds_revokes_role_when_no_more_binding() {
        Long[] ids = new Long[]{11L, 12L};
        when(majorLeadMapper.selectUserIdsByIds(ids)).thenReturn(java.util.Arrays.asList(7L));
        when(majorLeadMapper.deleteByIds(ids)).thenReturn(2);
        when(sysLinkageMapper.selectRoleIdByKey("major_lead")).thenReturn(103L);
        when(majorLeadMapper.countByUserId(7L)).thenReturn(0);

        service.deleteByIds(ids);

        verify(sysLinkageMapper).deleteUserRole(7L, 103L);
    }

    @Test
    void insert_invalid_user_should_throw() {
        MajorLead ml = new MajorLead();
        ml.setMajorId(1L); ml.setUserId(9999L);
        when(sysLinkageMapper.existsUser(9999L)).thenReturn(0);
        try {
            service.insert(ml);
            assert false : "Expected ServiceException";
        } catch (ServiceException ex) {
            // ok
        }
        verify(majorLeadMapper, never()).insert(any());
    }

    @Test
    void retireUser_should_remove_all_and_revoke_role() {
        when(majorLeadMapper.deleteByUserId(7L)).thenReturn(2);
        when(sysLinkageMapper.selectRoleIdByKey("major_lead")).thenReturn(103L);
        int rows = service.retireUser(7L);
        assert rows == 2;
        verify(sysLinkageMapper).deleteUserRole(7L, 103L);
    }
}
