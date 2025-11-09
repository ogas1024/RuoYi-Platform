package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityRoom;

import java.util.List;

/**
 * 功能房-房间 服务接口
 */
public interface IFacilityRoomService {
    /**
     * 查询房间列表
     * @param query 查询条件（楼房、楼层、状态等）
     * @return 房间集合
     */
    List<FacilityRoom> selectList(FacilityRoom query);

    /**
     * 查询房间详情
     * @param id 房间ID
     * @return 实体
     */
    FacilityRoom selectById(Long id);

    /**
     * 新增房间
     * @param data 实体
     * @return 影响行数
     */
    int insert(FacilityRoom data);

    /**
     * 编辑房间
     * @param data 实体
     * @return 影响行数
     */
    int update(FacilityRoom data);

    /**
     * 批量删除房间
     * @param ids ID数组
     * @return 影响行数
     */
    int deleteByIds(Long[] ids);

    /**
     * 启用/禁用房间
     * @param id 房间ID
     * @param enable true 启用 / false 禁用
     * @return 影响行数
     */
    int enable(Long id, boolean enable);
}
