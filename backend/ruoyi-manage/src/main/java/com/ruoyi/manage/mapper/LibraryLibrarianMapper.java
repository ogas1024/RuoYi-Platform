package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LibraryLibrarian;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LibraryLibrarianMapper {
    List<LibraryLibrarian> selectList(LibraryLibrarian query);
    int insert(LibraryLibrarian data);
    int deleteByUserIds(@Param("userIds") Long[] userIds);
    Integer countByUserId(@Param("userId") Long userId);
}

