package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBan;

import java.util.List;

public interface IFacilityBanService {
    int ban(FacilityBan data);

    int unban(Long id);

    List<FacilityBan> list(String status, Long userId);
    /**
     * 带用户名筛选的查询（用户名模糊匹配）。
     */
    default List<FacilityBan> list(String status, Long userId, String username) {
        // 默认实现保留向后兼容，由具体实现类决定是否使用用户名
        return list(status, userId);
    }

    boolean isApplicantBanned(Long userId);
}
