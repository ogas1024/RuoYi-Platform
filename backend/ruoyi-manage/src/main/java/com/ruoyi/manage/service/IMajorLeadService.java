package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.MajorLead;

import java.util.List;

/**
 * 专业负责人 服务接口
 */
public interface IMajorLeadService {
    /**
     * 列表查询
     */
    List<MajorLead> selectList(MajorLead query);

    /**
     * 新增负责人映射
     */
    int insert(MajorLead data);

    /**
     * 批量删除负责人映射
     */
    int deleteByIds(Long[] ids);

    /**
     * 按专业与用户删除映射
     */
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
