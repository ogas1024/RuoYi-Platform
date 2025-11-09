package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBan;

import java.util.List;

/**
 * 功能房-封禁 服务接口
 */
public interface IFacilityBanService {
    /**
     * 新增封禁记录（生效）
     */
    int ban(FacilityBan data);

    /**
     * 解除封禁
     */
    int unban(Long id);

    /**
     * 列表查询
     * @param status 0正常 1停用
     * @param userId 用户ID（可空）
     */
    List<FacilityBan> list(String status, Long userId);
    /**
     * 带用户名筛选的查询（用户名模糊匹配）。
     */
    default List<FacilityBan> list(String status, Long userId, String username) {
        // 默认实现保留向后兼容，由具体实现类决定是否使用用户名
        return list(status, userId);
    }

    /**
     * 申请人是否在封禁期内
     */
    boolean isApplicantBanned(Long userId);
}
