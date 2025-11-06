package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.manage.domain.LibraryLibrarian;
import com.ruoyi.manage.mapper.LibraryLibrarianMapper;
import com.ruoyi.manage.mapper.SysLinkageMapper;
import com.ruoyi.manage.service.ILibraryLibrarianService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LibraryLibrarianServiceImpl implements ILibraryLibrarianService {

    private static final String ROLE_KEY = "librarian";

    @Resource
    private LibraryLibrarianMapper mapper;
    @Resource
    private SysLinkageMapper sysLinkageMapper;

    @Override
    public List<LibraryLibrarian> selectList(LibraryLibrarian query) {
        return mapper.selectList(query);
    }

    @Override
    @Transactional
    public int appoint(Long userId, String operator) {
        // 校验用户存在
        Integer u = sysLinkageMapper.existsUser(userId);
        if (u == null || u == 0) return 0;
        // 入库
        LibraryLibrarian data = new LibraryLibrarian();
        data.setUserId(userId);
        // 快照账号/姓名，便于前端直接展示
        java.util.Map<String, Object> user = sysLinkageMapper.selectUserNameNickName(userId);
        if (user != null) {
            Object un = user.get("username");
            Object nn = user.get("nickname");
            data.setUsername(un == null ? null : String.valueOf(un));
            data.setNickname(nn == null ? null : String.valueOf(nn));
        }
        data.setCreateBy(operator);
        data.setCreateTime(DateUtils.getNowDate());
        int n = mapper.insert(data);
        // 绑定角色
        Long roleId = sysLinkageMapper.selectRoleIdByKey(ROLE_KEY);
        if (roleId != null) {
            Integer exists = sysLinkageMapper.existsUserRole(userId, roleId);
            if (exists == null || exists == 0) sysLinkageMapper.insertUserRole(userId, roleId);
        }
        return n;
    }

    @Override
    @Transactional
    public int dismiss(Long[] userIds, String operator) {
        int n = mapper.deleteByUserIds(userIds);
        Long roleId = sysLinkageMapper.selectRoleIdByKey(ROLE_KEY);
        if (roleId != null) {
            for (Long uid : userIds) {
                Integer remains = mapper.countByUserId(uid);
                if (remains == null || remains == 0) {
                    sysLinkageMapper.deleteUserRole(uid, roleId);
                }
            }
        }
        return n;
    }
}
