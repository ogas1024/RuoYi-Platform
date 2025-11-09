package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LibraryDownloadLog;
import com.ruoyi.manage.domain.vo.DayCount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 图书下载日志 Mapper
 */
public interface LibraryDownloadLogMapper {
    int insert(LibraryDownloadLog log);

    /**
     * 统计：按天分组下载成功数量（result='0'）。
     *
     * @param from 起始（含）
     * @param to   截止（不含）
     */
    List<DayCount> selectDownloadCountByDay(@Param("from") Date from, @Param("to") Date to);

    /**
     * 下载用户TOP榜（按成功下载次数统计）
     */
    List<com.ruoyi.manage.vo.TopUserVO> selectTopDownloadUsers(@Param("limit") Integer limit);
}
