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
}
