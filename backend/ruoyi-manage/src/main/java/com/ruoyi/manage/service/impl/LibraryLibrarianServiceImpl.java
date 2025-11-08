package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.manage.domain.LibraryLibrarian;
import com.ruoyi.manage.mapper.LibraryLibrarianMapper;
import com.ruoyi.manage.mapper.SysLinkageMapper;
import com.ruoyi.manage.service.ILibraryLibrarianService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class LibraryLibrarianServiceImpl implements ILibraryLibrarianService {

    private static final String ROLE_KEY = "librarian";

    @Autowired
    private LibraryLibrarianMapper mapper;
    @Autowired
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
        // 为避免“仅有角色、无记录”场景下返回0导致前端提示失败，这里按“受影响的用户数”返回
        Long roleId = sysLinkageMapper.selectRoleIdByKey(ROLE_KEY);
        int affectedUsers = 0;
        // 预先记录每个用户是否存在绑定记录或角色，用于成功判定
        java.util.Map<Long, Boolean> hadAnyBinding = new java.util.HashMap<>();
        for (Long uid : userIds) {
            boolean hadRecord = (mapper.countByUserId(uid) != null && mapper.countByUserId(uid) > 0);
            boolean hadRole = roleId != null && (sysLinkageMapper.existsUserRole(uid, roleId) != null && sysLinkageMapper.existsUserRole(uid, roleId) > 0);
            hadAnyBinding.put(uid, hadRecord || hadRole);
        }

        // 删除自定义表记录（若存在）
        mapper.deleteByUserIds(userIds);

        // 若无任何剩余记录，则撤销角色（保持与列表联动一致）
        if (roleId != null) {
            for (Long uid : userIds) {
                Integer remains = mapper.countByUserId(uid);
                if (remains == null || remains == 0) {
                    sysLinkageMapper.deleteUserRole(uid, roleId);
                }
            }
        }

        // 统计受影响的用户：原先拥有记录或角色的都计为1（幂等操作也视为成功）
        for (Long uid : userIds) {
            if (Boolean.TRUE.equals(hadAnyBinding.get(uid))) affectedUsers++;
        }
        return affectedUsers;
    }
}
