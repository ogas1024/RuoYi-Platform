package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBuilding;

import java.util.List;

/**
 * 功能房-楼房 服务接口
 */
public interface IFacilityBuildingService {
    /**
     * 查询楼房列表
     * @param query 查询条件（名称、状态等）
     * @return 楼房集合
     */
    List<FacilityBuilding> selectList(FacilityBuilding query);

    /**
     * 查询楼房详情
     * @param id 楼房ID
     * @return 实体
     */
    FacilityBuilding selectById(Long id);

    /**
     * 新增楼房
     * @param data 实体
     * @return 影响行数
     */
    int insert(FacilityBuilding data);

    /**
     * 编辑楼房
     * @param data 实体
     * @return 影响行数
     */
    int update(FacilityBuilding data);

    /**
     * 批量删除楼房
     * @param ids ID数组
     * @return 影响行数
     */
    int deleteByIds(Long[] ids);
}
