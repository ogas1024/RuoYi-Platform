package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.MajorLead;

import java.util.List;

public interface IMajorLeadService {
    List<MajorLead> selectList(MajorLead query);

    int insert(MajorLead data);

    int deleteByIds(Long[] ids);

    int deleteByMajorAndUser(Long majorId, Long userId);

    /**
     * 列出拥有 RuoYi 角色 major_lead 的用户（可按专业筛选是否已绑定该专业）
     */
    List<com.ruoyi.manage.vo.RoleUserLeadVO> listRoleUsers(Long majorId);

    /**
     * 卸任负责人：移除该用户的所有专业映射，并撤销 major_lead 角色
     */
    int retireUser(Long userId);

    /**
     * 查询当前或指定用户被分配的专业列表
     */
    java.util.List<com.ruoyi.manage.domain.Major> listMyMajors(Long userId);
}
