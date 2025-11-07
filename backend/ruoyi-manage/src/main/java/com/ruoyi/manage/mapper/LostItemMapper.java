package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LostItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LostItemMapper {
    LostItem selectById(Long id);
    List<LostItem> selectList(@Param("query") LostItem query, @Param("statusList") List<Integer> statusList);
    int insert(LostItem data);
    int update(LostItem data);
    int softDeleteById(@Param("id") Long id, @Param("username") String username, @Param("admin") boolean admin);

    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time);
    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time, @Param("reason") String reason);
    int offline(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time, @Param("reason") String reason);
    int restore(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time);
    int solve(@Param("id") Long id, @Param("username") String username);

    int setSolvedAdmin(@Param("id") Long id, @Param("solved") Integer solved, @Param("auditBy") String auditBy);
}
