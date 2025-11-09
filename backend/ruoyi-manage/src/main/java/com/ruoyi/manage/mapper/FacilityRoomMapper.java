package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilityRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能房-房间 Mapper
 */
public interface FacilityRoomMapper {
    FacilityRoom selectById(Long id);

    List<FacilityRoom> selectList(@Param("query") FacilityRoom query);

    int insert(FacilityRoom data);

    int update(FacilityRoom data);

    int softDeleteByIds(@Param("ids") Long[] ids);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 统计指定楼房ID集合下未删除的房间数量
     * @param ids 楼房ID数组
     * @return 数量
     */
    int countByBuildingIds(@Param("ids") Long[] ids);
}
